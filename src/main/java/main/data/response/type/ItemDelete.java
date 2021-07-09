package main.data.response.type;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemDelete {
    private Integer id;

    public ItemDelete(Integer id){
        this.id = id;
    }
}
