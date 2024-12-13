package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.SupportTicket;
import com.mhealth.admin.repository.SupportTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class SupportTicketService {
    @Autowired
    private SupportTicketRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<PaginationResponse<SupportTicket>> searchSupportTickets(
            int page, int size, String title, String status, Locale locale) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SupportTicket> ticketsPage = repository.findByTitleAndStatus(title, status, pageable);

        return ResponseEntity.ok(new PaginationResponse<>(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.SUPPORT_TICKETS_FETCHED_SUCCESS,null,locale),
                ticketsPage.getContent(),
                ticketsPage.getTotalElements(),
                (long) ticketsPage.getSize(),
                (long) ticketsPage.getNumber()
        ));
    }

    public ResponseEntity<Response> findSupportTicketById(Integer id, Locale locale) {
        SupportTicket ticket = repository.findById(id).orElse(null);
        if (ticket == null) {
            return ResponseEntity.status(404)
                    .body(new Response(
                            Status.SUCCESS,
                            Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.SUPPORT_TICKET_NOT_FOUND,null,locale),
                            null
                    ));
        }

        return ResponseEntity.ok(new Response(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.SUPPORT_TICKETS_FETCHED_SUCCESS,null,locale),
                ticket
        ));
    }
}
