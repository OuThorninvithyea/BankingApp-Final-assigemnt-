package com.example.bankingapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bankingapp.R
import com.example.bankingapp.data.model.User
import com.example.bankingapp.databinding.FragmentWithdrawBinding
import com.example.bankingapp.viewmodel.BankingViewModel
import com.google.android.material.snackbar.Snackbar

class WithdrawFragment : Fragment() {

    private var _binding: FragmentWithdrawBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var viewModel: BankingViewModel
    private var currentAmountStr = ""
    private var currentUser: User? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(BankingViewModel::class.java)

        setupObservers()
        setupListeners()
        setupKeypad()
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            currentUser = user
            if (user != null) {
                binding.tvAvailableBalance.text =
                        "Available: $${String.format("%.2f", user.balance)}"
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnWithdraw.setOnClickListener { handleWithdraw() }
    }

    private fun setupKeypad() {
        // dynamic listener assignment for keypad buttons
        binding.layoutKeypad.children.forEach { view ->
            if (view is Button && view.id != R.id.btn_backspace) {
                view.setOnClickListener { appendDigit(view.text.toString()) }
            }
        }
        binding.btnBackspace.setOnClickListener { removeDigit() }
    }

    private fun appendDigit(digit: String) {
        if (digit == "." && currentAmountStr.contains(".")) return
        if (currentAmountStr.length >= 10) return // Max length

        if (currentAmountStr == "0" && digit != ".") {
            currentAmountStr = digit
        } else {
            currentAmountStr += digit
        }
        updateDisplay()
    }

    private fun removeDigit() {
        if (currentAmountStr.isNotEmpty()) {
            currentAmountStr = currentAmountStr.dropLast(1)
            if (currentAmountStr.isEmpty()) {
                currentAmountStr = "0"
            }
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        if (currentAmountStr.isEmpty()) {
            binding.tvAmount.text = "$0"
        } else {
            binding.tvAmount.text = "$$currentAmountStr"
        }
    }

    private fun handleWithdraw() {
        val amount = currentAmountStr.toDoubleOrNull() ?: 0.0

        if (amount <= 0) {
            Toast.makeText(context, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentUser == null) {
            Toast.makeText(context, "User not loaded", Toast.LENGTH_SHORT).show()
            return
        }

        if (amount > currentUser!!.balance) {
            Snackbar.make(binding.root, "Insufficient balance!", Snackbar.LENGTH_LONG).show()
            return
        }

        viewModel.withdrawMoney(currentUser!!, amount)

        Toast.makeText(context, "Withdrawal Successful!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
