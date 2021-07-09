package main.service;

import lombok.AllArgsConstructor;
import main.core.OffsetPageRequest;
import main.data.request.TagRequest;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.DataMessage;
import main.data.response.type.SingleTag;
import main.model.Tag;
import main.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public ListResponse<SingleTag> getPostTags() {
        return getPostTags(null);
    }

    @Override
    public ListResponse<SingleTag> getPostTags(String tag) {
        return getPostTags(tag, null, null);
    }

    @Override
    public ListResponse<SingleTag> getPostTags(String tag, Integer offset, Integer itemsPerPage) {
        List<SingleTag> tags = new ArrayList<>();
        if (offset == null || itemsPerPage == null) {
            Iterable<Tag> tagIterable = tag == null ? tagRepository.findAll() : tagRepository.findByTagLike(tag);
            for (Tag t : tagIterable) {
                tags.add(new SingleTag(t.getId(), t.getTag()));
            }
        } else {
            OffsetPageRequest offsetPageRequest = new OffsetPageRequest(offset, itemsPerPage, Sort.by("tag"));
            Page<Tag> tagsPage = tagRepository.findAll(offsetPageRequest);
            for (Tag t : tagsPage.getContent()) {
                tags.add(new SingleTag(t.getId(), t.getTag()));
            }
        }

        return new ListResponse<>(tags);
    }

    @Override
    public Response<SingleTag> createTag(TagRequest request) {
        Optional<Tag> optionalTag = tagRepository.findTagByTag(request.getTag());
        SingleTag singleTag;
        Tag tag;
        if (optionalTag.isPresent()) {
            tag = optionalTag.get();
        } else {
            tag = new Tag();
            tag.setTag(request.getTag());
            tag = tagRepository.save(tag);
        }
        singleTag = new SingleTag(tag.getId(), tag.getTag());
        return new Response<>(singleTag);
    }

    @Override
    public Response<DataMessage> deleteTag(int id) {
        tagRepository.deleteById(id);
        return new Response<>(new DataMessage("ok"));
    }
}
