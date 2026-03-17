package com.example.doannhom10mobile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doannhom10mobile.databinding.ActivityCreateOrderBinding
import com.example.doannhom10mobile.models.Drug
import com.example.doannhom10mobile.models.Order
import com.example.doannhom10mobile.models.OrderItem
import java.text.SimpleDateFormat
import java.util.*

class CreateOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateOrderBinding
    private lateinit var dbHelper: DatabaseHelper
    private val selectedDrugs = mutableListOf<OrderItem>()
    private lateinit var drugAdapter: SelectedDrugAdapter
    private var drugsList: List<Drug> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        drugsList = dbHelper.getAllDrugs()

        initViews()
        setupListeners()
        updateTotal()
    }

    private fun initViews() {
        // Set ngày hiện tại
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvDate.text = "Ngày: ${sdf.format(Date())}"

        // Set tên bác sĩ mặc định
        binding.etDoctorName.setText("Bác sĩ Nguyễn Văn A")

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        drugAdapter = SelectedDrugAdapter(
            selectedDrugs,
            onEditClick = { position -> showEditDrugDialog(position) },
            onDeleteClick = { position -> removeDrug(position) }
        )
        binding.recyclerView.adapter = drugAdapter
    }

    private fun setupListeners() {
        binding.btnAddDrug.setOnClickListener {
            showDrugSelectionDialog()
        }

        binding.btnSave.setOnClickListener {
            saveOrder()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun showDrugSelectionDialog() {
        val drugs = drugsList.filter { it.quantity > 0 }

        if (drugs.isEmpty()) {
            Toast.makeText(this, "Không còn thuốc trong kho!", Toast.LENGTH_SHORT).show()
            return
        }

        val drugNames = drugs.map { "${it.name} - Còn: ${it.quantity} - Giá: ${it.priceSell}đ" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Chọn thuốc")
            .setItems(drugNames) { _, which ->
                val selectedDrug = drugs[which]
                showAddDrugDialog(selectedDrug)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showAddDrugDialog(drug: Drug) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_drug, null)
        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
        val etDosage = dialogView.findViewById<EditText>(R.id.etDosage)
        val tvDrugInfo = dialogView.findViewById<TextView>(R.id.tvDrugInfo)

        tvDrugInfo.text = "${drug.name}\nGiá: ${drug.priceSell}đ - Còn: ${drug.quantity}"

        AlertDialog.Builder(this)
            .setTitle("Thêm thuốc")
            .setView(dialogView)
            .setPositiveButton("Thêm") { _, _ ->
                val quantityStr = etQuantity.text.toString()
                val dosage = etDosage.text.toString()

                if (quantityStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val quantity = quantityStr.toInt()
                if (quantity <= 0 || quantity > drug.quantity) {
                    Toast.makeText(this, "Số lượng không hợp lệ!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (dosage.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập liều dùng", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val existingItem = selectedDrugs.find { it.drugId == drug.id }
                if (existingItem != null) {
                    val newQuantity = existingItem.quantity + quantity
                    if (newQuantity <= drug.quantity) {
                        existingItem.quantity = newQuantity
                        existingItem.dosage = dosage
                        existingItem.total = existingItem.quantity * existingItem.price
                    } else {
                        Toast.makeText(this, "Số lượng vượt quá tồn kho!", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                } else {
                    selectedDrugs.add(
                        OrderItem(
                            drugId = drug.id,
                            drugName = drug.name,
                            quantity = quantity,
                            price = drug.priceSell,
                            dosage = dosage,
                            total = quantity * drug.priceSell
                        )
                    )
                }

                drugAdapter.notifyDataSetChanged()
                updateTotal()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showEditDrugDialog(position: Int) {
        val item = selectedDrugs[position]
        val drug = drugsList.find { it.id == item.drugId }

        if (drug == null) {
            removeDrug(position)
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_drug, null)
        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
        val etDosage = dialogView.findViewById<EditText>(R.id.etDosage)
        val tvDrugInfo = dialogView.findViewById<TextView>(R.id.tvDrugInfo)

        tvDrugInfo.text = "${drug.name}\nGiá: ${drug.priceSell}đ - Còn: ${drug.quantity}"
        etQuantity.setText(item.quantity.toString())
        etDosage.setText(item.dosage)

        AlertDialog.Builder(this)
            .setTitle("Sửa thuốc")
            .setView(dialogView)
            .setPositiveButton("Cập nhật") { _, _ ->
                val quantityStr = etQuantity.text.toString()
                val dosage = etDosage.text.toString()

                if (quantityStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val quantity = quantityStr.toInt()
                if (quantity <= 0 || quantity > drug.quantity) {
                    Toast.makeText(this, "Số lượng không hợp lệ!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (dosage.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập liều dùng", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                item.quantity = quantity
                item.dosage = dosage
                item.total = quantity * item.price

                drugAdapter.notifyDataSetChanged()
                updateTotal()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun removeDrug(position: Int) {
        selectedDrugs.removeAt(position)
        drugAdapter.notifyDataSetChanged()
        updateTotal()
    }

    private fun updateTotal() {
        val total = selectedDrugs.sumOf { it.total }
        binding.tvTotal.text = String.format("Tổng tiền: %,.0f đ", total)
    }

    private fun saveOrder() {
        // Validate thông tin bệnh nhân
        val patientName = binding.etPatientName.text.toString().trim()
        val patientAgeStr = binding.etPatientAge.text.toString().trim()
        val patientGender = if (binding.radioMale.isChecked) "Nam" else "Nữ"
        val patientAddress = binding.etPatientAddress.text.toString().trim()
        val patientPhone = binding.etPatientPhone.text.toString().trim()
        val diagnosis = binding.etDiagnosis.text.toString().trim()
        val doctorName = binding.etDoctorName.text.toString().trim()

        if (patientName.isEmpty()) {
            binding.etPatientName.error = "Vui lòng nhập tên bệnh nhân"
            return
        }

        if (patientAgeStr.isEmpty()) {
            binding.etPatientAge.error = "Vui lòng nhập tuổi"
            return
        }

        val patientAge = patientAgeStr.toIntOrNull() ?: 0
        if (patientAge <= 0 || patientAge > 150) {
            binding.etPatientAge.error = "Tuổi không hợp lệ"
            return
        }

        if (diagnosis.isEmpty()) {
            binding.etDiagnosis.error = "Vui lòng nhập chuẩn đoán"
            return
        }

        if (selectedDrugs.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn thuốc", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo mã đơn thuốc
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val orderCode = "ĐT${sdf.format(Date())}"

        // Tạo đơn thuốc
        val order = Order(
            orderCode = orderCode,
            patientName = patientName,
            patientAge = patientAge,
            patientGender = patientGender,
            patientAddress = patientAddress,
            patientPhone = patientPhone,
            diagnosis = diagnosis,
            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            doctorName = doctorName,
            totalAmount = selectedDrugs.sumOf { it.total },
            status = "Chưa thanh toán",
            items = selectedDrugs.toMutableList()
        )

        // Lưu vào database
        val result = dbHelper.createOrder(order)

        if (result != -1L) {
            Toast.makeText(this, "Tạo đơn thuốc thành công!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Tạo đơn thuốc thất bại!", Toast.LENGTH_SHORT).show()
        }
    }
}

// Adapter cho danh sách thuốc đã chọn
class SelectedDrugAdapter(
    private val items: List<OrderItem>,
    private val onEditClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<SelectedDrugAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDrugName: TextView = itemView.findViewById(R.id.tvDrugName)
        val tvDosage: TextView = itemView.findViewById(R.id.tvDosage)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_drug, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvDrugName.text = item.drugName
        holder.tvDosage.text = "Liều dùng: ${item.dosage}"
        holder.tvQuantity.text = "Số lượng: ${item.quantity}"
        holder.tvTotal.text = String.format("Thành tiền: %,.0f đ", item.total)

        holder.btnEdit.setOnClickListener { onEditClick(position) }
        holder.btnDelete.setOnClickListener { onDeleteClick(position) }
    }

    override fun getItemCount() = items.size
}