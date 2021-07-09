package main.service;

import main.data.response.CaptchaResponse;

public interface CaptchaService {
    CaptchaResponse checkCaptcha(String token, String secretCode, String captchaUrl);
}
