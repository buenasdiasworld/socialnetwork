package main.controller;

import main.AbstractIntegrationIT;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostControllerIT extends AbstractIntegrationIT {

    @Test
    @WithUserDetails(value = "user@user.ru")
    public void getFeeds() throws Exception {
        mockMvc.perform(get("/api/v1/feeds")
            .param("name", "")
            .param("offset", "0")
            .param("itemPerPage", "20")
            .accept("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(authenticated())
            .andExpect(jsonPath("$.total").value("6"))
            .andExpect(jsonPath("$.data[0].author.email").value("user@user.ru"))
            .andExpect(jsonPath("$.data[1].title").value("Hello, Post Two"))
        ;
    }
}