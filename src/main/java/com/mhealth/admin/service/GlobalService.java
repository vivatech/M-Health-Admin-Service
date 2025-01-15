package com.mhealth.admin.service;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
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

    @Value("${app.country.id}")
    private List<Integer> countryIdList;
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private FileService fileService;

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

    public Object deleteProfilePicture(Locale locale, Integer userId) throws IOException {
        Response response = new Response();

        // Find the user
        Optional<Users> existingPatientUser = usersRepository.findById(userId);
        if (existingPatientUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingPatientUser.get();

        //check for picture validation
        if(StringUtils.isEmpty(existingUser.getProfilePicture())){
            response.setCode(Constants.CODE_O);
            response.setMessage("Picture not found");
            response.setStatus(Status.FAILED);
            return response;
        }
        String directory = Constants.USER_PROFILE_PICTURE + existingUser.getUserId();
        fileService.deleteFile(directory, existingUser.getProfilePicture());

        //update table with null
        existingUser.setProfilePicture(null);

        usersRepository.save(existingUser);

        response.setStatus(Status.SUCCESS);
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.PROFILE_PICTURE_DELETE_SUCCESSFULLY, null, locale));

        return response;

    }
}
