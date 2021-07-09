package main.service;

import main.data.request.ListCityRequest;
import main.data.response.base.ListResponse;
import main.data.response.type.CityList;

public interface CityService {
    ListResponse<CityList> list(ListCityRequest request);
}
