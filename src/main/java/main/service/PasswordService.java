package main.service;

import main.data.request.PasswordRecoveryRequest;
import main.data.request.PasswordSetRequest;
import main.data.response.base.Response;
import main.data.response.type.InfoInResponse;

public interface PasswordService {

    Response<InfoInResponse> restorePassword(PasswordRecoveryRequest request, String link);

    Response<InfoInResponse> setPassword(PasswordSetRequest request, String referer);

    Response<InfoInResponse> changePassOrEmail(String subject, String link);

    Response<InfoInResponse> setEmail(PasswordRecoveryRequest request);
}
