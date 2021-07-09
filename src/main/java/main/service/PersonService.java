package main.service;

import main.data.request.LoginRequest;
import main.data.request.MeProfileRequest;
import main.data.response.base.Response;
import main.data.response.type.*;
import main.model.Person;

public interface PersonService {
    Response<PersonInLogin> login(LoginRequest request);
    Response<ResponseMessage> logout();
    Response<MeProfile> getMe();
    Response<MeProfile> putMe(MeProfileRequest updatedCurrentPerson);
    Response<InfoInResponse> deleteMe();
    Person loginTelegram(long chatId);
    Person getById(int personId);
    Person getAuthUser();
    Person checkAuthUser(int id);
    boolean isAuthenticated();
    Person save(Person person);
    Response unblockUser(int id);
    Response blockUser(int id);
}
