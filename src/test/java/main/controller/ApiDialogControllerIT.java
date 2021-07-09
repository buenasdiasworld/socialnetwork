package main.controller;

import main.AbstractIntegrationIT;
import main.data.request.DialogAddRequest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails("user@user.ru")
public class ApiDialogControllerIT extends AbstractIntegrationIT {

    @Test
    public void createDialogTest() throws Exception {
        List<Integer> userIds = Collections.singletonList(2);
        DialogAddRequest request = new DialogAddRequest();
        request.setUserIds(userIds);
        String body = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        ResultActions perform = mockMvc.perform(post("/api/v1/dialogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        );

        perform.andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void listDialogTest() throws Exception {
        createDialogTest();
        ResultActions perform = mockMvc.perform(get("/api/v1/dialogs")
                .accept("application/json")
        );

        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].persons").isArray())
        ;
    }
}
