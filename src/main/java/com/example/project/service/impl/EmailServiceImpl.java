package com.example.project.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.project.entity.Otp;
import com.example.project.repository.OtpRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.EmailService;
import jakarta.annotation.PostConstruct;

@Service
public class EmailServiceImpl implements EmailService {

        private final OtpRepository otpRepository;
        private final UserRepository userRepository;
        private final RestTemplate restTemplate;

        @Value("${brevo.api.key}")
        private String brevoApiKey;

        @Value("${brevo.api.url}")
        private String brevoApiUrl;

        public EmailServiceImpl(OtpRepository otpRepository, UserRepository userRepository) {
                this.otpRepository = otpRepository;
                this.userRepository = userRepository;
                this.restTemplate = new RestTemplate();
        }

        @Override
        public void requestForgotPassword(String email) {

                userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

                String otpCode = generateOtp();

                LocalDateTime expiredTime = LocalDateTime.now().plusMinutes(5);

                Otp otp = otpRepository.findByEmail(email)
                                .orElse(new Otp());

                otp.setEmail(email);
                otp.setOtpCode(otpCode);
                otp.setExpiredTime(expiredTime);

                otpRepository.save(otp);

                sendOtpEmail(email, otpCode);
        }

        @Async
        @Override
        public void sendOtpEmail(String email, String otpCode) {

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("api-key", brevoApiKey);

                        String htmlContent = """
                                        <html>
                                        <body style="font-family: Arial, sans-serif; color: #333; line-height: 1.6;">
                                          <div style="max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 10px; overflow: hidden;">

                                            <div style="background-color: #2196F3; padding: 20px; text-align: center;">
                                              <h1 style="color: white; margin: 0; font-size: 24px;">
                                                Ôn thi tốt nghiệp THPT S15
                                              </h1>
                                            </div>

                                            <div style="padding: 30px;">
                                              <h2 style="color: #2196F3; margin-top: 0;">
                                                Đổi mật khẩu tài khoản
                                              </h2>

                                              <p>Chào bạn,</p>

                                              <p>
                                                Đây là mã xác thực <b>OTP</b> để đổi mật khẩu cho tài khoản của bạn.
                                                Vui lòng <strong>không chia sẻ mã này cho bất kỳ ai</strong>.
                                              </p>

                                              <div style="background-color: #f5f5f5; padding: 20px;
                                                          text-align: center; border-radius: 8px; margin: 25px 0;">

                                                <span style="font-size: 14px; color: #666; display: block; margin-bottom: 10px;">
                                                  Mã xác thực của bạn là:
                                                </span>

                                                <span style="font-size: 32px; font-weight: bold;
                                                             color: #2196F3; letter-spacing: 5px;">
                                                  %s
                                                </span>

                                              </div>

                                              <p style="font-size: 13px; color: #888;">
                                                Mã này sẽ hết hạn sau 5 phút. Nếu bạn không có nhu cầu vui lòng bỏ qua email này.
                                              </p>
                                            </div>

                                            <div style="background-color: #fafafa; padding: 15px;
                                                        text-align: center; border-top: 1px solid #eeeeee;">

                                              <p style="font-size: 12px; color: #aaa; margin: 0;">
                                                © 2026 Nhóm S15
                                              </p>

                                            </div>
                                          </div>
                                        </body>
                                        </html>
                                        """
                                        .formatted(otpCode);

                        Map<String, Object> body = new HashMap<>();

                        body.put(
                                        "sender",
                                        Map.of(
                                                        "name", "Hệ Thống",
                                                        "email", "buiductuan234@gmail.com"));

                        body.put(
                                        "to",
                                        Collections.singletonList(
                                                        Map.of("email", email)));

                        body.put(
                                        "subject",
                                        "Mã xác thực khôi phục mật khẩu");

                        body.put("htmlContent", htmlContent);

                        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                        restTemplate.postForEntity(
                                        brevoApiUrl,
                                        requestEntity,
                                        String.class);

                } catch (Exception e) {
                        System.err.println(
                                        "Lỗi gửi email: " + e.getMessage());
                }
        }

        @Override
        public boolean verifyOtp(String email, String otpCode) {
                Otp otp = otpRepository.findByEmail(email).orElseThrow(
                                () -> new RuntimeException("Không tìm thấy OTP"));

                if (otp.getExpiredTime().isBefore(LocalDateTime.now())) {
                        throw new RuntimeException("Mã OTP đã hết hạn");
                }

                if (!otp.getOtpCode().equals(otpCode)) {
                        throw new RuntimeException("OTP không chính xác");
                }

                return true;
        }

        private String generateOtp() {
                Random random = new Random();
                int number = random.nextInt(900000) + 100000;

                return String.valueOf(number);
        }

        @PostConstruct
        public void checkBrevoConfig() {
            System.out.println("========== BREVO CONFIG ==========");
            System.out.println("URL: [" + brevoApiUrl + "]");

            if (brevoApiKey == null) {
                System.out.println("API KEY = NULL");
            } else {
                System.out.println("API KEY LENGTH = " + brevoApiKey.length());
                System.out.println("API KEY START = [" +
                        brevoApiKey.substring(0, Math.min(20, brevoApiKey.length())) + "]");
                System.out.println("API KEY END = [" +
                        brevoApiKey.substring(Math.max(0, brevoApiKey.length() - 10)) + "]");

                System.out.println("HAS SPACE = " + brevoApiKey.contains(" "));
                System.out.println("HAS NEWLINE = " +
                        (brevoApiKey.contains("\n") || brevoApiKey.contains("\r")));
            }

            System.out.println("==================================");
        }

        @PostConstruct
        public void testBrevoApi() {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("api-key", brevoApiKey.trim());

                HttpEntity<Void> request = new HttpEntity<>(headers);

                var response = restTemplate.exchange(
                        "https://api.brevo.com/v3/account",
                        org.springframework.http.HttpMethod.GET,
                        request,
                        String.class
                );

                System.out.println("========== BREVO API TEST ==========");
                System.out.println("STATUS: " + response.getStatusCode());
                System.out.println("BODY: " + response.getBody());
                System.out.println("====================================");

            } catch (Exception e) {
                System.out.println("========== BREVO API TEST ERROR ==========");

                if (e instanceof org.springframework.web.client.HttpStatusCodeException ex) {
                    System.out.println("STATUS: " + ex.getStatusCode());
                    System.out.println("BODY: " + ex.getResponseBodyAsString());
                } else {
                    System.out.println("ERROR: " + e.getMessage());
                }

                System.out.println("==========================================");
            }
        }
}