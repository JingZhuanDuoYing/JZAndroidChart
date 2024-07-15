package cn.jingzhuan.lib.chart2.demo.chart3

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import cn.jingzhuan.lib.chart.utils.ForceAlign
import cn.jingzhuan.lib.chart2.demo.R
import cn.jingzhuan.lib.chart2.demo.chart3.chart.MainCallAuctionChartView
import cn.jingzhuan.lib.chart2.demo.chart3.chart.MainKlineChartView
import cn.jingzhuan.lib.chart2.demo.chart3.chart.MainMinuteChartView
import cn.jingzhuan.lib.chart2.demo.chart3.chart.OnSubChartTouchListener
import cn.jingzhuan.lib.chart2.demo.chart3.chart.SubChartView
import cn.jingzhuan.lib.chart3.Highlight
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.TimeRange
import cn.jingzhuan.lib.chart3.data.dataset.BarDataSet
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.MinuteLineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterTextDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ZeroCenterBarDataSet
import cn.jingzhuan.lib.chart3.data.value.BarValue
import cn.jingzhuan.lib.chart3.data.value.CandlestickValue
import cn.jingzhuan.lib.chart3.data.value.DrawLineValue
import cn.jingzhuan.lib.chart3.data.value.LineValue
import cn.jingzhuan.lib.chart3.data.value.ScatterTextValue
import cn.jingzhuan.lib.chart3.data.value.ScatterValue
import cn.jingzhuan.lib.chart3.drawline.DrawLineState
import cn.jingzhuan.lib.chart3.drawline.DrawLineType
import cn.jingzhuan.lib.chart3.event.OnDrawLineListener
import cn.jingzhuan.lib.chart3.event.OnFlagClickListener
import cn.jingzhuan.lib.chart3.event.OnHighlightListener
import cn.jingzhuan.lib.chart3.event.OnRangeChangeListener
import cn.jingzhuan.lib.chart3.event.OnScaleListener
import cn.jingzhuan.lib.chart3.formatter.DateTimeFormatter
import cn.jingzhuan.lib.chart3.formatter.DateTimeFormatter.formatTime
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_HISTORY_MINUTE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_LHB
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_LIMIT_UP
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_NOTICE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_SIMULATE_TRADE_DETAIL
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_TRADE_DETAIL
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_FOREVER
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_BOTTOM
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_CENTER
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_PARENT_BOTTOM
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_TOP
import cn.jingzhuan.lib.chart3.widget.KlineTimeRangeView
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class Chart3Activity : AppCompatActivity() {

    private lateinit var klineMain: MainKlineChartView

    private lateinit var llKlineOp: LinearLayout

    private lateinit var tvZoomIn: TextView

    private lateinit var tvZoomOot: TextView

    private lateinit var tvRange: TextView

    private lateinit var tvFull: TextView

    private lateinit var tvPriceLine: TextView

    private lateinit var tvDrawLine: TextView

    private lateinit var tvDrawSegment: TextView

    private lateinit var tvDrawStraight: TextView

    private lateinit var tvDrawEndAnchor: TextView

    private lateinit var tvDrawRect: TextView

    private lateinit var tvDrawHJFG: TextView

    private lateinit var tvDrawFBNC: TextView

    private lateinit var tvDrawHorizon: TextView

    private lateinit var tvDrawVertical: TextView

    private lateinit var tvDrawParallel: TextView

    private lateinit var tvDrawRay: TextView

    private lateinit var tvDrawPriceLabel: TextView

    private lateinit var tvDrawFont: TextView

    private lateinit var tvAdsorb: TextView

    private lateinit var tvRevoke: TextView

    private lateinit var tvDelete: TextView

    private lateinit var minuteMain: MainMinuteChartView

    private lateinit var callAuctionMain: MainCallAuctionChartView

    private lateinit var sub1: SubChartView

    private lateinit var sub2: SubChartView

    private lateinit var rg: RadioGroup

    private lateinit var rbDay: RadioButton

    private lateinit var rbYear: RadioButton

    private lateinit var rbMinute: RadioButton

    private lateinit var rbCallAuction: RadioButton

    private lateinit var llHistory: LinearLayout

    private lateinit var llDrawLineTool: ConstraintLayout

    private lateinit var tvInfo: TextView

    private lateinit var timeRangeView: KlineTimeRangeView

    private lateinit var tvStep: TextView

    private lateinit var ivCap: ImageView

    private val scatterDrawable by lazy { ContextCompat.getDrawable(this@Chart3Activity, R.drawable.ico_range_touch_left) }

    private val subCharts by lazy { mutableListOf(sub1, sub2) }

    private val lastClose = 3152.98f

    private val highPrice = 3365.22f

    private val lowPrice = 2940.74f

    private var klineList = mutableListOf<CandlestickValue>()

    private val sb = StringBuilder()

    private var showPriceLine = false

    private var index = 2

    private val minuteHistoryTimes: List<Long> by lazy {
        val historyTimes = ArrayList<Long>()
        val diff = 2420 - 242
        var i = diff - 1

        while (i > 0) {
            val time = 1692322131L - 86400 * (242 + i)
            historyTimes.add(time)
            i--
        }

        historyTimes.addAll(DataConfig.candlestickList.map { it.time })
        historyTimes
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart3)

        klineList = DataConfig.candlestickList.toMutableList()

        initView()

        initListener()
//        val timer = Timer()
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                runOnUiThread {
//                    setMainKlineChartData(update = true)
//                }
//            }
//        }, 0, 1000L)
        setMainKlineChartData(update = false)
        setSubKlineChartData()
    }

    private fun initView() {
        klineMain = findViewById(R.id.kline_main)
        llKlineOp = findViewById(R.id.ll_kline_op)
        tvZoomIn = findViewById(R.id.tv_zoom_in)
        tvZoomOot = findViewById(R.id.tv_zoom_out)
        tvRange = findViewById(R.id.tv_range)
        tvFull = findViewById(R.id.tv_full)
        tvPriceLine = findViewById(R.id.tv_price_line)
        tvDrawLine = findViewById(R.id.tv_draw_line)
        tvDrawSegment = findViewById(R.id.tv_draw_segment)
        tvDrawStraight = findViewById(R.id.tv_draw_straight)
        tvDrawEndAnchor = findViewById(R.id.tv_draw_end_anchor)
        tvDrawRect = findViewById(R.id.tv_draw_rect)
        tvDrawHJFG = findViewById(R.id.tv_draw_hjfg)
        tvDrawFBNC = findViewById(R.id.tv_draw_fbnc)
        tvDrawHorizon = findViewById(R.id.tv_draw_horizon)
        tvDrawVertical = findViewById(R.id.tv_draw_vertical)
        tvDrawRay = findViewById(R.id.tv_draw_ray)
        tvDrawPriceLabel = findViewById(R.id.tv_draw_price_label)
        tvDrawFont = findViewById(R.id.tv_draw_font)
        tvDrawParallel = findViewById(R.id.tv_draw_parallel)
        tvAdsorb = findViewById(R.id.tv_adsorb)
        tvRevoke = findViewById(R.id.tv_revoke)
        tvDelete = findViewById(R.id.tv_delete)
        minuteMain = findViewById(R.id.minute_main)
        callAuctionMain = findViewById(R.id.call_auction_main)
        sub1 = findViewById(R.id.kline_sub1)
        sub2 = findViewById(R.id.kline_sub2)

        subCharts.forEach { it.relatedMainChart(klineMain) }

        rg = findViewById(R.id.rg)
        rbDay = findViewById(R.id.rb_day)
        rbYear = findViewById(R.id.rb_year)
        rbMinute = findViewById(R.id.rb_minute)
        rbCallAuction = findViewById(R.id.rb_callAuction)
        llHistory = findViewById(R.id.ll_history)
        llDrawLineTool = findViewById(R.id.ll_draw_line_tool)
        tvInfo = findViewById(R.id.tv_info)
        timeRangeView = findViewById(R.id.time_range_view)
        tvStep = findViewById(R.id.tv_step)
        ivCap = findViewById(R.id.iv_cap)
    }


    private fun initListener() {
        if (rbDay.isChecked) {
            klineMain.showBottomFlags = true
            klineMain.valueIndexPattern = "yyyy-MM-dd"
        }
        rg.setOnCheckedChangeListener { group, id ->
            minuteMain.onHighlightClean()
            when (id) {
                rbDay.id -> {
                    klineMain.visibility = View.VISIBLE
                    minuteMain.visibility = View.GONE
                    callAuctionMain.visibility = View.GONE
                    llKlineOp.visibility = View.VISIBLE
                    klineMain.apply {
                        showBottomFlags = true
                        valueIndexPattern = "yyyy-MM-dd"
                        finishScroll()
                        cleanHighlight()
                        cleanAllDataSet()
                        cleanRange()
                        currentViewport = Viewport()
                        invalidate()
                    }

                    subCharts.forEach {
                        it.visibility = View.VISIBLE
                        it.cleanAllDataSet()
                    }

                    setMainKlineChartData()
                    setSubKlineChartData()
                }

                rbYear.id -> {
                    klineMain.visibility = View.VISIBLE
                    minuteMain.visibility = View.GONE
                    callAuctionMain.visibility = View.GONE
                    llKlineOp.visibility = View.VISIBLE
                    klineMain.apply {
                        showBottomFlags = false
                        valueIndexPattern = "dd/HH:mm"
                        finishScroll()
                        cleanHighlight()
                        cleanAllDataSet()
                        cleanRange()
                        currentViewport = Viewport()
                        invalidate()
                    }

                    subCharts.forEach {
                        it.visibility = View.VISIBLE
                        it.cleanAllDataSet()
                    }
                    setMainKlineYearChartData()
                    setSubKlineYearChartData()
                }

                rbMinute.id -> {
                    klineMain.visibility = View.GONE
                    minuteMain.visibility = View.VISIBLE
                    callAuctionMain.visibility = View.GONE
                    llKlineOp.visibility = View.GONE
                    subCharts.forEach {
                        it.visibility = View.GONE
                    }

                    setMainMinuteChartData()
                }

                rbCallAuction.id -> {
                    klineMain.visibility = View.GONE
                    minuteMain.visibility = View.GONE
                    callAuctionMain.visibility = View.VISIBLE
                    llKlineOp.visibility = View.GONE
                    subCharts.forEach {
                        it.visibility = View.GONE
                    }
                    setMainCallAuctionChartData()
                }
            }
        }

        klineMain.apply {
            setViewportChangeListener { viewport->

                subCharts.forEach {
                    if (it.totalEntryCount != klineMain.totalEntryCount){
                        it.totalEntryCount = klineMain.totalEntryCount
                    }
                    it.setCurrentViewport(viewport)
                }

                if (isHighlightEnable) {
                    val entryCount = klineList.size
                    val index = if (viewport.right > 1f) {
                        entryCount - 1
                    } else {
                        (viewport.right * entryCount - 1).roundToInt()
                    }
                    sb.clear()
                    sb.append("开: ${klineList.getOrNull(index)?.open} 高: ${klineList.getOrNull(index)?.high} 低: ${klineList.getOrNull(index)?.low} 收: ${klineList.getOrNull(index)?.close}")
                    runOnUiThread {
                        tvInfo.text = sb.toString()
                    }
                }
            }
            setOnHighlightListener(object : OnHighlightListener {
                override fun onHighlightShow(highlight: Highlight?) {
                    Log.d("klineMain", "onHighlightShow: ")
                    val index = highlight?.dataIndex ?: 0
                    sb.clear()
                    sb.append("开: ${klineList.getOrNull(index)?.open} 高: ${klineList.getOrNull(index)?.high} 低: ${klineList.getOrNull(index)?.low} 收: ${klineList.getOrNull(index)?.close}")
                    runOnUiThread {
                        tvInfo.text = sb.toString()
                    }
                    subCharts.forEach {
                        val h = Highlight().apply {
                            x = highlight?.x ?: Float.NaN
                            y = Float.NaN
                            dataIndex = index
                            touchX = highlight?.touchX ?: Float.NaN
                            touchY = Float.NaN
                        }
                        it.highlightValue(h)
                    }
                }

                override fun onHighlightHide() {
                    Log.d("klineMain", "onHighlightHide: ")
                    subCharts.forEach { it.onHighlightClean() }
                }

            })

            addOnFlagClickListener(object : OnFlagClickListener{
                override fun onClick(type: Int, index: Int) {
                    when (type) {
                        FLAG_HISTORY_MINUTE -> {
                            if (klineMain.highlightState == HIGHLIGHT_STATUS_FOREVER) return
                            llHistory.visibility = View.VISIBLE
                            klineMain.highlightState = HIGHLIGHT_STATUS_FOREVER
                            Toast.makeText(this@Chart3Activity, "历史分时-> $index", Toast.LENGTH_SHORT).show()
                        }
                        FLAG_TRADE_DETAIL -> {
                            Toast.makeText(this@Chart3Activity, "交易详情-> $index", Toast.LENGTH_SHORT).show()
                        }
                        FLAG_SIMULATE_TRADE_DETAIL -> {
                            Toast.makeText(this@Chart3Activity, "交易详情(模)-> $index", Toast.LENGTH_SHORT).show()
                        }
                        FLAG_LIMIT_UP -> {
                            Toast.makeText(this@Chart3Activity, "涨停分析-> $index", Toast.LENGTH_SHORT).show()
                        }
                        FLAG_NOTICE -> {
                            Toast.makeText(this@Chart3Activity, "公告-> $index", Toast.LENGTH_SHORT).show()
                        }
                        FLAG_LHB -> {
                            Toast.makeText(this@Chart3Activity, "龙虎榜-> $index", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            })

            setOnRangeChangeListener(object : OnRangeChangeListener{
                override fun onRange(startIndex: Int, endIndex: Int, touchType: Int) {
                    val startX = klineMain.getEntryX(startIndex)
                    val endX = klineMain.getEntryX(endIndex)
                    val cycle = endIndex - startIndex + 1
                    val startTime = DateTimeFormatter.ofPattern(bottomLabelPattern).formatTime((klineList.getOrNull(startIndex)?.time ?: 0) * 1000L)
                    val endTime = DateTimeFormatter.ofPattern(bottomLabelPattern).formatTime((klineList.getOrNull(endIndex)?.time ?: 0) * 1000L)

                    val data = TimeRange(startTime, endTime, "${cycle}周期", startX, endX, touchType)
                    Log.d("klineMain", "OnRangeChangeListener: startIndex =$startIndex, endIndex=$endIndex, touchType=$touchType")
                    timeRangeView.timeRange = data
                }

                override fun onClose() {
                    Toast.makeText(this@Chart3Activity, "关闭区间统计", Toast.LENGTH_SHORT).show()
                    tvInfo.visibility = View.VISIBLE
                    timeRangeView.visibility = View.GONE
                }

            })

            setOnScaleListener(object: OnScaleListener {
                override fun onScaleStart(viewport: Viewport) {
                }

                override fun onScale(viewport: Viewport, isMin: Boolean, isMax: Boolean) {
                    if (isMin) {
                        Toast.makeText(context, "已缩小为最小值", Toast.LENGTH_SHORT).show()
                    }
                    if (isMax) {
                        Toast.makeText(context, "已放大至最大值", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onScaleEnd(viewport: Viewport) {

                }

            })

            setOnLoadMoreListener {
                isLoadMore = true
                setMainKlineChartData(loadMore = true)
                setSubKlineChartData()
            }

            setOnDrawLineListener(object : OnDrawLineListener {
                override fun onTouchText(onSuccess: (String) -> Unit) {
                    onSuccess("你是谁，在干什么？你是谁，在干什么？你是谁，在干什么？你是谁，在干什么？你是谁")
                    tvStep.visibility = View.VISIBLE
                    tvStep.text = "已完成"
                    val timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                tvStep.visibility = View.GONE
                                tvStep.text = ""
                            }
                        }
                    }, 1000L)
                }
                override fun onTouch(state: DrawLineState, lineKey: String, type: Int, reselected: Boolean) {
                    if (type == 0) {
//                        Toast.makeText(this@Chart3Activity, "点击了外部", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if (reselected) return
                    if (state == DrawLineState.first) {
                        tvStep.visibility = View.VISIBLE
                        tvStep.text = if(type == DrawLineType.ltParallelLine2.ordinal) "请确第一个点的位置" else "请点击放置终点 1/2"
                    } else if (state == DrawLineState.second) {
                        if(type == DrawLineType.ltParallelLine2.ordinal) {
                            tvStep.text = "请确第二个点的位置"
                        }
                    } else if (state == DrawLineState.complete) {
                        tvStep.visibility = View.VISIBLE
                        tvStep.text = "已完成"

                        val timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                runOnUiThread {
                                    tvStep.visibility = View.GONE
                                    tvStep.text = ""
                                }
                            }
                        }, 1000L)
                    }
                }

                override fun onDrag(bitmapPoint: PointF, translatePoint: PointF, state: Int) {
                    if (state != ChartConstant.DRAW_LINE_NONE) {
                        ivCap.visibility = View.GONE
                        val bitmap =  getBitmap(bitmapPoint)
                        ivCap.visibility = View.VISIBLE
                        ivCap.setImageBitmap(bitmap)
                        ivCap.translationX = translatePoint.x - 100f
                        ivCap.translationY = translatePoint.y - 50f
                    } else {
                        ivCap.visibility = View.GONE
                        ivCap.setImageBitmap(null)
                    }
                    Log.d("onPressDrawLine", "正在拖拽 point={${translatePoint.x}, ${translatePoint.y}----state=$state")
                }

            })
        }

        minuteMain.apply {
            setOnDrawLineListener(object : OnDrawLineListener {
                override fun onTouchText(onSuccess: (String) -> Unit) {
                    onSuccess("你是谁，在干什么？")
                    tvStep.visibility = View.VISIBLE
                    tvStep.text = "已完成"
                    val timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                tvStep.visibility = View.GONE
                                tvStep.text = ""
                            }
                        }
                    }, 1000L)
                }


                override fun onTouch(state: DrawLineState, lineKey: String, type: Int, reselected: Boolean) {
                    if (reselected) return
                    if (state == DrawLineState.first) {
                        tvStep.visibility = View.VISIBLE
                        tvStep.text = if(type == DrawLineType.ltParallelLine2.ordinal) "请确第一个点的位置" else "请点击放置终点 1/2"
                    } else if (state == DrawLineState.second) {
                        if(type == DrawLineType.ltParallelLine2.ordinal) {
                            tvStep.text = "请确第二个点的位置"
                        }
                    } else if (state == DrawLineState.complete) {
                        tvStep.visibility = View.VISIBLE
                        tvStep.text = "已完成"

                        val timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                runOnUiThread {
                                    tvStep.visibility = View.GONE
                                    tvStep.text = ""
                                }
                            }
                        }, 1000L)
                    }
                }

                override fun onDrag(bitmapPoint: PointF, translatePoint: PointF, state: Int) {
                    if (state != ChartConstant.DRAW_LINE_NONE) {
                        ivCap.visibility = View.GONE
                        val bitmap =  getBitmap(bitmapPoint)
                        ivCap.visibility = View.VISIBLE
                        ivCap.setImageBitmap(bitmap)
                        ivCap.translationX = translatePoint.x - 100f
                        ivCap.translationY = translatePoint.y - 50f
                    } else {
                        ivCap.visibility = View.GONE
                        ivCap.setImageBitmap(null)
                    }
                    Log.d("onPressDrawLine", "正在拖拽 point={${translatePoint.x}, ${translatePoint.y}----state=$state")
                }

            })
        }

        subCharts.forEach {
            it.apply {
                addSubChartTouchListener(object : OnSubChartTouchListener{
                    override fun touchHighlight(highlight: Highlight, x: Float, y: Float) {
                        touchSubHighlight(highlight, x, y)

                    }

                    override fun checkIfClickToSwitch() {
                        Toast.makeText(this@Chart3Activity, "切换指标", Toast.LENGTH_SHORT).show()
                        val index = subCharts.indexOf(it)
                        if (index == 1) {
                            val lineDataSets = (it.chartData as CombineData).getLineDataSets()
                            lineDataSets.forEach { dataSet->
                                dataSet.isPointLine = false
                            }
                            it.postInvalidate()
                        }
                    }

                })
            }
        }

        tvZoomIn.setOnClickListener {
            klineMain.zoomIn(ForceAlign.LEFT)
        }

        tvZoomOot.setOnClickListener {
            klineMain.zoomOut(ForceAlign.LEFT)
        }

        tvRange.setOnClickListener {
            if (!klineMain.isOpenRange) {
                klineMain.openRange()
                tvInfo.visibility = View.GONE
                timeRangeView.visibility = View.VISIBLE
            }
        }

        llHistory.setOnClickListener {
            klineMain.onHighlightClean()
            llHistory.visibility = View.GONE

        }

        tvFull.setOnClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        tvPriceLine.setOnClickListener {
            showPriceLine = !showPriceLine
            klineMain.isShowLastPriceLine = showPriceLine
            klineMain.postInvalidate()
        }

        tvDrawLine.setOnClickListener {
            if (rbDay.isChecked) {
                klineMain.isOpenDrawLine = true
            } else if (rbMinute.isChecked) {
                minuteMain.isOpenDrawLine = true
            }
            llDrawLineTool.visibility = View.VISIBLE
        }

        tvDrawSegment.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltSegment$index"
                lineType = DrawLineType.ltSegment.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                dash = "7,2"
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置起点 0/2"
        }

        tvDrawStraight.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltStraight$index"
                lineType = DrawLineType.ltStraightLine.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                dash = "7,2"
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置起点 0/2"
        }

        tvDrawEndAnchor.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltEndAnchorLine$index"
                lineType = DrawLineType.ltEndAnchorLine.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                dash = "7,2"
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置起点 0/2"
        }

        tvDrawRect.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltRect$index"
                lineType = DrawLineType.ltRect.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                dash = "7,2"
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置起点 0/2"
        }

        tvDrawHJFG.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltHJFG$index"
                lineType = DrawLineType.ltHJFG.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                dash = "7,2"
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置起点 0/2"
        }

        tvDrawFBNC.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltFBNC$index"
                lineType = DrawLineType.ltFBNC.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                dash = "7,2"
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置起点 0/2"
        }

        tvDrawParallel.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltParallelLine2$index"
                lineType = DrawLineType.ltParallelLine2.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                dash = "7,2"
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置起点 0/3"
        }

        tvDrawHorizon.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltHorizon$index"
                lineType = DrawLineType.ltHorizon.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置水平线 0/1"
        }

        tvDrawVertical.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltVerticalLine$index"
                lineType = DrawLineType.ltVerticalLine.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置垂直线 0/1"
        }

        tvDrawRay.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltRaysLine$index"
                lineType = DrawLineType.ltRaysLine.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置起点 0/2"
        }

        tvDrawPriceLabel.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltPriceLabel$index"
                lineType = DrawLineType.ltPriceLabel.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置价格标注 0/2"
        }

        tvDrawFont.setOnClickListener {
            index ++

            val dataSet = DrawLineDataSet().apply {
                lineKey = "ltFont$index"
                lineType = DrawLineType.ltFont.ordinal
                lineState = DrawLineState.prepare
                lineSize = 1f
                appFontSize = 14
                maxTextWidth = dp2px(112f)
                cycle = if (rbDay.isChecked) 8 else -1
            }

            if (rbDay.isChecked) {
                klineMain.chartData.add(dataSet)
            } else if (rbMinute.isChecked) {
                minuteMain.chartData.add(dataSet)
            }
            tvStep.visibility = View.VISIBLE
            tvStep.text = "请点击放置文字标注 0/1"
        }

        if (klineMain.isDrawLineAdsorb) {
            tvAdsorb.setBackgroundColor(ContextCompat.getColor(tvAdsorb.context, R.color.colorPrimary))
        }

        tvAdsorb.setOnClickListener {
            klineMain.isDrawLineAdsorb = !klineMain.isDrawLineAdsorb
            if (klineMain.isDrawLineAdsorb) {
                tvAdsorb.setBackgroundColor(ContextCompat.getColor(tvAdsorb.context, R.color.colorPrimary))
            } else {
                tvAdsorb.setBackgroundColor(Color.TRANSPARENT)
            }
        }


        tvRevoke.setOnClickListener {
            if (rbDay.isChecked) {
                val data =  (klineMain.chartData as CombineData)
                val drawLineDataSets = data.getDrawLineDataSets().toMutableList()
                if (drawLineDataSets.isEmpty()) return@setOnClickListener
                drawLineDataSets.removeLast()
                data.drawLineChartData.clear()
                data.addAll(drawLineDataSets)
                klineMain.postInvalidate()

            } else if (rbMinute.isChecked) {

            }
        }

        tvDelete.setOnClickListener {
            if (rbDay.isChecked) {
                val data =  (klineMain.chartData as CombineData)
                data.drawLineChartData.clear()
                klineMain.postInvalidate()

            }
        }

        llDrawLineTool.setOnClickListener {
            llDrawLineTool.visibility = View.GONE
            if (rbDay.isChecked) {
                (klineMain.chartData as CombineData).getDrawLineDataSets().forEach {
                    it.isSelect = false
                }
                klineMain.isOpenDrawLine = false
            } else if (rbMinute.isChecked) {
                minuteMain.isOpenDrawLine = false
            }
        }

    }

    private fun getBitmap(point: PointF): Bitmap {
        val view = window.decorView
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.save()
        canvas.clipRect(point.x - 100f, point.y - 100f, point.x + 100f, point.y +100f)
        view.draw(canvas)
        canvas.restore()
        view.isDrawingCacheEnabled = false

        // 宽高缩小3倍
        val width = dp2px(100f / 3f)
        val height = dp2px(80f / 3f)
        var x = max(point.x- width * 0.5f, 0f).toInt()

        if (x >= bitmap.width - width) x = bitmap.width - width
        var y = max(point.y - height * 0.5f, 0f).toInt()
        if (y >= bitmap.height - height) y = bitmap.height - height
        val clipBitmap = Bitmap.createBitmap(bitmap, x, y, width, height)
        return clipBitmap
    }

    private fun touchSubHighlight(highlight: Highlight, ex: Float, ey: Float) {
        highlight.touchX = ex
        highlight.touchY = Float.NaN
        var index = max(klineMain.getEntryIndex(ex), 0)
        val dataSet = klineMain.chartData.getTouchDataSet()
        if (dataSet != null && dataSet.values.isNotEmpty()) {
            index = min(index, dataSet.values.size - 1)
            val value = dataSet.values.getOrNull(index) ?: return
            highlight.x = value.x
            highlight.y = Float.NaN
            highlight.dataIndex = index
            klineMain.highlightValue(highlight)
        }
    }

    private fun setSubKlineYearChartData() {
        var candlestickList = DataConfig.candlestickList
        candlestickList = candlestickList.subList(0, 30)
        val barList = ArrayList<BarValue>()
        val zeroBarList = ArrayList<BarValue>()

        var color: Int
        var style: Paint.Style
        candlestickList.forEachIndexed { index, value ->
            if (value.open < value.close) {
                color = Color.RED
                style = Paint.Style.STROKE
            } else if (value.open > value.close) {
                color = Color.GREEN
                style = Paint.Style.FILL
            } else {
                color = Color.GRAY
                style = Paint.Style.FILL
            }
            barList.add(BarValue(value.low, color, style))

            if (index % 3 == 0) {
                zeroBarList.add(BarValue(-value.high, color, Paint.Style.FILL))
            } else {
                zeroBarList.add(BarValue(value.high, color, Paint.Style.FILL))
            }
        }

        val barDataSet = BarDataSet(barList).apply {
            isAutoBarWidth = true
            axisDependency = AxisY.DEPENDENCY_LEFT
        }
        val data = CombineData().apply {
            add(barDataSet)
        }

        val zeroBarDataSet = ZeroCenterBarDataSet(zeroBarList).apply {
            isAutoBarWidth = true
            axisDependency = AxisY.DEPENDENCY_LEFT
        }
        val zeroData = CombineData().apply {
            add(zeroBarDataSet)
        }

        subCharts.forEachIndexed { index, subChartView ->
            if (index == 0) {
                subChartView.setCombineData(data)
            }
            if (index == 1) {
                subChartView.setCombineData(zeroData)
            }
        }
    }
    private fun setSubKlineChartData(update: Boolean = false, loadMore: Boolean = false) {
        val barList = ArrayList<BarValue>()
        val lineList = ArrayList<LineValue>()

        var color: Int
        var style: Paint.Style
        klineList.forEachIndexed { index, value ->
            if (value.open < value.close) {
                color = Color.RED
                style = Paint.Style.STROKE
            } else if (value.open > value.close) {
                color = Color.GREEN
                style = Paint.Style.FILL
            } else {
                color = Color.GRAY
                style = Paint.Style.FILL
            }
            barList.add(BarValue(value.low, color, style))

            lineList.add(LineValue(value.close))

        }

        val barDataSet = BarDataSet(barList).apply {
            isAutoBarWidth = true
            axisDependency = AxisY.DEPENDENCY_LEFT
        }
        val data = CombineData().apply {
            add(barDataSet)
        }

        val lineDataSet = LineDataSet(lineList).apply {
            isPointLine = true
            axisDependency = AxisY.DEPENDENCY_LEFT
        }
        val lineData = CombineData().apply {
            add(lineDataSet)
        }

        subCharts.forEachIndexed { index, subChartView ->
            if (index == 0) {
                subChartView.setCombineData(data)
            }
            if (index == 1) {
                subChartView.setCombineData(lineData)
            }
        }

    }

    private fun setMainKlineYearChartData() {
        var klineList = DataConfig.candlestickList
        klineList = klineList.subList(0, 9)

        val candlestickDataSet = CandlestickDataSet(klineList).apply {
            increasingPaintStyle = Paint.Style.STROKE
            strokeThickness = 3f
        }
        val lineList = ArrayList<LineValue>()
        klineList.forEach { value ->
            lineList.add(LineValue((value.high + value.low) * 0.5f, value.time))
        }

        val lineDataSet = LineDataSet(lineList).apply {
            color = Color.RED
            lineThickness = 3
        }

        val data = CombineData().apply {
            add(candlestickDataSet)
            add(lineDataSet)
        }
        klineMain.setCombineData(data)

        sb.clear()
        sb.append("开: ${klineList.last().open} 高: ${klineList.last().high} 低: ${klineList.last().low} 收: ${klineList.last().close}")
        tvInfo.text = sb.toString()
    }

    private fun setMainKlineChartData(update: Boolean = false, loadMore: Boolean = false) {
        if (update) {
            val random = klineList.random()
            klineList.removeLast()
            klineList.add(random)
        }

        if (loadMore) {
            val newList = mutableListOf<CandlestickValue>()
            DataConfig.candlestickList.forEachIndexed { index, value ->
                val time = 1692322131L - 86400 * (klineList.size + index)
                newList.add(CandlestickValue(value.high, value.low, value.open, value.close, time))
            }
            val list = newList.reversed().toMutableList()
            list.addAll(klineList)
            klineList.clear()
            klineList.addAll(list)
        }

        val candlestickDataSet = CandlestickDataSet(klineList).apply {
            increasingPaintStyle = Paint.Style.STROKE
            strokeThickness = 3f
            enableGap = true
            isBasis = true
        }

        val bandList = ArrayList<LineValue>()
        val lineList = ArrayList<LineValue>()
        val scatterList = ArrayList<ScatterValue>()
        val scatterTextList = ArrayList<ScatterTextValue>()
//        val overLayList = ArrayList<BarValue>()
//        val overLay2List = ArrayList<BarValue>()
        klineList.forEachIndexed { index, value ->
            if (index == klineList.size - 1 || index == klineList.size - 2 || index == klineList.size - 7 || index == klineList.size - 8) {
                bandList.add(LineValue(Float.NaN, Float.NaN).apply {
                    pathColor = Color.BLUE
                })
            } else {
                bandList.add(LineValue((value.high) * 1f, (value.low) * 1.2f).apply {
                    pathColor = Color.BLUE
                })
            }
            lineList.add(LineValue((value.high) * 1.1f, value.time))
//            overLayList.add(BarValue(value.open * 1.5f, (value.close) * 1.5f, Color.BLUE))
//            overLay2List.add(BarValue(value.high * 1.5f, (value.low) * 1.5f, Color.RED))
            when (index) {
                klineList.size - 3 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(0, 1, 2, 3, 5, 6)))
                    scatterTextList.add(ScatterTextValue(false, value.high,value.low))
                }
                klineList.size - 13 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(1, 2, 6)))
                    scatterTextList.add(ScatterTextValue(false, value.high,value.low))
                }
                klineList.size - 23 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(2, 3, 6)))
                    scatterTextList.add(ScatterTextValue(false, value.high,value.low))
                }
                klineList.size - 33 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(3, 5, 6)))
                    scatterTextList.add(ScatterTextValue(true, value.high,value.low))
                }
                klineList.size - 43 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(0, 1, 2)))
                    scatterTextList.add(ScatterTextValue(false, value.high,value.low))
                }
                klineList.size - 53 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(2)))
                    scatterTextList.add(ScatterTextValue(false, value.high,value.low))
                }
                klineList.size - 63 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(5)))
                    scatterTextList.add(ScatterTextValue(false, value.high,value.low))
                }
                else -> {
                    scatterList.add(ScatterValue(value.close, false))
                    scatterTextList.add(ScatterTextValue(false, value.high,value.low))
                }
            }
        }

        val lineDataSet = LineDataSet(lineList).apply {
            color = Color.RED
            lineThickness = 3
            isEnable = true
        }

