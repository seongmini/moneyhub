package com.example.moneyhub.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.activity.camera.CameraActivity
import com.example.moneyhub.activity.registerdetails.RegisterDetailsActivity
import com.example.moneyhub.adapter.TransactionAdapter
import com.example.moneyhub.databinding.FragmentBudgetBinding
import com.example.moneyhub.model.sessions.RegisterTransactionSession
import kotlinx.coroutines.launch


class BudgetFragment : Fragment() {
    private val sharedViewModel: SharedTransactionViewModel by activityViewModels()

    // ViewBinding null 안전성을 위한 처리
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!  // !! 연산자로 null이 아님을 보장

    private lateinit var adapter: TransactionAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupAddButton()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }


    private fun setupRecyclerView() {
        adapter = TransactionAdapter(emptyList(), isForBudget = true) { transaction ->
            val intent = Intent(requireContext(), CameraActivity::class.java).apply {
                RegisterTransactionSession.setTransaction(transaction)
            }
            startActivity(intent)
        }

        binding.recyclerViewBudget.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BudgetFragment.adapter
        }
    }

    // 거래내역 추가 버튼 설정
    private fun setupAddButton() {
        binding.btnAddBudget.setOnClickListener{
            val intent = Intent(requireActivity(), RegisterDetailsActivity::class.java)
            startActivity(intent)
        }
    }


    // 메모리 누수 방지를 위한 binding null 처리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.filteredBudgets.collect { transactions ->
                    adapter.updateData(transactions)
                }
            }
        }
    }
}