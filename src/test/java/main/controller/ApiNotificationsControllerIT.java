package main.controller;

import main.AbstractIntegrationIT;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiNotificationsControllerIT extends AbstractIntegrationIT {

    @Test
    @WithUserDetails(value = "user@user.ru")
    public void shouldGetListOfNotifications() throws Exception {
        mockMvc.perform(get("/api/v1/notifications")
                .param("offset", "0")
                .param("itemPerPage", "20")
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.total").value("1"))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].entity_author.id").value("2"))
                .andExpect(jsonPath("$.data[0].read_status").value("SENT"))
                .andExpect(jsonPath("$.data[0].info").value("first comment to the post"))
                .andExpect(jsonPath("$.data[0].event_type").value("POST_COMMENT"));
    }

    @Test
    @WithUserDetails(value = "user@user.ru")
    public void shouldReadNotifications() throws Exception {
        mockMvc.perform(put("/api/v1/notifications")
                .param("id", "0")
                .param("all", "true")
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.total").value("1"))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].entity_author.id").value("2"))
                .andExpect(jsonPath("$.data[0].read_status").value("READ"))
                .andExpect(jsonPath("$.data[0].info").value("first comment to the post"))
                .andExpect(jsonPath("$.data[0].event_type").value("POST_COMMENT"));
    }
}
