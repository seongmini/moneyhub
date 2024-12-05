package com.example.moneyhub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyhub.R
import com.example.moneyhub.adapter.TransactionAdapter
import com.example.moneyhub.databinding.FragmentCalendarBinding
import com.example.moneyhub.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore

class CalendarFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapter: TransactionAdapter

    // 캘린더 샘플 데이터
    private val calendarData = listOf(
        Transaction(
            tid = "31",
            title = "희진이 간식비",
            category = "희진이 복지",
            type = false, // 지출
            amount = -7700L,
            content = "",
            payDate = System.currentTimeMillis(),
            verified = true,
            createdAt = System.currentTimeMillis()

        ),

        Transaction(
            tid = "32",
            title = "지환이 노래방",
            category = "지환이 복지",
            type = false, // 지출
            amount = -10000L,
            content = "",
            payDate = System.currentTimeMillis(),
            verified = true,
            createdAt = System.currentTimeMillis()

        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCalendarView()
        updateMonthlyTotals()
    }

    // RecyclerView 초기화 및 설정
    private fun setupRecyclerView() {
        adapter = TransactionAdapter(calendarData, true, true)
        binding.transactionList.apply {
            adapter = this@CalendarFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupCalendarView() {
        val dailyTotals = mutableMapOf<String, Pair<Double, Double>>() // 날짜별 <수입, 지출> 맵

        // 각 날짜의 수입/지출 총액 계산
        calendarData.forEach { transaction ->
            transaction.amount?.let { amount -> // null이 아닌 경우에만 처리
                val currentPair = dailyTotals[transaction.payDate.toString()] ?: Pair(0.0, 0.0)
                dailyTotals[transaction.payDate.toString()] = when {
                    amount > 0 -> Pair(currentPair.first + amount, currentPair.second)
                    else -> Pair(currentPair.first, currentPair.second - amount)
                }
            }
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

            // 선택된 날짜의 거래 내역 필터링
            val transactionsForDate = calendarData.filter { it.payDate.toString() == selectedDate }
            adapter = TransactionAdapter(transactionsForDate, true, true)
            binding.transactionList.adapter = adapter

            // 선택된 날짜의 수입/지출 표시
            val totals = dailyTotals[selectedDate] ?: Pair(0.0, 0.0)
            binding.selectedDateIncome.text = if (totals.first > 0) "+${totals.first.toInt()}" else ""
            binding.selectedDateExpense.text = if (totals.second > 0) "-${totals.second.toInt()}" else ""
        }
    }

    private fun updateMonthlyTotals() {
        var monthlyIncome = 0.0
        var monthlyExpense = 0.0

        calendarData.forEach { transaction ->
            transaction.amount?.let { amount ->  // null이 아닌 경우에만 처리
                when {
                    amount > 0 -> monthlyIncome += amount
                    else -> monthlyExpense += -amount
                }
            }
        }

        binding.textViewIncome.text = String.format(" ₩ %.0f", monthlyIncome)
        binding.textViewExpense.text = String.format("₩ %.0f", monthlyExpense)
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}