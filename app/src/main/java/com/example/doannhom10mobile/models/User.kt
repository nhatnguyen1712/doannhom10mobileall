package com.example.doannhom10mobile.models

data class User(
    var id: Int = 0,
    var name: String,
    var email: String,
    var password: String,
    var role: String = "Nhân viên"
)