package com.example.doannhom10mobile

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OrderDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail) // Dùng layout thường, không dùng binding

        try {
            // Nhận dữ liệu từ Intent
            val orderId = intent.getIntExtra("order_id", 0)
            val orderCode = intent.getStringExtra("order_code") ?: "Không có"
            val customerName = intent.getStringExtra("customer_name") ?: "Không có"
            val orderTotal = intent.getDoubleExtra("order_total", 0.0)

            // Hiển thị dữ liệu
            val tvOrderId = findViewById<TextView>(R.id.tvOrderId)
            val tvCustomerName = findViewById<TextView>(R.id.tvCustomerName)
            val tvOrderTotal = findViewById<TextView>(R.id.tvOrderTotal)

            tvOrderId.text = "Mã đơn: $orderCode"
            tvCustomerName.text = "Khách hàng: $customerName"
            tvOrderTotal.text = String.format("Tổng tiền: %,.0f đ", orderTotal)

            // Nút quay lại
            val btnBack = findViewById<Button>(R.id.btnBack)
            btnBack.setOnClickListener {
                finish()
            }

            Toast.makeText(this, "Mở chi tiết đơn hàng #$orderId thành công!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            finish() // Tự động đóng nếu lỗi
        }
    }
}