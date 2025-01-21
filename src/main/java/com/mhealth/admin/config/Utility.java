package com.mhealth.admin.config;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Service
public class Utility {

    @Autowired
    UsersRepository usersRepository;

    public Users getLoginUser() {
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            String contactNumber =  userDetails.getUsername();
            return usersRepository.findByContactNumber(contactNumber).orElse(null);
        }catch (Exception e){
            return null;
        }
    }

    public static String getLoginContactNumber() {
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            return  userDetails.getUsername();
        }catch (Exception e){
            return null;
        }
    }

    public String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static Locale getUserNotificationLanguageLocale(String notificationLanguage, Locale locale) {
        if(notificationLanguage.equalsIgnoreCase(Constants.LOCALE_ENGLISH)){
            locale = Locale.ENGLISH;
        }else {
            locale = new Locale(Constants.LOCALE_SOMALIA);
        }
        return locale;
    }

    public static Date startDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date endDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }
}
