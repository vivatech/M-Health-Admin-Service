package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/check")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class Tcontroller {

    @Autowired
    Utility utility;

    @GetMapping("/login-user")
    public Object checkValidation(){
        return utility.getLoginUser();
    }
}
