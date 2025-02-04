package com.mhealth.admin.dto;

import com.mhealth.admin.dto.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BaseResponseDto {

   private String statusCode ;
   private ResponseStatus responseStatus;
   private String message;
   private Object data;

   public BaseResponseDto(ResponseStatus status, String statusCode, String message) {
      this.statusCode = statusCode;
      this.message = message;
      this.responseStatus = status;
   }
   public BaseResponseDto(ResponseStatus status, String statusCode, String message, Object data) {
      this.statusCode = statusCode;
      this.message = message;
      this.responseStatus = status;
      this.data = data;
   }
   public BaseResponseDto(Exception e) {
      this.statusCode = "500";
      this.responseStatus = ResponseStatus.NOCONTENT;
      this.data = new ArrayList<>();
      this.message = "Something went wrong";
   }

}
