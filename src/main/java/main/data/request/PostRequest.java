package main.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PostRequest {
    @ApiModelProperty(value = "title", example = "Заголовок поста")
    private String title;
    @ApiModelProperty(value = "post_text", example = "Текст поста")
    @JsonProperty(value = "post_text")
    private String postText;
    private List<String> tags;
}
