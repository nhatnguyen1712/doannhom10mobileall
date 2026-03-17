package com.example.doannhom10mobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doannhom10mobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = intent.getStringExtra("user_name") ?: "Quản Lý"
        val userEmail = intent.getStringExtra("user_email") ?: ""
        val userRole = intent.getStringExtra("user_role") ?: "Nhân viên"

        // Hiển thị tên người dùng
        val tvWelcome = binding.root.findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = "Xin chào, $userName!"

        setupListeners()
    }

    private fun setupListeners() {
        // Xử lý click menu Bán Hàng - chỗ để làm đơn thuốc
        binding.menuBanHang.setOnClickListener {
            startActivity(Intent(this, OrderListActivity::class.java))
        }

        // Xử lý click menu Quản Lý Thuốc
        binding.menuThuoc.setOnClickListener {
            Toast.makeText(this, "Quản lý thuốc", Toast.LENGTH_SHORT).show()
        }

        // Xử lý click menu Tồn Kho
        binding.menuTonKho.setOnClickListener {
            Toast.makeText(this, "Quản lý tồn kho", Toast.LENGTH_SHORT).show()
        }

        // Xử lý click menu Doanh Thu
        binding.menuDoanhThu.setOnClickListener {
            Toast.makeText(this, "Xem doanh thu", Toast.LENGTH_SHORT).show()
        }

        // Xử lý click menu Thống Kê
        binding.menuThongKe.setOnClickListener {
            Toast.makeText(this, "Thống kê", Toast.LENGTH_SHORT).show()
        }

        // Xử lý click menu Chat AI
        binding.menuChatAI.setOnClickListener {
            Toast.makeText(this, "Chat AI", Toast.LENGTH_SHORT).show()
        }

        // Xử lý click vào card thuốc sắp hết hạn
        binding.expiringContainer.setOnClickListener {
            Toast.makeText(this, "Xem thuốc sắp hết hạn", Toast.LENGTH_SHORT).show()
        }

        // Xử lý click vào card doanh thu
        binding.revenueContainer.setOnClickListener {
            Toast.makeText(this, "Xem chi tiết doanh thu", Toast.LENGTH_SHORT).show()
        }

        // Đăng xuất
        binding.btnLogout.setOnClickListener {
            Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}