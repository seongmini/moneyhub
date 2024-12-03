package com.example.moneyhub.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyhub.R
import com.example.moneyhub.model.Transaction
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private val items: List<Transaction>,
    private val isForBudget: Boolean,
    private val isForCalendar: Boolean = false,  // 새로운 파라미터 추가, 기본값은 false

    // TransactionItem을 매개변수로 받는 함수 타입
    private val onItemClick: (Transaction) -> Unit = {} // 클릭된 아이템의 정보를 전달 가능
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    // ViewHolder: 아이템 뷰를 저장하는 클래스
    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val category: TextView = itemView.findViewById(R.id.item_category)
        val transaction: TextView = itemView.findViewById(R.id.item_transaction)

        init {
            itemView.setOnClickListener {
                // 거래 내역 아이템 리스트를 가져온다.
                onItemClick.invoke(items[position])  // 클릭된 아이템 전체를 전달
            }
        }
    }

    // onCreateViewHolder: 새로운 뷰 홀더를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        // isForBudget 값에 따라 다른 layout 사용
        val layoutId = R.layout.item_layout_history
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        // CalendarFragment에서만 노란색 배경 적용
        if (isForCalendar) {
            view.setBackgroundResource(R.drawable.yellow_thin_block_less_corners)
        }
        if(isForBudget){
            view.setBackgroundResource(R.drawable.grey_block)
        }
        else {
            view.setBackgroundResource(R.drawable.cyan_block)
        }

        return TransactionViewHolder(view)
    }

    // onBindViewHolder: 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]

        // 데이터를 ViewHolder에 반영
// icon 설정
//        holder.icon.setImageResource(item.icon)
        holder.title.text = item.title
        holder.category.text = item.category

        // amount가 null일 경우 처리
        holder.transaction.text = when {
            item.amount == null -> "$ -"
            item.amount < 0 -> "-$ ${-item.amount}"
            else -> "$ ${item.amount}"
        }

        // 날짜에 따른 배경색 설정
        if (isForCalendar) {
            val currentDate = Calendar.getInstance()
            val itemDate = Date(item.payDate)  // 타임스탬프를 Date 객체로 변환


            if (itemDate.before(currentDate.time)) {
                holder.itemView.setBackgroundResource(R.drawable.yellow_thin_block_less_corners)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.grey_block)
            }
        } else if (isForBudget) {
            holder.itemView.setBackgroundResource(R.drawable.grey_block)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.cyan_block)
        }

    // 금액 색상 설정 - null 처리 추가
    val textColor = when {
        item.amount == null -> R.color.moneyGrey  // null일 경우 기본 색상 지정
        item.amount < 0 -> R.color.moneyRed
        else -> R.color.moneyGreenThick
    }
    holder.transaction.setTextColor(holder.itemView.context.getColor(textColor))
}




    // getItemCount: 아이템의 총 개수를 반환
    override fun getItemCount(): Int = items.size
}