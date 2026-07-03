package com.devid.expensetracker.model

data class Expense(

    val id: Int = 0,

    val amount: Double,

    val category: String,

    val type: String,

    val note: String,

    val date: String

)