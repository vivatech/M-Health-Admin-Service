package com.mhealth.admin.security;//package com.mhealth.admin.security;
//
//import org.springframework.core.MethodParameter;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//@ControllerAdvice
//@RestControllerAdvice
//public class GlobalResponseBodyModifier implements ResponseBodyAdvice<Object> {
//
//    @Override
//    public boolean supports(MethodParameter returnType, Class converterType) {
//        return true;
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
//                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
//        try {
//
//            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
//            return body;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to convert response to string", e);
//        }
//    }
//}