package main.service;

import main.data.request.ListLanguageRequest;
import main.data.response.base.ListResponse;
import main.data.response.type.LanguageList;

public interface LanguageService {
    ListResponse<LanguageList> list(ListLanguageRequest request);
}
