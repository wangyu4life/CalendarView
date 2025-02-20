package com.wyyzlxg.calendarviewdemo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wyyzlxg.calendarview.R
import com.wyyzlxg.calendarviewdemo.ui.HorizontalCalendarActivity
import com.wyyzlxg.calendarviewdemo.ui.MultipleCalendarActivity
import com.wyyzlxg.calendarviewdemo.ui.RangeCalendarActivity
import com.wyyzlxg.calendarviewdemo.ui.SingleCalendarActivity
import com.wyyzlxg.calendarviewdemo.ui.VerticalCalendarActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initListener()
    }

    private fun initListener() {
        findViewById<TextView>(R.id.tv_horizontal_calendar).setOnClickListener {
            val intent = Intent(this, HorizontalCalendarActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.tv_vertical_calendar).setOnClickListener {
            val intent = Intent(this, VerticalCalendarActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.tv_single_calendar).setOnClickListener {
            val intent = Intent(this, SingleCalendarActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.tv_multiple_calendar).setOnClickListener {
            val intent = Intent(this, MultipleCalendarActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.tv_range_calendar).setOnClickListener {
            val intent = Intent(this, RangeCalendarActivity::class.java)
            startActivity(intent)
        }
    }
}