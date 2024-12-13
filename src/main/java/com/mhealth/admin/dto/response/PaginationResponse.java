package com.mhealth.admin.dto.response;

import com.mhealth.admin.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.mhealth.admin.config.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse <T>{
    private List<T> data;
    private String code = SUCCESS_CODE;
    private String message = SUCCESS;
    private Status status = Status.SUCCESS;
    private Long page = 0L;
    private Long size = 0L;
    private Long total = 0L;

    public PaginationResponse(Exception e){
        this.code = INTERNAL_SERVER_ERROR_CODE;
        this.message = e.getMessage();
        this.status = Status.FAILED;
    }

    public PaginationResponse(String message){
        this.message = message;
    }

    public PaginationResponse(Status status,String code,String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public PaginationResponse(Status status,String code,String message,
                              List<T> data,Long total,Long size,Long page){
        this.status = status;
        this.code = code;
        this.message = message;
        this.total = total;
        this.size = size;
        this.page = page;
        this.data = data;
    }
}
