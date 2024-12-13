package com.mhealth.admin.dto.response;

import com.mhealth.admin.dto.Status;
import com.mhealth.admin.config.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Response {
    private String code = Constants.SUCCESS_CODE;
    private String message = Constants.SUCCESS;
    private Status status = Status.SUCCESS;
    private Object data;

    public Response(Exception e){
        this.code = Constants.INTERNAL_SERVER_ERROR_CODE;
        this.message = e.getMessage();
        this.status = Status.FAILED;
    }

    public Response(String message){
        this.message = message;
    }

    public Response(Status status,String code,String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public Response(Status status,String code,String message,Object data){
        this.status = status;
        this.code = code;
        this.message = message;
//        try{
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new JavaTimeModule());
//            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//            String dataString = objectMapper.writeValueAsString(data);
//            this.data = dataString;
//        }catch (Exception e){
            this.data = data;
//        }
    }

}
