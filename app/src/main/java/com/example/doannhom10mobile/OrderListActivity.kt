package com.example.doannhom10mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doannhom10mobile.adapters.OrderAdapter
import com.example.doannhom10mobile.databinding.ActivityOrderListBinding


class OrderListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderListBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        initViews()
        loadOrders()
    }

    private fun initViews() {
        // Toolbar
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Floating Action Button
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, CreateOrderActivity::class.java))
        }

        // Search
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterOrders(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterOrders(newText)
                return true
            }
        })

        // Filter buttons
        binding.btnFilterDate.setOnClickListener {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
        }

        binding.btnFilterStatus.setOnClickListener {
            showStatusFilterDialog()
        }
    }

    private fun loadOrders() {
        try {
            // Lấy dữ liệu từ database
            val orders = dbHelper.getAllOrders()

            if (orders.isEmpty()) {
                binding.tvNoData.visibility = android.view.View.VISIBLE
                binding.recyclerView.visibility = android.view.View.GONE
                Toast.makeText(this, "Chưa có đơn thuốc nào", Toast.LENGTH_SHORT).show()
            } else {
                binding.tvNoData.visibility = android.view.View.GONE
                binding.recyclerView.visibility = android.view.View.VISIBLE
            }

            // Tạo adapter với sự kiện click
            val adapter = OrderAdapter(orders) { order ->
                // CHUYỂN TRANG KHI CLICK
                try {
                    val intent = Intent(this, OrderDetailActivity::class.java)
                    intent.putExtra("order_id", order.id)
                    intent.putExtra("order_code", order.orderCode)
                    intent.putExtra("customer_name", order.patientName)
                    intent.putExtra("customer_phone", order.patientPhone)
                    intent.putExtra("order_date", order.date)
                    intent.putExtra("order_total", order.totalAmount)
                    intent.putExtra("order_status", order.status)
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Không thể mở chi tiết: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            binding.recyclerView.adapter = adapter

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterOrders(query: String?) {
        try {
            val allOrders = dbHelper.getAllOrders()
            val filteredOrders = if (query.isNullOrEmpty()) {
                allOrders
            } else {
                allOrders.filter {
                    it.orderCode.contains(query, ignoreCase = true) ||
                            it.patientName.contains(query, ignoreCase = true) ||
                            it.patientPhone.contains(query, ignoreCase = true)
                }
            }

            val adapter = OrderAdapter(filteredOrders) { order ->
                val intent = Intent(this, OrderDetailActivity::class.java)
                intent.putExtra("order_id", order.id)
                intent.putExtra("order_code", order.orderCode)
                intent.putExtra("customer_name", order.patientName)
                intent.putExtra("customer_phone", order.patientPhone)
                intent.putExtra("order_date", order.date)
                intent.putExtra("order_total", order.totalAmount)
                intent.putExtra("order_status", order.status)
                startActivity(intent)
            }
            binding.recyclerView.adapter = adapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showStatusFilterDialog() {
        val statuses = arrayOf("Tất cả", "Đã thanh toán", "Chưa thanh toán", "Đã hủy")

        android.app.AlertDialog.Builder(this)
            .setTitle("Chọn trạng thái")
            .setItems(statuses) { _, which ->
                val selectedStatus = statuses[which]
                try {
                    val allOrders = dbHelper.getAllOrders()
                    val filteredOrders = if (selectedStatus == "Tất cả") {
                        allOrders
                    } else {
                        allOrders.filter { it.status == selectedStatus }
                    }

                    val adapter = OrderAdapter(filteredOrders) { order ->
                        val intent = Intent(this, OrderDetailActivity::class.java)
                        intent.putExtra("order_id", order.id)
                        intent.putExtra("order_code", order.orderCode)
                        intent.putExtra("customer_name", order.patientName)
                        intent.putExtra("customer_phone", order.patientPhone)
                        intent.putExtra("order_date", order.date)
                        intent.putExtra("order_total", order.totalAmount)
                        intent.putExtra("order_status", order.status)
                        startActivity(intent)
                    }
                    binding.recyclerView.adapter = adapter

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }
}