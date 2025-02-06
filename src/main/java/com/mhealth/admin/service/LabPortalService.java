package com.mhealth.admin.service;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.*;
import com.mhealth.admin.dto.enums.AddedType;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.response.*;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    private FileService fileService;

    private final List<String> sortFieldLabPrice = List.of("categoryName", "subCategoryName", "labPrice", "labPriceComment", "caseId", "patientName", "doctorName", "reportDate", "deliveryDate", "reportTimeSlot", "paymentStatus", "status");

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

    public Object servicePrice(Integer labId, Integer catId, Integer subCatId, Integer page, Integer size, String sortField, String sortBy, Locale locale) {
        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if (lab == null) {
            return new Response(Status.FAILED, Constants.FAILED, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        Specification<LabPrice> specification = filterByParam(labId, catId, subCatId, sortField, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LabPrice> pageResponse = labPriceRepository.findAll(specification, pageable);

        List<ServicePriceResponseDto> dtoList = new ArrayList<>();
        pageResponse.getContent().forEach(ele -> {
            ServicePriceResponseDto dto = new ServicePriceResponseDto();
            dto.setCatName(ele.getCatId().getCatName());
            dto.setSubCatName(ele.getSubCatId().getSubCatName());
            dto.setLabPrice(ele.getLabPrice());
            dto.setLabComment(ele.getLabPriceComment());

            dtoList.add(dto);
        });
        return new PaginationResponse<>(Status.SUCCESS, com.mhealth.admin.config.Constants.SUCCESS_CODE,
                messageSource.getMessage(Messages.LAB_PRICE_LIST_FETCH, null, locale),
                dtoList, pageResponse.getTotalElements(), (long) pageResponse.getSize(), (long) page);
    }

    private Specification<LabPrice> filterByParam(Integer labId, Integer catId, Integer subCatId, String sortField, String sortBy) {
        if(!sortFieldLabPrice.contains(sortField)){
            sortField = "labPriceId";
        }
        String finalSortField = sortField;
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            //filter by lab id
            predicates.add(criteriaBuilder.equal(root.get("labUser").get("userId"), labId));

            // Filter by Sub category id
            if (subCatId != null && subCatId > 0) {
                predicates.add(criteriaBuilder.equal(root.get("subCatId").get("subCatId"), subCatId));
            }

            // Filter by Category id
            if (catId != null && catId > 0) {
                predicates.add(criteriaBuilder.equal(root.get("catId").get("catId"), catId));
            }

            // Sorting logic with support for related entities
            Path<?> sortPath;
            if ("categoryName".equals(finalSortField)) {
                sortPath = root.get("catId").get("catName");
            } else if ("subCategoryName".equals(finalSortField)) {
                sortPath = root.get("subCatId").get("subCatName");
            } else {
                sortPath = root.get(finalSortField); // Default to direct field in the main entity
            }

            if ("desc".equalsIgnoreCase(sortBy)) {
                query.orderBy(criteriaBuilder.desc(sortPath));
            } else {
                query.orderBy(criteriaBuilder.asc(sortPath));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Object getReportRequest(Integer labId, Integer catId, Integer subCatId, String patientName, String doctorName, LocalDate createDate, int page, int size, String sortField, String sortBy, Locale locale) {
        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null){
            return new Response(Status.FAILED, Constants.FAILED, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        if(!sortFieldLabPrice.contains(sortField)){
            sortField = "id";
        }
        String finalSortField = sortField;
        String query = "Select u.id, u.case_id, CONCAT(p.first_name , ' ' , p.last_name), " +
                "CONCAT(p.country_code, '' , p.contact_number), " +
                "p.residence_address, " +
                "CONCAT(d.first_name , ' ' , d.last_name), u.report_date, " +
                "u.delivery_date, u.report_time_slot, u.payment_status, u.status ";
        StringBuilder basicQuery = new StringBuilder("FROM mh_lab_orders u " +
                "LEFT JOIN mh_users p ON p.user_id = u.patient_id " +
                "LEFT JOIN mh_users d ON d.user_id = u.doctor_id " +
                "LEFT JOIN ( " +
                "SELECT lab_orders_id, GROUP_CONCAT(DISTINCT category_id) AS category_ids, GROUP_CONCAT(DISTINCT sub_cat_id) AS sub_cat_ids " +
                        "FROM mh_lab_consultation " +
                        "GROUP BY lab_orders_id" +
                        ") AS sub ON u.id = sub.lab_orders_id " +
                "where u.lab_id = " +labId);

        if (catId != null) {
            basicQuery.append(" AND FIND_IN_SET('").append(catId).append("', sub.category_ids)");
        }
        if (subCatId != null) {
            basicQuery.append(" AND FIND_IN_SET('").append(subCatId).append("', sub.sub_cat_ids)");
        }
        if(!StringUtils.isEmpty(patientName)){
            basicQuery.append(" AND CONCAT(p.first_name , ' ' , p.last_name) like :patientName");
        }
        if(!StringUtils.isEmpty(doctorName)){
            basicQuery.append(" AND CONCAT(d.first_name , ' ' , d.last_name) like :doctorName");
        }
        if(createDate != null){
            basicQuery.append(" AND DATE(u.created_at) = :createdDate");
        }

        //sorting logic
        if ("caseId".equals(finalSortField)) {
            finalSortField = "u.case_id";
        } else if ("patientName".equals(finalSortField)) {
            finalSortField = "p.first_name";
        } else if ("doctorName".equals(finalSortField)) {
            finalSortField = "d.first_name";
        } else if ("reportDate".equals(finalSortField)) {
            finalSortField = "u.report_date";
        } else if ("reportTimeSlot".equals(finalSortField)) {
            finalSortField = "u.report_time_slot";
        } else if ("deliveryDate".equals(finalSortField)) {
            finalSortField = "u.delivery_date";
        } else if ("paymentStatus".equals(finalSortField)) {
            finalSortField = "u.payment_status";
        } else if ("status".equals(finalSortField)) {
            finalSortField = "u.status";
        }

        basicQuery.append(" ORDER BY " + finalSortField + " " + (!sortBy.equals("asc") ? "desc" : "asc"));

        Query baseQuery = entityManager.createNativeQuery(query + basicQuery.toString());
        if(!StringUtils.isEmpty(patientName)){
            baseQuery.setParameter("patientName", "%" + patientName + "%");
        }
        if(!StringUtils.isEmpty(doctorName)){
            baseQuery.setParameter("doctorName", "%" + doctorName + "%");
        }
        if (createDate != null) {
            baseQuery.setParameter("createdDate", createDate);
        }

        // Pagination
        Pageable pageable = PageRequest.of(page - 1, size);
        baseQuery.setFirstResult((int) pageable.getOffset());
        baseQuery.setMaxResults(pageable.getPageSize());

        List<Object[]> result = baseQuery.getResultList();

        List<LabReportResponseDto> responseList = mapResultIntoLabReportResponseDto(result, locale);

        String countQuery = "SELECT COUNT(*) " + basicQuery.toString() ;
        Query countQ = entityManager.createNativeQuery(countQuery);

        if(!StringUtils.isEmpty(patientName)){
            countQ.setParameter("patientName", "%" + patientName + "%");
        }
        if(!StringUtils.isEmpty(doctorName)){
            countQ.setParameter("doctorName", "%" + doctorName + "%");
        }
        if (createDate != null) {
            countQ.setParameter("createdDate", createDate);
        }
        long totalCount = ((Number) countQ.getSingleResult()).longValue();

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
        return docList.stream().map(row-> new ReportDocumentDto(row.getId(), row.getLabReportDocDisplayName(), (row.getCaseId() == null ? Constants.LAB_DOCUMENT_PATH + row.getLabReportDocName() : Constants.LAB_DOCUMENT_PATH + row.getCaseId() + "/" + row.getLabReportDocName()))).toList();
    }

    public Object deleteLabReport(Integer labId, Integer documentId, Locale locale) throws IOException {
        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null){
            return new Response(Status.FAILED, Constants.FAILED, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        LabReportDoc labReportDoc = labReportDocRepository.findById(documentId).orElse(null);
        if(labReportDoc == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.DOCUMENT_NOT_FOUND, null, locale));

        // Delete existing profile if present
        String filePath = labReportDoc.getCaseId() != null
                ? Constants.LAB_DOCUMENT_PATH + labReportDoc.getCaseId()
                : Constants.LAB_DOCUMENT_PATH ;
        fileService.deleteFile(filePath, labReportDoc.getLabReportDocName());

        labReportDocRepository.delete(labReportDoc);

        return new Response(Status.SUCCESS, Constants.SUCCESS, messageSource.getMessage(Messages.DOCUMENT_DELETED_SUCCESSFULLY, null, locale));
    }

    public Object updateReportRequest(Integer labId, Integer reportId, UpdateReportRequestDto request, Locale locale) throws IOException {
        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null){
            return new Response(Status.FAILED, Constants.FAILED, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        LabOrders orders = labOrdersRepository.findById(reportId).orElse(null);
        if(orders == null){
            return new Response(Status.FAILED, Constants.FAILED, "Lab order not found!");
        }

        String message = "";
        //Update delivery date
        if (request.getDeliveryDate() != null) {
            orders.setDeliveryDate(request.getDeliveryDate());
            labOrdersRepository.save(orders);

            message = messageSource.getMessage(Messages.USER_UPDATED, null, locale);

        }

        //upload lab report
        if (request.getReport() != null && !request.getReport().isEmpty()){
            // Extract the file extension
            String extension = fileService.getFileExtension(Objects.requireNonNull(request.getReport().getOriginalFilename()));

            // Generate a random file name
            String fileName = UUID.randomUUID() + "." + extension;

            String filePath = orders.getCaseId() != null
                    ? Constants.LAB_DOCUMENT_PATH + orders.getCaseId().getCaseId()
                    : Constants.LAB_DOCUMENT_PATH ;

            // Save the file
            fileService.saveFile(request.getReport(), filePath, fileName);

            //create new entry into lab document table
            LabReportDoc reportDoc = new LabReportDoc();
            reportDoc.setCaseId(orders.getCaseId() == null ? null : orders.getCaseId().getCaseId());
            reportDoc.setLabOrdersId(orders.getId());
            reportDoc.setLabReportDocName(fileName);
            reportDoc.setLabReportDocType(request.getReport().getContentType());
            reportDoc.setLabReportDocDisplayName(request.getReport().getOriginalFilename());
            reportDoc.setAddedType(AddedType.Lab);
            reportDoc.setAddedBy(labId);
            reportDoc.setStatus(StatusAI.A);
            reportDoc.setCreatedAt(LocalDateTime.now());

            labReportDocRepository.save(reportDoc);

            message += " Report added successfully";
        }
        return new Response(Status.SUCCESS, Constants.CODE_1, message.trim());
    }
}
