package com.mhealth.admin.report.controller;

import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.HealthTipPackageUser;
import com.mhealth.admin.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mhealth.admin.config.Constants.*;

@RestController
@Tag(name = "Health Tips Payment", description = "APIs for managing health tips payment")
@RequestMapping("/api/v1/admin/health-tips-report")
public class HealthTipsPaymentController {

    @Autowired
    private HealthTipPackageUserRepository healthTipPackageUserRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private HealthTipCategoryMasterRepository  healthTipCategoryMasterRepository;

    @Autowired
    private HealthTipRepository healthTipRepository;

    @Autowired
    private HealthTipPackageRepository healthTipPackageRepository;

    @Autowired
    private HealthTipPackageCategoriesRepository healthTipPackageCategoriesRepository;

    @Operation(summary = "Get health tips payment", description = "Fetch health tips payment details based on package name and created date with pagination")
    @GetMapping("/get")
    public PaginationResponse<HealthTipsPaymentDto> getHealthTipsPayment(
            @RequestParam(value = "packageName", required = false) String packageName,
            @RequestParam(value = "createdDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            // Handle date range based on createdDate parameter
            LocalDateTime startDate = (createdDate != null) ? createdDate.atStartOfDay() : LocalDate.MIN.atStartOfDay();
            LocalDateTime endDate = (createdDate != null) ? createdDate.plusDays(1).atStartOfDay() : LocalDateTime.now();

            // Fetch paginated data from the repository
            Page<HealthTipPackageUser> healthTipPackageUsers = healthTipPackageUserRepository.findByPackageNameAndCreatedAtRange(
                    packageName, startDate, endDate, pageable);

            // Map the results to DTOs
            List<HealthTipsPaymentDto> dtoList = healthTipPackageUsers.stream()
                    .map(user -> {
                        HealthTipsPaymentDto dto = new HealthTipsPaymentDto();
                        dto.setUserName(user.getUser().getFirstName() + " " + user.getUser().getLastName());
                        dto.setPackageName(user.getHealthTipPackage().getPackageName());
                        dto.setUserType(user.getUser().getType().name());
                        dto.setCreatedAt(user.getCreatedAt());
                        dto.setPackagePrice(user.getIsVideo().equals(YesNo.Yes)
                                ? user.getHealthTipPackage().getPackagePriceVideo()
                                : user.getHealthTipPackage().getPackagePrice());
                        dto.setIsExpired(user.getIsExpire());
                        dto.setIsCancelled(user.getIsCancel());
                        dto.setExpiredAt(user.getExpiredAt());
                        return dto;
                    })
                    .collect(Collectors.toList());

            // Prepare and return the response
            return new PaginationResponse<>(
                    Status.SUCCESS,
                    "200",
                    "Health tips payment fetched successfully",
                    dtoList,
                    healthTipPackageUsers.getTotalElements(),
                    (long) size,
                    (long) page
            );

        } catch (Exception e) {
            // Handle and return error response
            return new PaginationResponse<>(e);
        }
    }


}
