package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {


    @Autowired
    private UserService userService;


    @Async("asyncServiceExecutor")
    public void sendSms(String mobile){
        userService.sendSms(mobile);
    }


    @Async("asyncServiceExecutor")
    public void sendEmail(String email){
        userService.sendEmail(email);
    }
}
