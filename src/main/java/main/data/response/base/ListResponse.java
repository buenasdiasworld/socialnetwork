package main.data.response.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class ListResponse<T> {
    private String error = "";
    @ApiModelProperty(value = "timestamp", example = "1559751301818")
    private long timestamp = Instant.now().toEpochMilli();
    @ApiModelProperty(value = "total", example = "40")
    private long total;
    private int offset;
    @ApiModelProperty(value = "perPage", example = "10")
    private int perPage;
    private List<T> data;

    public ListResponse(List<T> data) {
        this.data = data;
    }

    public void addAll(List<T> data) {
        this.data.addAll(data);
    }

    public void add(T data) {
        this.data.add(data);
    }

    public ListResponse(List<T> data, long total, int offset, int perPage) {
        this.data = data;
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
    }
}
