package com.mhealth.admin.service;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.labpricedto.LabPriceListDto;
import com.mhealth.admin.dto.labpricedto.LabPriceRequestDto;
import com.mhealth.admin.dto.labpricedto.LabPriceResponseDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.LabCategoryMasterRepository;
import com.mhealth.admin.repository.LabPriceRepository;
import com.mhealth.admin.repository.LabSubCategoryMasterRepository;
import com.mhealth.admin.repository.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mhealth.admin.constants.Constants.Currency_USD;

@Service
public class LabPriceManagementService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LabPriceRepository labPriceRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public static final List<String> sortByValues = List.of("catName", "subCatName", "labPrice");
    @Autowired
    private LabCategoryMasterRepository labCategoryMasterRepository;
    @Autowired
    private LabSubCategoryMasterRepository labSubCategoryMasterRepository;

    public Response getLabPriceList(Locale locale, Integer labId, String categoryName, String subCategoryName, String sortField, String sortBy, int page, int size) {
        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null){
            return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }

        if(!sortByValues.contains(sortField)){
            sortField = "labPriceComment";
        }

        StringBuilder baseQuery = new StringBuilder("Select ")
                .append("u.labPriceId as id, ")
                .append("u.catId.catName as catName, ")
                .append("u.subCatId.subCatName as subCateName, ")
                .append("u.labPrice as price, ")
                .append("u.labPriceComment as comment ")
                .append("From LabPrice u WHERE u.labUser.userId = ")
                .append(labId);

        // Dynamically add filters
        if (!StringUtils.isEmpty(categoryName)) {
            baseQuery.append(" AND u.catId.catName ").append(categoryName);
        }
        if (!StringUtils.isEmpty(subCategoryName)) {
            baseQuery.append(" AND u.subCatId.subCatName ").append(subCategoryName);
        }

        if(sortField.equalsIgnoreCase("labPriceComment")
                || sortField.equalsIgnoreCase("labPrice"))
        baseQuery.append(" ORDER BY u." + sortField + " " + (sortBy.equals("0") ? "ASC" : "DESC"));

        else if(sortField.equalsIgnoreCase("catName"))
            baseQuery.append(" ORDER BY u.catId.catName " + (sortBy.equals("0") ? "ASC" : "DESC"));

        else if(sortField.equalsIgnoreCase("subCatName"))
            baseQuery.append(" ORDER BY u.subCatId.subCatName " + (sortBy.equals("0") ? "ASC" : "DESC"));

        // Create query
        Query query = entityManager.createQuery(baseQuery.toString());

        // Pagination
        Pageable pageable = PageRequest.of(page - 1, size);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Fetch results
        List<Object[]> results = query.getResultList();

        List<LabPriceListDto> responseList = mapResultToLabPriceListDto(results);

        // Total count query
        String countQuery = "SELECT COUNT(*) FROM (" + baseQuery + ") AS countQuery";
        Query countQ = entityManager.createQuery(countQuery);

        long totalCount = ((Number) countQ.getSingleResult()).longValue();

        // Create pageable response
        Page<LabPriceListDto> pageableResponse = new PageImpl<>(responseList, pageable, totalCount);

        // Build response
        Map<String, Object> data = new HashMap<>();
        data.put("labPriceList", pageableResponse.getContent());
        data.put("totalCount", pageableResponse.getTotalElements());

        return new Response(Status.SUCCESS, Constants.CODE_1, messageSource.getMessage(Messages.LAB_PRICE_LIST_FETCH, null, locale), data);

    }

    public Response getFilteredLabPrice(Locale locale, Integer labId, String categoryName, String subCategoryName, String sortField, String sortBy, int page, int size) {
        if (page == 0) page = 1;
        Specification<LabPrice> specification = filterByParams(labId, categoryName, subCategoryName, sortField, sortBy);
        // Pagination
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LabPrice> pageableResponse = labPriceRepository.findAll(specification, pageable);
        // Build response
        Map<String, Object> data = new HashMap<>();
        data.put("labPriceList", mapResultToLabPriceListDto2(pageableResponse.getContent()));
        data.put("totalCount", pageableResponse.getTotalElements());
        return new Response(Status.SUCCESS, Constants.CODE_1, messageSource.getMessage(Messages.LAB_PRICE_LIST_FETCH, null, locale), data);
    }

    public static Specification<LabPrice> filterByParams(Integer labId, String categoryName, String subCategoryName, String sortField, String sortBy) {
        if(!sortByValues.contains(sortField)){
            sortField = "labPriceComment";
        }
        String finalSortField = sortField;
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by labId
            if (labId != null) {
                Join<LabPrice, Users> lab = root.join("labUser");
                predicates.add(criteriaBuilder.equal(lab.get("userId"), labId));
            }

            // Filter by categoryName
            if (!StringUtils.isEmpty(categoryName)) {
                Join<LabPrice, LabCategoryMaster> category = root.join("catId");
                predicates.add(criteriaBuilder.equal(category.get("catName"), categoryName));
            }

            // Filter by subCategoryName
            if (!StringUtils.isEmpty(subCategoryName)) {
                Join<LabPrice, LabSubCategoryMaster> subCategory = root.join("subCatId");
                predicates.add(criteriaBuilder.equal(subCategory.get("subCatName"), subCategoryName));
            }

            // Sorting logic
            if ("desc".equalsIgnoreCase(sortBy)) {
                query.orderBy(criteriaBuilder.desc(root.get(finalSortField)));
            } else {
                query.orderBy(criteriaBuilder.asc(root.get(finalSortField)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<LabPriceListDto> mapResultToLabPriceListDto2(List<LabPrice> results) {
        return results.stream().map(row->{
            Integer labPriceId = row.getLabPriceId();
            String catName = row.getCatId().getCatName();
            String subCatName = row.getSubCatId().getSubCatName();
            Float labPrice = row.getLabPrice();
            String comment = row.getLabPriceComment();

            return new LabPriceListDto(labPriceId, catName, subCatName, Currency_USD + " " + labPrice, comment);
        }).collect(Collectors.toList());
    }

    private List<LabPriceListDto> mapResultToLabPriceListDto(List<Object[]> results) {
        return results.stream().map(row->{
            Integer labPriceId = (Integer) row[0];
            String catName = (String) row[1];
            String subCatName = (String) row[2];
            Float labPrice = (Float) row[3];
            String comment = (String) row[4];

            return new LabPriceListDto(labPriceId, catName, subCatName, Currency_USD + " " + labPrice, comment);
        }).collect(Collectors.toList());
    }

    public Response updateLabPrice(Locale locale, Integer labId, LabPriceRequestDto labPriceRequestDto) {

        // Validate the input
        if(labPriceRequestDto.getLabPriceId() == null || labPriceRequestDto.getLabPriceId() <= 0) return new Response(Status.FAILED, Constants.CODE_O, "labPriceId is required!");

        String validationMessage = labPriceRequestDto.validate();
        if (validationMessage != null) {
            return new Response(Status.FAILED, Constants.CODE_O, validationMessage);
        }

        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));

        LabPrice labPrice = labPriceRepository.findByLabPriceIdAndLabUser(labPriceRequestDto.getLabPriceId(), lab);
        if(labPrice == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.RECORD_NOT_FOUND, null, locale));

        LabCategoryMaster categoryMaster = labCategoryMasterRepository.findById(labPriceRequestDto.getCategoryId()).orElse(null);
        if(categoryMaster == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.LAB_CATEGORY_NOT_FOUND, null, locale));

        LabSubCategoryMaster subCategoryMaster = labSubCategoryMasterRepository.findById(labPriceRequestDto.getSubCategoryId()).orElse(null);
        if(subCategoryMaster == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.LAB_SUB_CATEGORY_NOT_FOUND, null, locale));

        labPrice.setCatId(categoryMaster);
        labPrice.setSubCatId(subCategoryMaster);
        labPrice.setLabPrice(labPriceRequestDto.getLabPrice());
        labPrice.setLabPriceComment(StringUtils.isEmpty(labPriceRequestDto.getLabPriceComment()) ? "" : labPriceRequestDto.getLabPriceComment());
        labPrice.setLabPriceUpdatedAt(LocalDateTime.now());

        labPriceRepository.save(labPrice);

        return new Response(Status.SUCCESS, Constants.CODE_1, messageSource.getMessage(Messages.USER_UPDATED, null, locale));
    }

    public Response createLabPrice(Locale locale, Integer labId, LabPriceRequestDto labPriceRequestDto) {
        // Validate the input
        String validationMessage = labPriceRequestDto.validate();
        if (validationMessage != null) {
            return new Response(Status.FAILED, Constants.CODE_O, validationMessage);
        }

        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));

        LabCategoryMaster categoryMaster = labCategoryMasterRepository.findById(labPriceRequestDto.getCategoryId()).orElse(null);
        if(categoryMaster == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.LAB_CATEGORY_NOT_FOUND, null, locale));

        LabSubCategoryMaster subCategoryMaster = labSubCategoryMasterRepository.findById(labPriceRequestDto.getSubCategoryId()).orElse(null);
        if(subCategoryMaster == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.LAB_SUB_CATEGORY_NOT_FOUND, null, locale));

        List<LabPrice> labPriceList = labPriceRepository.findByLabIdAndCatIdAndSubCatId(lab.getUserId(), categoryMaster.getCatId(), subCategoryMaster.getSubCatId());
        if(!labPriceList.isEmpty()) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.LAB_PRICE_EXISTS, null, locale));

        LabPrice labPrice = new LabPrice();
        labPrice.setCatId(categoryMaster);
        labPrice.setSubCatId(subCategoryMaster);
        labPrice.setLabUser(lab);
        labPrice.setLabPrice(labPriceRequestDto.getLabPrice());
        labPrice.setLabPriceComment(StringUtils.isEmpty(labPriceRequestDto.getLabPriceComment()) ? "" : labPriceRequestDto.getLabPriceComment());
        labPrice.setLabPriceCreatedAt(LocalDateTime.now());
        labPrice.setLabPriceUpdatedAt(LocalDateTime.now());

        labPriceRepository.save(labPrice);

        return new Response(Status.SUCCESS, Constants.CODE_1, messageSource.getMessage(Messages.USER_CREATED, null, locale));
    }

    public Response getLabPriceDetails(Locale locale, Integer labId, Integer labPriceId) {
        // Validate the input
        if(labId == null || labId <= 0) return new Response(Status.FAILED, Constants.CODE_O, "labId is required!");
        if(labPriceId == null || labPriceId <= 0) return new Response(Status.FAILED, Constants.CODE_O, "labPriceId is required!");

        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));

        LabPrice labPrice = labPriceRepository.findByLabPriceIdAndLabUser(labPriceId, lab);
        if(labPrice == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.RECORD_NOT_FOUND, null, locale));

        LabPriceResponseDto responseDto = getLabPriceResponseDto(labPrice);

        return new Response(Status.SUCCESS, Constants.CODE_1, messageSource.getMessage(Messages.USER_LIST_FETCHED, null, locale), responseDto);
    }

    private LabPriceResponseDto getLabPriceResponseDto(LabPrice labPrice) {
        LabPriceResponseDto responseDto = new LabPriceResponseDto();
        responseDto.setLabPriceId(labPrice.getLabPriceId());
        responseDto.setCategory(Collections.singletonMap(labPrice.getCatId().getCatId(), labPrice.getCatId().getCatName()));
        responseDto.setSubCategory(Collections.singletonMap(labPrice.getSubCatId().getSubCatId(), labPrice.getSubCatId().getSubCatName()));
        responseDto.setLabPrice(labPrice.getLabPrice());
        responseDto.setLabPriceComment(labPrice.getLabPriceComment());
        return responseDto;
    }

    public Response deleteLabPrice(Locale locale, Integer labId, Integer labPriceId) {
        // Validate the input
        if(labId == null || labId <= 0) return new Response(Status.FAILED, Constants.CODE_O, "labId is required!");
        if(labPriceId == null || labPriceId <= 0) return new Response(Status.FAILED, Constants.CODE_O, "labPriceId is required!");

        Users lab = usersRepository.findByUserIdAndType(labId, UserType.Lab).orElse(null);
        if(lab == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));

        LabPrice labPrice = labPriceRepository.findByLabPriceIdAndLabUser(labPriceId, lab);
        if(labPrice == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.RECORD_NOT_FOUND, null, locale));

        labPriceRepository.delete(labPrice);
        return new Response(Status.SUCCESS, Constants.CODE_1, messageSource.getMessage(Messages.USER_DELETED, null, locale));
    }
}
