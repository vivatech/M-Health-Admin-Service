package com.mhealth.admin.repository;

import com.mhealth.admin.model.SupportTicketMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SupportTicketMessageRepository extends JpaRepository<SupportTicketMessage, Integer> {
    @Query("Select u from SupportTicketMessage u where u.supportTicket.supportTicketId = ?1 order by u.supportTicketMsgsId DESC")
    Page<SupportTicketMessage> findByTicketId(Integer supportTicketId, Pageable pageable);
}