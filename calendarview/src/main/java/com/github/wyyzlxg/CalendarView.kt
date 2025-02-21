package com.github.wyyzlxg

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.wyyzlxg.adapter.MonthAdapter
import com.github.wyyzlxg.manager.CalendarManager
import com.github.wyyzlxg.utils.DensityUtils
import com.wangyu.calendarview.R
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * @author wangyu
 *
 * 自定义可多选日历 View.
 */
class CalendarView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {
    private val calendarManager by lazy { CalendarManager() }

    /** 月视图  */
    private lateinit var monthContainerView: RecyclerView
    private var monthAdapter: MonthAdapter? = null
    private var pagerSnapHelper: PagerSnapHelper? = null

    /** 日历数据  */
    private val dateList: MutableList<Date> = mutableListOf()

    /** 日历最小高度  */
    private val minHeight = DensityUtils.dp2px(350f)

    /** 事件监听  */
    private var mOnDateClickListener: OnDateClickListener? = null
    private var mOnDateChangeListener: OnDateChangeListener? = null

    private var currentPosition = 0

    interface OnDateClickListener {
        /**
         * 日期点击监听.
         * @param view     与次监听器相关联的 View.
         * @param year     对应的年.
         * @param month    对应的月.
         * @param day      对应的日.
         */
        fun onDateClick(view: CalendarView, year: Int, month: Int, day: Int)
    }

    interface OnDateChangeListener {
        /**
         * 选中的天发生了改变监听回调.
         * @param view     与次监听器相关联的 View.
         * @param selectedDate   被选中的日期列表.
         */
        fun onSelectedDateChange(view: CalendarView, selectedDate: List<String>)

        /**
         * 日历月份改变监听
         */
        fun onSelectedMonthChange(view: CalendarView, year: Int, month: Int)
    }

