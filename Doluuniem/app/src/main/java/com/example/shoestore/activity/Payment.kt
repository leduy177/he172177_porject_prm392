package com.example.shoestore.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.example.shoestore.Api.CreateOrder
import com.example.shoestore.databinding.ActivityPaymentBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener

class Payment : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private var totalAmount: Int = 0
    private var orderDetail: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        totalAmount = intent.getIntExtra("totalAmount", 0)
        orderDetail = intent.getStringExtra("orderDetail") ?: ""
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        ZaloPaySDK.init(2553, Environment.SANDBOX)


        binding.tvOrderDetail.text = orderDetail
        binding.tvTotal.text = "Tổng tiền: $totalAmount VND"

        val current = java.util.Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("EEEE, dd/MM/yyyy", java.util.Locale("vi"))
        val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale("vi"))

        binding.tvOrderDate.text = dateFormat.format(current.time)
        binding.tvOrderTime.text = "Giờ: ${timeFormat.format(current.time)}"


        binding.btnPay.setOnClickListener {
            val orderApi = CreateOrder()

            try {
                val amountStr = String.format("%d", totalAmount)
                val data: JSONObject = orderApi.createOrder(amountStr)
                val code = data.getString("return_code")
                Toast.makeText(applicationContext, "return_code: $code", Toast.LENGTH_LONG).show()

                if (code == "1") {
                    val token: String = data.getString("zp_trans_token")
                    ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", object : PayOrderListener {
                        override fun onPaymentSucceeded(transactionId: String?, transToken: String?, appTransID: String?) {
                            runOnUiThread {
//                                val intent = Intent(this@Payment, PaymentNotificatioj::class.java)
//                                intent.putExtra("result", "Thanh toán thành công")
//                                startActivity(intent)
//                                finish()
                                savePaymentAndClearCart(transactionId, transToken, appTransID)
                            }
                        }

                        override fun onPaymentCanceled(zpTransToken: String?, appTransID: String?) {
                            runOnUiThread {
                                val intent = Intent(this@Payment, PaymentNotificatioj::class.java)
                                intent.putExtra("result", "Thanh toán Thất bại")
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onPaymentError(zaloPayError: ZaloPayError?, zpTransToken: String?, appTransID: String?) {
                            runOnUiThread {
                                val intent = Intent(this@Payment, PaymentNotificatioj::class.java)
                                intent.putExtra("result", "Thanh toán lỗi")
                                startActivity(intent)
                                finish()
                            }
                        }
                    })
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
    }


    private fun openCustomTab(url: String) {
        try {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        } catch (e: Exception) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }



    private fun savePaymentAndClearCart(transactionId: String?, transToken: String?, appTransID: String?) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

        val paymentData = hashMapOf(
            "userId" to userId,
            "transactionId" to transactionId,
            "transToken" to transToken,
            "appTransID" to appTransID,
            "orderDetail" to orderDetail,
            "totalAmount" to totalAmount,
            "timestamp" to Timestamp.now()
        )

        db.collection("payment")
            .add(paymentData)
            .addOnSuccessListener {
                // Sau khi lưu payment, xóa giỏ hàng của user
                db.collection("cart")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        for (doc in snapshot.documents) {
                            doc.reference.delete()
                        }
                        goToNotification("Thanh toán thành công và giỏ hàng đã được xóa")
                    }
                    .addOnFailureListener {
                        goToNotification("Thanh toán thành công, nhưng xóa giỏ hàng thất bại")
                    }
            }
            .addOnFailureListener {
                goToNotification("Thanh toán thành công, nhưng lưu dữ liệu thất bại")
            }
    }

    private fun goToNotification(message: String) {
        val intent = Intent(this@Payment, PaymentNotificatioj::class.java)
        intent.putExtra("result", message)
        startActivity(intent)
        finish()
    }

}