package com.example.controller;

import com.example.config.RabbitConfig;
import com.example.dto.UserDto;
import com.example.service.AsyncService;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * sbsbsbsb
 * sbsbsbsb
 * hello,hot-fix
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AsyncService asyncService;


    //247毫秒
    @PostMapping("/testlogin1")
    public String testLogin1(@RequestBody UserDto userDto){
//        开始时间
        long start = System.currentTimeMillis();

        if (!"15675436505".equals(userDto.getMobile())){
            return "账号不存在";
        }

//        消息持久化
        MessagePostProcessor messagePostProcessor = (message) -> {
          message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
          return message;
        };

        CorrelationData correlationData = new CorrelationData("sms:1");

//        发送短信
        rabbitTemplate.convertAndSend(RabbitConfig.ACCOUNT_EXCHANGE,RabbitConfig.SMS_ROUTING_KEY,userDto.getMobile(),messagePostProcessor,correlationData);

//        发送邮件
        CorrelationData correlationData2 = new CorrelationData("email:2");

        rabbitTemplate.convertAndSend(RabbitConfig.ACCOUNT_EXCHANGE,RabbitConfig.EMAIL_ROUTING_KEY,userDto.getEmail(),messagePostProcessor,correlationData2);

//        结束时间
        long end = System.currentTimeMillis();

//        耗时
        log.info("登录耗时: {}毫秒",(end - start));
        return "登录成功";
    }

    //9285毫秒
    @PostMapping("/testlogin2")
    public String testLogin2(@RequestBody UserDto userDto){
 //        开始时间
        long start = System.currentTimeMillis();

        if (!"15675436505".equals(userDto.getMobile())){
            return "账号不存在";
        }

//        发送短信
        userService.sendSms(userDto.getMobile());

//        发送邮件
        userService.sendEmail(userDto.getEmail());

//        结束时间
        long end = System.currentTimeMillis();

//        耗时
        log.info("登录耗时: {}毫秒",(end - start));
        return "登录成功";
    }

    //
    @PostMapping("/testlogin3")
    public String testLogin3(@RequestBody UserDto userDto){
        //        开始时间
        long start = System.currentTimeMillis();

        if (!"15675436505".equals(userDto.getMobile())){
            return "账号不存在";
        }

//        发送短信
        asyncService.sendSms(userDto.getMobile());

//        发送邮件
        asyncService.sendEmail(userDto.getMobile());

//        结束时间
        long end = System.currentTimeMillis();

//        耗时
        log.info("登录耗时: {}毫秒",(end - start));
        return "登录成功";
    }
}
