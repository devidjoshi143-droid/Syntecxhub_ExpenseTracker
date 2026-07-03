package com.devid.expensetracker.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.devid.expensetracker.model.Expense

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_NAME = "ExpenseTracker.db"

        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "expenses"

        const val COL_ID = "id"

        const val COL_AMOUNT = "amount"

        const val COL_CATEGORY = "category"

        const val COL_TYPE = "type"

        const val COL_NOTE = "note"

        const val COL_DATE = "date"

    }
    override fun onCreate(db: SQLiteDatabase) {

        val createTable = """
            CREATE TABLE $TABLE_NAME(
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_AMOUNT REAL,
                $COL_CATEGORY TEXT,
                $COL_TYPE TEXT,
                $COL_NOTE TEXT,
                $COL_DATE TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)

    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

        onCreate(db)

    }
    fun insertExpense(expense: Expense): Boolean {

        val db = writableDatabase

        val values = ContentValues()

        values.put(COL_AMOUNT, expense.amount)

        values.put(COL_CATEGORY, expense.category)

        values.put(COL_TYPE, expense.type)

        values.put(COL_NOTE, expense.note)

        values.put(COL_DATE, expense.date)

        val result = db.insert(TABLE_NAME, null, values)

        db.close()

        return result != -1L

    }
    fun getAllExpenses(): MutableList<Expense> {

        val expenseList = mutableListOf<Expense>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME ORDER BY $COL_ID DESC",
            null
        )

        if (cursor.moveToFirst()) {

            do {

                expenseList.add(

                    Expense(

                        id = cursor.getInt(
                            cursor.getColumnIndexOrThrow(COL_ID)
                        ),

                        amount = cursor.getDouble(
                            cursor.getColumnIndexOrThrow(COL_AMOUNT)
                        ),

                        category = cursor.getString(
                            cursor.getColumnIndexOrThrow(COL_CATEGORY)
                        ),

                        type = cursor.getString(
                            cursor.getColumnIndexOrThrow(COL_TYPE)
                        ),

                        note = cursor.getString(
                            cursor.getColumnIndexOrThrow(COL_NOTE)
                        ),

                        date = cursor.getString(
                            cursor.getColumnIndexOrThrow(COL_DATE)
                        )

                    )

                )

            } while (cursor.moveToNext())

        }

        cursor.close()

        db.close()

        return expenseList

    }
    fun getTotalIncome(): Double {

        var total = 0.0

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT SUM($COL_AMOUNT) FROM $TABLE_NAME WHERE $COL_TYPE='Income'",
            null
        )

        if (cursor.moveToFirst()) {

            total = cursor.getDouble(0)

        }

        cursor.close()

        db.close()

        return total

    }
    fun getTotalExpense(): Double {

        var total = 0.0

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT SUM($COL_AMOUNT) FROM $TABLE_NAME WHERE $COL_TYPE='Expense'",
            null
        )

        if (cursor.moveToFirst()) {

            total = cursor.getDouble(0)

        }

        cursor.close()

        db.close()

        return total

    }
    fun getBalance(): Double {

        return getTotalIncome() - getTotalExpense()

    }
    fun deleteExpense(id: Int): Boolean {

        val db = writableDatabase

        val result = db.delete(
            TABLE_NAME,
            "$COL_ID=?",
            arrayOf(id.toString())
        )

        db.close()

        return result > 0
    }
    fun updateExpense(expense: Expense): Boolean {

        val db = writableDatabase

        val values = ContentValues()

        values.put(COL_AMOUNT, expense.amount)
        values.put(COL_CATEGORY, expense.category)
        values.put(COL_TYPE, expense.type)
        values.put(COL_NOTE, expense.note)
        values.put(COL_DATE, expense.date)

        val result = db.update(
            TABLE_NAME,
            values,
            "$COL_ID=?",
            arrayOf(expense.id.toString())
        )

        db.close()

        return result > 0
    }
}