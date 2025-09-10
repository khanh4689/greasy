package com.gearsy.gearsy.dto;

import lombok.Data;

@Data
public class MomoPaymentResponse {
    private int resultCode;
    private String message;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
}