package main.data.response.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GeoLocationResponseShort {

  @JsonProperty("country_name")
  @ApiModelProperty(example = "Аргентина")
  private String countryName;

  @JsonProperty("city")
  @ApiModelProperty(example = "Ушуая")
  private String city;

}
