package main.service;

import main.data.request.ListCountryRequest;
import main.data.response.base.ListResponse;
import main.data.response.type.CountryList;

public interface CountryService {
    ListResponse<CountryList> list(ListCountryRequest request);
}
