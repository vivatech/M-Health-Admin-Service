package com.mhealth.admin.constants;

public interface Messages {
    String USER_LIST_FETCHED = "user.list.fetched";
    String USER_CREATED = "user.created";
    String EMAIL_ALREADY_EXISTS = "email.already.exists";
    String CONTACT_NUMBER_ALREADY_EXISTS = "contact-number.already.exists";
    String USER_NOT_FOUND = "user.not.found";
    String USER_UPDATED = "user.updated";
    String USER_FETCHED = "user.fetched";
    String USER_DELETED = "user.deleted";
    String INCORRECT_USER_STATUS = "incorrect.users.status";
    String SELECT_PROFILE_PICTURE = "select.profile.picture";
    String DOCTOR_ID_SIZE_LIMIT = "doctor-id.size.limit";
    String RECORD_NOT_FOUND = "list.not.found";

    // Marketing User's SMS Body
    String REGISTER_MARKETING_USER = "register.marketing.user";

    //Patient user's SMS Body
    String PROFILE_PICTURE_NOT_SELECTED = "profile.picture.not.selected";
    String COUNTRY_NOT_FOUND = "country.not.found";
    String REGISTER_PATIENT_USER = "register.patient.user";
    String GENERAL_PRACTITIONER = "general_practitioner";

    // Doctor User's SMS Body
    String REGISTER_DOCTOR_USER = "register.doctor.user";
    String DOCTOR_AVAILABILITY_FOUND = "doctor.availability.list.found";
    String SLOTS_SAVED_SUCCESSFULLY = "slot.saved";

    // Hospital User's SMS Body
    String HOSPITAL_REGISTRATION_CONFIRMATION = "hospital.registration.confirmation";

    // Hospital Link
    String HOSPITAL_LINK = "admin.login.url";

    String PRIORITY_ALREADY_EXISTS = "priority.already.exists";
  
    //Lab user's SMS Body
    String DOCUMENT_NOT_FOUND = "document.not.found";

    String DOCUMENT_DELETED_SUCCESSFULLY = "document.delete";
    String PROFILE_PICTURE_DELETE_SUCCESSFULLY = "profile.picture.delete";

    //Lab Price Management
    String LAB_PRICE_LIST_FETCH = "lab.price.list.fetch";
    String LAB_CATEGORY_NOT_FOUND = "app.lab.master.category.not.found";
    String LAB_SUB_CATEGORY_NOT_FOUND = "app.lab.sub.category.not.found";
    String LAB_PRICE_EXISTS = "lab.price.exists";
    String AVAILABILITY_SORT = "availability.sort.by";
    String AVAILABLE_LIST_RETRIEVED = "AVAILABLE.LIST.RETRIEVED";
    String LANGUAGE_LIST_RETRIEVED = "LANGUAGE.LIST.RETRIEVED";
    String SORT_BY = "sort.by";
    String SORT_LIST_RETRIEVED = "SORT.LIST.RETRIEVED";
    String RECORD_FOUND = "List.Found";

//    Book appointment
    String CONSULTATION_NOT_FOUND = "consultation.not.found";
    String CANCEL_REQUEST_CANNOT_PROCESSED = "cancel.cannot.processed";
    String CONSULTATION_CANCEL_SUCCESSFULLY = "consultation.cancel";
}
