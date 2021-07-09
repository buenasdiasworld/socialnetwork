package main.data.response.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Storage {

  @JsonProperty("id")
  @ApiModelProperty(example = "/img/be5b8369-f8b9-442b-a899-9e920cff4cb1_1.jpg")
  private String id;
  @ApiModelProperty(example = "1")
  private int ownerId;
  @ApiModelProperty(example = "be5b8369-f8b9-442b-a899-9e920cff4cb1_1.jpg")
  private String fileName;
  @ApiModelProperty(example = "/be5b8369-f8b9-442b-a899-9e920cff4cb1_1.jpg")
  private String relativeFilePath;
  @ApiModelProperty(example = "/img/be5b8369-f8b9-442b-a899-9e920cff4cb1_1.jpg")
  private String rawFileURL;
  @ApiModelProperty(example = "jpg")
  private String fileFormat;
  @ApiModelProperty(example = "5613")
  private long bytes;
  @ApiModelProperty(example = "IMAGE")
  private String fileType;
  @ApiModelProperty(example = "1606480153556")
  private long createdAt;


}
