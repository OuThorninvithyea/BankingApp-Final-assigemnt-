package com.example.bankingapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bankingapp.R
import com.example.bankingapp.databinding.FragmentHomeBinding
import com.example.bankingapp.ui.adapters.TransactionAdapter
import com.example.bankingapp.viewmodel.BankingViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BankingViewModel
    private val adapter = TransactionAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(BankingViewModel::class.java)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.rvTransactions.layoutManager = LinearLayoutManager(context)
        binding.rvTransactions.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvBalance.text = "$${String.format("%.2f", user.balance)}"
                binding.tvAccountNumber.text = user.accountNumber
            } else {
                // Initialize default user if not exists (For testing)
                // In production this would be registration/login
                viewModel.insertUser(
                    com.example.bankingapp.data.model.User(
                        name = "Vithea",
                        balance = 5000.00,
                        accountNumber = "1234 5678 9012"
                    )
                )
            }
        }

        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
        }
    }

    private fun setupListeners() {
        binding.btnTransfer.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_transferFragment)
        }
        binding.btnWithdraw.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_withdrawFragment)
        }
        binding.btnTopup.setOnClickListener {
            // TODO: Navigate to TopUp Fragment
        }
        binding.btnMore.setOnClickListener {
            // TODO: Show more options
        }
        binding.btnNotification.setOnClickListener {
            // TODO: Navigate to Notifications
        }
        binding.btnViewAll.setOnClickListener {
            // TODO: Navigate to All Transactions
        }
        
        // Dynamic Greeting
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Good Morning,"
            in 12..17 -> "Good Afternoon,"
            else -> "Good Evening,"
        }
        binding.tvGreeting.text = greeting
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
