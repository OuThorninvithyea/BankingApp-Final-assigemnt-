package com.example.bankingapp.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bankingapp.data.model.Transaction
import com.example.bankingapp.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions = listOf<Transaction>()

    fun submitList(newData: List<Transaction>) {
        transactions = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount() = transactions.size

    class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.tvTransactionType.text = transaction.type
            binding.tvTransactionDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(transaction.timestamp))
            
            if (transaction.type == "Deposit") {
                binding.tvTransactionAmount.text = "+$${transaction.amount}"
                binding.tvTransactionAmount.setTextColor(Color.GREEN)
                binding.tvTransactionRecipient.text = "From: ${transaction.recipientInfo ?: "Self"}"
            } else {
                binding.tvTransactionAmount.text = "-$${transaction.amount}"
                binding.tvTransactionAmount.setTextColor(Color.RED)
                binding.tvTransactionRecipient.text = "To: ${transaction.recipientInfo ?: "Self"}"
            }
        }
    }
}
