package com.example.consumer;

import com.example.service.UserService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class AccountConsumer {

    @Autowired
    private UserService userService;

    @RabbitListener(queues = "sms_account_queue")
    public void receiveSms(Message message, Channel channel){
        String mobile = new String(message.getBody());
        userService.sendSms(mobile);
    }

    @RabbitListener(queues = "email_account_queue")
    public void receiveEmail(Message message, Channel channel){
        String email = new String(message.getBody());
        userService.sendEmail(email);
    }

    @RabbitListener(queues = "dead_account_queue")
    public void receiveDead(Message message, Channel channel){
        String mobile = new String(message.getBody());
        userService.sendSms(mobile);
    }
}
