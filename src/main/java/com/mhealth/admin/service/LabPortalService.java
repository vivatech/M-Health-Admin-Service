package com.mhealth.admin.service;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.*;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.response.DashboardResponse;
import com.mhealth.admin.dto.response.MarketingUserListResponseDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class LabPortalService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LabOrdersRepository labOrdersRepository;
    @Autowired
    private LabConsultationRepository labConsultationRepository;
    @Autowired
    private LabPriceRepository labPriceRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private LabReportDocRepository labReportDocRepository;

    public Object getDashBoard(Integer labId, Locale locale) {
        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null){
            return new Response(Status.FAILED, Constants.FAILED, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        List<LabOrders> labOrdersList = labOrdersRepository.findByLabOrders(labId);
        if(labOrdersList.isEmpty()){
            return new Response(Status.FAILED, Constants.FAILED, messageSource.getMessage(Messages.RECORD_NOT_FOUND, null, locale));
        }
        labOrdersList = labOrdersList.stream().limit(10).toList();
        List<LabDashBoardResponse> responseList = mapLabOrdersListIntoLabOrderResponse(labOrdersList, locale);
        return new Response(Status.SUCCESS, Constants.SUCCESS, messageSource.getMessage(Messages.LAB_REPORT_LIST_FETCH, null, locale), responseList);

    }

    private List<LabDashBoardResponse> mapLabOrdersListIntoLabOrderResponse(List<LabOrders> labOrdersList, Locale locale) {
        return labOrdersList.stream().map(row->{
            LabDashBoardResponse response = new LabDashBoardResponse();
            response.setReportId(row.getId());
            response.setPatientName(row.getPatientId().getFullName());
            response.setDoctorName(row.getDoctor() == null ? "" : row.getDoctor().getFullName());
            response.setCreatedAt(String.valueOf(row.getCreatedAt().toLocalDate()));
            response.setPaymentStatus(messageSource.getMessage(row.getPaymentStatus().name(), null, locale));
            response.setViewReport(labConsultationRepository.findByLabOrderIdAndName(row.getId()));
            return response;
        }).toList();
    }

    public Object servicePrice(Integer labId, String catName, String subCatName, Integer page, Integer size, Locale locale) {
        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null){
            return new Response(Status.FAILED, Constants.FAILED, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        StringBuilder s = new StringBuilder("Select c.cat_name, " +
                "s.sub_cat_name, " +
                "u.lab_price, " +
                "u.lab_price_comment ");
        StringBuilder sb = new StringBuilder(" FROM mh_lab_price u " +
                "LEFT JOIN mh_lab_cat_master c ON c.cat_id = u.cat_id " +
                "LEFT JOIN mh_lab_sub_cat_master s ON s.sub_cat_id = u.sub_cat_id " +
                "LEFT JOIN mh_users r ON r.user_id = u.lab_id " +
                "WHERE r.user_id = "+ labId);

        if(!StringUtils.isEmpty(catName)){
            sb.append(" AND c.cat_name like :catName");
        }
        if(!StringUtils.isEmpty(subCatName)){
            sb.append(" AND s.sub_cat_name like :subCatName");
        }

        String mainQuery = s + sb.toString();
        Query baseQuery = entityManager.createNativeQuery(mainQuery);
        if(!StringUtils.isEmpty(catName)){
            baseQuery.setParameter("catName", "%" + catName + "%");
        }
        if(!StringUtils.isEmpty(subCatName)){
            baseQuery.setParameter("subCatName", "%" + subCatName + "%");
        }

        String countQuery = "SELECT COUNT(*) " + sb.toString() ;
        Query countQ = entityManager.createNativeQuery(countQuery);

        if(!StringUtils.isEmpty(catName)){
            countQ.setParameter("catName", "%" + catName + "%");
        }
        if(!StringUtils.isEmpty(subCatName)){
            countQ.setParameter("subCatName", "%" + subCatName + "%");
        }
        long totalCount = ((Number) countQ.getSingleResult()).longValue();
        // Pagination
        Pageable pageable = PageRequest.of(page - 1, size);
        baseQuery.setFirstResult((int) pageable.getOffset());
        baseQuery.setMaxResults(pageable.getPageSize());

        List<Object[]> result = baseQuery.getResultList();
        List<ServicePriceResponseDto> responseList = mapResultIntoServiceResponseDto(result);


        // Create pageable response
        Page<ServicePriceResponseDto> pageableResponse = new PageImpl<>(responseList, pageable, totalCount);

        return new Response(Status.SUCCESS, Constants.SUCCESS, messageSource.getMessage(Messages.LAB_PRICE_LIST_FETCH, null, locale), pageableResponse);
    }

    private List<ServicePriceResponseDto> mapResultIntoServiceResponseDto(List<Object[]> result) {
        return result.stream().map(row -> new ServicePriceResponseDto((String) row[0], (String) row[1], (Float) row[2], (String) row[3])).toList();
    }

    public Object getReportRequest(Integer labId, String catName, String subCatName, String patientName, String doctorName, LocalDate createDate, int page, int size, Locale locale) {
        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null){
            return new Response(Status.FAILED, Constants.FAILED, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        String query = "Select u.id, u.case_id, CONCAT(p.first_name , ' ' , p.last_name), " +
                "CONCAT(p.country_code, '' , p.contact_number), " +
                "p.residence_address, " +
                "CONCAT(d.first_name , ' ' , d.last_name), u.report_date, " +
                "u.delivery_date, u.report_time_slot, u.payment_status, u.status ";
        StringBuilder basicQuery = new StringBuilder("FROM mh_lab_orders u " +
                "LEFT JOIN mh_users p ON p.user_id = u.patient_id " +
                "LEFT JOIN mh_users d ON d.user_id = u.doctor_id " +
                "where u.lab_id = " +labId);

        if(!StringUtils.isEmpty(catName)){
            basicQuery.append(" AND c.cat_name like :catName");
        }
        if(!StringUtils.isEmpty(subCatName)){
            basicQuery.append(" AND s.sub_cat_name like :subCatName");
        }
        if(!StringUtils.isEmpty(patientName)){
            basicQuery.append(" AND CONCAT(p.first_name , ' ' , p.last_name) like :patientName");
        }
        if(!StringUtils.isEmpty(doctorName)){
            basicQuery.append(" AND CONCAT(d.first_name , ' ' , d.last_name) like :doctorName");
        }
        if(createDate != null){
            basicQuery.append(" AND DATE(u.lab_price_created_at) = " + createDate);
        }
        basicQuery.append(" ORDER BY u.id DESC");

        Query baseQuery = entityManager.createNativeQuery(query + basicQuery.toString());
        if(!StringUtils.isEmpty(catName)){
            baseQuery.setParameter("catName", "%" + catName + "%");
        }
        if(!StringUtils.isEmpty(subCatName)){
            baseQuery.setParameter("subCatName", "%" + subCatName + "%");
        }
        if(!StringUtils.isEmpty(patientName)){
            baseQuery.setParameter("patientName", "%" + patientName + "%");
        }
        if(!StringUtils.isEmpty(doctorName)){
            baseQuery.setParameter("doctorName", "%" + doctorName + "%");
        }
        List<Object[]> result = baseQuery.getResultList();

        List<LabReportResponseDto> responseList = mapResultIntoLabReportResponseDto(result, locale);


        String countQuery = "SELECT COUNT(*) " + basicQuery.toString() ;
        Query countQ = entityManager.createNativeQuery(countQuery);

        if(!StringUtils.isEmpty(catName)){
            countQ.setParameter("catName", "%" + catName + "%");
        }
        if(!StringUtils.isEmpty(subCatName)){
            countQ.setParameter("subCatName", "%" + subCatName + "%");
        }
        if(!StringUtils.isEmpty(patientName)){
            countQ.setParameter("patientName", "%" + patientName + "%");
        }
        if(!StringUtils.isEmpty(doctorName)){
            countQ.setParameter("doctorName", "%" + doctorName + "%");
        }
        long totalCount = ((Number) countQ.getSingleResult()).longValue();

        // Pagination
        Pageable pageable = PageRequest.of(page - 1, size);
        baseQuery.setFirstResult((int) pageable.getOffset());
        baseQuery.setMaxResults(pageable.getPageSize());

        // Create pageable response
        Page<LabReportResponseDto> pageableResponse = new PageImpl<>(responseList, pageable, totalCount);

        return new Response(Status.SUCCESS, Constants.SUCCESS, messageSource.getMessage(Messages.LAB_REPORT_LIST_FETCH, null, locale), pageableResponse);

    }

    private List<LabReportResponseDto> mapResultIntoLabReportResponseDto(List<Object[]> result, Locale locale) {
        return result.stream().map(row->{
            Integer reportId = (Integer) row[0];
            Integer caseId = (Integer) row[1];
            String patientName = (String) row[2];
            String contactNumber = "+" + (String) row[3];
            String address = (String) row[4];
            String doctorName = (String) row[5];
            Date reportDate = (Date) row[6];
            Date  deliveryDate = (Date) row[7];
            String reportTimeSlot = (String) row[8];
            String paymentStatus = (String) row[9];
            String status = (String) row[10];

            List<LabReportDoc> docList = labReportDocRepository.findByLabOrderId(reportId);
            List<ReportDocumentDto> documentList = mapDocListIntoDocumentList(docList);
            List<String> labConsultationList = labConsultationRepository.findByLabOrderId(reportId);
            return new LabReportResponseDto(reportId, caseId, patientName,
                    doctorName, reportDate, deliveryDate, reportTimeSlot, messageSource.getMessage(paymentStatus, null, locale), messageSource.getMessage(status, null, locale),
                    new ViewRequestInformation(patientName, contactNumber, address, labConsultationList), documentList);

        }).toList();
    }

    private List<ReportDocumentDto> mapDocListIntoDocumentList(List<LabReportDoc> docList) {
        return docList.stream().map(row-> new ReportDocumentDto(row.getId(), row.getLabReportDocDisplayName(), row.getCaseId() == null ? Constants.LAB_DOCUMENT_PATH + row.getLabReportDocName() : Constants.LAB_DOCUMENT_PATH + row.getCaseId() + "/" + row.getLabReportDocName())).toList();
    }
}
