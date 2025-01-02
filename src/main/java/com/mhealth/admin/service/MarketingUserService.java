package com.mhealth.admin.service;


import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.MarketingUserListResponseDto;
import com.mhealth.admin.dto.response.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MarketingUserService {

    @Autowired
    private EntityManager entityManager;

    public Object getMarketingUserList(String name, String email, String status, String contactNumber, String sortBy, int page, int size) {
        StringBuilder baseQuery = new StringBuilder("SELECT ")
                .append("u.user_id AS userId, ")
                .append("CONCAT(u.first_name, ' ', u.last_name) AS name, ")
                .append("u.email, ")
                .append("p.promo_code AS promoCode, ")
                .append("COUNT(DISTINCT cwp.user_id) AS totalRegistration, ")
                .append("COUNT(DISTINCT co.case_id) AS totalConsultation, ")
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

        baseQuery.append(" GROUP BY u.user_id, p.promo_code, u.status");

        // Determine sorting based on sortBy
        String sortOrder = " ORDER BY u.user_id ";
        if ("0".equals(sortBy)) {
            sortOrder += "ASC"; // Ascending order
        } else {
            sortOrder += "DESC"; // Default to descending order
        }
        baseQuery.append(sortOrder);

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
        response.setCode(Constants.SUCCESS);
        response.setData(data);
        response.setMessage(Messages.USER_LIST_FETCHED);
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
            String status = (String) row[6];

            return new MarketingUserListResponseDto(
                    userId, name, email, promoCode, totalRegistration, totalConsultation, status
            );
        }).collect(Collectors.toList());
    }

}
