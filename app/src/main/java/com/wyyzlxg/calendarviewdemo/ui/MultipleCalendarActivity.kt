package com.wyyzlxg.calendarviewdemo.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.wyyzlxg.CalendarView
import com.github.wyyzlxg.utils.DensityUtils
import com.wyyzlxg.calendarview.R

class MultipleCalendarActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_multiple_calendar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setSelectDayBackground(AppCompatResources.getDrawable(this, R.drawable.shape_selected_date))
        calendarView.setTodayBackground(AppCompatResources.getDrawable(this, R.drawable.shape_today_date))
        calendarView.setDisableDayBackground(AppCompatResources.getDrawable(this, R.drawable.shape_disable_date))
        calendarView.setSelectTextColor(Color.WHITE)
        calendarView.setTextColor(Color.BLACK)
        calendarView.setTextSize(DensityUtils.sp2px(15f).toFloat())
        calendarView.setSelectTextSize(DensityUtils.sp2px(15f).toFloat())
        calendarView.setOnDateChangeListener(object : CalendarView.OnDateChangeListener {
            override fun onSelectedDateChange(view: CalendarView, selectedDate: List<String>) {
                Toast.makeText(this@MultipleCalendarActivity, "已选：${selectedDate.size}天", Toast.LENGTH_SHORT).show()
            }

            override fun onSelectedMonthChange(view: CalendarView, year: Int, month: Int) {

            }
        })
    }
}