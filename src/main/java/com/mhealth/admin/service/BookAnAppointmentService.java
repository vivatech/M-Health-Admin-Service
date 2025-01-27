package com.mhealth.admin.service;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.DoctorProfileResponse;
import com.mhealth.admin.dto.dto.SearchDoctorRequest;
import com.mhealth.admin.dto.enums.RequestType;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.mhealth.admin.constants.Constants.*;

@Service
public class BookAnAppointmentService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private DoctorSpecializationRepository doctorSpecializationRepository;

    public Object viewDoctorProfile(Integer doctorId, Locale locale) {
        Users doctor = usersRepository.findByUserIdAndType(doctorId, UserType.Doctor).orElse(null);
        if(doctor == null){
            return new Response(Status.FAILED, FAILED, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        DoctorProfileResponse response = new DoctorProfileResponse();

        String fName = StringUtils.isEmpty(doctor.getFirstName()) ? "" : doctor.getFirstName();
        String lName = StringUtils.isEmpty(doctor.getLastName()) ? "" : doctor.getLastName();
        response.setDoctorName((fName + " " + lName).trim());

        response.setDoctorPicture(StringUtils.isEmpty(doctor.getProfilePicture()) ? null : USER_PROFILE_PICTURE + doctor.getUserId()+"/"+doctor.getProfilePicture());

        float exp = doctor.getExperience() == null ? 0.00f : doctor.getExperience();
        response.setExperience(exp);

        String countryName = doctor.getCountry() == null ? "" : doctor.getCountry().getName();
        response.setCountry(countryName);

        Users hospital = usersRepository.findByUserIdAndType(doctor.getHospitalId(), UserType.Clinic).orElse(null);
        if (hospital != null) {
            String clinicName = StringUtils.isEmpty(hospital.getClinicName()) ? "" : hospital.getClinicName();
            response.setHospitalNameWithCountry(clinicName + "-" + countryName);
            response.setHospitalName(clinicName);
        }

        if(doctor.getState() != null){
            State state = stateRepository.findById(doctor.getState()).orElse(null);
            if(state != null) response.setProvince(state.getName());
        }

        if(doctor.getCity() != null){
            City city = cityRepository.findById(doctor.getCity()).orElse(null);
            if(city != null) response.setCity(city.getName());;
        }

        List<String> langList = new ArrayList<>();
        if (!doctor.getLanguageFluency().isEmpty()) {
            String[] lang = doctor.getLanguageFluency().split(",");
            for (String language : lang) {
                languageRepository.findById(Integer.valueOf(language)).ifPresent(langName -> langList.add(langName.getName()));
            }
        }
        response.setLanguageSpoken(langList);

        List<String> specializationList = new ArrayList<>(Collections.singletonList(GENERAL_PRACTITIONER));
        if(!doctor.getDoctorClassification().equalsIgnoreCase(GENERAL_PRACTITIONER)) {
            specializationList = doctorSpecializationRepository.findBySpecializationNames(doctorId, locale.getLanguage());
        }
        response.setSpecializationName(specializationList);

        return response;
    }

}
