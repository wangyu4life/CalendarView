package com.github.wyyzlxg.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.github.wyyzlxg.manager.CalendarManager
import com.github.wyyzlxg.view.MonthView
import java.util.Calendar
import java.util.Date

/**
 * @author wangyu
 *
 */
class MonthAdapter(private val context: Context, private val data: MutableList<Date>, private val manager: CalendarManager) : RecyclerView.Adapter<MonthAdapter.ViewHolder>() {
    private var mOnDateClickListener: ((Int, Int, Int) -> Unit)? = null
    private var mOnDateStateChangeListener: ((List<String>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MonthView(context)
        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        view.setCalendarManager(manager)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val monthView = holder.itemView as MonthView
        monthView.refresh(data[position])

        monthView.setOnDateClickListener { year, month, day ->
            mOnDateClickListener?.invoke(year, month, day)
        }
        monthView.setOnDateStateChangeListener {
            mOnDateStateChangeListener?.invoke(it)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshMonthData() {
        notifyDataSetChanged()
    }

    fun setOnDateClickListener(listener: (Int, Int, Int) -> Unit) {
        mOnDateClickListener = listener
    }

    fun setOnDateStateChangeListener(listener: (List<String>) -> Unit) {
        mOnDateStateChangeListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}