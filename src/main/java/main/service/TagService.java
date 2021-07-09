package main.service;

import main.data.request.TagRequest;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.DataMessage;
import main.data.response.type.SingleTag;

public interface TagService {
    ListResponse<SingleTag> getPostTags();
    ListResponse<SingleTag> getPostTags(String tag);
    ListResponse<SingleTag> getPostTags(String tag, Integer offset, Integer itemsPerPage);
    Response<SingleTag> createTag(TagRequest request);
    Response<DataMessage> deleteTag(int id);
}
