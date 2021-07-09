package main.controller;

import main.AbstractIntegrationIT;
import main.data.request.LoginRequest;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiAuthControllerIT extends AbstractIntegrationIT {

    @Test
    public void tryShowLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@user.ru");
        request.setPassword("12345678");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(post("http://localhost:8080/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void tryAccessDenyTesting() throws Exception{
        LoginRequest request = new LoginRequest();
        request.setEmail("user@user.ru");
        request.setPassword("1234567");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);
        //TODO isResponse 403 right?
        mockMvc.perform(post("http://localhost:8080/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


}
