package com.example.bankingapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bankingapp.data.model.User
import com.example.bankingapp.databinding.FragmentTransferBinding
import com.example.bankingapp.viewmodel.BankingViewModel
import com.google.android.material.snackbar.Snackbar

class TransferFragment : Fragment() {

    private var _binding: FragmentTransferBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var viewModel: BankingViewModel
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(BankingViewModel::class.java)

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnSend.setOnClickListener { handleTransfer() }
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

    private fun handleTransfer() {
        val recipient = binding.etRecipient.text.toString()
        val amountStr = binding.etAmount.text.toString()
        val note = binding.etNote.text.toString()

        if (recipient.isBlank()) {
            binding.tilRecipient.error = "Recipient required"
            return
        } else {
            binding.tilRecipient.error = null
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentUser == null) {
            Toast.makeText(context, "User data not loaded", Toast.LENGTH_SHORT).show()
            return
        }

        if (amount > currentUser!!.balance) {
            Snackbar.make(binding.root, "Insufficient funds!", Snackbar.LENGTH_LONG).show()
            return
        }

        // Use Atomic Transfer
        viewModel.transferMoney(currentUser!!, amount, recipient, note)

        Toast.makeText(context, "Transfer Successful!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
