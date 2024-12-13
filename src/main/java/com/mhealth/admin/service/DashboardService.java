package com.mhealth.admin.service;

import com.mhealth.admin.dto.enums.*;
import com.mhealth.admin.dto.response.DashboardResponse;
import com.mhealth.admin.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SupportTicketRepository supportTicketService;

    @Autowired
    private ConsultationRepository consultationService;

    @Autowired
    private OrdersRepository orderService;

    @Autowired
    private DoctorPaymentRepository doctorPaymentRepository;

    @Autowired
    private PartnerNurseRepository partnerNurseRepository;

    @Autowired
    private NurseDemandOrdersRepository nurseDemandOrdersRepository;

    public ResponseEntity<?> getDashboardDetails(Locale locale) {
        Long totalClinicCount = usersRepository.countUsersByType(UserType.Clinic);

        Long totalRatingCount = consultationService.countConsultationRatings();

        Long totalDoctorCount = usersRepository.countUsersByType(UserType.Doctor);

        Long totalPatientCount = usersRepository.countUsersByType(UserType.Patient);

        Double totalConsultationAmount = orderService.getTotalConsultationAmount(
                List.of(OrderStatus.Cancelled,OrderStatus.Failed));

        Double completedPayments = doctorPaymentRepository.getTotalCompletedPayments();

        Long totalConsultations = consultationService.getTotalConsultations(RequestType.Book);

        Long totalCancelledConsultations = consultationService.getTotalConsultations(RequestType.Cancel);

        Long todayConsultations = consultationService.getTotalConsultations(RequestType.Book, LocalDate.now());

        Long todayCancelledConsultations = consultationService.getTotalConsultations(RequestType.Cancel, LocalDate.now());

        String today = LocalDate.now().getDayOfWeek().name().toLowerCase();
        Long availableDoctors = usersRepository.countAvailableDoctors(today);
        Long activeDoctors = usersRepository.countUsersByTypeAndStatus(UserType.Doctor, StatusAI.A);

        Long totalNursePartners = partnerNurseRepository.countNursePartners();

        Long totalSupportTickets = supportTicketService.countSupportTickets();

        Long totalNurseBookings = nurseDemandOrdersRepository.countActiveNurseBookings(StatusFullName.Cancelled);

        return ResponseEntity.ok(new DashboardResponse(
                totalConsultationAmount,
                completedPayments,
                totalRatingCount,
                totalDoctorCount,
                totalPatientCount,
                totalClinicCount,
                totalConsultations,
                totalCancelledConsultations,
                todayConsultations,
                todayCancelledConsultations,
                availableDoctors,
                totalNursePartners,
                totalSupportTickets,
                totalNurseBookings,
                activeDoctors
        ));
    }
}
