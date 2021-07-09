package main.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

@Data
public class MeProfileRequest {

  @ApiModelProperty(value = "имя", example = "Иван")
  @JsonProperty("first_name")
  private String firstName;

  @ApiModelProperty(value = "фамилия", example = "Иванов")
  @JsonProperty("last_name")
  private String lastName;

  @ApiModelProperty(value = "дата рождения", example = "2017-01-01T00:00:00+03:00")
  @JsonProperty("birth_date")
  private Date birthDate;

  @ApiModelProperty(value = "телефон", example = "76666666666")
  private String phone;

  @ApiModelProperty(value = "айди фото", example = "null")
  @JsonProperty("photo_id")
  private String photoURL;

  @ApiModelProperty(value = "о себе", example = "привет привет")
  private String about;

  @ApiModelProperty(value = "город", example = "1")
  private int city;
  @ApiModelProperty(value = "страна", example = "1")
  private int country;


}
