package com.devid.expensetracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devid.expensetracker.R
import com.devid.expensetracker.model.Expense

class ExpenseAdapter(
    private val expenseList: MutableList<Expense>,
    private val onClick: (Expense) -> Unit,
    private val onLongClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtIcon: TextView = itemView.findViewById(R.id.txtIcon)
        val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
        val txtNote: TextView = itemView.findViewById(R.id.txtNote)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtType: TextView = itemView.findViewById(R.id.txtType)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)

        return ExpenseViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: ExpenseViewHolder,
        position: Int
    ) {

        val expense = expenseList[position]

        holder.txtCategory.text = expense.category
        holder.txtNote.text = expense.note
        holder.txtDate.text = expense.date
        holder.txtType.text = expense.type
        if (expense.type == "Income") {

            holder.txtType.setTextColor(
                android.graphics.Color.parseColor("#4CAF50")
            )

        } else {

            holder.txtType.setTextColor(
                android.graphics.Color.parseColor("#F44336")
            )

        }
        holder.txtAmount.text = "₹ %.2f".format(expense.amount)
        if (expense.type == "Income") {

            holder.txtAmount.setTextColor(
                android.graphics.Color.parseColor("#4CAF50")
            )

        } else {

            holder.txtAmount.setTextColor(
                android.graphics.Color.parseColor("#F44336")
            )

        }

        holder.txtIcon.text = when (expense.category.lowercase()) {

            "food" -> "🍔"

            "shopping" -> "🛍️"

            "travel" -> "✈️"

            "salary" -> "💰"

            "bills" -> "💡"

            "health" -> "❤️"

            "education" -> "📚"

            "transport" -> "🚗"

            "entertainment" -> "🎮"

            "gift" -> "🎁"

            "home" -> "🏠"

            else -> if (expense.type == "Income") "💰" else "💸"
        }
        holder.itemView.setOnClickListener {

            onClick(expense)

        }
        holder.itemView.setOnLongClickListener {

            onLongClick(expense)

            true
        }
    }

    override fun getItemCount(): Int {

        return expenseList.size

    }
    fun updateList(newList: MutableList<Expense>) {

        expenseList.clear()
        expenseList.addAll(newList)
        notifyDataSetChanged()

    }
}