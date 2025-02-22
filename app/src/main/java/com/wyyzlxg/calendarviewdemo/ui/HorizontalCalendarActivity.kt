package com.wyyzlxg.calendarviewdemo.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.wyyzlxg.CalendarView
import com.wyyzlxg.calendarview.R

class HorizontalCalendarActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_horizontal_calendar)
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
        calendarView.setOnDateClickListener(object : CalendarView.OnDateClickListener {
            override fun onDateClick(view: CalendarView, year: Int, month: Int, day: Int) {
                Toast.makeText(this@HorizontalCalendarActivity, "$year-${month + 1}-$day", Toast.LENGTH_SHORT).show()
            }
        })
        calendarView.setOnDateChangeListener(object : CalendarView.OnDateChangeListener {
            override fun onSelectedDateChange(view: CalendarView, selectedDate: List<String>) {

            }

            override fun onSelectedMonthChange(view: CalendarView, year: Int, month: Int) {
                Toast.makeText(this@HorizontalCalendarActivity, "当前日期：${year}-${month + 1}", Toast.LENGTH_SHORT).show()
            }
        })

        findViewById<TextView>(R.id.tv_today).setOnClickListener {
            calendarView.jumpToCurrentDate(System.currentTimeMillis())
        }
    }
}