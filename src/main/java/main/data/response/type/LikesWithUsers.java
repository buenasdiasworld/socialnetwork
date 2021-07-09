package main.data.response.type;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LikesWithUsers {
    int likes;
    List<Integer> users;
}
