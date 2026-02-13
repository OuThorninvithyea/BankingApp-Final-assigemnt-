package com.example.bankingapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bankingapp.databinding.FragmentProfileBinding
import com.example.bankingapp.data.model.User
import com.example.bankingapp.viewmodel.BankingViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BankingViewModel
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(BankingViewModel::class.java)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            currentUser = user
            if (user != null) {
                binding.tvUserName.text = user.name
                binding.tvUserEmail.text = "vithea@example.com" // Mock email or add to User entity
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(context, "Edit Profile Feature Coming Soon", Toast.LENGTH_SHORT).show()
        }

        binding.btnNotifications.setOnClickListener {
            Toast.makeText(context, "Notifications Settings Coming Soon", Toast.LENGTH_SHORT).show()
        }

        binding.btnSecurity.setOnClickListener {
            Toast.makeText(context, "Security Settings Coming Soon", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            Toast.makeText(context, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
            // Implement actual logout logic here
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
