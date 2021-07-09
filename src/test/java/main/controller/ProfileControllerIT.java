package main.controller;

import main.AbstractIntegrationIT;
import main.data.request.PostRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = "user@user.ru")
public class ProfileControllerIT extends AbstractIntegrationIT {

    private PostRequest request;
    private List<String> tags;

    @Before
    public void before() {
        request = new PostRequest();
        request.setTitle("Test Post");
        request.setPostText("Text to the test post");

        tags = new ArrayList<>();
        tags.add("super");
        tags.add("duper");
    }

    @Test
    public void addPostToWall_withEmptyTags() throws Exception {
        request.setTags(new ArrayList<>());
        String jsonRequest = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(post("/api/v1/users/{id}/wall", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.title").value("Test Post"))
        ;
    }

    @Test
    public void addPostToWall_withTags() throws Exception {
        request.setTags(tags);
        String jsonRequest = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(post("/api/v1/users/{id}/wall", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.title").value("Test Post"))
            .andExpect(jsonPath("$.data.tags[1]").value("duper"))
        ;

        checkWallAfterPost().andExpect(jsonPath("$.total").value("7"));

    }

    private ResultActions checkWallAfterPost() throws Exception{
        return mockMvc.perform(get("/api/v1/feeds")
            .param("name", "")
            .param("offset", "0")
            .param("itemPerPage", "20")
            .accept("application/json"));
    }

    @Test
    public void addPostToWall_withBadRequest_withoutTags() throws Exception {
        String jsonRequest = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(post("/api/v1/users/{id}/wall", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error_description").isNotEmpty())
        ;
    }

    @Test(expected = IllegalArgumentException.class)
    public void addPostToWall_withoutPathVarId() throws Exception {
        request.setTags(new ArrayList<>());
        String jsonRequest = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(post("/api/v1/users/{id}/wall")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)).andDo(print())
        ;
    }

    @Test
    public void showPersonWall() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/wall", "1")
            .param("offset", "0")
            .param("itemPerPage", "20")
            .accept("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(authenticated())
            .andExpect(jsonPath("$.total").value("4"))
            .andExpect(jsonPath("$.data[0].author.email").value("user@user.ru"))
            .andExpect(jsonPath("$.data[1].title").value("Hello, Post Two"))
        ;
    }
}
