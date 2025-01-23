package com.mhealth.admin.service;


import com.mhealth.admin.config.Utility;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.request.MarketingUserRequestDto;
import com.mhealth.admin.dto.response.MarketingUserListResponseDto;
import com.mhealth.admin.dto.response.MarketingUserReportResponseDto;
import com.mhealth.admin.dto.response.MarketingUserResponseDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.model.UsersPromoCode;
import com.mhealth.admin.repository.AuthAssignmentRepository;
import com.mhealth.admin.repository.UsersPromoCodeRepository;
import com.mhealth.admin.repository.UsersRepository;
import com.mhealth.admin.sms.SMSApiService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MarketingUserService {

    @Value("${m-health.country.code}")
    private String countryCode;

    @Value("${m-health.country}")
    private String country;

    @Value("${app.sms.sent}")
    private boolean smsSent;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersPromoCodeRepository usersPromoCodeRepository;

    @Autowired
    private AuthAssignmentRepository authAssignmentRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SMSApiService smsApiService;

    public Object getMarketingUserList(Locale locale, String name, String email, String status, String contactNumber, String sortField, String sortBy, int page, int size) {
        // Define valid sort fields
        Set<String> validSortFields = new HashSet<>(Arrays.asList("name", "contactNumber", "email"));

        StringBuilder baseQuery = new StringBuilder("SELECT ")
                .append("u.user_id AS userId, ")
                .append("CONCAT(u.first_name, ' ', u.last_name) AS name, ")
                .append("u.email, ")
                .append("p.promo_code AS promoCode, ")
                .append("COUNT(DISTINCT cwp.user_id) AS totalRegistration, ")
                .append("COUNT(DISTINCT co.case_id) AS totalConsultation, ")
                .append("u.contact_number AS contactNumber, ")
                .append("u.status ")
                .append("FROM mh_users u ")
                .append("LEFT JOIN mh_users_promo_code p ON u.user_id = p.user_id ")
                .append("LEFT JOIN mh_users_created_with_promocode cwp ON p.user_id = cwp.created_by ")
                .append("LEFT JOIN mh_consultation co ON co.patient_id = cwp.user_id AND co.request_type NOT IN ('Cancel', 'Failed') ")
                .append("WHERE u.type = 'Marketing'"); // Base query

        // Dynamically add filters
        if (name != null && !name.isEmpty()) {
            baseQuery.append(" AND CONCAT(u.first_name, ' ', u.last_name) LIKE :name");
        }
        if (email != null && !email.isEmpty()) {
            baseQuery.append(" AND u.email LIKE :email");
        }
        if (status != null && !status.isEmpty()) {
            baseQuery.append(" AND u.status = :status");
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            baseQuery.append(" AND CONCAT(u.country_code, '', u.contact_number) LIKE :contactNumber");
        }

        baseQuery.append(" GROUP BY u.user_id, p.promo_code, u.contact_number, u.status ");

        // Add sorting field
        if (sortField != null && !sortField.isEmpty() && validSortFields.contains(sortField)) {
            baseQuery.append("ORDER BY ")
                     .append(sortField);
        } else {
            baseQuery.append("ORDER BY userId");
        }

        // Add sorting direction based on sortBy (ASC or DESC)
        if (sortBy != null && !sortBy.isEmpty()) {
            if (Objects.equals(sortBy, "1")) {
                baseQuery.append(" DESC ");
            } else {
                baseQuery.append(" ASC ");
            }
        } else {
            baseQuery.append(" DESC "); // Default to DESC if sortBy is not provided
        }

        // Create query
        Query query = entityManager.createNativeQuery(baseQuery.toString());

        // Set parameters
        if (name != null && !name.isEmpty()) {
            query.setParameter("name", "%" + name + "%");
        }
        if (email != null && !email.isEmpty()) {
            query.setParameter("email", "%" + email + "%");
        }
        if (status != null && !status.isEmpty()) {
            query.setParameter("status", status);
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            query.setParameter("contactNumber", "%" + contactNumber + "%");
        }

        // Pagination
        Pageable pageable = PageRequest.of(page - 1, size);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Fetch results
        List<Object[]> results = query.getResultList();

        // Map results to DTO
        List<MarketingUserListResponseDto> responseList = mapResultsToMarketingUserListResponseDto(results);

        // Total count query
        String countQuery = "SELECT COUNT(*) FROM (" + baseQuery + ") AS countQuery";
        Query countQ = entityManager.createNativeQuery(countQuery);

        // Set parameters for count query
        if (name != null && !name.isEmpty()) {
            countQ.setParameter("name", "%" + name + "%");
        }
        if (email != null && !email.isEmpty()) {
            countQ.setParameter("email", "%" + email + "%");
        }
        if (status != null && !status.isEmpty()) {
            countQ.setParameter("status", status);
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            countQ.setParameter("contactNumber", "%" + contactNumber + "%");
        }

        Long totalCount = ((Number) countQ.getSingleResult()).longValue();

        // Create pageable response
        Page<MarketingUserListResponseDto> pageableResponse = new PageImpl<>(responseList, pageable, totalCount);

        // Build response
        Map<String, Object> data = new HashMap<>();
        data.put("userList", pageableResponse.getContent());
        data.put("totalCount", pageableResponse.getTotalElements());

        Response response = new Response();
        response.setCode(Constants.CODE_1);
        response.setData(data);
        response.setMessage(messageSource.getMessage(Messages.USER_LIST_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    private List<MarketingUserListResponseDto> mapResultsToMarketingUserListResponseDto(List<Object[]> results) {
        return results.stream().map(row -> {
            Integer userId = (Integer) row[0];
            String name = (String) row[1];
            String email = (String) row[2];
            String promoCode = (String) row[3];
            Long totalRegistration = (Long) row[4];
            Long totalConsultation = (Long) row[5];
            String contactNumber = (String) row[6];
            String status = (String) row[7];

            return new MarketingUserListResponseDto(
                    userId, name, email, promoCode, totalRegistration, totalConsultation, contactNumber, status
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public Object createMarketingUser(Locale locale, MarketingUserRequestDto requestDto) {
        Response response = new Response();

        // Validate the input
        String validationMessage = requestDto.validate();
        if (validationMessage != null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(validationMessage);
            response.setStatus(Status.FAILED);
            return response;
        }

        // Check for duplicate email and contact number
        long emailCount = usersRepository.countByEmail(requestDto.getEmail());
        long contactNumberCount = usersRepository.countByContactNumberAndType(requestDto.getContactNumber(), UserType.Marketing);

        if (emailCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.EMAIL_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        } else if (contactNumberCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.CONTACT_NUMBER_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }


        // Create marketing user
        Users marketingUser = new Users();
        marketingUser.setType(UserType.Marketing);
        marketingUser.setFirstName(requestDto.getFirstName());
        marketingUser.setLastName(requestDto.getLastName());
        marketingUser.setEmail(requestDto.getEmail());
        marketingUser.setContactNumber(requestDto.getContactNumber());
        marketingUser.setCountryCode(countryCode);
        marketingUser.setIsInternational(YesNo.No);
        marketingUser.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);

        marketingUser = usersRepository.save(marketingUser);

        // Assign role
        assignRole(marketingUser.getUserId(), UserType.Marketing.name());

        // Create promo code
        String uniquePromoCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        UsersPromoCode usersPromoCode = new UsersPromoCode();
        usersPromoCode.setUserId(marketingUser.getUserId());
        usersPromoCode.setPromoCode(uniquePromoCode);
        usersPromoCodeRepository.save(usersPromoCode);

        // Send SMS
        try {
            locale = Utility.getUserNotificationLanguageLocale(marketingUser.getNotificationLanguage(), locale);
            String smsMessage = messageSource.getMessage(Messages.REGISTER_MARKETING_USER, new Object[]{marketingUser.getFirstName() + " " + marketingUser.getLastName(), uniquePromoCode}, locale);
            String smsNumber = "+" + countryCode + requestDto.getContactNumber();
            if(smsSent){
                smsApiService.sendMessage(smsNumber, smsMessage, country);
            }
        } catch (Exception ex) {
            log.error("exception occurred while sending the sms", ex);
        }

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_CREATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    @Transactional
    public void assignRole(Integer userId, String roleType) {
        try {
            // Delete existing roles
            authAssignmentRepository.deleteByUserId(String.valueOf(userId));

            // Insert new role
            authAssignmentRepository.insertRole(roleType, String.valueOf(userId), (int) (System.currentTimeMillis() / 1000));

        } catch (Exception ex) {
            log.error("exception occurred while assigning role to the user", ex);
            throw new RuntimeException("failed to assign role to user: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public Object updateMarketingUser(Locale locale, Integer userId, MarketingUserRequestDto requestDto) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingMarketingUser = usersRepository.findByUserIdAndType(userId, UserType.Marketing);
        if (existingMarketingUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingMarketingUser.get();

        // Validate the input
        String validationMessage = requestDto.validate();
        if (validationMessage != null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(validationMessage);
            response.setStatus(Status.FAILED);
            return response;
        }

        // Check for duplicate email and contact number
        long emailCount = usersRepository.countByEmailAndUserIdNot(requestDto.getEmail(), userId);
        long contactNumberCount = usersRepository.countByContactNumberAndTypeAndUserIdNot(requestDto.getContactNumber(), UserType.Marketing, userId);

        if (emailCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.EMAIL_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        } else if (contactNumberCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.CONTACT_NUMBER_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        // Update the user fields
        existingUser.setFirstName(requestDto.getFirstName());
        existingUser.setLastName(requestDto.getLastName());
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setContactNumber(requestDto.getContactNumber());
        existingUser.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);

        usersRepository.save(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_UPDATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    public Object getMarketingUser(Locale locale, Integer userId) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingMarketingUser = usersRepository.findByUserIdAndType(userId, UserType.Marketing);
        if (existingMarketingUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingMarketingUser.get();

        // Construct users entity to marketing user response dto
        MarketingUserResponseDto marketingUserResponseDto = convertToMarketingUserResponseDto(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setData(marketingUserResponseDto);
        response.setMessage(messageSource.getMessage(Messages.USER_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;

    }

    private MarketingUserResponseDto convertToMarketingUserResponseDto(Users users) {
        MarketingUserResponseDto responseDto = new MarketingUserResponseDto();
        responseDto.setUserId(users.getUserId());
        responseDto.setFirstName(users.getFirstName());
        responseDto.setLastName(users.getLastName());
        responseDto.setEmail(users.getEmail());
        responseDto.setContactNumber(users.getContactNumber());
        responseDto.setNotificationLanguage(users.getNotificationLanguage());
        return responseDto;
    }

    public Object updateMarketingUserStatus(Locale locale, Integer userId, String status) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingMarketingUser = usersRepository.findByUserIdAndType(userId, UserType.Marketing);
        if (existingMarketingUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingMarketingUser.get();

        // Validate the status
        if (!validateStatus(status)) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.INCORRECT_USER_STATUS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        // Update the user's status field
        existingUser.setStatus(StatusAI.valueOf(status));

        usersRepository.save(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_UPDATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    private boolean validateStatus(String status) {
        boolean containsStatus = false;
        for (StatusAI statusAI : StatusAI.values()) {
            if (statusAI.name().equals(status)) {
                containsStatus = true;
                break;
            }
        }
        return containsStatus;
    }

    @Transactional
    public Object deleteMarketingUser(Locale locale, Integer userId) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingMarketingUser = usersRepository.findByUserIdAndType(userId, UserType.Marketing);
        if (existingMarketingUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingMarketingUser.get();

        // Delete related records from auth_assignment table
        authAssignmentRepository.deleteByUserId(String.valueOf(userId));

        // Delete related records from mh_users_promo_code table
        usersPromoCodeRepository.deleteByUserId(userId);

        // Delete the user from the mh_users table
        usersRepository.delete(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_DELETED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    public Object getMarketingUserReport(Locale locale, Integer marketingUserId, String name, String email, LocalDate startDate, LocalDate endDate, String contactNumber, String sortBy, int page, int size) {

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT DISTINCT u.user_id AS userId, " +
                        "CONCAT(u.first_name, ' ', u.last_name) AS name, " +
                        "u.email AS email, " +
                        "CONCAT(u.country_code, u.contact_number) AS contactNumber, " +
                        "(SELECT COUNT(c.case_id) FROM mh_consultation c WHERE c.patient_id = u.user_id AND c.request_type NOT IN ('Cancel', 'Failed')) AS totalConsultations, " +
                        "u.created_at AS createdAt " +
                        "FROM mh_users u " +
                        "JOIN mh_users_created_with_promocode pc ON u.user_id = pc.user_id " +
                        "WHERE pc.created_by = :marketingUserId ");

        // Apply filters
        if (name != null && !name.isEmpty()) {
            queryBuilder.append("AND LOWER(CONCAT(u.first_name, ' ', u.last_name)) LIKE LOWER(:name) ");
        }
        if (email != null && !email.isEmpty()) {
            queryBuilder.append("AND LOWER(u.email) LIKE LOWER(:email) ");
        }
        if (startDate != null && endDate != null) {
            queryBuilder.append("AND u.created_at BETWEEN :startDate AND :endDate ");
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            queryBuilder.append("AND CONCAT(u.country_code, u.contact_number) LIKE :contactNumber ");
        }

        // Apply sorting
        queryBuilder.append("ORDER BY ")
                .append("u.user_id")
                .append(" ")
                .append("0".equalsIgnoreCase(sortBy) ? "ASC" : "DESC");

        // Create count query for total records
        String countQuery = "SELECT COUNT(*) FROM (" + queryBuilder + ") AS countTable";

        // Create main query
        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        query.setParameter("marketingUserId", marketingUserId);

        // Set parameters dynamically
        if (name != null && !name.isEmpty()) {
            query.setParameter("name", "%" + name + "%");
        }
        if (email != null && !email.isEmpty()) {
            query.setParameter("email", "%" + email + "%");
        }
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate.atStartOfDay());
            query.setParameter("endDate", endDate.atTime(23, 59, 59));
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            query.setParameter("contactNumber", "%" + contactNumber + "%");
        }

        // Pagination
        Pageable pageable = PageRequest.of(page - 1, size);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Execute query and map results
        List<Object[]> resultList = query.getResultList();

        // Map results to DTO
        List<MarketingUserReportResponseDto> responseList = mapResultsToMarketingUserReportResponseDto(resultList);

        // Get total count
        Query totalQuery = entityManager.createNativeQuery(countQuery);
        totalQuery.setParameter("marketingUserId", marketingUserId);
        if (name != null && !name.isEmpty()) {
            totalQuery.setParameter("name", "%" + name + "%");
        }
        if (email != null && !email.isEmpty()) {
            totalQuery.setParameter("email", "%" + email + "%");
        }
        if (startDate != null && endDate != null) {
            totalQuery.setParameter("startDate", startDate.atStartOfDay());
            totalQuery.setParameter("endDate", endDate.atTime(23, 59, 59));
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            totalQuery.setParameter("contactNumber", "%" + contactNumber + "%");
        }

        long totalRecords = ((Number) totalQuery.getSingleResult()).longValue();

        // Create pageable response
        Page<MarketingUserReportResponseDto> pageableResponse = new PageImpl<>(responseList, pageable, totalRecords);

        // Build response
        Map<String, Object> data = new HashMap<>();
        data.put("userList", pageableResponse.getContent());
        data.put("totalCount", pageableResponse.getTotalElements());

        Response response = new Response();
        response.setCode(Constants.CODE_1);
        response.setData(data);
        response.setMessage(messageSource.getMessage(Messages.USER_LIST_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }


    private List<MarketingUserReportResponseDto> mapResultsToMarketingUserReportResponseDto(List<Object[]> results) {
        return results.stream().map(row -> {
            Integer userId = (Integer) row[0];
            String name = (String) row[1];
            String email = (String) row[2];
            String contactNumber = (String) row[3];
            Long totalConsultation = (Long) row[4];
            String createdAt = formatDateTime((Timestamp) row[5]);

            return new MarketingUserReportResponseDto(
                    userId, name, email, contactNumber, totalConsultation, createdAt
            );
        }).collect(Collectors.toList());
    }

    private String formatDateTime(Timestamp timestamp) {
        // Convert Timestamp to LocalDateTime
        LocalDateTime localDateTime = timestamp.toLocalDateTime();

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        // Format LocalDateTime to the desired string format
        return localDateTime.format(formatter);
    }

}
