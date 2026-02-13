package com.example.bankingapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bankingapp.databinding.FragmentAnalyticsBinding
import com.example.bankingapp.viewmodel.BankingViewModel

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BankingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(BankingViewModel::class.java)

        setupObservers()
    }

    private fun setupObservers() {
        // Observe User for Balance (Optional, for context)
        viewModel.user.observe(viewLifecycleOwner) { user ->
            // Could use user.balance for chart calculation if needed
        }

        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            var totalIncome = 0.0
            var totalTransfer = 0.0
            var totalWithdraw = 0.0

            transactions.forEach { transaction ->
                when (transaction.type) {
                    "Deposit" -> totalIncome += transaction.amount
                    "Transfer" -> totalTransfer += transaction.amount
                    "Withdraw" -> totalWithdraw += transaction.amount
                }
            }
            
            val totalSpent = totalTransfer + totalWithdraw
            updateUI(totalSpent, totalTransfer, totalWithdraw, totalIncome)
        }
    }

    private fun updateUI(spent: Double, transfer: Double, withdraw: Double, income: Double) {
        binding.tvTotalSpent.text = "$${String.format("%.2f", spent)}"
        binding.tvStatTransfer.text = "$${String.format("%.2f", transfer)}"
        binding.tvStatWithdraw.text = "$${String.format("%.2f", withdraw)}"

        // Chart Progress: Spending vs Income
        val progress = if (income > 0) {
            ((spent / income) * 100).toInt()
        } else {
            if (spent > 0) 100 else 0
        }
        
        binding.progressChart.progress = progress.coerceIn(0, 100)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
