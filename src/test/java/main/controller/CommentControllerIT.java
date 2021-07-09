package main.controller;

import main.AbstractIntegrationIT;
import main.data.request.CommentRequest;
import main.service.NotificationService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WithUserDetails("user@user.ru")
public class CommentControllerIT extends AbstractIntegrationIT {

    @Autowired
    private NotificationService notificationService;

    @Test
    public void shouldShowPostComment() throws Exception {
        Integer postId = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post/{id}/comments", postId)
                .param("offset", "0")
                .param("itemPerPage", "20")
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.total").value("1"))
                .andExpect(jsonPath("$.data[0].sub_comments[0].comment_text").value("second comment to the post"))
                .andExpect(jsonPath("$.data[0].comment_text").value("first comment to the post"))
        ;
    }

    @Test
    public void shouldCreatePostComment_zeroLevel() throws Exception {
        String postId = "2";
        String commentText = "new post comment fdsafsda sda fsa";

        CommentRequest request = new CommentRequest();
        request.setParentId(2);
        request.setCommentText(commentText);
        String jsonRequest = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(post("/api/v1/post/{id}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)).andDo(print())
                .andDo(print())
                .andExpect(jsonPath("$.data.comment_text").value(commentText))
        ;

    }
}
