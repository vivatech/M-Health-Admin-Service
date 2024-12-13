package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.OrdersSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Orders;
import com.mhealth.admin.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class OrdersService {

    @Autowired
    private OrdersRepository repository;

    @Autowired
    private MessageSource messageSource;


    public ResponseEntity<PaginationResponse<Orders>> searchOrders(OrdersSearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Orders> orders = repository.searchOrders(
                request.getPatientName(),
                request.getDoctorName(),
                request.getConsultationDate(),
                pageable);

        return ResponseEntity.ok(new PaginationResponse<>(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.ORDERS_FETCHED_SUCCESS,null,locale),
                orders.getContent(),
                orders.getTotalElements(),
                (long) orders.getSize(),
                (long) orders.getNumber()));
    }

    public ResponseEntity<Response> findById(Integer id, Locale locale) {
        Orders order = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage(Constants.ORDER_NOT_FOUND,null,locale)
                ));

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                Constants.ORDER_FETCHED_SUCCESS, order));
    }
}
