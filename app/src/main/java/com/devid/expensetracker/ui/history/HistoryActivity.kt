package com.devid.expensetracker.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devid.expensetracker.R
import com.devid.expensetracker.adapter.ExpenseAdapter
import com.devid.expensetracker.database.DatabaseHelper
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.devid.expensetracker.model.Expense
import android.view.LayoutInflater
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerHistory: RecyclerView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: ExpenseAdapter

    private lateinit var etSearch: TextInputEditText

    private lateinit var btnFilter: com.google.android.material.button.MaterialButton

    private lateinit var btnSort: com.google.android.material.button.MaterialButton
    private lateinit var allExpenses: MutableList<Expense>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        etSearch = findViewById(R.id.etSearch)

        btnFilter = findViewById(R.id.btnFilter)

        btnFilter.setOnClickListener {

            val options = arrayOf(
                "Today",
                "This Week",
                "This Month",
                "All Transactions"
            )

            AlertDialog.Builder(this)
                .setTitle("Filter by Date")
                .setItems(options) { _, which ->

                    when (which) {

                        0 -> filterToday()

                        1 -> filterThisWeek()

                        2 -> filterThisMonth()

                        3 -> loadTransactions()

                    }

                }
                .show()

        }
        btnSort = findViewById(R.id.btnSort)

        btnSort.setOnClickListener {
            showSortDialog()
        }

        recyclerHistory = findViewById(R.id.recyclerHistory)

        databaseHelper = DatabaseHelper(this)

        recyclerHistory.layoutManager =
            LinearLayoutManager(this)

        loadTransactions()

        etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                filterTransactions(s.toString())

            }

            override fun afterTextChanged(s: Editable?) {}

        } )
    }

    private fun loadTransactions() {

        allExpenses = databaseHelper.getAllExpenses()

        adapter = ExpenseAdapter(
            allExpenses,

            {},

            {}

        )

        recyclerHistory.adapter = adapter

    }
    private fun filterTransactions(query: String) {

        val filteredList = allExpenses.filter {

            it.category.contains(query, true) ||
                    it.note.contains(query, true) ||
                    it.amount.toString().contains(query)

        }.toMutableList()



        adapter = ExpenseAdapter(

            filteredList,

            {},

            {}

        )

        recyclerHistory.adapter = adapter

    }
    private fun showFilterDialog() {

        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_filter, null)

        val radioGroup =
            view.findViewById<RadioGroup>(R.id.radioGroupFilter)

        AlertDialog.Builder(this)
            .setTitle("Filter")
            .setView(view)
            .setPositiveButton("Apply") { _, _ ->

                when (radioGroup.checkedRadioButtonId) {

                    R.id.rbAll -> {

                        adapter = ExpenseAdapter(
                            allExpenses,
                            {},
                            {}
                        )

                    }

                    R.id.rbIncome -> {

                        adapter = ExpenseAdapter(

                            allExpenses.filter {

                                it.type == "Income"

                            }.toMutableList(),

                            {},

                            {}

                        )

                    }

                    R.id.rbExpense -> {

                        adapter = ExpenseAdapter(

                            allExpenses.filter {

                                it.type == "Expense"

                            }.toMutableList(),

                            {},

                            {}

                        )

                    }

                }

                recyclerHistory.adapter = adapter

            }

            .setNegativeButton("Cancel", null)

            .show()

    }
    private fun showSortDialog() {

        val options = arrayOf(
            "Newest First",
            "Oldest First",
            "Highest Amount",
            "Lowest Amount"
        )

        AlertDialog.Builder(this)
            .setTitle("Sort Transactions")
            .setItems(options) { _, which ->

                val sortedList = when (which) {

                    // Newest (highest ID first)
                    0 -> allExpenses.sortedByDescending { it.id }

                    // Oldest (lowest ID first)
                    1 -> allExpenses.sortedBy { it.id }

                    // Highest amount first
                    2 -> allExpenses.sortedByDescending { it.amount }

                    // Lowest amount first
                    3 -> allExpenses.sortedBy { it.amount }

                    else -> allExpenses
                }

                adapter = ExpenseAdapter(

                    sortedList.toMutableList(),

                    {},

                    {}

                )

                recyclerHistory.adapter = adapter

            }
            .show()
    }
    private fun filterToday() {

        val today = java.text.SimpleDateFormat(
            "dd/MM/yyyy",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        val filteredList = databaseHelper
            .getAllExpenses()
            .filter { it.date == today }

        adapter.updateList(filteredList.toMutableList())
    }

    private fun filterThisWeek() {

        // Temporary
        // We'll implement week calculation later.
        loadTransactions()
    }

    private fun filterThisMonth() {

        val currentMonth = java.text.SimpleDateFormat(
            "MM",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        val filteredList = databaseHelper
            .getAllExpenses()
            .filter {

                it.date.substring(3,5) == currentMonth

            }

        adapter.updateList(filteredList.toMutableList())
    }
}