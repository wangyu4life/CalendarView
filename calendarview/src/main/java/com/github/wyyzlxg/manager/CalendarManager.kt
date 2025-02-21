package com.github.wyyzlxg.manager

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.github.wyyzlxg.CalendarView.OnDateClickListener
import com.github.wyyzlxg.utils.DensityUtils
import java.util.Calendar
import java.util.Locale

/**
 * @author wangyu
 *
 */
internal class CalendarManager {

    private var _calendar: Calendar = Calendar.getInstance(Locale.CHINA)
    private var _startDate: Calendar? = null
    private var _endDate: Calendar? = null
    private var _weekTextColor = 0
    private var _weekTextSize = 0f
    private var _weekTypeface: Typeface? = null
    private var _textColor = 0
    private var _selectTextColor = 0
    private var _textSize = 0f
    private var _selectTextSize = 0f
    private var _typeface: Typeface? = null
    private var _dayBackground: Drawable? = null
    private var _todayBackground: Drawable? = null
    private var _selectDayBackground: Drawable? = null
    private var _disableDayBackground: Drawable? = null
    private var _rangeBackgroundColor = 0
    private var _isChangeDateStatus = false
    private var _selectMode = 0
    private var _selectDate: MutableList<String> = mutableListOf()
    private var _disableDate: MutableList<String> = mutableListOf()
    private var _dateFormatPattern: String? = null

    /** 当前显示的日期  */
    var mCalendar: Calendar
        get() {
            return _calendar
        }
        set(value) {
            _calendar = value
        }

    /** 日历开始的日期  */
    var mStartDate: Calendar?
        get() {
            return _startDate
        }
        set(value) {
            _startDate = value
        }

    /** 日历结束的日期  */
    var mEndDate: Calendar?
        get() {
            return _endDate
        }
        set(value) {
            _endDate = value
        }

    /** 默认周文字颜色  */
    var mWeekTextColor: Int
        get() {
            if (_weekTextColor == 0) {
                _weekTextColor = -0x1000000
            }
            return _weekTextColor
        }
        set(value) {
            _weekTextColor = value
        }

    /** 默认周文字大小  */
    var mWeekTextSize: Float
        get() {
            if (_weekTextSize == 0f) {
                _weekTextSize = DensityUtils.dp2px(14f).toFloat()
            }
            return _weekTextSize
        }
        set(value) {
            _weekTextSize = value
        }

    /** 默认周文字字体  */
    var mWeekTypeface: Typeface?
        get() {
            if (_weekTypeface == null) {
                _weekTypeface = Typeface.DEFAULT
            }
            return _weekTypeface
        }
        set(value) {
            _weekTypeface = value
        }

    /** 默认文字颜色  */
    var mTextColor: Int
        get() {
            if (_textColor == 0) {
                _textColor = -0x1000000
            }
            return _textColor
        }
        set(value) {
            _textColor = value
        }

    /** 选中后文字颜色  */
    var mSelectTextColor: Int
        get() {
            if (_selectTextColor == 0) {
                _selectTextColor = -0x1000000
            }
            return _selectTextColor
        }
        set(value) {
            _selectTextColor = value
        }

    /** 默认文字大小  */
    var mTextSize: Float
        get() {
            if (_textSize == 0f) {
                _textSize = DensityUtils.dp2px(14f).toFloat()
            }
            return _textSize
        }
        set(value) {
            _textSize = value
        }

    /** 选中后文字大小  */
    var mSelectTextSize: Float
        get() {
            if (_selectTextSize == 0f) {
                _selectTextSize = DensityUtils.dp2px(14f).toFloat()
            }
            return _selectTextSize
        }
        set(value) {
            _selectTextSize = value
        }

    /** 默认字体  */
    var mTypeface: Typeface?
        get() {
            if (_typeface == null) {
                _typeface = Typeface.DEFAULT
            }
            return _typeface
        }
        set(value) {
            _typeface = value
        }

    /** 默认天的背景  */
    var mDayBackground: Drawable?
        get() {
            return _dayBackground
        }
        set(value) {
            _dayBackground = value
        }

    /** 今天的背景  */
    var mTodayBackground: Drawable?
        get() {
            return _todayBackground
        }
        set(value) {
            _todayBackground = value
        }

    /** 选中后天的背景  */
    var mSelectDayBackground: Drawable?
        get() {
            return _selectDayBackground
        }
        set(value) {
            _selectDayBackground = value
        }

    /** 禁用天的背景  */
    var mDisableDayBackground: Drawable?
        get() {
            return _disableDayBackground
        }
        set(value) {
            _disableDayBackground = value
        }

    /** 范围背景颜色  */
    var mRangeBackgroundColor: Int
        get() {
            if (_rangeBackgroundColor == 0) {
                _rangeBackgroundColor = -0x1000000
            }
            return _rangeBackgroundColor
        }
        set(value) {
            _rangeBackgroundColor = value
        }

    /**
     * 设置点击是否能够改变日期状态 (默认或选中状态).
     *
     * 默认是 false, 即点击只会响应点击事件 [OnDateClickListener], 日期状态而不会做出任何改变.
     */
    var isChangeDateStatus: Boolean
        get() {
            return _isChangeDateStatus
        }
        set(value) {
            _isChangeDateStatus = value
        }

    /**
     * 选择模式
     *
     * 0 单选 默认
     * 1 多选
     * 2 范围选择
     */
    var selectMode: Int
        get() {
            return _selectMode
        }
        set(value) {
            _selectMode = value
        }

    /** 已选择日期数据  */
    var mSelectDate: MutableList<String>
        get() {
            return _selectDate
        }
        set(value) {
            _selectDate = value
        }

    /** 禁用的日期数据  */
    var mDisableDate: MutableList<String>
        get() {
            return _disableDate
        }
        set(value) {
            _disableDate = value
        }

    /** 日期格式化格式  */
    var mDateFormatPattern: String?
        get() {
            if (_dateFormatPattern == null) {
                _dateFormatPattern = "yyyyMMdd"
            }
            return _dateFormatPattern
        }
        set(value) {
            _dateFormatPattern = value
        }
}