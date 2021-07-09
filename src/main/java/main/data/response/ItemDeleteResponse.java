package main.data.response;

import lombok.Data;
import main.data.response.base.RecordResponse;
import main.data.response.type.ItemDelete;

@Data
public class ItemDeleteResponse extends RecordResponse {
    private ItemDelete data;
}
