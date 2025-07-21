package com.example.shoestore.data

// PayOSRequest.kt
data class PayOSRequest(
    val orderCode: String,
    val amount: Long,
    val description: String,
    val returnUrl: String,
    val cancelUrl: String
)

// PayOSResponse.kt
data class PayOSResponse(
    val checkoutUrl: String,
    val qrCode: String,
    val orderCode: String
)