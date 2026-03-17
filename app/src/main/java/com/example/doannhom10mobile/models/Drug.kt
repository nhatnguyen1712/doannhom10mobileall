package com.example.doannhom10mobile.models

data class Drug(
    var id: Int = 0,
    var name: String = "",
    var priceSell: Double = 0.0,
    var quantity: Int = 0,
    var expiryDate: String = ""
)