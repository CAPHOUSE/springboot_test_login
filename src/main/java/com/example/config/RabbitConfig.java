package com.example.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {
    public static final String ACCOUNT_EXCHANGE = "account_exchange";

    public static final String SMS_ACCOUNT_QUEUE = "sms_account_queue";

    public static final String EMAIL_ACCOUNT_QUEUE = "email_account_queue";

    public static final String SMS_ROUTING_KEY = "sms_key";

    public static final String EMAIL_ROUTING_KEY = "email_key";

    //    死信队列
    public static final String DEAD_ACCOUNT_EXCHANGE = "dead_account_exchange";

    public static final String DEAD_ACCOUNT_QUEUE = "dead_account_queue";

    public static final String DEAD_ROUTING_KEY = "dead_key";

    @Bean("accountExchange")
    public CustomExchange customExchange(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");//自定义交换机的类型
        return new CustomExchange(ACCOUNT_EXCHANGE,"x-delayed-message",true,false,arguments);
    }


    @Bean("smsQueue")
    public Queue smsQueue(){
        Map<String, Object> arguments = new HashMap<>(3);
        arguments.put("x-dead-letter-exchange", DEAD_ACCOUNT_EXCHANGE);//声明当前队列绑定的死信交换机
        arguments.put("x-dead-letter-routing-key", DEAD_ROUTING_KEY);//声明当前队列的死信路由 key
        return new Queue(SMS_ACCOUNT_QUEUE,true,false,false,arguments);
    }

    @Bean("emailQueue")
    public Queue emailQueue(){
        return new Queue(EMAIL_ACCOUNT_QUEUE,true,false,false,null);
    }


    @Bean
    public Binding  smsBinding(@Qualifier("accountExchange") CustomExchange customExchange,
                               @Qualifier("smsQueue") Queue smsQueue){
        return BindingBuilder.bind(smsQueue).to(customExchange).with(SMS_ROUTING_KEY).noargs();
    }

    @Bean
    public Binding emailBinding(@Qualifier("accountExchange") CustomExchange customExchange,
                                @Qualifier("emailQueue") Queue emailQueue){
        return BindingBuilder.bind(emailQueue).to(customExchange).with(EMAIL_ROUTING_KEY).noargs();
    }


    //    死信队列
    @Bean("deadExchange")
    public DirectExchange deadExchange(){
        return new DirectExchange(DEAD_ACCOUNT_EXCHANGE,true,false,null);
    }

    @Bean("deadQueue")
    public Queue deadQueue(){
        return new Queue(DEAD_ACCOUNT_QUEUE,true,false,false,null);
    }

    @Bean
    public Binding deadBinging(@Qualifier("deadExchange") DirectExchange directExchange,
                               @Qualifier("deadQueue") Queue deadQueue){
        return BindingBuilder.bind(deadQueue).to(directExchange).with(DEAD_ROUTING_KEY);
    }
}
