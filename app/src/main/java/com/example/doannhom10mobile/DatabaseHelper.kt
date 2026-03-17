package com.example.doannhom10mobile

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.SimpleDateFormat
import com.example.doannhom10mobile.models.*
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "PharmacyDB"
        private const val DATABASE_VERSION = 3  // Tăng lên 3 vì thay đổi cấu trúc

        // Bảng Users
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_USER_NAME = "name"
        private const val COLUMN_USER_EMAIL = "email"
        private const val COLUMN_USER_PASSWORD = "password"
        private const val COLUMN_USER_ROLE = "role"

        // Bảng Drugs
        private const val TABLE_DRUGS = "drugs"
        private const val COLUMN_DRUG_ID = "drug_id"
        private const val COLUMN_DRUG_NAME = "name"
        private const val COLUMN_DRUG_PRICE_SELL = "price_sell"
        private const val COLUMN_DRUG_QUANTITY = "quantity"
        private const val COLUMN_DRUG_EXPIRY = "expiry_date"

        // Bảng Orders - Cập nhật thêm các trường mới
        private const val TABLE_ORDERS = "orders"
        private const val COLUMN_ORDER_ID = "order_id"
        private const val COLUMN_ORDER_CODE = "order_code"
        private const val COLUMN_PATIENT_NAME = "patient_name"
        private const val COLUMN_PATIENT_AGE = "patient_age"
        private const val COLUMN_PATIENT_GENDER = "patient_gender"
        private const val COLUMN_PATIENT_ADDRESS = "patient_address"
        private const val COLUMN_PATIENT_PHONE = "patient_phone"
        private const val COLUMN_DIAGNOSIS = "diagnosis"
        private const val COLUMN_ORDER_DATE = "order_date"
        private const val COLUMN_DOCTOR_NAME = "doctor_name"
        private const val COLUMN_ORDER_TOTAL = "total_amount"
        private const val COLUMN_ORDER_STATUS = "status"

        // Bảng Order Details - Cập nhật thêm dosage
        private const val TABLE_ORDER_DETAILS = "order_details"
        private const val COLUMN_OD_ORDER_ID = "order_id"
        private const val COLUMN_OD_DRUG_ID = "drug_id"
        private const val COLUMN_OD_DRUG_NAME = "drug_name"
        private const val COLUMN_OD_QUANTITY = "quantity"
        private const val COLUMN_OD_PRICE = "price"
        private const val COLUMN_OD_DOSAGE = "dosage"
        private const val COLUMN_OD_TOTAL = "total"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tạo bảng Users
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_NAME TEXT NOT NULL,
                $COLUMN_USER_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_USER_PASSWORD TEXT NOT NULL,
                $COLUMN_USER_ROLE TEXT DEFAULT 'Nhân viên'
            )
        """.trimIndent()
        db.execSQL(createUsersTable)

        // Tạo bảng Drugs
        val createDrugsTable = """
            CREATE TABLE $TABLE_DRUGS (
                $COLUMN_DRUG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DRUG_NAME TEXT NOT NULL,
                $COLUMN_DRUG_PRICE_SELL REAL,
                $COLUMN_DRUG_QUANTITY INTEGER DEFAULT 0,
                $COLUMN_DRUG_EXPIRY TEXT
            )
        """.trimIndent()
        db.execSQL(createDrugsTable)

        // Tạo bảng Orders (cập nhật)
        val createOrdersTable = """
            CREATE TABLE $TABLE_ORDERS (
                $COLUMN_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ORDER_CODE TEXT UNIQUE NOT NULL,
                $COLUMN_PATIENT_NAME TEXT NOT NULL,
                $COLUMN_PATIENT_AGE INTEGER,
                $COLUMN_PATIENT_GENDER TEXT,
                $COLUMN_PATIENT_ADDRESS TEXT,
                $COLUMN_PATIENT_PHONE TEXT,
                $COLUMN_DIAGNOSIS TEXT,
                $COLUMN_ORDER_DATE TEXT NOT NULL,
                $COLUMN_DOCTOR_NAME TEXT,
                $COLUMN_ORDER_TOTAL REAL,
                $COLUMN_ORDER_STATUS TEXT DEFAULT 'Chưa thanh toán'
            )
        """.trimIndent()
        db.execSQL(createOrdersTable)

        // Tạo bảng Order Details (cập nhật)
        val createOrderDetailsTable = """
            CREATE TABLE $TABLE_ORDER_DETAILS (
                $COLUMN_OD_ORDER_ID INTEGER,
                $COLUMN_OD_DRUG_ID INTEGER,
                $COLUMN_OD_DRUG_NAME TEXT,
                $COLUMN_OD_QUANTITY INTEGER,
                $COLUMN_OD_PRICE REAL,
                $COLUMN_OD_DOSAGE TEXT,
                $COLUMN_OD_TOTAL REAL,
                PRIMARY KEY ($COLUMN_OD_ORDER_ID, $COLUMN_OD_DRUG_ID),
                FOREIGN KEY ($COLUMN_OD_ORDER_ID) REFERENCES $TABLE_ORDERS($COLUMN_ORDER_ID),
                FOREIGN KEY ($COLUMN_OD_DRUG_ID) REFERENCES $TABLE_DRUGS($COLUMN_DRUG_ID)
            )
        """.trimIndent()
        db.execSQL(createOrderDetailsTable)

        // Thêm dữ liệu mẫu
        insertSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_DETAILS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DRUGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun insertSampleData(db: SQLiteDatabase) {
        // Thêm tài khoản mẫu
        val users = listOf(
            arrayOf("Quản Lý", "admin@gmail.com", "123456", "Admin"),
            arrayOf("Nguyễn Văn A", "nhanvien@gmail.com", "123456", "Nhân viên"),
            arrayOf("Bác sĩ Nguyễn Văn A", "bacsi@gmail.com", "123456", "Bác sĩ")
        )

        for (user in users) {
            val userValues = ContentValues().apply {
                put(COLUMN_USER_NAME, user[0] as String)
                put(COLUMN_USER_EMAIL, user[1] as String)
                put(COLUMN_USER_PASSWORD, user[2] as String)
                put(COLUMN_USER_ROLE, user[3] as String)
            }
            db.insert(TABLE_USERS, null, userValues)
        }

        // Thêm thuốc mẫu
        val drugs = listOf(
            arrayOf("Paracetamol 500mg", 20000.0, 120, "12/2025"),
            arrayOf("Aspirin 80mg", 15000.0, 80, "08/2025"),
            arrayOf("Vitamin C 1000mg", 25000.0, 50, "10/2025"),
            arrayOf("Amoxicillin 500mg", 35000.0, 30, "06/2025"),
            arrayOf("Omeprazole 20mg", 28000.0, 45, "09/2025"),
            arrayOf("Augmentin 625mg", 45000.0, 25, "07/2025"),
            arrayOf("Cetirizine 10mg", 5000.0, 100, "11/2025"),
            arrayOf("Bisolvon 8mg", 12000.0, 60, "04/2025")
        )

        for (drug in drugs) {
            val drugValues = ContentValues().apply {
                put(COLUMN_DRUG_NAME, drug[0] as String)
                put(COLUMN_DRUG_PRICE_SELL, drug[1] as Double)
                put(COLUMN_DRUG_QUANTITY, drug[2] as Int)
                put(COLUMN_DRUG_EXPIRY, drug[3] as String)
            }
            db.insert(TABLE_DRUGS, null, drugValues)
        }
    }

    // ========== USER METHODS ==========
    fun registerUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, user.name)
            put(COLUMN_USER_EMAIL, user.email)
            put(COLUMN_USER_PASSWORD, user.password)
            put(COLUMN_USER_ROLE, user.role)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun checkLogin(email: String, password: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun isEmailExist(email: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // ========== DRUG METHODS ==========
    fun getAllDrugs(): List<Drug> {
        val drugs = mutableListOf<Drug>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_DRUGS ORDER BY $COLUMN_DRUG_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val drug = Drug(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DRUG_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRUG_NAME)),
                priceSell = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DRUG_PRICE_SELL)),
                quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DRUG_QUANTITY)),
                expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRUG_EXPIRY))
            )
            drugs.add(drug)
        }
        cursor.close()
        return drugs
    }

    fun getDrugById(id: Int): Drug? {
        val db = readableDatabase
        val cursor = db.query(TABLE_DRUGS, null, "$COLUMN_DRUG_ID = ?", arrayOf(id.toString()), null, null, null)

        return if (cursor.moveToFirst()) {
            val drug = Drug(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DRUG_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRUG_NAME)),
                priceSell = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DRUG_PRICE_SELL)),
                quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DRUG_QUANTITY)),
                expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRUG_EXPIRY))
            )
            cursor.close()
            drug
        } else {
            cursor.close()
            null
        }
    }

    // THÊM HÀM NÀY - Cập nhật tồn kho
    fun updateDrugQuantity(drugId: Int, quantitySold: Int): Boolean {
        val db = writableDatabase
        val drug = getDrugById(drugId)
        if (drug != null) {
            val newQuantity = drug.quantity - quantitySold
            if (newQuantity >= 0) {
                val values = ContentValues().apply {
                    put(COLUMN_DRUG_QUANTITY, newQuantity)
                }
                return db.update(TABLE_DRUGS, values, "$COLUMN_DRUG_ID = ?", arrayOf(drugId.toString())) > 0
            }
        }
        return false
    }

    // ========== ORDER METHODS ==========
    fun createOrder(order: Order): Long {
        val db = writableDatabase

        val orderValues = ContentValues().apply {
            put(COLUMN_ORDER_CODE, order.orderCode)
            put(COLUMN_PATIENT_NAME, order.patientName)
            put(COLUMN_PATIENT_AGE, order.patientAge)
            put(COLUMN_PATIENT_GENDER, order.patientGender)
            put(COLUMN_PATIENT_ADDRESS, order.patientAddress)
            put(COLUMN_PATIENT_PHONE, order.patientPhone)
            put(COLUMN_DIAGNOSIS, order.diagnosis)
            put(COLUMN_ORDER_DATE, order.date)
            put(COLUMN_DOCTOR_NAME, order.doctorName)
            put(COLUMN_ORDER_TOTAL, order.totalAmount)
            put(COLUMN_ORDER_STATUS, order.status)
        }

        val orderId = db.insert(TABLE_ORDERS, null, orderValues)

        if (orderId != -1L) {
            for (item in order.items) {
                val itemValues = ContentValues().apply {
                    put(COLUMN_OD_ORDER_ID, orderId)
                    put(COLUMN_OD_DRUG_ID, item.drugId)
                    put(COLUMN_OD_DRUG_NAME, item.drugName)
                    put(COLUMN_OD_QUANTITY, item.quantity)
                    put(COLUMN_OD_PRICE, item.price)
                    put(COLUMN_OD_DOSAGE, item.dosage)
                    put(COLUMN_OD_TOTAL, item.total)
                }
                db.insert(TABLE_ORDER_DETAILS, null, itemValues)

                // Cập nhật tồn kho - gọi hàm đã thêm
                updateDrugQuantity(item.drugId, item.quantity)
            }
        }

        return orderId
    }

    fun getAllOrders(): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_ORDERS ORDER BY $COLUMN_ORDER_DATE DESC"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val order = Order(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)),
                orderCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_CODE)),
                patientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_NAME)),
                patientAge = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_AGE)),
                patientGender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_GENDER)),
                patientAddress = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_ADDRESS)),
                patientPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_PHONE)),
                diagnosis = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIAGNOSIS)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE)),
                doctorName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_NAME)),
                totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS))
            )
            orders.add(order)
        }
        cursor.close()
        return orders
    }

    fun getOrderById(orderId: Int): Order? {
        val db = readableDatabase
        val cursor = db.query(TABLE_ORDERS, null, "$COLUMN_ORDER_ID = ?", arrayOf(orderId.toString()), null, null, null)

        return if (cursor.moveToFirst()) {
            val order = Order(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)),
                orderCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_CODE)),
                patientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_NAME)),
                patientAge = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_AGE)),
                patientGender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_GENDER)),
                patientAddress = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_ADDRESS)),
                patientPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_PHONE)),
                diagnosis = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIAGNOSIS)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE)),
                doctorName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_NAME)),
                totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS))
            )
            cursor.close()
            order.items = getOrderItems(order.id).toMutableList()
            order
        } else {
            cursor.close()
            null
        }
    }

    fun getOrderItems(orderId: Int): List<OrderItem> {
        val items = mutableListOf<OrderItem>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_ORDER_DETAILS WHERE $COLUMN_OD_ORDER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(orderId.toString()))

        while (cursor.moveToNext()) {
            val item = OrderItem(
                drugId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_OD_DRUG_ID)),
                drugName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OD_DRUG_NAME)),
                quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_OD_QUANTITY)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_OD_PRICE)),
                dosage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OD_DOSAGE)),
                total = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_OD_TOTAL))
            )
            items.add(item)
        }
        cursor.close()
        return items
    }

    // ========== STATISTICS ==========
    fun getTodayRevenue(): Double {
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val db = readableDatabase
        val query = "SELECT SUM($COLUMN_ORDER_TOTAL) FROM $TABLE_ORDERS WHERE $COLUMN_ORDER_DATE = ?"
        val cursor = db.rawQuery(query, arrayOf(today))

        return if (cursor.moveToFirst()) {
            cursor.getDouble(0)
        } else {
            0.0
        }
    }

    fun getTodayOrderCount(): Int {
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_ORDERS WHERE $COLUMN_ORDER_DATE = ?"
        val cursor = db.rawQuery(query, arrayOf(today))

        return if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            0
        }
    }
}