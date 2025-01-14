package com.mhealth.admin.service;

import com.mhealth.admin.model.City;
import com.mhealth.admin.model.Country;
import com.mhealth.admin.model.Language;
import com.mhealth.admin.model.State;
import com.mhealth.admin.repository.CityRepository;
import com.mhealth.admin.repository.CountryRepository;
import com.mhealth.admin.repository.LanguageRepository;
import com.mhealth.admin.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GlobalService {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private LanguageRepository languageRepository;

//    @Value()
    private List<Integer> countryIdList;

    public Map<Integer, String> getCities(Locale locale) {
        // Fetch states based on the country ID
        List<State> states = stateRepository.findByCountry_IdIn(countryIdList);

        // Extract state IDs
        List<Integer> stateIds = states.stream()
                .map(State::getId)
                .collect(Collectors.toList());

        // Fetch cities based on the state IDs
        List<City> cities = cityRepository.findByState_IdIn(stateIds);

        // Map city IDs to city names
        return cities.stream()
                .collect(Collectors.toMap(City::getId, City::getName));
    }

    public Map<String, Object> getCountries(Locale locale, List<Integer> phonecodeList) {
        List<Country> countries;

        // Filter countries based on the provided IDs and phone codes
        if (countryIdList != null && !countryIdList.isEmpty() && phonecodeList != null && !phonecodeList.isEmpty()) {
            countries = countryRepository.findByIdInAndPhonecodeIn(countryIdList, phonecodeList);
        } else if (countryIdList != null && !countryIdList.isEmpty()) {
            countries = countryRepository.findByIdIn(countryIdList);
        } else if (phonecodeList != null && !phonecodeList.isEmpty()) {
            countries = countryRepository.findByPhonecodeIn(phonecodeList);
        } else {
            countries = countryRepository.findAll();
        }

        // Map phone codes
        List<Integer> countryCodes = countries.stream()
                .map(Country::getPhonecode)
                .distinct()
                .collect(Collectors.toList());

        // Map country IDs to names
        Map<Integer, String> countryList = countries.stream()
                .collect(Collectors.toMap(Country::getId, Country::getName));

        // Prepare the output
        Map<String, Object> output = new HashMap<>();
        output.put("country_code", countryCodes);
        output.put("country_list", countryList);

        return output;
    }

    public Map<Integer, String> getLanguageList(Locale locale) {
        // Fetch languages with active status
        List<Language> languages = languageRepository.findByStatus("A");

        // Map the list to a key-value pair of ID and name
        return languages.stream()
                .collect(Collectors.toMap(Language::getId, Language::getName));
    }
}
