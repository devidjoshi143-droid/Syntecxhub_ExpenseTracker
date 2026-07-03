package com.devid.expensetracker.ui.report

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.devid.expensetracker.R
import com.devid.expensetracker.database.DatabaseHelper
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

import com.devid.expensetracker.model.Expense

import com.github.mikephil.charting.formatter.PercentFormatter

class ReportActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var txtCategoryAnalysis: TextView

    private lateinit var txtIncome: TextView
    private lateinit var txtExpense: TextView
    private lateinit var txtBalance: TextView

    private lateinit var txtPieIncome: TextView
    private lateinit var txtPieExpense: TextView

    private lateinit var txtHighestIncome: TextView
    private lateinit var txtHighestExpense: TextView
    private lateinit var txtTotalTransactions: TextView

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_report)

        databaseHelper = DatabaseHelper(this)

        txtIncome = findViewById(R.id.txtReportIncome)
        txtExpense = findViewById(R.id.txtReportExpense)
        txtBalance = findViewById(R.id.txtReportBalance)

        txtPieIncome = findViewById(R.id.txtPieIncome)
        txtPieExpense = findViewById(R.id.txtPieExpense)

        txtCategoryAnalysis =
            findViewById(R.id.txtCategoryAnalysis)

        txtHighestIncome = findViewById(R.id.txtHighestIncome)
        txtHighestExpense = findViewById(R.id.txtHighestExpense)
        txtTotalTransactions =
            findViewById(R.id.txtTotalTransactions)

        pieChart = findViewById(R.id.reportPieChart)

        barChart = findViewById(R.id.barChart)

        loadReport()

    }

    private fun loadReport() {

        // Totals
        val income = databaseHelper.getTotalIncome()
        val expense = databaseHelper.getTotalExpense()
        val balance = databaseHelper.getBalance()

        txtIncome.text = "₹ %.2f".format(income)
        txtExpense.text = "₹ %.2f".format(expense)
        txtBalance.text = "₹ %.2f".format(balance)

        txtPieIncome.text = "₹ %.2f".format(income)
        txtPieExpense.text = "₹ %.2f".format(expense)

        // Get all transactions
        val expenseList = databaseHelper.getAllExpenses()

        txtTotalTransactions.text = expenseList.size.toString()

        // Highest Income
        val highestIncome = expenseList
            .filter { it.type == "Income" }
            .maxOfOrNull { it.amount } ?: 0.0

        // Highest Expense
        val highestExpense = expenseList
            .filter { it.type == "Expense" }
            .maxOfOrNull { it.amount } ?: 0.0

        txtHighestIncome.text = "₹ %.2f".format(highestIncome)
        txtHighestExpense.text = "₹ %.2f".format(highestExpense)
        updateCategoryAnalysis(expenseList)

        updatePieChart(income, expense)

        updateBarChart(expenseList)

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

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))

        pieChart.data = data

        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(false)

        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = true
        pieChart.legend.textSize = 13f
        pieChart.legend.formSize = 12f
        pieChart.legend.isWordWrapEnabled = true

        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 65f
        pieChart.transparentCircleRadius = 0f
        pieChart.setHoleColor(Color.WHITE)

        pieChart.centerText = "Balance\n₹ %.2f".format(income - expense)
        pieChart.setCenterTextSize(18f)
        pieChart.setCenterTextColor(Color.BLACK)

        pieChart.animateY(1000)

        pieChart.invalidate()
    }

    private fun updateBarChart(expenseList: MutableList<com.devid.expensetracker.model.Expense>) {

        val monthlyExpense = FloatArray(12)

        for (expense in expenseList) {

            if (expense.type == "Expense") {

                try {

                    val month = expense.date.substring(3, 5).toInt()

                    monthlyExpense[month - 1] += expense.amount.toFloat()

                } catch (_: Exception) {
                }

            }

        }

        val entries = ArrayList<BarEntry>()

        for (i in monthlyExpense.indices) {

            entries.add(
                BarEntry(
                    i.toFloat(),
                    monthlyExpense[i]
                )
            )

        }

        val dataSet = BarDataSet(entries, "Monthly Expense")
        dataSet.color = Color.parseColor("#3F51B5")
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val data = BarData(dataSet)

        barChart.data = data

        val months = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )

        barChart.xAxis.valueFormatter =
            com.github.mikephil.charting.formatter.IndexAxisValueFormatter(months)

        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawGridLines(false)

        barChart.axisRight.isEnabled = false

        barChart.description.isEnabled = false

        barChart.animateY(1000)

        barChart.invalidate()

    }
    private fun updateCategoryAnalysis(
        expenseList: MutableList<Expense>
    ) {

        val categoryMap = mutableMapOf<String, Double>()

        for (expense in expenseList) {

            if (expense.type == "Expense") {

                categoryMap[expense.category] =
                    categoryMap.getOrDefault(
                        expense.category,
                        0.0
                    ) + expense.amount

            }

        }

        if (categoryMap.isEmpty()) {

            txtCategoryAnalysis.text = "No expense data available."

            return

        }

        val builder = StringBuilder()

        categoryMap.entries
            .sortedByDescending { it.value }
            .forEach {

                builder.append("• ")
                    .append(it.key)
                    .append(" : ₹ ")
                    .append("%.2f".format(it.value))
                    .append("\n")

            }

        txtCategoryAnalysis.text = builder.toString()

    }
}