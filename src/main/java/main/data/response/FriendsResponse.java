package main.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.internal.cglib.core.$MethodInfoTransformer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import main.data.response.base.ListResponse;
import main.data.response.type.DataMessage;
import main.data.response.type.MeProfile;
import main.model.Friendship;
import main.model.Person;
import main.service.PersonService;
import main.service.PersonServiceImpl;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendsResponse extends ListResponse {

    @JsonProperty(value = "data")
    private List<MeProfile> FriendsOrRecommendedList;
    private DataMessage dataMessage;

    public FriendsResponse(Page<Friendship> friends) {
        this.setOffset(friends.getNumber() * friends.getNumberOfElements());
        this.setPerPage(friends.getNumberOfElements());
        this.setError("");
        this.setTimestamp(Instant.now().toEpochMilli());
        this.setTotal(friends.getTotalElements());
        FriendsOrRecommendedList = new ArrayList<>();

        for (Friendship item : friends.getContent()) {
            FriendsOrRecommendedList.add(new MeProfile(item.getSrc()));
        }
    }

    public FriendsResponse(List<Optional<Person>> recommendedFriends, int offset, int itemPerPage) {
        this.setOffset(itemPerPage);
        this.setPerPage(itemPerPage);
        this.setError("");
        this.setTimestamp(Instant.now().toEpochMilli());
        this.setTotal(recommendedFriends.size());
        FriendsOrRecommendedList = new ArrayList<>();
        for (Optional<Person> item : recommendedFriends) {
            item.ifPresent(person -> {
                if (IsThisPersonAllowed(item)) {
                    FriendsOrRecommendedList.add(new MeProfile(person));
                }
            });
        }
    }


    private boolean IsThisPersonAllowed(Optional<Person> item){
        if (!item.get().isDeleted()) {
            return true;
        } else {
            return false;
        }
    }
}
