package com.mhealth.admin.service;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.consultationDto.ConsultationFees;
import com.mhealth.admin.dto.consultationDto.DoctorAvailabilityResponse;
import com.mhealth.admin.dto.consultationDto.HospitalListResponse;
import com.mhealth.admin.dto.consultationDto.SlotsResponse;
import com.mhealth.admin.dto.dto.*;
import com.mhealth.admin.dto.enums.*;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.dto.response.ViewConsultationResponse;
import com.mhealth.admin.model.*;
import com.mhealth.admin.payment.PaymentService;
import com.mhealth.admin.repository.*;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private ConsultationRepository consultationRepository;
    @Value("${cancel.appointment.difference}")
    private Long timeDifference;
    @Value("${transaction.mode}")
    private Integer transactionMode;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private SpecializationRepository specializationRepository;
    @Autowired
    private UserLocationRepository userLocationRepository;
    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;
    @Autowired
    private SlotMasterRepository slotMasterRepository;
    @Autowired
    private ConsultationRatingRepository consultationRatingRepository;
    @Autowired
    private ChargesRepository chargesRepository;

    public Object viewDoctorProfile(Integer doctorId, Locale locale) {
        Users doctor = usersRepository.findByUserIdAndType(doctorId, UserType.Doctor).orElse(null);
        if (doctor == null) {
            return new Response(Status.FAILED, Constants.FORBIDDEN_STATUS_CODE, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        DoctorProfileResponse response = new DoctorProfileResponse();

        String fName = StringUtils.isEmpty(doctor.getFirstName()) ? "" : doctor.getFirstName();
        String lName = StringUtils.isEmpty(doctor.getLastName()) ? "" : doctor.getLastName();
        response.setDoctorName((fName + " " + lName).trim());

        response.setDoctorPicture(StringUtils.isEmpty(doctor.getProfilePicture()) ? null : Constants.USER_PROFILE_PICTURE + doctor.getUserId() + "/" + doctor.getProfilePicture());

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

        if (doctor.getState() != null) {
            stateRepository.findById(doctor.getState()).ifPresent(state -> response.setProvince(state.getName()));
        }

        if (doctor.getCity() != null) {
            cityRepository.findById(doctor.getCity()).ifPresent(city -> response.setCity(city.getName()));
            ;
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
        if (!doctor.getDoctorClassification().equalsIgnoreCase(GENERAL_PRACTITIONER)) {
            List<DoctorSpecialization> doctorSpecializations = doctorSpecializationRepository.findByUserId(doctorId);
            specializationList = doctorSpecializations.stream().map(ele -> {
                if (locale.getLanguage().equalsIgnoreCase("en")) {
                    return ele.getSpecializationId().getName();
                } else {
                    return ele.getSpecializationId().getNameSl();
                }
            }).distinct().toList();
        }
        response.setSpecializationName(specializationList);

        return response;
    }

    public Object sortByAvailability(Locale locale) {
        String[] list = messageSource.getMessage(Messages.AVAILABILITY_SORT, null, locale).split(",");
        return new Response(Status.SUCCESS, Constants.SUCCESS, messageSource.getMessage(Messages.AVAILABLE_LIST_RETRIEVED, null, locale), list);
    }

    public Object getLanguage(Locale locale) {
        List<Language> languageList = languageRepository.findAll();

        if (!languageList.isEmpty()) {
            List<LanguageListResponse> list = languageList.stream().map(item -> new LanguageListResponse(item.getId(), item.getName())).toList();
            return new Response(Status.SUCCESS, Constants.SUCCESS, messageSource.getMessage(Messages.LANGUAGE_LIST_RETRIEVED, null, locale), list);
        }
        return new Response(Status.FAILED, Constants.SUCCESS, messageSource.getMessage(Messages.RECORD_NOT_FOUND, null, locale));
    }

    public Object getSortBy(Locale locale) {
        String[] list = messageSource.getMessage(Messages.SORT_BY, null, locale).split(",");
        return new Response(Status.SUCCESS, Constants.SUCCESS, messageSource.getMessage(Messages.SORT_LIST_RETRIEVED, null, locale), list);
    }

    public Object getPaymentMethod(Locale locale) {
        Map<String, List<String>> paymentMethodResponse = new HashMap<>();
        List<String> paymentMethodList = new ArrayList<>();
        List<String> currencyList = new ArrayList<>();

        paymentMethodList.add(Payment_Method_EVC);
        currencyList.add(locale.getLanguage().equalsIgnoreCase(LOCALE_SOMALIA) ? Dollar_Currency : Currency_USD);

        paymentMethodResponse.put("paymentMethods", paymentMethodList);
        paymentMethodResponse.put("currencyOption", currencyList);
        return new Response(Status.SUCCESS, SUCCESS, messageSource.getMessage(Messages.RECORD_FOUND, null, locale), paymentMethodResponse);
    }

    public Object cancelAppointment(CancelAppointmentRequest request, Locale locale) {
        if (request.validate() != null) {
            return new Response(Status.FAILED, FORBIDDEN_STATUS_CODE, request.validate());
        }
        Users users = usersRepository.findById(request.getUserId()).orElse(null);
        if (users == null) {
            return new Response(Status.FAILED, FORBIDDEN_STATUS_CODE, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        Consultation consultation = consultationRepository.findById(request.getCaseId()).orElse(null);
        if (consultation == null) {
            return new Response(Status.FAILED, FORBIDDEN_STATUS_CODE, messageSource.getMessage(Messages.CONSULTATION_NOT_FOUND, null, locale));
        }

        LocalDateTime dateTime = LocalDateTime.of(consultation.getConsultationDate(), consultation.getSlotId().getSlotStartTime());
        Duration difference = Duration.between(LocalDateTime.now(), dateTime);

        if (difference.toHours() < timeDifference) {
            return new Response(Status.FAILED, FORBIDDEN_STATUS_CODE, messageSource.getMessage(Messages.CANCEL_REQUEST_CANNOT_PROCESSED, null, locale));
        }

        Orders orders = ordersRepository.findByCaseId(consultation);
        orders.setStatus(OrderStatus.Cancelled);
        orders.setUpdatedAt(LocalDateTime.now());

        consultation.setCancelMessage(request.getMessage());
        consultation.setRequestType(RequestType.Cancel);
        consultation.setUpdatedAt(LocalDateTime.now());

        WalletTransaction existTransaction = walletTransactionRepository.findByOrderIdANDServiceType(orders.getId());

        WalletTransaction currentTransaction = createWalletTransactionEntry(orders, existTransaction);

        //payment
        if (transactionMode == 3) {
            Response response = paymentService.refundPayment(existTransaction.getPayerMobile(), existTransaction.getTransactionId(), com.mhealth.admin.config.Constants.DEFAULT_COUNTRY);
            if (response.getStatus().equals(Status.FAILED)) {
                return new Response(Status.FAILED, FORBIDDEN_STATUS_CODE, "Payment Failed");
            }
        }

        consultationRepository.save(consultation);
        ordersRepository.save(orders);
        currentTransaction.setTransactionStatus(Transaction_COMPLETE);
        walletTransactionRepository.save(currentTransaction);

        return new Response(Status.SUCCESS, SUCCESS, messageSource.getMessage(Messages.CONSULTATION_CANCEL_SUCCESSFULLY, null, locale));
    }

    private WalletTransaction createWalletTransactionEntry(Orders order, WalletTransaction existTransaction) {

        WalletTransaction transaction = new WalletTransaction();
        transaction.setPaymentMethod(existTransaction.getPaymentMethod());
        transaction.setPatientId(order.getPatientId());
        transaction.setOrderId(order.getId());
        transaction.setPaymentGatewayType(existTransaction.getPaymentGatewayType());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(REFUND_TRANSFER);
        transaction.setAmount(order.getAmount());
        transaction.setIsDebitCredit(DEBIT);
        transaction.setPayeeId(order.getPatientId().getUserId()); // payee is Patient id
        transaction.setPayerId(1); //SuperAdmin
        transaction.setReferenceNumber(order.getPatientId().getUserId().toString());  //Since status is same for both case

        Users adminContactNumber = usersRepository.findById(1).orElse(null);
        transaction.setPayeeMobile(String.valueOf(order.getPatientId().getContactNumber()));
        assert adminContactNumber != null;
        transaction.setPayerMobile(String.valueOf(adminContactNumber.getContactNumber()));

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setRefTransactionId(order.getId().toString());

        transaction.setCurrentBalance(0.0F); // by-default
        transaction.setPreviousBalance(0.0f); // by-default
        transaction.setServiceType(Service_Type_Consultation);
        transaction.setTransactionId(generateDateTime());
        transaction.setTransactionStatus(Transaction_PENDING);

        return walletTransactionRepository.save(transaction);

    }

    String generateDateTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + (int) (Math.random() * 1000);
    }

    public Object getSpecialization(Locale locale) {
        List<Specialization> list = specializationRepository.findAll();
        if (list.isEmpty()) {
            return new Response(Status.FAILED, FORBIDDEN_STATUS_CODE, messageSource.getMessage(Messages.RECORD_NOT_FOUND, null, locale));
        }
        List<SpecializationResponse> specializationListName = list.stream().map(ele -> {
            if (locale.getLanguage().equalsIgnoreCase("en")) {
                return new SpecializationResponse(ele.getId(), ele.getName());
            } else {
                return new SpecializationResponse(ele.getId(), ele.getNameSl());
            }
        }).toList();
        return new Response(Status.SUCCESS, SUCCESS, messageSource.getMessage(Messages.RECORD_FOUND, null, locale), specializationListName);
    }

    public Object getHospitalList(Locale locale, String clinicName, String address, int page1, int size) {

        Specification<Users> specification = filterByHospitalParams(clinicName, address);

        // Pagination
        Pageable pageable = PageRequest.of(page1 - 1, size);
        Page<Users> page = usersRepository.findAll(specification, pageable);

        List<HospitalListResponse> dtoList = new ArrayList<>();
        page.getContent().forEach(ele -> {
            UserLocation userLocation = userLocationRepository.findByUser(ele.getUserId());
            String latitude = null;
            String longitude = null;
            if (userLocation != null) {
                latitude = userLocation.getLatitude().toString();
                longitude = userLocation.getLongitude().toString();
            }

            HospitalListResponse dto = new HospitalListResponse();
            dto.setHospitalId(ele.getUserId());
            dto.setHospitalAddress(ele.getHospitalAddress());
            dto.setPicture(StringUtils.isEmpty(ele.getProfilePicture()) ? null : USER_PROFILE_PICTURE + ele.getUserId() + "/" + ele.getProfilePicture());
            dto.setClinicName(ele.getClinicName());
            dto.setLatitude(latitude);
            dto.setLongitude(longitude);
            dtoList.add(dto);
        });

        return new PaginationResponse<>(Status.SUCCESS, com.mhealth.admin.config.Constants.SUCCESS_CODE,
                messageSource.getMessage(Messages.HOSPITAL_LIST_FETCH, null, locale),
                dtoList, page.getTotalElements(), (long) page.getSize(), (long) page.getNumber());

    }

    private Specification<Users> filterByHospitalParams(String clinicName, String address) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by clinic name
            if (!StringUtils.isEmpty(clinicName))
                predicates.add(criteriaBuilder.like(root.get("clinicName"), "%" + clinicName + "%"));

            // Filter by address
            if (!StringUtils.isEmpty(address))
                predicates.add(criteriaBuilder.like(root.get("hospitalAddress"), "%" + address + "%"));

            //status
            predicates.add(criteriaBuilder.equal(root.get("status"), StatusAI.A));

            // Sorting logic
            query.orderBy(criteriaBuilder.desc(root.get("userId")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Object searchDoctor(Locale locale, SearchDoctorRequest request) throws ParseException {
        List<Users> allUsers = usersRepository.findAll(filterDoctors(request, locale));
        if(request.getSortBy().equalsIgnoreCase(Messages.SORT_BY_EXPERIENCE)){
            allUsers = allUsers.stream().sorted(Comparator.comparingDouble(Users::getExperience).reversed()).toList();
        }
        else if(request.getSortBy().equalsIgnoreCase(Messages.SORT_BY_RECOMMENDATION)){
            List<ConsultationRating> doctorRating = consultationRatingRepository.findByDoctorIdIn(allUsers);
            Map<Users, Double> ratingSumMap = doctorRating.stream()
                    .collect(Collectors.groupingBy(
                            ConsultationRating::getDoctorId,
                            Collectors.summingDouble(ConsultationRating::getRating)
                    ));
            List<ConsultationRating> sortedList = doctorRating.stream()
                    .sorted(Comparator.comparingDouble((ConsultationRating cr) -> ratingSumMap.get(cr.getDoctorId()))
                            .reversed())
                    .toList();

            List<Users> userList = sortedList.stream().map(ConsultationRating::getDoctorId).distinct().toList();
            List<Users> finalAllUsers = new ArrayList<>(allUsers); // Create a new list to avoid modifying the original while iterating
            finalAllUsers.removeIf(userList::contains);
            finalAllUsers.addAll(0, userList); // Add sorted users at the beginning

            allUsers = finalAllUsers;
        }


        // Pagination logic
        Pageable pageable = PageRequest.of(request.getPageNumber() - 1, request.getPageSize());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allUsers.size());

        List<Users> paginatedList = allUsers.subList(start, end);
        Page<Users> page = new PageImpl<>(paginatedList, pageable, allUsers.size());


        List<SearchDocResponse> responses = saveIntoSearchDoctorResponse(request, page.getContent(), locale);
        // Pagination and response creation
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("doctorList", responses);
        responseMap.put("totalCount", page.getTotalElements());

        return new Response(Status.SUCCESS, CODE_1, messageSource.getMessage(Messages.DOCTOR_LIST_FOUND, null, locale), responseMap);
    }

    private List<SearchDocResponse> saveIntoSearchDoctorResponse(SearchDoctorRequest request, List<Users> listOfDoctor, Locale locale) throws ParseException {
        List<SearchDocResponse> responses = new ArrayList<>();
        for (Users val : listOfDoctor) {
            SearchDocResponse docResponse = new SearchDocResponse();

            docResponse.setId(val.getUserId());
            docResponse.setName(val.getFirstName() + " " + val.getLastName());

            int totalCases = consultationRepository.findTotalCases(val.getUserId());
            docResponse.setCases(totalCases);

            ConsultationFees fees = new ConsultationFees();
            List<Charges> chargesList = chargesRepository.findByUserId(val.getUserId());
            Float visitCharge = 0.0f;
            Float callCharge = 0.0f;
            if (!chargesList.isEmpty()) {
                for (Charges ch : chargesList) {
                    if (ch.getFeeType().equals("visit")) visitCharge = ch.getFinalConsultationFees();
                    else callCharge = ch.getFinalConsultationFees();
                }
            }

            fees.setVisit(visitCharge);
            fees.setCall(callCharge);
            docResponse.setConsultationFees(fees);

            docResponse.setAboutMe(val.getAboutMe());
            docResponse.setExperience(val.getExperience() + " " + messageSource.getMessage(Messages.SORT_BY_EXPERIENCE, null, locale));
            docResponse.setProfilePicture(val.getProfilePicture() == null ? null : USER_PROFILE_PICTURE + val.getUserId() + "/" + val.getProfilePicture());

            Float rating = consultationRatingRepository.findDoctorRating(val.getUserId());
            docResponse.setRating(rating);
            Integer reviews = consultationRatingRepository.findReviews(val.getUserId());
            docResponse.setReview(reviews);

            float maxCharges = request.getConsultType().equalsIgnoreCase(CONSULT_VIDEO) ? callCharge : visitCharge ;
            docResponse.setMaxFees(maxCharges);

            List<String> knownLang = new ArrayList<>();
            if (!org.apache.commons.lang.StringUtils.isEmpty(val.getLanguageFluency())) {
                List<Integer> lang = Arrays.stream(val.getLanguageFluency().split(",")).map(Integer::parseInt).toList();
                knownLang = languageRepository.findLanguages(lang);
            }
            docResponse.setLanguage(knownLang);

            Users hospital = usersRepository.findByUserIdAndStatus(val.getHospitalId(), StatusAI.A);
            if (hospital != null) {
                docResponse.setHospitalId(val.getHospitalId());
                docResponse.setHospitalName(hospital.getClinicName());
            }

            List<String> specialities = new ArrayList<>();
            if(val.getDoctorClassification().equalsIgnoreCase(GENERAL_PRACTITIONER)){
                specialities.add(messageSource.getMessage(Messages.GENERAL_PRACTITIONER, null, locale));
                docResponse.setSpeciality(specialities);
            }
            else {
                docResponse.setSpeciality(doctorSpecializationRepository.findSpName(val.getUserId()));
            }

            LocalDateTime dateTime = LocalDateTime.now();
            List<RequestType> type = Arrays.asList(RequestType.Book, RequestType.Inprocess, RequestType.Pending);
            DayOfWeek dayOfWeek = dateTime.toLocalDate().getDayOfWeek();
            String[] dayName = new String[]{dayOfWeek.toString().toLowerCase()};

            List<Integer> daySlots = slotMasterRepository.findBySlotDayAndSlotStartTime(dayName, dateTime.toLocalTime(), dateTime.toLocalDate(), type);
            List<DoctorAvailability> doctorAvailabilities = doctorAvailabilityRepository.findBySlots(val.getUserId(), daySlots);
            if (!doctorAvailabilities.isEmpty()) docResponse.setIsAvailableToday(true);

            responses.add(docResponse);
        }
        return responses;
    }

    public Specification<Users> filterDoctors(SearchDoctorRequest request, Locale locale) {
        return (root, query, cb) -> {

            Predicate doctorType = cb.equal(root.get("type"), UserType.Doctor);

            // Determine video consultation type
            String doctorVideoVisit = request.getConsultType().equalsIgnoreCase(CONSULT_VIDEO) ? CONSULT_VIDEO : CONSULT_VISIT;
            Predicate hasDoctorVideo = cb.or(
                    cb.equal(root.get("hasDoctorVideo"), doctorVideoVisit),
                    cb.equal(root.get("hasDoctorVideo"), "both")
            );

            // Mandatory fields validation
            Predicate cityNotNull = cb.isNotNull(root.get("city"));
            Predicate stateNotNull = cb.isNotNull(root.get("state"));
            Predicate countryNotNull = cb.isNotNull(root.get("country").get("id"));
            Predicate isActive = cb.equal(root.get("status"), StatusAI.A);

            Predicate finalPredicate = cb.and(doctorType, hasDoctorVideo, cityNotNull, stateNotNull, countryNotNull, isActive);

            // Filter by International
            if (request.getIsInternational() != null) {
                YesNo internationalDoc = request.getIsInternational() ? YesNo.Yes : YesNo.No;
                finalPredicate = cb.and(finalPredicate, cb.equal(root.get("isInternational"), internationalDoc));
            }

            // Filter by Clinic
            if (request.getClinicId() != null && request.getClinicId() > 0) {
                finalPredicate = cb.and(finalPredicate, cb.equal(root.get("hospitalId"), request.getClinicId()));
            }

            // Filter by City
            if (request.getCityId() != null && request.getCityId() > 0) {
                finalPredicate = cb.and(finalPredicate, cb.equal(root.get("city"), request.getCityId()));
            }

            // Filter by Doctor Name
            if (!StringUtils.isEmpty(request.getDoctorName())) {
                String docName = request.getDoctorName().trim();
                Predicate firstNameLike = cb.like(root.get("firstName"), "%" + docName + "%");
                Predicate lastNameLike = cb.like(root.get("lastName"), "%" + docName + "%");
                finalPredicate = cb.and(finalPredicate, cb.or(firstNameLike, lastNameLike));
            }

            // Filter by Language
            if (request.getLanguageFluency() != null && request.getLanguageFluency() > 0) {
                finalPredicate = cb.and(finalPredicate, cb.like(root.get("languageFluency"), "%" + request.getLanguageFluency() + "%"));
            }

            // Filter by Specialization
            if (request.getSpecializationIds() != null && !request.getSpecializationIds().isEmpty()) {
                Subquery<Long> specializationSubQuery = query.subquery(Long.class);
                Root<DoctorSpecialization> dsRoot = specializationSubQuery.from(DoctorSpecialization.class);
                specializationSubQuery.select(dsRoot.get("userId").get("userId"))
                        .where(
                                cb.notEqual(dsRoot.get("userId").get("doctorClassification"), "general_practitioner"),
                                dsRoot.get("specializationId").get("id").in(request.getSpecializationIds())
                        );
                finalPredicate = cb.and(finalPredicate, root.get("userId").in(specializationSubQuery));
            }

            // Filter by Availability
            if (!StringUtils.isEmpty(request.getAvailability())) {
                LocalDateTime dateTime = LocalDateTime.now();
                LocalDate startDate = dateTime.toLocalDate();
                LocalDate endDate = startDate;

                List<Integer> daySlots = null;
                List<RequestType> type = List.of(
                        RequestType.Book,
                        RequestType.Inprocess,
                        RequestType.Pending
                );

                if (request.getAvailability().equalsIgnoreCase(messageSource.getMessage(Messages.DAY_TODAY, null, locale))) {
                    String dayName = dateTime.getDayOfWeek().toString().toLowerCase();
                    daySlots = slotMasterRepository.findBySlotDayAndSlotStartTime(
                            new String[]{dayName}, dateTime.toLocalTime(), startDate, type);
                } else if (request.getAvailability().equalsIgnoreCase(messageSource.getMessage(Messages.DAY_TOMORROW, null, locale))) {
                    startDate = startDate.plusDays(1);
                    endDate = endDate.plusDays(1);
                    String dayName = startDate.getDayOfWeek().toString().toLowerCase();
                    daySlots = slotMasterRepository.findBySlotDay(new String[]{dayName}, startDate, endDate, type);
                } else {
                    endDate = startDate.plusDays(6);
                    daySlots = slotMasterRepository.findBySlotDay(new String[]{
                            "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
                    }, startDate, endDate, type);
                }

                if (daySlots != null && !daySlots.isEmpty()) {
                    Subquery<Long> availabilitySubQuery = query.subquery(Long.class);
                    Root<DoctorAvailability> daRoot = availabilitySubQuery.from(DoctorAvailability.class);
                    availabilitySubQuery.select(daRoot.get("doctorId").get("userId"))
                            .where(daRoot.get("slotId").get("slotId").in(daySlots))
                            .groupBy(daRoot.get("doctorId").get("userId"));
                    finalPredicate = cb.and(finalPredicate, root.get("userId").in(availabilitySubQuery));
                }
            }

            //order by userId
            query.orderBy(cb.desc(root.get("userId")));

            return finalPredicate;
        };
    }

    public Object getDoctorAvailabilityLatestList(DoctorAvailabilityRequest request, Locale locale) {
        Users doctor = usersRepository.findByUserIdAndType(request.getDoctorId(), UserType.Doctor).orElse(null);
        Users patient = usersRepository.findByUserIdAndType(request.getPatientId(), UserType.Patient).orElse(null);
        if (doctor == null)
            return new Response(Status.FAILED, CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));

        if(patient == null)
            return new Response(Status.FAILED, CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));

        int pageSize = request.getPageSize();
        int pageNumber = request.getPageNo();
        int startIndex = pageSize * pageNumber;
        int endIndex = startIndex + pageSize;

        List<DoctorAvailabilityResponse> responseList = new ArrayList<>();
        LocalDateTime dateTime = LocalDateTime.now();

        for (int i = startIndex; i < endIndex; i++) {
            LocalDate localDate = dateTime.toLocalDate().plusDays(i);
            LocalTime localTime = dateTime.toLocalTime();

            Specification<DoctorAvailability> spec = filterDoctorAvailability(doctor.getUserId(), localDate, localTime);
            List<DoctorAvailability> availableSlots = doctorAvailabilityRepository.findAll(spec);

            List<SlotsResponse> morningSlotsList = new ArrayList<>();
            List<SlotsResponse> afternoonSlotsList = new ArrayList<>();
            List<SlotsResponse> eveningSlotsList = new ArrayList<>();

            if (!availableSlots.isEmpty()) {
                for (DoctorAvailability item : availableSlots) {
                    LocalTime dbTime = item.getSlotId().getSlotStartTime();
                    String[] timePart = item.getSlotId().getSlotTime().split(":");

                    // Define the time boundaries
                    LocalTime morningStart = LocalTime.of(0, 0);
                    LocalTime afternoonStart = LocalTime.of(12, 0);
                    LocalTime eveningStart = LocalTime.of(16, 0);
                    LocalTime midNight = LocalTime.of(23, 59);

                    // Time categorization
                    if (dbTime.equals(morningStart) || (dbTime.isAfter(morningStart) && dbTime.isBefore(afternoonStart))) {
                        morningSlotsList.add(new SlotsResponse(item.getSlotId().getSlotId(), dbTime.toString(), timePart[0] + ":" + timePart[1], timePart[2] + ":" + timePart[3]));
                    } else if (dbTime.equals(afternoonStart) || (dbTime.isAfter(afternoonStart) && dbTime.isBefore(eveningStart))) {
                        afternoonSlotsList.add(new SlotsResponse(item.getSlotId().getSlotId(), dbTime.toString(), timePart[0] + ":" + timePart[1], timePart[2] + ":" + timePart[3]));
                    } else if (dbTime.equals(eveningStart) || (dbTime.isAfter(eveningStart) && dbTime.isBefore(midNight))) {
                        eveningSlotsList.add(new SlotsResponse(item.getSlotId().getSlotId(), dbTime.toString(), timePart[0] + ":" + timePart[1], timePart[2] + ":" + timePart[3]));
                    }
                }

                // Sort the time slots
                morningSlotsList.sort(Comparator.comparing(SlotsResponse::getSlotTime));
                afternoonSlotsList.sort(Comparator.comparing(SlotsResponse::getSlotTime));
                eveningSlotsList.sort(Comparator.comparing(SlotsResponse::getSlotTime));
            }

            // Create response object
            DoctorAvailabilityResponse response = new DoctorAvailabilityResponse();
            response.setDate(localDate);
            response.setTotalAvailableSlots(morningSlotsList.size() + afternoonSlotsList.size() + eveningSlotsList.size());
            response.setMorningSlot(morningSlotsList);
            response.setAfternoonSlot(afternoonSlotsList);
            response.setEveningSlot(eveningSlotsList);
            response.setMorningSlotCount(morningSlotsList.size());
            response.setAfternoonSlotCount(afternoonSlotsList.size());
            response.setEveningSlotCount(eveningSlotsList.size());

            responseList.add(response);
        }
        return responseList;
    }
    private Specification<DoctorAvailability> filterDoctorAvailability(Integer doctorId, LocalDate localDate, LocalTime localTime) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join with mh_slot_master table
            Join<DoctorAvailability, SlotMaster> slotJoin = root.join("slotId");

            // Filter by doctor ID
            predicates.add(criteriaBuilder.equal(root.get("doctorId").get("userId"), doctorId));

            // Convert LocalDate to string format YYYY-MM-DD
            Expression<String> slotDay = slotJoin.get("slotDay");
            String dayOfWeek = localDate.getDayOfWeek().toString(); // Example: MONDAY, TUESDAY
            predicates.add(criteriaBuilder.equal(slotDay, dayOfWeek));

            // If the date is today, filter slots where slot start time is greater than the current time
            if (localDate.isEqual(LocalDate.now())) {
                predicates.add(criteriaBuilder.greaterThan(slotJoin.get("slotStartTime"), localTime));
            }

            // Subquery to exclude booked slots
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Consultation> consultationRoot = subquery.from(Consultation.class);
            subquery.select(criteriaBuilder.count(consultationRoot))
                    .where(
                            criteriaBuilder.equal(consultationRoot.get("doctorId").get("userId"), doctorId),
                            criteriaBuilder.equal(consultationRoot.get("consultationDate"), localDate),
                            criteriaBuilder.equal(consultationRoot.get("slotId").get("slotId"), slotJoin.get("slotId")),
                            criteriaBuilder.equal(consultationRoot.get("requestType"), RequestType.Book),
                            criteriaBuilder.equal(consultationRoot.get("consultStatus"), ConsultStatus.pending)
                    );

            // Ensure the slot is not already booked
            predicates.add(criteriaBuilder.equal(subquery, 0L));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
