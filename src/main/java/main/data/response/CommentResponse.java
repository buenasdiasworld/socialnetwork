package main.data.response;

import lombok.Data;
import main.data.response.base.RecordResponse;
import main.data.response.type.CommentInResponse;

@Data
public class CommentResponse extends RecordResponse {
    private CommentInResponse data;


}
