package com.example.service;

import com.example.utils.MailClient;
import com.example.utils.RandomUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Value("${SecretId}")
    private String secretId;

    @Value("${SecretKey}")
    private String secretKey;

    @Value("${SDKAppID}")
    private String SDKAppID;

    @Value("${templateID}")
    private String templateID;

    @Value("${signName}")
    private String SignName;

    @Autowired
    private MailClient mailClient;

    public void sendEmail(String email) {
        mailClient.sendMail(email,"markdown","注册成功");
    }


    public void sendSms(String mobile) {
        try {
//            定义返回常量
            String code = null;

            Credential cred = new Credential(secretId, secretKey);

            HttpProfile httpProfile = new HttpProfile();

            httpProfile.setReqMethod("POST");
            httpProfile.setConnTimeout(60);
            httpProfile.setEndpoint("sms.tencentcloudapi.com");


            ClientProfile clientProfile = new ClientProfile();

            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);

            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
            SendSmsRequest req = new SendSmsRequest();


            String sdkAppId = SDKAppID;
            req.setSmsSdkAppId(sdkAppId);

            String signName = SignName;
            req.setSignName(signName);

            String senderid = "";
            req.setSenderId(senderid);

            String sessionContext = "xxx";
            req.setSessionContext(sessionContext);

            String extendCode = "";
            req.setExtendCode(extendCode);

            String templateId = templateID;
            req.setTemplateId(templateId);

            String[] phoneNumberSet = {"+86" + mobile};
            req.setPhoneNumberSet(phoneNumberSet);

//            生成验证码，并设置过期时间
            String verification = RandomUtils.getNum();

//            生成随机验证码，
            String[] templateParamSet = {verification, "5"};
            req.setTemplateParamSet(templateParamSet);

            SendSmsResponse res = client.SendSms(req);

            SendStatus[] sendStatusSet = res.getSendStatusSet();
            for (SendStatus sendStatus : sendStatusSet) {
                code = sendStatus.getCode();
            }

//            将验证码存入redis
            redisTemplate.opsForValue().set(mobile, verification, Long.parseLong("5"), TimeUnit.MINUTES);

        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            throw new RuntimeException("发送短信失败");
        }
    }
}
