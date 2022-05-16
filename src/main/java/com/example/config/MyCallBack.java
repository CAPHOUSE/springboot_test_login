package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {


    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 由于ConfirmCallback是RabbitTemplate类的内部接口，需要将当前类注入ConfirmCallback
     */
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }


    /**
     * 交换机确认回调方法
     * @param correlationData: 报错回调的ID以及相关信息
     * @param ack   交换机是否收到消息
     * @param cause 失败的原因，成功为null
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack){ //消息成功发送到交换机
            log.info("交换机以及收到消息ID为:" + correlationData.getId());
        }else {
            log.info("交换机未收到消息ID,原因为{}",cause);
        }
    }


    /**
     * 当交换机路由不到队列时，消息回退
     * @param message   消息
     * @param replyCode 回退的状态
     * @param replyText 回退的原因
     * @param exchange  交换机
     * @param routingKey    路由key
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息{},被交换机{}给退回了,退回的原因{},路由key是{}",new String(message.getBody()),exchange,replyText,routingKey);
    }
}
