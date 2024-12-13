package com.mhealth.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private Double totalConsultationAmount;
    private Double completedPayments;
    private Long totalRatingCount;
    private Long totalDoctorCount;
    private Long totalPatientCount;
    private Long totalClinicCount;
    private Long totalConsultations;
    private Long totalCancelledConsultations;
    private Long todayConsultations;
    private Long todayCancelledConsultations;
    private Long availableDoctors;
    private Long totalNursePartners;
    private Long totalSupportTickets;
    private Long totalNurseBookings;
    private Long activeDoctors;
}
