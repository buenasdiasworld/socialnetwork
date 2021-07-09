package main.service;

import lombok.AllArgsConstructor;
import main.core.OffsetPageRequest;
import main.data.request.ListCountryRequest;
import main.data.response.base.ListResponse;
import main.data.response.type.CountryList;
import main.model.Country;
import main.repository.CountryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public ListResponse<CountryList> list(ListCountryRequest request) {
        List<CountryList> countries = new ArrayList<>();

        Pageable pageable;
        Page<Country> page;

        if (request.getItemPerPage() > 0) {
            pageable = new OffsetPageRequest(request.getOffset(), request.getItemPerPage(), Sort.unsorted());
        } else {
            pageable = Pageable.unpaged();
        }

        if (request.getCountry() != null && !request.getCountry().isEmpty()) {
            page = countryRepository.findByTitleIgnoreCaseContaining(request.getCountry(), pageable);
        } else {
            page = countryRepository.findAll(pageable);
        }

        page.forEach(i -> {
            CountryList item = new CountryList(i);
            countries.add(item);
        });

        return new ListResponse<>(
                countries,
                page.getTotalElements(),
                request.getOffset(),
                request.getItemPerPage()
        );
    }
}
