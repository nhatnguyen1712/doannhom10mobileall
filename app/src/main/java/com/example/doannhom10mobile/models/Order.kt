package com.example.doannhom10mobile.models

data class Order(
    var id: Int = 0,
    var orderCode: String = "",                // Mã đơn thuốc
    var patientName: String = "",              // Tên bệnh nhân
    var patientAge: Int = 0,                   // Tuổi
    var patientGender: String = "",             // Giới tính
    var patientAddress: String = "",            // Địa chỉ
    var patientPhone: String = "",              // Số điện thoại
    var diagnosis: String = "",                 // Chuẩn đoán bệnh
    var date: String = "",                      // Ngày khám
    var doctorName: String = "",                // Tên bác sĩ
    var totalAmount: Double = 0.0,              // Tổng tiền
    var status: String = "Chưa thanh toán",      // Trạng thái
    var items: MutableList<OrderItem> = mutableListOf()
)

data class OrderItem(
    var drugId: Int = 0,
    var drugName: String = "",
    var quantity: Int = 0,
    var price: Double = 0.0,
    var dosage: String = "",                     // Liều dùng (VD: Uống 2 viên/ngày)
    var total: Double = 0.0
)