    init {
        initAttrs(attrs)
        initView()
        loadCalendarData()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
        val weekTextColor = a.getColor(R.styleable.CalendarView_cv_weekTextColor, Color.BLACK)
        setWeekTextColor(weekTextColor)
        val weekTextSize = a.getDimension(R.styleable.CalendarView_cv_weekTextSize, DensityUtils.sp2px(14f).toFloat())
        setWeekTextSize(weekTextSize)
        val textColor = a.getColor(R.styleable.CalendarView_cv_textColor, Color.BLACK)
        setTextColor(textColor)
        val selectTextColor = a.getColor(R.styleable.CalendarView_cv_selectTextColor, Color.BLACK)
        setSelectTextColor(selectTextColor)
        val textSize = a.getDimension(R.styleable.CalendarView_cv_textSize, DensityUtils.sp2px(14f).toFloat())
        setTextSize(textSize)
        val selectTextSize = a.getDimension(R.styleable.CalendarView_cv_selectTextSize, DensityUtils.sp2px(14f).toFloat())
        setSelectTextSize(selectTextSize)
        val dayBackground = a.getDrawable(R.styleable.CalendarView_cv_dayBackground)
        setDayBackground(dayBackground)
        val todayBackground = a.getDrawable(R.styleable.CalendarView_cv_todayBackground)
        setTodayBackground(todayBackground)
        val selectDayBackground = a.getDrawable(R.styleable.CalendarView_cv_selectDayBackground)
        setSelectDayBackground(selectDayBackground)
        val disableDayBackground = a.getDrawable(R.styleable.CalendarView_cv_disableDayBackground)
        setDisableDayBackground(disableDayBackground)
        val rangeBackgroundColor = a.getColor(R.styleable.CalendarView_cv_rangeBackgroundColor, Color.BLACK)
        setRangeBackgroundColor(rangeBackgroundColor)
        val selectMode = a.getInt(R.styleable.CalendarView_cv_selectMode, 0)
        calendarManager.selectMode = selectMode
        val isChange = a.getBoolean(R.styleable.CalendarView_cv_isChangeDateStatus, true)
        calendarManager.isChangeDateStatus = isChange
        val pattern = a.getString(R.styleable.CalendarView_cv_dateFormatPattern)
        dateFormatPattern = pattern
        a.recycle()
    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        monthContainerView = RecyclerView(context)
        monthContainerView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(monthContainerView)

        monthAdapter = MonthAdapter(context, dateList, calendarManager)
        monthContainerView.adapter = monthAdapter
        monthContainerView.isNestedScrollingEnabled = false
        monthContainerView.layoutManager = LinearLayoutManager(context, orientation, false)
        if (orientation == HORIZONTAL) {
            pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper!!.attachToRecyclerView(monthContainerView)
        }
        monthContainerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    calendarManager.mCalendar.time = dateList[currentPosition]
                    mOnDateChangeListener?.onSelectedMonthChange(
                        this@CalendarView,
                        calendarManager.mCalendar[Calendar.YEAR],
                        calendarManager.mCalendar[Calendar.MONTH]
                    )
                }
            }
        })
        monthAdapter?.setOnDateClickListener { year, month, day ->
            mOnDateClickListener?.onDateClick(this, year, month, day)
        }
        monthAdapter?.setOnDateStateChangeListener {
            mOnDateChangeListener?.onSelectedDateChange(this, it)
            monthAdapter?.refreshMonthData()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, if (orientation == HORIZONTAL) minHeight else heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        monthContainerView.layout(0, 0, width, height)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadCalendarData() {
        dateList.clear()
        if (calendarManager.mStartDate == null) {
            calendarManager.mStartDate = Calendar.getInstance(Locale.CHINA)
            calendarManager.mStartDate!!.set(2000, 0, 1)
        }
        if (calendarManager.mEndDate == null) {
            calendarManager.mEndDate = Calendar.getInstance(Locale.CHINA)
            calendarManager.mEndDate!!.set(2070, 0, 1)
        }
        if (startDate!!.time > endDate!!.time) {
            throw IllegalArgumentException("开始时间不能大于结束时间")
        }
        val startYear = startDate!!.get(Calendar.YEAR)
        val startMonth = startDate!!.get(Calendar.MONTH)
        val endYear = endDate!!.get(Calendar.YEAR)
        val endMonth = endDate!!.get(Calendar.MONTH)
        val calendar = Calendar.getInstance(Locale.CHINA)
        for (i in startYear..endYear) {
            calendar.set(Calendar.YEAR, i)
            for (j in 0 until 12) {
                if ((i == startYear && j < startMonth) || (i == endYear && j > endMonth)) {
                    continue
                }
                calendar.set(Calendar.MONTH, j)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                dateList.add(calendar.time)
            }
        }
        monthAdapter?.refreshMonthData()
        jumpToCurrentDate()
    }

    /**
     * 跳转到指定日期，默认为当前日期.
     */
    fun jumpToCurrentDate(time: Long = 0L) {
        for (i in dateList.indices) {
            val calendar = Calendar.getInstance(Locale.CHINA)
            calendar.time = dateList[i]
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            if (time != 0L) {
                calendarManager.mCalendar.timeInMillis = time
            }
            val currentYear = calendarManager.mCalendar[Calendar.YEAR]
            val currentMonth = calendarManager.mCalendar[Calendar.MONTH]
            if (currentYear == year && currentMonth == month) {
                currentPosition = i
                break
            }
        }
        monthContainerView.scrollToPosition(currentPosition)
    }


    /**
     * 设置选中的日期数据.
     *
     * @param days 日期数据, 日期格式为 [.setDateFormatPattern] 方法所指定,
     * 如果没有设置则以默认的格式 [.DATE_FORMAT_PATTERN] 进行格式化.
     */
    fun setSelectDate(days: MutableList<String>) {
        calendarManager.mSelectDate.clear()
        calendarManager.mSelectDate.addAll(days)
        monthAdapter?.refreshMonthData()
    }

    /**
     * 设置禁用的日期数据.
     *
     * @param days 日期数据, 日期格式为 [.setDateFormatPattern] 方法所指定,
     * 如果没有设置则以默认的格式 [.DATE_FORMAT_PATTERN] 进行格式化.
     */
    fun setDisableDate(days: MutableList<String>) {
        calendarManager.mDisableDate.clear()
        calendarManager.mDisableDate.addAll(days)
        monthAdapter?.refreshMonthData()
    }

    val selectDate: List<String>
        /**
         * 获取选中的日期数据.
         *
         * @return 日期数据.
         */
        get() = calendarManager.mSelectDate

    val disableDate: List<String>
        /**
         * 获取选中的日期数据.
         *
         * @return 日期数据.
         */
        get() = calendarManager.mDisableDate

    /**
     * 切换到下一个月.
     */
    fun nextMonth() {
        if (currentPosition < dateList.size - 1) {
            calendarManager.mCalendar.add(Calendar.MONTH, 1)
            currentPosition++
            monthContainerView.scrollToPosition(currentPosition)
            mOnDateChangeListener?.onSelectedMonthChange(this, calendarManager.mCalendar[Calendar.YEAR], calendarManager.mCalendar[Calendar.MONTH])
        }
    }

    /**
     * 切换到上一个月.
     */
    fun lastMonth() {
        if (currentPosition > 0) {
            calendarManager.mCalendar.add(Calendar.MONTH, -1)
            currentPosition--
            monthContainerView.scrollToPosition(currentPosition)
            mOnDateChangeListener?.onSelectedMonthChange(this, calendarManager.mCalendar[Calendar.YEAR], calendarManager.mCalendar[Calendar.MONTH])
        }
    }

    val year: Int
        /**
         * 获取当前年份.
         *
         * @return year.
         */
        get() = calendarManager.mCalendar[Calendar.YEAR]
    val month: Int
        /**
         * 获取当前月份.
         *
         * @return month.
         */
        get() = calendarManager.mCalendar[Calendar.MONTH]
    var calendar: Calendar
        /**
         * 获取当前显示的 Calendar 对象.
         *
         * @return Calendar 对象.
         */
        get() = calendarManager.mCalendar
        /**
         * 设置当前显示的 Calendar 对象.
         *
         * @param calendar 对象.
         */
        set(calendar) {
            calendarManager.mCalendar = calendar
        }
    var startDate: Calendar?
        get() = calendarManager.mStartDate
        set(calendar) {
            calendarManager.mStartDate = calendar
            loadCalendarData()
        }

    var endDate: Calendar?
        get() = calendarManager.mEndDate
        set(calendar) {
            calendarManager.mEndDate = calendar
            loadCalendarData()
        }

    /**
     * 设置周文字颜色.
     *
     * @param textColor 文字颜色 [ColorInt].
     */
    fun setWeekTextColor(@ColorInt textColor: Int) {
        calendarManager.mWeekTextColor = textColor
        monthAdapter?.refreshMonthData()
    }

    /**
     * 设置周文字大小.
     *
     * @param textSize 文字大小 (sp).
     */
    fun setWeekTextSize(textSize: Float) {
        calendarManager.mWeekTextSize = textSize
        monthAdapter?.refreshMonthData()
    }

    /**
     * 设置周字体.
     *
     * @param typeface [Typeface].
     */
    fun setWeekTypeface(typeface: Typeface?) {
        calendarManager.mWeekTypeface = typeface
        monthAdapter?.refreshMonthData()
    }

    /**
     * 设置文字颜色.
     *
     * @param textColor 文字颜色 [ColorInt].
     */
    fun setTextColor(@ColorInt textColor: Int) {
        calendarManager.mTextColor = textColor
        monthAdapter?.refreshMonthData()
    }

    /**
     * 设置选中后的的文字颜色.
     *
     * @param textColor 文字颜色 [ColorInt].
     */
    fun setSelectTextColor(@ColorInt textColor: Int) {
        calendarManager.mSelectTextColor = textColor
        monthAdapter?.refreshMonthData()
    }

    /**
     * 设置文字大小.
     *
     * @param textSize 文字大小 (sp).
     */
    fun setTextSize(textSize: Float) {
        calendarManager.mTextSize = textSize
        monthAdapter?.refreshMonthData()
    }

    /**
     * 设置选中后的的文字大小.
     *
     * @param textSize 文字大小 (sp).
     */
    fun setSelectTextSize(textSize: Float) {
        calendarManager.mSelectTextSize = textSize
        monthAdapter?.refreshMonthData()
    }

    /**
     * 设置字体.
     *
     * @param typeface [Typeface].
     */
    fun setTypeface(typeface: Typeface?) {
        calendarManager.mTypeface = typeface
        monthAdapter?.refreshMonthData()
    }

    /**
     * 设置天的背景.
     *
     * @param background 背景 drawable.
     */
    fun setDayBackground(background: Drawable?) {
        if (background != null && calendarManager.mDayBackground !== background) {
            calendarManager.mDayBackground = background
            setCompoundDrawablesWithIntrinsicBounds(calendarManager.mDayBackground)
        }
    }

    /**
     * 设置今天的背景.
     *
     * @param background 背景 drawable.
     */
    fun setTodayBackground(background: Drawable?) {
        if (background != null && calendarManager.mTodayBackground !== background) {
            calendarManager.mTodayBackground = background
            setCompoundDrawablesWithIntrinsicBounds(calendarManager.mTodayBackground)
        }
    }

    /**
     * 设置选择后天的背景.
     *
     * @param background 背景 drawable.
     */
    fun setSelectDayBackground(background: Drawable?) {
        if (background != null && calendarManager.mSelectDayBackground !== background) {
            calendarManager.mSelectDayBackground = background
            setCompoundDrawablesWithIntrinsicBounds(calendarManager.mSelectDayBackground)
        }
    }

    /**
     * 设置禁用天的背景.
     *
     * @param background 背景 drawable.
     */
    fun setDisableDayBackground(background: Drawable?) {
        if (background != null && calendarManager.mDisableDayBackground !== background) {
            calendarManager.mDisableDayBackground = background
            setCompoundDrawablesWithIntrinsicBounds(calendarManager.mDisableDayBackground)
        }
    }

    /**
     * 设置范围选择背景颜色.
     *
     * @param color 背景颜色 [ColorInt].
     */
    fun setRangeBackgroundColor(@ColorInt color: Int) {
        calendarManager.mRangeBackgroundColor = color
    }

    /**
     * 设置日期点击监听.
     *
     * @param listener 被通知的监听器.
     */
    fun setOnDateClickListener(listener: OnDateClickListener?) {
        mOnDateClickListener = listener
    }

    /**
     * 设置选中日期改变监听器.
     *
     * @param listener 被通知的监听器.
     */
    fun setOnDateChangeListener(listener: OnDateChangeListener?) {
        mOnDateChangeListener = listener
    }

    var dateFormatPattern: String?
        /**
         * 获取日期格式化格式.
         *
         * @return 格式化格式.
         */
        get() = calendarManager.mDateFormatPattern
        /**
         * 设置日期格式化格式.
         *
         * @param pattern 格式化格式, 如: yyyy-MM-dd.
         */
        set(pattern) {
            if (!TextUtils.isEmpty(pattern)) {
                calendarManager.mDateFormatPattern = pattern
            } else {
                calendarManager.mDateFormatPattern = DATE_FORMAT_PATTERN
            }
        }

    private fun setCompoundDrawablesWithIntrinsicBounds(drawable: Drawable?) {
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    }

    companion object {
        /** 默认的日期格式化格式  */
        private const val DATE_FORMAT_PATTERN = "yyyyMMdd"
    }
}
