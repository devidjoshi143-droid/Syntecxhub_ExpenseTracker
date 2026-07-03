package com.devid.expensetracker.ui.addexpense

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devid.expensetracker.R
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast

import com.devid.expensetracker.database.DatabaseHelper
import com.devid.expensetracker.model.Expense

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class AddExpenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
        val etAmount = findViewById<EditText>(R.id.etAmount)

        val etCategory = findViewById<EditText>(R.id.etCategory)

        val etNote = findViewById<EditText>(R.id.etNote)

        val rbIncome = findViewById<RadioButton>(R.id.rbIncome)

        val btnSave = findViewById<Button>(R.id.btnSave)

        val databaseHelper = DatabaseHelper(this)
        // Check if editing an existing transaction
        val expenseId = intent.getIntExtra("id", -1)

        if (expenseId != -1) {

            etAmount.setText(intent.getDoubleExtra("amount", 0.0).toString())

            etCategory.setText(intent.getStringExtra("category"))

            etNote.setText(intent.getStringExtra("note"))

            val type = intent.getStringExtra("type")

            if (type == "Income") {
                rbIncome.isChecked = true
            }

            btnSave.text = "Update Transaction"
        }
        btnSave.setOnClickListener {

            val amountText = etAmount.text.toString()

            val category = etCategory.text.toString()

            val note = etNote.text.toString()

            if (amountText.isEmpty() || category.isEmpty()) {

                Toast.makeText(
                    this,
                    "Fill all required fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }
                val amount = amountText.toDouble()

                val type = if (rbIncome.isChecked) {
                    "Income"
                } else {
                    "Expense"
                }

            val date = if (expenseId == -1) {

                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date())

            } else {

                intent.getStringExtra("date") ?: ""

            }

                val expense = Expense(
                    amount = amount,
                    category = category,
                    type = type,
                    note = note,
                    date = date
                )

            val success = if (expenseId == -1) {

                databaseHelper.insertExpense(expense)

            } else {

                databaseHelper.updateExpense(

                    expense.copy(id = expenseId)

                )

            }
                if (success) {

                    Toast.makeText(
                        this,
                        "Transaction Saved",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to Save",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }
}
