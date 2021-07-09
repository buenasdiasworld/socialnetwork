package main.service;

import lombok.AllArgsConstructor;
import main.data.response.CaptchaResponse;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.Objects;

@AllArgsConstructor
@Service
public class CaptchaServiceImpl implements CaptchaService{


    @Override
    public CaptchaResponse checkCaptcha(String token, String secretCode, String captchaUrl) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = String.format(captchaUrl, secretCode, token);
        CaptchaResponse response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponse.class);
        if (!Objects.requireNonNull(response).isSuccess()) {
            throw  new BadRequestException(new ApiError("invalid_request", "Заполните капчу"));
        } else {
            return response;
        }
    }
}
