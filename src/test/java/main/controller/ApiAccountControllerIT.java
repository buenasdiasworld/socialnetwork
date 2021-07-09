package main.controller;

import main.AbstractIntegrationIT;
import main.core.auth.JwtUtils;
import main.data.request.NotificationSettingsRequest;
import main.data.request.PasswordRecoveryRequest;
import main.data.request.PasswordSetRequest;
import main.data.request.RegistrationRequest;
import main.data.response.type.GeoLocationResponseShort;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiAccountControllerIT extends AbstractIntegrationIT {

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    RestTemplate restTemplate;

    @MockBean
    JavaMailSender emailSender;

    @Value("${linkToChange.password}")
    public String passwordChangeLink;

    @Value("${reCaptcha.secretCode}")
    public String secretCode;
    @Value("${reCaptcha.url}")
    public String captchaUrl;

    @Test
    @WithUserDetails(value = "user@user.ru")
    public void shouldGetSettings() throws Exception {

        mockMvc.perform(get("/api/v1/account/notifications"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.data[0].enable").value("true"))
                .andExpect(jsonPath("$.data[0].type").value("POST_COMMENT"))
                .andExpect(jsonPath("$.data[1].enable").value("false"))
                .andExpect(jsonPath("$.data[1].type").value("POST"))
                .andExpect(jsonPath("$.data[2].enable").value("true"))
                .andExpect(jsonPath("$.data[2].type").value("LIKE"));
    }

    @Test
    @WithUserDetails(value = "user@user.ru")
    public void shouldSetNotifications() throws Exception {
        NotificationSettingsRequest request = new NotificationSettingsRequest();
        request.setNotificationType("POST");
        request.setEnable(true);

        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(put("/api/v1/account/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.data.message").value("Успешная смена статуса"));
    }

    @Test
    public void shouldSetPasswordWhenPersonIsUnauthenticated() throws Exception {
        PasswordSetRequest request = new PasswordSetRequest();
        request.setToken("");
        request.setPassword("12345678");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.REFERER, passwordChangeLink + "?code=referer");

        mockMvc.perform(put("/api/v1/account/password/set")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(jsonPath("$.data.message").value("Успешная смена пароля"));
    }

    @Test
    @WithUserDetails(value = "user@user.ru")
    public void shouldSetPasswordWhenPersonIsAuthenticated() throws Exception {
        PasswordSetRequest request = new PasswordSetRequest();
        request.setToken("");
        request.setPassword("12345678");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        Mockito.when(jwtUtils.validateJwtToken(request.getToken())).thenReturn(true);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.REFERER, "");

        mockMvc.perform(put("/api/v1/account/password/set")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.data.message").value("Успешная смена пароля"));
    }

    @Test
    public void shouldRestorePasswordWhenEmailIsCorrect() throws Exception {
        PasswordRecoveryRequest request = new PasswordRecoveryRequest();
        request.setEmail("user@user.ru");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(put("/api/v1/account/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(jsonPath("$.data.message").value("Успешный запрос"));
    }

    @Test
    public void shouldReturnBadRequestWhenEmailIsIncorrect() throws Exception {
        PasswordRecoveryRequest request = new PasswordRecoveryRequest();
        request.setEmail("person@user.ru");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(put("/api/v1/account/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(unauthenticated())
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("Такой email не зарегистрирован"));
    }

    @Test
    @WithUserDetails(value = "user@user.ru")
    public void shouldSendLinkToChangePassword() throws Exception {

        mockMvc.perform(put("/api/v1/account/password/change"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.data.message").value("Успешный запрос"));
    }

    @Test
    public void shouldReturnForbiddenWhenUnauthenticatedByChangePassword() throws Exception {

        mockMvc.perform(put("/api/v1/account/password/change"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(unauthenticated());
    }

    @Test
    @WithUserDetails(value = "user@user.ru")
    public void shouldSendLinkToChangeEmail() throws Exception {

        mockMvc.perform(put("/api/v1/account/email/change"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.data.message").value("Успешный запрос"));
    }

    @Test
    public void shouldReturnForbiddenWhenUnauthenticatedByChangeEmail() throws Exception {

        mockMvc.perform(put("/api/v1/account/email/change"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(unauthenticated());
    }

    @Test
    @WithUserDetails(value = "user@user.ru")
    public void shouldSetEmailWhenPersonIsAuthenticatedAndEmailIsNew() throws Exception {
        PasswordRecoveryRequest request = new PasswordRecoveryRequest();
        request.setEmail("person@user.ru");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(put("/api/v1/account/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.data.message").value("Успешная смена email"));
    }

    @Test
    @WithUserDetails(value = "user@user.ru")
    public void shouldReturnBadRequestWhenPersonIsAuthenticatedAndEmailIsOld() throws Exception {
        PasswordRecoveryRequest request = new PasswordRecoveryRequest();
        request.setEmail("user@user.ru");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(put("/api/v1/account/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("Такой email уже зарегистрирован в сети"));
    }

    @Test
    public void shouldReturnForbiddenWhenUnauthenticatedBySettingEmail() throws Exception {
        PasswordRecoveryRequest request = new PasswordRecoveryRequest();
        request.setEmail("bobby@user.ru");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(put("/api/v1/account/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(unauthenticated());
    }

    @Test
    public void shouldRegisterNewPerson() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "email@mail.ru", "password", "password",
                "Danny", "Wilds", "", "testCaptcha");

        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        GeoLocationResponseShort geoResp = new GeoLocationResponseShort();
        geoResp.setCountryName("Россия");
        geoResp.setCity("Москва");
        String requestUrl = "http://api.ipstack.com/" + "127.0.0.1"
                + "?access_key=1f3f0d1d66eaeb5f262fb6f2603da2ce&fields=country_name,city&language=ru";
        Mockito.when(restTemplate.getForObject(requestUrl, GeoLocationResponseShort.class)).thenReturn(geoResp);

        doNothing().when(emailSender).send(new SimpleMailMessage());

        mockMvc.perform(post("/api/v1/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(jsonPath("$.data.message").value("Успешная регистрация"));
    }

    @Test
    public void shouldNotRegisterNewPersonWhenEmailAlreadyInDB() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "user@user.ru", "password", "password",
                "Danny", "Wilds", "", "");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(post("/api/v1/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(unauthenticated())
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("такой email уже существует"));
    }

    @Test
    public void shouldNotRegisterNewPersonWhenPasswordsNotMatchWithEachOther() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "email@mail.ru", "pass1", "pass2",
                "Danny", "Wilds", "","");
        String requestJson = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(request);

        mockMvc.perform(post("/api/v1/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(unauthenticated())
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("пароли не совпадают"));
    }
}