//        val scatterTextDataSet = ScatterTextDataSet(scatterTextList).apply {
//            axisDependency = AxisY.DEPENDENCY_BOTH
//            text = "自"
//            textColor = 0xffFD263F.toInt()
//            textBgColor = 0xB3FFFFFF.toInt()
//            lineColor = 0xffFD263F.toInt()
//            frameColor = 0xffFD263F.toInt()
//            textSize = 30
//        }

        val scatterDataSet = ScatterDataSet(scatterList).apply {
            tag = ChartConstant.FLAG_TAG_NAME
            shape = scatterDrawable
            shapeAlign = SHAPE_ALIGN_PARENT_BOTTOM
            isAutoWidth = false
        }

        val drawLineDataSet = klineMain.chartData.dataSets.find { it is DrawLineDataSet }
//
//        val overLayDataSet = BarDataSet(overLayList).apply {
//            overlayKline = true
//            isAutoBarWidth = true
//            color = Color.BLUE
//            drawIndex = 888
//        }
//        val overLayDataSet2 = BarDataSet(overLay2List).apply {
//            overlayKline = true
//            isAutoBarWidth = true
//            color = Color.RED
//            drawIndex = 889
//        }

        val bandLineDataSet = LineDataSet(bandList).apply {
            lineThickness = 2
            isDrawBand = true
        }


        val data = CombineData().apply {
            add(candlestickDataSet)
            add(lineDataSet)
            add(scatterDataSet)
//            add(drawLineDataSet)
            add(bandLineDataSet)
//            add(overLayDataSet)
//            add(overLayDataSet2)
//            add(scatterTextDataSet)
        }

        if (loadMore && klineMain.isLoadMore) {
            klineMain.setCombineDataByLoadMore(data)
        } else {
            klineMain.setCombineData(data)
        }
        sb.clear()
        sb.append("开: ${klineList.last().open} 高: ${klineList.last().high} 低: ${klineList.last().low} 收: ${klineList.last().close}")
        tvInfo.text = sb.toString()

    }

    private fun setMainMinuteChartData() {
        val klineList = DataConfig.candlestickList.toMutableList()
        val newList = mutableListOf<CandlestickValue>()

//        DataConfig.candlestickList.forEachIndexed { index, value ->
//            val time = 1692322131L - 86400 * (klineList.size + index)
//            newList.add(CandlestickValue(value.high, value.low, value.open, value.close, time))
//        }
        val list = newList.reversed().toMutableList()
        list.addAll(klineList)
        list.addAll(klineList)


        val lineList = ArrayList<LineValue>()
        val scatterTopValues = ArrayList<ScatterValue>()
        val scatterBottomValues = ArrayList<ScatterValue>()
        val circleScatters = ArrayList<ScatterValue>()

        list.forEachIndexed { index, value ->
            lineList.add(LineValue(value.close, value.time))
            if (index == 0) {
                scatterTopValues.add(ScatterValue(value.close, true))
                scatterBottomValues.add(ScatterValue(value.close, false))
                circleScatters.add(ScatterValue(value.close, true, Color.GREEN))
            } else if (index == list.size - 2) {
                scatterTopValues.add(ScatterValue(value.close, false))
                scatterBottomValues.add(ScatterValue(value.close, true))
                circleScatters.add(ScatterValue(value.close, true, Color.GREEN))
            } else if (value.close == 2940.74f) {
                scatterTopValues.add(ScatterValue(value.close, true))
                scatterBottomValues.add(ScatterValue(value.close, false))
                circleScatters.add(ScatterValue(value.close, true, Color.GREEN))
            } else if (value.close == 3365.22f) {
                scatterTopValues.add(ScatterValue(value.close, false))
                scatterBottomValues.add(ScatterValue(value.close, true))
                circleScatters.add(ScatterValue(value.close, true, Color.GREEN))
            } else {
                scatterTopValues.add(ScatterValue(Float.NaN, false))
                scatterBottomValues.add(ScatterValue(Float.NaN, false))
                circleScatters.add(ScatterValue(Float.NaN, false))
            }

        }

        val lineDataSet = MinuteLineDataSet(lineList, lastClose, highPrice, lowPrice).apply {
            color = Color.BLUE
            lineThickness = 3
            forceValueCount = 242
        }

        val value = list.get(26)
        val startTime = 1692322131L - 86400 * (242 + 80)
        val endTime = 1692322131L - 86400 * (242 + 10)

        val drawLineDataSet = DrawLineDataSet().apply {
            lineKey = "ltStraightLine20"
            lineType = DrawLineType.ltStraightLine.ordinal
            lineSize = 1f
            dash = "7,2"
            startDrawValue = DrawLineValue(value.close, startTime)
            endDrawValue = DrawLineValue(value.close + 20, endTime)
            historyTimeList = minuteHistoryTimes
            cycle = -1
        }

        val topDrawable = ContextCompat.getDrawable(this, cn.jingzhuan.lib.chart.R.drawable.ico_range_touch_left)
        val bottomDrawable = ContextCompat.getDrawable(this, cn.jingzhuan.lib.chart.R.drawable.ico_range_touch_right)
        val pointShape = ContextCompat.getDrawable(this, R.drawable.ic_signal_minute_point_b)
        val scatterTopDataSet = ScatterDataSet(scatterTopValues)
            .apply {
                isAutoWidth = false
                isAutoExpand = false
                shapeAlign = SHAPE_ALIGN_TOP
                shape = topDrawable
                isAutoTurn = true
                autoTurnPointShape = pointShape
                forceValueCount = 242
            }

        val scatterBottomDataSet = ScatterDataSet(scatterBottomValues)
            .apply {
                isAutoWidth = false
                isAutoExpand = false
                shapeAlign = SHAPE_ALIGN_BOTTOM
                shape = bottomDrawable
                isAutoTurn = true
                autoTurnPointShape = pointShape
                forceValueCount = 242
            }


        val circleDataSet = ScatterDataSet(circleScatters).apply {
            shape = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setSize(10, 10) }
            isAutoWidth = false
            shapeAlign = SHAPE_ALIGN_CENTER
            drawOffsetY = 0f
            isAutoExpand = false
            forceValueCount = 242
        }

        val data = CombineData().apply {
            add(lineDataSet)
//            add(drawLineDataSet)
            add(scatterTopDataSet)
            add(scatterTopDataSet)
            add(scatterBottomDataSet)
            add(scatterBottomDataSet)
            add(circleDataSet)
        }
        minuteMain.setCombineData(data)
    }

    private fun setMainCallAuctionChartData() {
        val callActionList = DataConfig.callAuctionList.toMutableList()

        val lineValues = ArrayList<LineValue>()

        val timeData = convertToTimeData()
        val scatterFlagEntries = mutableListOf<ScatterTextValue>()
        timeData.forEach {time ->
            val foundData = callActionList.findLast {it.time == time}
            if (foundData != null) {
                lineValues.add(LineValue(foundData.value, true))
                val hasFlag = foundData.time == 1692322131L - 86400 * 120 + 500
                val value = ScatterTextValue(hasFlag, foundData.value, foundData.value)
                scatterFlagEntries.add(value)
            } else {
                lineValues.add(LineValue(if (lineValues.isEmpty()) lastClose else Float.NaN, false))
                scatterFlagEntries.add(ScatterTextValue(false, Float.NaN, Float.NaN))
            }
        }

       val dataSet = MinuteLineDataSet(lineValues, lastClose).apply {
            isPointLine = true
            isBasis = true
            lineThickness = 3
            color = Color.BLUE
            radius = 5f
            interval = 0f
            forceValueCount = 600
        }

        val flagDataSet = ScatterTextDataSet(scatterFlagEntries).apply {
            axisDependency = AxisY.DEPENDENCY_BOTH
            text = "异"
            textColor = Color.RED
            textBgColor = 0x4DFD263F
            lineColor = Color.RED
            frameColor = Color.RED
            textSize = 24
            isCircle = true
            forceValueCount = 600
        }

        val data = CombineData().apply {
            add(dataSet)
            add(flagDataSet)
        }
        callAuctionMain.setCombineData(data)
    }

    private fun convertToTimeData(): List<Long> {
        val values = mutableListOf<Long>()
        val start = 1692322131L - 86400 * 120
        for (i in 0 until 599) {
            val value = start + i
            values.add(value)
        }
        return values
    }

    fun dp2px(dpValue: Float): Int {
        val density = resources.displayMetrics.density
        return (dpValue * density + 0.5f).toInt()
    }

}
