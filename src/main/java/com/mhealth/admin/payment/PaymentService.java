package com.mhealth.admin.payment;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.response.RefundResponseDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.repository.SlotMasterRepository;
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

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class PaymentService {

    private final List<PaymentInterface> sortedProcessors;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;


    @Autowired
    private SlotMasterRepository slotMasterRepository;

    @Value("${m-health.country.currency}")
    private String currencyCode;

    public PaymentService(List<PaymentInterface> sortedProcessors) {
        this.sortedProcessors = sortedProcessors;
    }

    public PaymentInterface getMatchedProcessor(PaymentAggregator paymentAggregator) {
        for (PaymentInterface processor : sortedProcessors) {
            if(processor.supports(paymentAggregator)) {
                return processor;
            }
        }
        return null;
    }

    public Response sendPayment(PaymentDto paymentDto, String country){
        PaymentInterface matchedProcessor = getMatchedProcessor(getPaymentAggregator(country));
        return matchedProcessor.sendPayment(paymentDto);
    }

    public Response refundPayment(String msisdn, String transactionId, String country){
        PaymentInterface matchedProcessor = getMatchedProcessor(getPaymentAggregator(country));
        return matchedProcessor.reversePayment(msisdn, transactionId);
    }

    public PaymentAggregator getPaymentAggregator(String country) {
        HashMap<String, PaymentAggregator> map = new HashMap<>();
        map.put("SO", PaymentAggregator.WAAFI);
        map.put("KE", PaymentAggregator.SAFARI);
        return map.get(country);
    }

    public Object getRefundList(Locale locale, Integer caseId, String doctorName, String patientName, LocalDate startDate, LocalDate endDate, String sortField, String sortBy, int page, int size) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT " +
                        "c.case_id, " +
                        "p.first_name AS patientFirstName, p.last_name AS patientLastName, " +
                        "d.first_name AS doctorFirstName, d.last_name AS doctorLastName, " +
                        "h.clinic_name AS clinicName, " +  // Fetch clinic name from hospital user
                        "c.consultation_date, " +
                        "c.slot_id, " +
                        "o.amount, " +
                        "o.status " +
                        "FROM mh_orders o " +
                        "JOIN mh_users p ON o.patient_id = p.user_id " +
                        "JOIN mh_users d ON o.doctor_id = d.user_id " +
                        "LEFT JOIN mh_users h ON d.hospital_id = h.user_id " + // Self-join to get clinic name
                        "JOIN mh_consultation c ON o.case_id = c.case_id " +
                        "WHERE 1=1 " +
                        "AND o.case_id IS NOT NULL " +
                        "AND c.consultation_type = 'Paid' " +
                        "AND o.status NOT IN ('Completed') "
        );

        // Dynamic filters
        if (caseId != null) {
            queryStr.append("AND c.case_id = :caseId ");
        }
        if (startDate != null && endDate != null) {
            queryStr.append("AND c.consultation_date BETWEEN :startDate AND :endDate ");
        }
        if (patientName != null && !patientName.isEmpty()) {
            queryStr.append("AND CONCAT(p.first_name, ' ', p.last_name) LIKE :patientName ");
        }
        if (doctorName != null && !doctorName.isEmpty()) {
            queryStr.append("AND CONCAT(d.first_name, ' ', d.last_name) LIKE :doctorName ");
        }

        // Sorting logic
        if (sortField != null) {
            switch (sortField.toLowerCase()) {
                case "patient_name":
                    queryStr.append("ORDER BY p.first_name ");
                    break;
                case "doctor_name":
                    queryStr.append("ORDER BY d.first_name ");
                    break;
                case "clinic_name":
                    queryStr.append("ORDER BY h.clinic_name ");
                    break;
                case "amount":
                    queryStr.append("ORDER BY o.amount ");
                    break;
                case "status":
                    queryStr.append("ORDER BY CAST(o.status AS CHAR) ");
                    break;
                default:
                    queryStr.append("ORDER BY c.case_id ");
            }
        } else {
            queryStr.append("ORDER BY c.case_id ");
        }

        // Sorting order (ASC/DESC)
        if ("1".equalsIgnoreCase(sortBy)) {
            queryStr.append(" DESC ");
        } else {
            queryStr.append(" ASC ");
        }

        Query query = entityManager.createNativeQuery(queryStr.toString());

        // Set parameters dynamically
        if (caseId != null) {
            query.setParameter("caseId", caseId);
        }
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        if (patientName != null && !patientName.isEmpty()) {
            query.setParameter("patientName", "%" + patientName + "%");
        }
        if (doctorName != null && !doctorName.isEmpty()) {
            query.setParameter("doctorName", "%" + doctorName + "%");
        }

        // Pagination
        Pageable pageable = PageRequest.of(page - 1, size);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Fetch results
        List<Object[]> results = query.getResultList();

        // Map results to DTO
        List<RefundResponseDto> responseList = convertToResponseDto(results);

        // Total count query
        String countQuery = "SELECT COUNT(*) FROM (" + queryStr + ") AS countQuery";
        Query countQ = entityManager.createNativeQuery(countQuery);

        // Set parameters dynamically
        if (startDate != null && endDate != null) {
            countQ.setParameter("startDate", startDate);
            countQ.setParameter("endDate", endDate);
        }
        if (patientName != null && !patientName.isEmpty()) {
            countQ.setParameter("patientName", "%" + patientName + "%");
        }
        if (doctorName != null && !doctorName.isEmpty()) {
            countQ.setParameter("doctorName", "%" + doctorName + "%");
        }
        if (caseId != null) {
            countQ.setParameter("caseId", caseId);
        }

        Long totalCount = ((Number) countQ.getSingleResult()).longValue();

        // Create pageable response
        Page<RefundResponseDto> pageableResponse = new PageImpl<>(responseList, pageable, totalCount);

        // Build response
        Map<String, Object> data = new HashMap<>();
        data.put("refundList", pageableResponse.getContent());
        data.put("totalCount", pageableResponse.getTotalElements());

        Response response = new Response();
        if (pageableResponse.getContent().isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setStatus(Status.FAILED);
            response.setMessage(messageSource.getMessage(Messages.RECORD_NOT_FOUND, null, locale));
        } else {
            response.setCode(Constants.CODE_1);
            response.setStatus(Status.SUCCESS);
            response.setMessage(messageSource.getMessage(Messages.RECORD_FOUND, null, locale));
        }
        response.setData(data);


        return response;
    }

    private List<RefundResponseDto> convertToResponseDto(List<Object[]> results) {
        List<RefundResponseDto> responseList = new ArrayList<>();

        for (Object[] row : results) {
            RefundResponseDto dto = new RefundResponseDto();

            dto.setCaseId(row[0] != null ? ((Number) row[0]).intValue() : null);
            dto.setPatientName((row[1] != null ? row[1].toString() : "") + " " + (row[2] != null ? row[2].toString() : ""));
            dto.setDoctorName((row[3] != null ? row[3].toString() : "") + " " + (row[4] != null ? row[4].toString() : ""));
            dto.setClinicName(row[5] != null ? row[5].toString() : "");
            dto.setDate(row[6] != null ? row[6].toString() : "");
            Integer slotId = row[7] != null ? ((Number) row[7]).intValue() : null;
            if (slotId != null) {
                String time = slotMasterRepository.findBySlotId(slotId).getSlotTime();
                dto.setTime(formatTimeRange(time));
            }
            dto.setAmount(row[8] != null ? currencyCode + " " + ((Number) row[8]).floatValue() : "");
            dto.setStatus(row[9] != null ? row[9].toString() : "");

            responseList.add(dto);
        }

        return responseList;
    }

    private String formatTimeRange(String timeRange) {
        String[] parts = timeRange.split(":");
        if (parts.length == 4) {
            return parts[0] + ":" + parts[1] + " TO " + parts[2] + ":" + parts[3];
        }
        return timeRange;
    }


}
