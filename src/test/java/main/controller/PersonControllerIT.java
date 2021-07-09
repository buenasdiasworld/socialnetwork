package main.controller;

import main.AbstractIntegrationIT;
import main.data.request.MeProfileRequest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class PersonControllerIT extends AbstractIntegrationIT {

    @WithUserDetails("user@user.ru")
    @Test
    public void shouldShowMyProfile() throws Exception {

        mockMvc.perform(get("/api/v1/users/me"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("user@user.ru"))
                .andExpect(jsonPath("$.data.id").value("1"));
    }

    @Test
    public void shouldNotShowMyProfile() throws Exception {

        mockMvc.perform(get("/api/v1/users/me"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @WithUserDetails("user@user.ru")
    @Test
    public void updateMyProfile() throws Exception {

        MeProfileRequest request = new MeProfileRequest();

        request.setAbout("трололо");
        request.setLastName("Ivanov");
        request.setFirstName("testUser");
        request.setCity(1);
        request.setCountry(1);
        request.setBirthDate(Date.valueOf("2020-10-22"));
        request.setPhone("111-222-3333");
        request.setPhotoURL("/static/img/default_avatar.png");

        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(put("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.about").value("трололо"))
                .andExpect(jsonPath("$.data.last_name").value("Ivanov"));
    }

    @WithUserDetails("user@user.ru")
    @Test
    public void deleteMyProfile() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
