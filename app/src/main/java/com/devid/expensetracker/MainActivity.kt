package com.devid.expensetracker

import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devid.expensetracker.adapter.ExpenseAdapter
import com.devid.expensetracker.database.DatabaseHelper
import com.devid.expensetracker.ui.addexpense.AddExpenseActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.formatter.PercentFormatter
import android.graphics.Color
import com.devid.expensetracker.ui.history.HistoryActivity
import com.devid.expensetracker.ui.report.ReportActivity
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerTransactions: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var txtBalance: TextView
    private lateinit var txtIncome: TextView
    private lateinit var txtExpense: TextView

    private lateinit var txtChartIncome: TextView
    private lateinit var txtChartExpense: TextView

    private lateinit var txtNetBalance: TextView

    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        recyclerTransactions = findViewById(R.id.recyclerTransactions)

        txtBalance = findViewById(R.id.txtBalance)
        txtIncome = findViewById(R.id.txtIncome)
        txtExpense = findViewById(R.id.txtExpense)
        txtChartIncome = findViewById(R.id.txtChartIncome)
        txtChartExpense = findViewById(R.id.txtChartExpense)
        pieChart = findViewById(R.id.pieChart)
        txtNetBalance = findViewById(R.id.txtNetBalance)
        val txtViewAll = findViewById<TextView>(R.id.txtViewAll)

        txtViewAll.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    HistoryActivity::class.java
                )
            )

        }
        val txtReports = findViewById<TextView>(R.id.txtReports)

        txtReports.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ReportActivity::class.java
                )
            )

        }
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)

        // Database
        databaseHelper = DatabaseHelper(this)

        // RecyclerView
        recyclerTransactions.layoutManager = LinearLayoutManager(this)

        recyclerTransactions.isNestedScrollingEnabled = false
        recyclerTransactions.setHasFixedSize(false)

        // Load Dashboard Data
        loadDashboard()

        // Open Add Expense Screen
        fab.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // Refresh data whenever user returns from AddExpenseActivity
        loadDashboard()
    }

    private fun loadDashboard() {

        // Load Transactions
        val expenseList = databaseHelper.getAllExpenses()

        val recentExpenses =
            if (expenseList.size > 5)
                expenseList.take(5).toMutableList()
            else
                expenseList

        expenseAdapter = ExpenseAdapter(

            recentExpenses,

            // Click → Edit
            { expense ->

                val intent = Intent(
                    this,
                    AddExpenseActivity::class.java
                )

                intent.putExtra("id", expense.id)
                intent.putExtra("amount", expense.amount)
                intent.putExtra("category", expense.category)
                intent.putExtra("type", expense.type)
                intent.putExtra("note", expense.note)
                intent.putExtra("date", expense.date)

                startActivity(intent)

            },

            // Long Click → Delete
            { expense ->

                AlertDialog.Builder(this)
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Delete") { _, _ ->

                        databaseHelper.deleteExpense(expense.id)

                        loadDashboard()

                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            }

        )
        recyclerTransactions.adapter = expenseAdapter

        // Load Totals
        val income = databaseHelper.getTotalIncome()
        val expense = databaseHelper.getTotalExpense()
        val balance = databaseHelper.getBalance()

        txtIncome.text = "₹ %.2f".format(income)
        txtExpense.text = "₹ %.2f".format(expense)
        txtBalance.text = "₹ %.2f".format(balance)
        txtNetBalance.text = "₹ %.2f".format(balance)
        txtChartIncome.text = "₹ %.2f".format(income)
        txtChartExpense.text = "₹ %.2f".format(expense)
        updatePieChart(income, expense)
    }
    private fun updatePieChart(income: Double, expense: Double) {

        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        if (income > 0) {
            entries.add(PieEntry(income.toFloat(), "Income"))
            colors.add(Color.parseColor("#4CAF50"))
        }

        if (expense > 0) {
            entries.add(PieEntry(expense.toFloat(), "Expense"))
            colors.add(Color.parseColor("#F44336"))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors


        dataSet.sliceSpace = 4f
        dataSet.selectionShift = 8f

        dataSet.setDrawValues(false)
        dataSet.valueTextColor = android.graphics.Color.DKGRAY

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))

        pieChart.data = data

        pieChart.description.isEnabled = false

        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(android.graphics.Color.WHITE)
        pieChart.holeRadius = 65f
        pieChart.transparentCircleRadius = 0f

        pieChart.setUsePercentValues(true)

        pieChart.centerText =
            "Balance\n₹ %.2f".format(income - expense)

        pieChart.setCenterTextSize(19f)
        pieChart.setCenterTextColor(android.graphics.Color.BLACK)

        pieChart.setCenterTextSize(18f)

        pieChart.setDrawEntryLabels(false)
        pieChart.setEntryLabelTextSize(13f)

        val legend = pieChart.legend

        legend.isEnabled = true
        legend.textSize = 14f
        legend.formSize = 14f
        legend.xEntrySpace = 12f
        legend.yEntrySpace = 8f
        legend.isWordWrapEnabled = true
        legend.textColor = android.graphics.Color.DKGRAY

        pieChart.animateXY(1200, 1200)

        pieChart.invalidate()
    }
}