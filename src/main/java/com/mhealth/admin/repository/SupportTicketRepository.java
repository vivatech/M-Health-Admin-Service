package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.SupportTicketStatus;
import com.mhealth.admin.model.SupportTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    @Query("Select u from SupportTicket u where u.supportTicketStatus = ?1 and u.supportTicketTitle LIKE %?2% and u.supportTicketCreatedBy = ?3")
    Page<SupportTicket> findByStatusAndNameAndUserId(SupportTicketStatus status, String name, Integer userId, Pageable pageable);

    @Query("Select u from SupportTicket u where u.supportTicketTitle LIKE %?1% and u.supportTicketCreatedBy = ?2")
    Page<SupportTicket> findByNameAndUserId(String name, Integer userId,Pageable pageable);

    @Query("Select COUNT(u) from SupportTicket u")
    Long countSupportTickets();

    @Query("SELECT s FROM SupportTicket s WHERE " +
            " (:supportTicketTitle IS NULL OR s.supportTicketTitle LIKE %:supportTicketTitle%) AND " +
            " (:supportTicketStatus IS NULL OR s.supportTicketStatus = :supportTicketStatus)")
    Page<SupportTicket> findByTitleAndStatus(
            @Param("supportTicketTitle") String title,
            @Param("supportTicketStatus") String status, Pageable pageable);
}