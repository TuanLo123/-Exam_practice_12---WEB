package com.example.project.service.impl;

import com.example.project.service.EmailService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class EmailServiceImpl implements EmailService {
    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.api.url}")
    private String brevoApiUrl;

    @Override
    @Async
    public void sendOtpEmail(String email, String otpCode) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            String htmlContent = """
                <div style="background-color: #00b3ff; padding: 40px 15px; font-family: 'Segoe UI', Arial, sans-serif; min-height: 100%;">
                    <div style="max-width: 500px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.15);">
                        <div style="background-color: #00b3ff; padding: 30px 20px; text-align: center; border-bottom: 1px solid rgba(255,255,255,0.2);">
                            <h2 style="color: #ffffff; margin: 0; font-size: 26px; font-weight: bold; letter-spacing: 1px;">
                                Mã xác thực OTP S15
                            </h2>
                        </div>

                        <div style="padding: 30px; color: #333333; line-height: 1.6; font-size: 16px;">
                            <p style="margin-top: 0; font-weight: bold;">Xin chào,</p>
                            <p>Chúng tôi nhận được yêu cầu khôi phục mật khẩu từ bạn. Vui lòng sử dụng mã OTP dưới đây để xác thực:</p>
                            
                            <div style="text-align: center; margin: 30px 0;">
                                <div style="display: inline-block; background-color: #e6f7ff; border: 2px dashed #00b3ff; padding: 15px 35px; border-radius: 8px;">
                                    <span style="font-size: 32px; font-weight: bold; color: #00b3ff; letter-spacing: 8px;">
                                        %s
                                    </span>
                                </div>
                            </div>
                            
                            <p style="font-size: 14px; color: #666666; margin-bottom: 25px;">
                                * Mã này có hiệu lực trong vòng <b>5 phút</b>. Vì lý do bảo mật, tuyệt đối không chia sẻ mã này cho bất kỳ ai.
                                Nếu bạn không có yêu cầu này vui lòng bỏ qua email này.
                            </p>
                            
                            <hr style="border: 0; border-top: 1px solid #eeeeee; margin-bottom: 20px;" />
                            
                            <p style="font-size: 12px; color: #999999; margin: 0; text-align: center;">
                                Đây là email tự động từ hệ thống. Vui lòng không trả lời email này.
                            </p>
                        </div>
                        
                    </div>
                </div>
            """.formatted(otpCode);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of("name", "Hệ Thống", "email", "buiductuan234@gmail.com"));
            body.put("to", Collections.singletonList(Map.of("email", email)));
            body.put("subject", "Mã xác thực khôi phục mật khẩu");
            body.put("htmlContent", htmlContent);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            restTemplate.postForEntity(brevoApiUrl, requestEntity, String.class);
            System.out.println("-> [Brevo] Gửi OTP thành công tới: " + email);
        } catch (Exception e) {
            System.err.println("-> Lỗi gửi email: "+ e.getMessage());
        }
    }
}
