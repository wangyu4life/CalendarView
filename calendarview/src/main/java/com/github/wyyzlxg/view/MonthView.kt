package com.github.wyyzlxg.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.ColorInt
import com.github.wyyzlxg.manager.CalendarManager
import com.github.wyyzlxg.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/**
 * @author wangyu
 *
 */
internal class MonthView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mCalendarManager: CalendarManager? = null

    /** 当前月份的日期  */
    private var mCalendar: Calendar = Calendar.getInstance(Locale.CHINA)

    /** 选中的日期  */
    private val mSelectCalendar: Calendar = Calendar.getInstance(Locale.CHINA)

    /** 是否显示月份标题  */
    private var mShowMonthTitle = true

    /** 月份文字画笔  */
    private val mMonthPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** 周文字画笔  */
    private val mWeekPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** 天文字画笔  */
    private val mDayPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** 范围选择背景画笔  */
    private val mRangeBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** 星期  */
    private val mWeeks = arrayOf("日", "一", "二", "三", "四", "五", "六")

    /** 默认月份文字大小  */
    private var mMonthTextSize = 0f

    /** 默认月份文字颜色  */
    private var mMonthTextColor = 0

    /** 默认月份文字字体  */
    private var mMonthTypeface: Typeface? = null

    /** 默认周文字大小  */
    private var mWeekTextSize = 0f

    /** 默认周文字颜色  */
    private var mWeekTextColor = 0

    /** 默认周文字字体  */
    private var mWeekTypeface: Typeface? = null

    /** 默认文字颜色  */
    private var mTextColor = 0

    /** 选中后文字颜色  */
    private var mSelectTextColor = 0

    /** 默认文字大小  */
    private var mTextSize = 0f

    /** 选中后文字大小  */
    private var mSelectTextSize = 0f

    /** 默认文字字体  */
    private var mTypeface: Typeface? = null

    /** 默认天的背景  */
    private var mDayBackground: Drawable? = null

    /** 今天的背景  */
    private var mTodayBackground: Drawable? = null

    /** 选中后天的背景  */
    private var mSelectDayBackground: Drawable? = null

    /** 禁用天的背景  */
    private var mDisableDayBackground: Drawable? = null

    /** 范围背景颜色  */
    private var mRangeBackgroundColor = 0

    /** 已选择日期数据  */
    private lateinit var mSelectDate: MutableList<String>

    /** 禁用的日期数据  */
    private lateinit var mDisableDate: MutableList<String>

    /** 日期状态是否能够改变  */
    private var mIsChangeDateStatus = false

    /** 存储对应列行处的天  */
    private val mDays = Array(6) { IntArray(7) }

    /** 日期格式化格式  */
    private var mDateFormatPattern: String? = null

    private var mDateFormat :SimpleDateFormat? = null

    /** 总行数  */
    private var mTotalRow = 8

    /** 月份和周行数  */
    private var mMonthWeekRow = 1

    /** 每列宽度  */
    private var mColumnWidth = 0

    /** 每行高度  */
    private var mRowHeight = 0

    private val mSlop: Int = ViewConfiguration.get(context!!).scaledTouchSlop
    private var mDownX = 0
    private var mDownY = 0

    /** 事件监听  */
    private var mOnDateClickListener: ((Int, Int, Int) -> Unit)? = null
    private var mOnDateStateChangeListener: ((List<String>) -> Unit)? = null

    /**
     * 设置日历管理器.
     *
     * @param manager 日历管理器.
     */
    fun setCalendarManager(manager: CalendarManager) {
        mCalendarManager = manager
        initStyle()
    }

    fun setOnDateClickListener(listener: (Int, Int, Int) -> Unit) {
        mOnDateClickListener = listener
    }

    fun setOnDateStateChangeListener(listener: (List<String>) -> Unit) {
        mOnDateStateChangeListener = listener
    }

    init {
        isClickable = true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isClickable) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x.toInt()
                mDownY = event.y.toInt()
                return true
            }

            MotionEvent.ACTION_MOVE -> {}
            MotionEvent.ACTION_UP -> {
                val upX = event.x.toInt()
                val upY = event.y.toInt()
                val diffX = abs((upX - mDownX).toDouble()).toInt()
                val diffY = abs((upY - mDownY).toDouble()).toInt()
                if (diffX < mSlop && diffY < mSlop) {
                    val column = upX / mColumnWidth
                    val row = upY / mRowHeight
                    if (row > mMonthWeekRow - 1) {
                        onClick(mDays[row - mMonthWeekRow][column])
                    }
                }
            }

            else -> {}
        }
        return super.onTouchEvent(event)
    }

    private fun onClick(day: Int) {
        if (day < 1) {
            return
        }
        val year = mCalendar[Calendar.YEAR]
        val month = mCalendar[Calendar.MONTH]
        val date = getFormatDate(year, month, day)
        if (mDisableDate.contains(date)) {
            return
        }
        mOnDateClickListener?.invoke(year, month, day)
        if (mIsChangeDateStatus) {
            when (mCalendarManager!!.selectMode) {
                0 -> {  // 单选
                    mSelectDate.clear()
                    mSelectDate.add(date)
                }

                1 -> {  // 多选
                    if (mSelectDate.contains(date) && mSelectDate.size > 1) {
                        mSelectDate.remove(date)
                    } else {
                        mSelectDate.add(date)
                    }
                }

                2 -> {  // 范围选择
                    when (mSelectDate.size) {
                        0 -> {
                            mSelectDate.add(date)
                        }

                        1 -> {
                            val startTime = DateUtils.getTimeMillisWithPattern(mSelectDate[0], mCalendarManager!!.dateFormatPattern!!)
                            val endTime = DateUtils.getTimeMillisWithPattern(date, mCalendarManager!!.dateFormatPattern!!)
                            if (startTime / 1000 >= endTime / 1000) {
                                mSelectDate.clear()
                                mSelectDate.add(date)
                            } else {
                                val startDate = Calendar.getInstance(Locale.CHINA)
                                startDate.timeInMillis = startTime
                                val endDate = Calendar.getInstance(Locale.CHINA)
                                endDate.timeInMillis = endTime

                                while (startDate.timeInMillis / 1000 < endDate.timeInMillis / 1000) {
                                    startDate.add(Calendar.DATE, 1)
                                    mSelectDate.add(getFormatDate(startDate[Calendar.YEAR], startDate[Calendar.MONTH], startDate[Calendar.DATE]))
                                }
                            }
                        }

                        else -> {
                            mSelectDate.clear()
                            mSelectDate.add(date)
                        }
                    }
                }
            }
            mOnDateStateChangeListener?.invoke(mSelectDate)
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, mRowHeight * mTotalRow)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mColumnWidth = width / 7

        /** 绘制月份标题  */
        if (mCalendarManager!!.showMonthTitle) {
            mMonthPaint.setColor(mMonthTextColor)
            mMonthPaint.textSize = mMonthTextSize
            if (mMonthTypeface != null) {
                mMonthPaint.setTypeface(mMonthTypeface)
            }
            val monthStr = "${mCalendar[Calendar.YEAR]}年${mCalendar[Calendar.MONTH] + 1}月"
            val textWidth = mMonthPaint.measureText(monthStr)
            val x = (width - textWidth) / 2
            val y = mRowHeight / 2 - (mMonthPaint.ascent() + mMonthPaint.descent()) / 2
            canvas.drawText(monthStr, x, y, mMonthPaint)
        }

        /** 绘制星期  */
        mWeekPaint.textSize = mWeekTextSize
        if (mWeekTypeface != null) {
            mWeekPaint.setTypeface(mWeekTypeface)
        }
        for (i in mWeeks.indices) {
            val text = mWeeks[i]
            val textWidth = mWeekPaint.measureText(text).toInt()
            val startX = mColumnWidth * i + (mColumnWidth - textWidth) / 2
            val startY = (mRowHeight / 2 + (if(mShowMonthTitle) mRowHeight else 0) - (mWeekPaint.ascent() + mWeekPaint.descent()) / 2).toInt()
            drawWeekText(canvas, text, mWeekTextColor, mWeekTextSize, startX, startY)
        }

        /** 绘制日期  */
        mDayPaint.textSize = mTextSize
        if (mTypeface != null) {
            mDayPaint.setTypeface(mTypeface)
        }
        val year = mCalendar[Calendar.YEAR]
        // 获取的月份
        val month = mCalendar[Calendar.MONTH]
        // 获取当月的天数
        val days = DateUtils.getMonthDays(year, month)
        // 获取当月第一天位于周几
        val week = DateUtils.getFirstDayWeek(year, month)
        // 今天
        val today = Calendar.getInstance(Locale.CHINA)
        // 绘制每天
        for (day in 1..days) {
            // 获取天在行、列的位置
            val row = (day - 1 + week - 1) / 7 + mMonthWeekRow
            val column = (day - 1 + week - 1) % 7
            // 存储对应天
            mDays[row - mMonthWeekRow][column] = day
            val dayStr = day.toString()
            val textWidth = mDayPaint.measureText(dayStr)
            val x = (mColumnWidth * column + (mColumnWidth - textWidth) / 2).toInt()
            val y = (mRowHeight * row + mRowHeight / 2 - (mDayPaint.ascent() + mDayPaint.descent()) / 2).toInt()

            // 判断 day 是否在选择日期内
            val cDate = Calendar.getInstance(Locale.CHINA)
            cDate.set(year, month, day)
            if (!mSelectDate.contains(getFormatDate(today[Calendar.YEAR], today[Calendar.MONTH], today[Calendar.DAY_OF_MONTH])) &&
                today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) == month && today.get(Calendar.DAY_OF_MONTH) == day
                ) {
                // 绘制今天背景和文字颜色
                drawBackground(canvas, mTodayBackground, column, row)
                drawDayText(canvas, dayStr, mTextColor, mTextSize, x, y)
            }
            if (mDisableDate.size > 0 &&
                mDisableDate.contains(getFormatDate(year, month, day))
            ) {
                // 绘制禁用背景和文字颜色
                drawBackground(canvas, mDisableDayBackground, column, row)
                drawDayText(canvas, dayStr, mTextColor, mTextSize, x, y)
            } else if (mSelectDate.size == 0 ||
                !mSelectDate.contains(getFormatDate(year, month, day))
            ) {
                // 绘制默认背景和文字颜色
                drawBackground(canvas, mDayBackground, column, row)
                drawDayText(canvas, dayStr, mTextColor, mTextSize, x, y)
            } else {
                // 绘制选择后的背景和文字颜色
                drawBackground(canvas, mSelectDayBackground, column, row, mCalendarManager!!.selectMode == 2)
                drawDayText(canvas, dayStr, mSelectTextColor, mSelectTextSize, x, y)
            }
        }
    }

    private fun drawWeekText(canvas: Canvas, text: String, @ColorInt color: Int, size: Float, x: Int, y: Int) {
        mWeekPaint.setColor(color)
        mWeekPaint.textSize = size
        if (mWeekTypeface != null) {
            mWeekPaint.setTypeface(mWeekTypeface)
        }
        canvas.drawText(text, x.toFloat(), y.toFloat(), mWeekPaint)
    }

    private fun drawBackground(canvas: Canvas, background: Drawable?, column: Int, row: Int, isRangeMode: Boolean = false) {
        if (isRangeMode && mSelectDate.size > 1) {
            mRangeBackgroundPaint.color = mRangeBackgroundColor
            val backgroundHeight = background?.intrinsicHeight ?: mRowHeight

            val day = mDays[row - mMonthWeekRow][column]
            val date = getFormatDate(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH], day)
            val index = mSelectDate.indexOf(date)
            when (index) {
                0 -> {
                    if ((day != 1 || DateUtils.getFirstDayWeek(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH]) != Calendar.SATURDAY) &&
                        day != DateUtils.getMonthDays(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH])
                    ) {
                        val rect = RectF(
                            (mColumnWidth * column + mColumnWidth / 2).toFloat(),
                            (mRowHeight * row + mRowHeight / 2 - backgroundHeight / 2).toFloat(),
                            (mColumnWidth * (column + 1)).toFloat(),
                            (mRowHeight * row + mRowHeight / 2 + backgroundHeight / 2).toFloat()
                        )
                        canvas.drawRect(rect, mRangeBackgroundPaint)
                    }
                    if (background != null) {
                        canvas.save()
                        val dx = mColumnWidth * column + mColumnWidth / 2 - background.intrinsicWidth / 2
                        val dy = mRowHeight * row + mRowHeight / 2 - background.intrinsicHeight / 2
                        canvas.translate(dx.toFloat(), dy.toFloat())
                        background.draw(canvas)
                        canvas.restore()
                    }
                }

                mSelectDate.size - 1 -> {
                    if ((day != 1 || DateUtils.getFirstDayWeek(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH]) != Calendar.SATURDAY) &&
                        (day != DateUtils.getMonthDays(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH]) ||
                                DateUtils.getLastDayWeek(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH]) != Calendar.SUNDAY)
                    ) {
                        val rect = RectF(
                            (mColumnWidth * column).toFloat(),
                            (mRowHeight * row + mRowHeight / 2 - backgroundHeight / 2).toFloat(),
                            (mColumnWidth * column + mColumnWidth / 2).toFloat(),
                            (mRowHeight * row + mRowHeight / 2 + backgroundHeight / 2).toFloat()
                        )
                        canvas.drawRect(rect, mRangeBackgroundPaint)
                    }
                    if (background != null) {
                        canvas.save()
                        val dx = mColumnWidth * column + mColumnWidth / 2 - background.intrinsicWidth / 2
                        val dy = mRowHeight * row + mRowHeight / 2 - background.intrinsicHeight / 2
                        canvas.translate(dx.toFloat(), dy.toFloat())
                        background.draw(canvas)
                        canvas.restore()
                    }
                }

                else -> {
                    val calendar = Calendar.getInstance()
                    calendar.set(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH], day)
                    val week = calendar[Calendar.DAY_OF_WEEK]

                    val rect = RectF(
                        (mColumnWidth * column).toFloat(),
                        (mRowHeight * row + mRowHeight / 2 - backgroundHeight / 2).toFloat(),
                        (mColumnWidth * (column + 1)).toFloat(),
                        (mRowHeight * row + mRowHeight / 2 + backgroundHeight / 2).toFloat()
                    )
                    val path = Path()
                    if (day == 1 && DateUtils.getFirstDayWeek(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH]) == Calendar.SATURDAY ||
                        day == DateUtils.getMonthDays(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH]) && DateUtils.getLastDayWeek(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH]) == Calendar.SUNDAY) {
                        path.addRoundRect(rect, floatArrayOf(backgroundHeight.toFloat(),backgroundHeight.toFloat(),backgroundHeight.toFloat(),backgroundHeight.toFloat(),backgroundHeight.toFloat(),backgroundHeight.toFloat(),backgroundHeight.toFloat(),backgroundHeight.toFloat()), Path.Direction.CW)
                    } else if (week == Calendar.SUNDAY || day == 1) {
                        path.addRoundRect(rect, floatArrayOf(backgroundHeight.toFloat(),backgroundHeight.toFloat(),0f,0f,0f,0f,backgroundHeight.toFloat(),backgroundHeight.toFloat()), Path.Direction.CW)
                    } else if(week == Calendar.SATURDAY || day == DateUtils.getMonthDays(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH])) {
                        path.addRoundRect(rect, floatArrayOf(0f,0f,backgroundHeight.toFloat(),backgroundHeight.toFloat(),backgroundHeight.toFloat(),backgroundHeight.toFloat(),0f,0f), Path.Direction.CW)
                    } else {
                        path.addRoundRect(rect, floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f), Path.Direction.CW)
                    }
                    canvas.drawPath(path, mRangeBackgroundPaint)
                }
            }

        } else {
            if (background != null) {
                canvas.save()
                val dx = mColumnWidth * column + mColumnWidth / 2 - background.intrinsicWidth / 2
                val dy = mRowHeight * row + mRowHeight / 2 - background.intrinsicHeight / 2
                canvas.translate(dx.toFloat(), dy.toFloat())
                background.draw(canvas)
                canvas.restore()
            }
        }
    }

    private fun drawDayText(canvas: Canvas, text: String, @ColorInt color: Int, size: Float, x: Int, y: Int) {
        mDayPaint.setColor(color)
        mDayPaint.textSize = size
        if (mTypeface != null) {
            mDayPaint.setTypeface(mTypeface)
        }
        canvas.drawText(text, x.toFloat(), y.toFloat(), mDayPaint)
    }

    fun refresh(date: Date) {
        mCalendar.time = date
        calculateTotalRow()
        initStyle()
        invalidate()
    }

    private fun initStyle() {
        if (mCalendarManager != null) {
            mShowMonthTitle = mCalendarManager!!.showMonthTitle
            mMonthTextColor = mCalendarManager!!.monthTextColor
            mMonthTextSize = mCalendarManager!!.monthTextSize
            mMonthTypeface = mCalendarManager!!.monthTypeface

            mWeekTextColor = mCalendarManager!!.weekTextColor
            mWeekTextSize = mCalendarManager!!.weekTextSize
            mWeekTypeface = mCalendarManager!!.weekTypeface

            mTextColor = mCalendarManager!!.textColor
            mSelectTextColor = mCalendarManager!!.selectTextColor
            mTextSize = mCalendarManager!!.textSize
            mSelectTextSize = mCalendarManager!!.selectTextSize

            mRangeBackgroundColor = mCalendarManager!!.rangeBackgroundColor
            mDayBackground = mCalendarManager!!.dayBackground
            mTodayBackground = mCalendarManager!!.todayBackground
            mSelectDayBackground = mCalendarManager!!.selectDayBackground
            mDisableDayBackground = mCalendarManager!!.disableDayBackground
            mSelectDate = mCalendarManager!!.selectDate
            mDisableDate = mCalendarManager!!.disableDate

            mRowHeight = mCalendarManager!!.rowHeight
            mIsChangeDateStatus = mCalendarManager!!.isChangeDateStatus
            mDateFormatPattern = mCalendarManager!!.dateFormatPattern
            mDateFormat = SimpleDateFormat(mCalendarManager!!.dateFormatPattern, Locale.CHINA)
        }
    }

    /**
     * 计算总行数.
     */
    private fun calculateTotalRow() {
        val year = mCalendar[Calendar.YEAR]
        val month = mCalendar[Calendar.MONTH]
        val days = DateUtils.getMonthDays(year, month)
        val week = DateUtils.getFirstDayWeek(year, month)
        mMonthWeekRow = if (mShowMonthTitle) 2 else 1
        mTotalRow = if (DateUtils.getLastDayWeek(year, month) == Calendar.SATURDAY) {
            (days + week - 1) / 7 + mMonthWeekRow
        } else {
            (days + week - 1) / 7 + 1 + mMonthWeekRow
        }
    }

    /**
     * 根据指定的年月日按当前日历的格式格式化后返回.
     *
     * @param year  年.
     * @param month 月.
     * @param day   日.
     * @return 格式化后的日期.
     */
    private fun getFormatDate(year: Int, month: Int, day: Int): String {
        mSelectCalendar[year, month] = day
        return mDateFormat!!.format(mSelectCalendar.time)
    }
}