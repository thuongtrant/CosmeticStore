package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.config.MoMoConfig;
import com.ttt.CosmeticStore.dto.request.MoMoRequest;
import com.ttt.CosmeticStore.dto.response.MoMoResponse;
import com.ttt.CosmeticStore.exception.OrderException;
import com.ttt.CosmeticStore.exception.OrderProcessingException;
import com.ttt.CosmeticStore.service.MoMoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoMoServiceImpl implements MoMoService {

    @Autowired
    private MoMoConfig moMoConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public MoMoResponse createPayment(String orderNumber, Long amount, String orderInfo) {
        return createPayment(orderNumber, amount, orderInfo, "captureWallet");
    }

    @Override
    public MoMoResponse createPayment(String orderNumber, Long amount, String orderInfo, String requestType) {
        try {
            String requestId = UUID.randomUUID().toString();
            String orderId = orderNumber;
            String extraData = "";
            String lang = "vi";

            // Tạo raw signature
            String rawHash = "accessKey=" + moMoConfig.getAccessKey() +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + moMoConfig.getNotifyUrl() +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + moMoConfig.getPartnerCode() +
                    "&redirectUrl=" + moMoConfig.getReturnUrl() +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = generateSignature(rawHash);

            MoMoRequest request = MoMoRequest.builder()
                    .partnerCode(moMoConfig.getPartnerCode())
                    .requestId(requestId)
                    .amount(amount)
                    .orderId(orderId)
                    .orderInfo(orderInfo)
                    .redirectUrl(moMoConfig.getReturnUrl())
                    .ipnUrl(moMoConfig.getNotifyUrl())
                    .requestType(requestType)
                    .extraData(extraData)
                    .lang(lang)
                    .signature(signature)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<MoMoRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<MoMoResponse> response = restTemplate.postForEntity(
                    moMoConfig.getEndpoint(), entity, MoMoResponse.class);


            MoMoResponse momoResponse = response.getBody();

            if (momoResponse != null) {
                // Check if request failed
                if (momoResponse.getResultCode() != null && momoResponse.getResultCode() != 0) {
                    throw new OrderException("MoMo error: " + momoResponse.getMessage());
                }

                // For QR payment, check if we have QR code URL
                if ("captureWallet".equals(requestType) && momoResponse.getQrCodeUrl() == null && momoResponse.getPayUrl() == null) {
                    throw new OrderProcessingException("MoMo không trả về link thanh toán");
                }
            } else {
                throw new OrderProcessingException("Không nhận được phản hồi từ MoMo");
            }

            return momoResponse;

        } catch (Exception e) {
            throw new OrderProcessingException("Không thể tạo thanh toán MoMo: " + e.getMessage());
        }
    }

    @Override
    public boolean verifySignature(String signature, String rawData) {
        try {
            String generatedSignature = generateSignature(rawData);
            boolean isValid = signature.equals(generatedSignature);

            if (!isValid) {
                log.warn("Signature mismatch!");
                log.debug("Expected: {}", generatedSignature);
                log.debug("Received: {}", signature);
                log.debug("Raw data: {}", rawData);
            }

            return isValid;
        } catch (Exception e) {
            throw new OrderProcessingException("Failed to verify MoMo signature", e);
        }
    }

    @Override
    public String generateSignature(String rawData) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    moMoConfig.getSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKey);

            byte[] hash = hmacSha256.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            log.error("Error generating MoMo signature: ", e);
            throw new OrderProcessingException("Không thể tạo chữ ký MoMo");
        }
    }
}