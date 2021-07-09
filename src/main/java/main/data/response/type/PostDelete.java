package main.data.response.type;

import lombok.Data;

@Data
public class PostDelete {
    private Integer id;

    public PostDelete(Integer id){
        this.id = id;
    }
}
