package com.mhealth.admin.service;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.CategoryStatus;
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

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private FileService fileService;
    @Autowired
    private LabCategoryMasterRepository labCategoryMasterRepository;
    @Autowired
    private LabSubCategoryMasterRepository labSubCategoryMasterRepository;

    public Map<Integer, String> getCities(Locale locale, List<Integer> stateIdList) {
        // Fetch cities based on the state IDs
        List<City> cities = cityRepository.findByState_IdIn(stateIdList);

        // Map city IDs to city names
        return cities.stream()
                .collect(Collectors.toMap(City::getId, City::getName));
    }

    public Map<Integer, String> getCountries(Locale locale, List<Integer> phonecodeList) {
        // Filter countries based on the phone codes
        List<Country> countries = countryRepository.findByPhonecodeIn(phonecodeList);

        // Map country IDs to names
        return countries.stream()
                .collect(Collectors.toMap(Country::getId, Country::getName));

    }

    public Map<Integer, String> getLanguageList(Locale locale) {
        // Fetch languages with active status
        List<Language> languages = languageRepository.findByStatus("A");

        // Map the list to a key-value pair of ID and name
        return languages.stream()
                .collect(Collectors.toMap(Language::getId, Language::getName));
    }

    public Map<Integer, String> getProvinceList(Locale locale, List<Integer> countryIdList) {
        // Fetch states based on the country ID
        List<State> states = stateRepository.findByCountry_IdIn(countryIdList);

        // Map the list to a key-value pair of ID and name
        return states.stream()
                .collect(Collectors.toMap(State::getId, State::getName));
    }

    public Map<Integer, String> getHospitalList(Locale locale) {
        // Fetch hospitals directly using the repository
        List<Users> hospitals = usersRepository.getHospitalList();

        // Map the list to a key-value pair of hospitalId and clinicName
        return hospitals.stream()
                .collect(Collectors.toMap(Users::getUserId, Users::getClinicName));
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
    public Map<Integer, String> getAllCityList(Locale locale) {
        // Fetch all cities list
        List<City> cities = cityRepository.findAll();

        // Map city IDs to city names
        return cities.stream()
                .collect(Collectors.toMap(City::getId, City::getName));
    }
    public Map<Integer, String> getAllCategoriesList() {
        //Fetch all lab categories list
        List<LabCategoryMaster> labCategoryMasterList = labCategoryMasterRepository.findAllByCatStatus();

        // Map categories into -> categories IDs and categories name
        return labCategoryMasterList.stream()
                .collect(Collectors.toMap(LabCategoryMaster::getCatId, LabCategoryMaster::getCatName));
    }

    public Object getAllSubCategoriesList(Locale locale, Integer categoryId) {
        //Check weather categoryId exists or not
        LabCategoryMaster categoryMaster = labCategoryMasterRepository.findById(categoryId).orElse(null);
        if(categoryMaster == null){
            return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.LAB_CATEGORY_NOT_FOUND, null, locale));
        }
        //Now fetch all lab sub categories based on lab category id
        List<LabSubCategoryMaster> labSubCategoryMasterList = labSubCategoryMasterRepository.findByCatIdAndStatus(categoryId, CategoryStatus.Active);
        //if list is empty
        if(labSubCategoryMasterList.isEmpty()){
            return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.RECORD_NOT_FOUND, null, locale));
        }
        // Map categories into -> categories IDs and categories name
        return labSubCategoryMasterList.stream()
                .collect(Collectors.toMap(LabSubCategoryMaster::getSubCatId, LabSubCategoryMaster::getSubCatName));
    }
}
