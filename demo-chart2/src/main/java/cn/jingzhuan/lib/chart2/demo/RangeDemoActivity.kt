package cn.jingzhuan.lib.chart2.demo

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import cn.jingzhuan.lib.chart.Viewport
import cn.jingzhuan.lib.chart.component.AxisY
import cn.jingzhuan.lib.chart.component.Highlight
import cn.jingzhuan.lib.chart.data.CandlestickDataSet
import cn.jingzhuan.lib.chart.data.CandlestickValue
import cn.jingzhuan.lib.chart.data.ScatterTextDataSet
import cn.jingzhuan.lib.chart.data.ScatterTextValue
import cn.jingzhuan.lib.chart.event.HighlightStatusChangeListener
import cn.jingzhuan.lib.chart.event.OnScaleListener
import cn.jingzhuan.lib.chart.renderer.CandlestickDataSetArrowDecorator
import cn.jingzhuan.lib.chart.utils.ForceAlign
import kotlin.math.round

class RangeDemoActivity : AppCompatActivity() {

    private var candlestickValues: MutableList<CandlestickValue> = ArrayList()

    private lateinit var llRangeInfo: LinearLayout

    private lateinit var tvCloseRange: TextView

    private lateinit var tvInfo: TextView

    private lateinit var tvOpen: TextView

    private lateinit var tvNumber: TextView

    private lateinit var combineChart: TestChartKLineView

    private lateinit var btnRange: AppCompatButton

    private lateinit var btnAddTag: AppCompatButton

    private lateinit var btnScaleIn: AppCompatButton

    private lateinit var btnScaleOut: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_range_demo)
        llRangeInfo = findViewById(R.id.ll_range_info)
        tvCloseRange = findViewById(R.id.tv_close_range)
        tvInfo = findViewById(R.id.tv_info)
        tvOpen = findViewById(R.id.tv_open)
        tvNumber = findViewById(R.id.tv_number)
        combineChart = findViewById(R.id.combine_chart)
        btnRange = findViewById(R.id.btn_range)
        btnAddTag = findViewById(R.id.btn_add_tag)
        btnScaleIn = findViewById(R.id.btn_scale_in)
        btnScaleOut = findViewById(R.id.btn_scale_out)
        initData()

        val dataSet = CandlestickDataSet(candlestickValues)
        dataSet.isHighlightedHorizontalEnable = true
        dataSet.isHighlightedVerticalEnable = true
        dataSet.increasingPaintStyle = Paint.Style.STROKE
        dataSet.strokeThickness = 2f

        combineChart.addDataSet(CandlestickDataSetArrowDecorator(dataSet).apply { offsetPercent = 0.1f })

//        val dataSet2 = CandlestickDataSet(candlestickValues)
//        dataSet.isHighlightedHorizontalEnable = true
//        dataSet.isHighlightedVerticalEnable = true
//        dataSet.color = 0xFFFD263F.toInt()
//        dataSet.increasingPaintStyle = Paint.Style.FILL_AND_STROKE
//        dataSet.strokeThickness = 2f
//        dataSet.decreasingColor = 0xFF00AA3B.toInt()
//        dataSet.increasingColor = 0xFFFD263F.toInt()
//        combineChart.addDataSet(dataSet2)

        btnScaleIn.setOnClickListener {
            combineChart.zoomIn(ForceAlign.RIGHT)
        }

        btnScaleOut.setOnClickListener {
            combineChart.zoomOut(ForceAlign.RIGHT)
        }

        btnAddTag.setOnClickListener {
            val textDataSet = addTextData()
            textDataSet.drawIndex = 11000
            combineChart.addDataSet(textDataSet)
            combineChart.postInvalidate()
        }

        btnRange.setOnClickListener {
            if (combineChart.rangeEnable) return@setOnClickListener
            combineChart.renderer.rangeRenderer.resetData()
            combineChart.cleanHighlight()
            combineChart.rangeEnable = true
            combineChart.isDraggingToMoveEnable = false
            combineChart.isHighlightDisable = true
            combineChart.postInvalidate()
            tvCloseRange.visibility = View.VISIBLE
            llRangeInfo.visibility = View.VISIBLE
            tvOpen.visibility = View.GONE
            tvNumber.visibility = View.VISIBLE
        }

        tvCloseRange.setOnClickListener {
            combineChart.rangeEnable = false
            combineChart.isDraggingToMoveEnable = true
            combineChart.isHighlightDisable = false
            combineChart.postInvalidate()
            llRangeInfo.visibility = View.INVISIBLE
            tvOpen.visibility = View.VISIBLE
            tvNumber.visibility = View.GONE
            tvCloseRange.visibility = View.GONE
        }

        tvOpen.setOnClickListener {
            if (combineChart.rangeEnable) return@setOnClickListener
            combineChart.cleanHighlight()
            combineChart.rangeEnable = true
            combineChart.isDraggingToMoveEnable = false
            combineChart.isHighlightDisable = true
            combineChart.postInvalidate()
            llRangeInfo.visibility = View.VISIBLE
            tvCloseRange.visibility = View.VISIBLE
            tvOpen.visibility = View.GONE
            tvNumber.visibility = View.VISIBLE

        }

        combineChart.setOnScaleListener(object : OnScaleListener{
            override fun onScaleStart(viewport: Viewport) {

            }

            override fun onScale(viewport: Viewport) {
            }

            override fun onScaleEnd(viewport: Viewport) {
                val from = round(candlestickValues.size * viewport.left)
                val to = round(candlestickValues.size * viewport.right)
                Log.d("Chart", "onScaleEnd->size=${to - from}")
            }

        })

        combineChart.onHighlightStatusChangeListener =
            object : HighlightStatusChangeListener {
                override fun onHighlightShow(highlights: Array<out Highlight>?) {
                    llRangeInfo.visibility = View.VISIBLE
                    if (!highlights.isNullOrEmpty()) {
                        val data = candlestickValues[highlights[0].dataIndex]
                        tvInfo.text = "开：${data.open} 高：${data.high} 收：${data.close} 低：${data.low}"
                    }

                }

                override fun onHighlightHide() {
                    llRangeInfo.visibility = View.INVISIBLE
                }
            }

        combineChart.renderer.rangeRenderer.setOnRangeListener{ startX, endX, _ ->
            val startIndex = combineChart.renderer.rangeRenderer.getEntryIndexByCoordinate(startX, 0f)
            val endIndex = combineChart.renderer.rangeRenderer.getEntryIndexByCoordinate(endX, 0f)
            tvNumber.text = "周期数：${endIndex - startIndex}"
            updateCloseRangeButton(startX, endX)
        }
    }

    private fun addTextData(): ScatterTextDataSet {
        val entries = java.util.ArrayList<ScatterTextValue>(candlestickValues.size)
        candlestickValues.forEachIndexed { index, value ->
            entries.add(ScatterTextValue(true, value.high,value.low))
        }
        val dataSet = ScatterTextDataSet(entries)

        dataSet.axisDependency = AxisY.DEPENDENCY_BOTH
        dataSet.text = "加自选"
        dataSet.textColor = 0xffFD263F.toInt()
        dataSet.textBgColor = 0xB3FFFFFF.toInt()
        dataSet.lineColor = 0xffFD263F.toInt()
        dataSet.frameColor = 0xffFD263F.toInt()
        dataSet.textSize = 30

        return dataSet
    }

    private fun updateCloseRangeButton(startX: Float, endX: Float) {

        val centerX = (startX + endX) * 0.5f

        var transX = centerX - tvCloseRange.width * 0.5f

        if (transX < combineChart.left) {
            transX = 0f
        } else if (transX > combineChart.width - tvCloseRange.width) {
            transX = combineChart.width.toFloat() - tvCloseRange.width
        }

        tvCloseRange.translationX = transX
    }

    private fun initData() {
        candlestickValues.add(CandlestickValue(3145.27f, 3117.44f, 3123.88f, 3134.57f))
        candlestickValues.add(CandlestickValue(3152.94f, 3131.41f, 3132.91f, 3140.85f))
        candlestickValues.add(CandlestickValue(3155.00f, 3097.33f, 3131.35f, 3152.18f))
        candlestickValues.add(CandlestickValue(3154.72f, 3136.58f, 3144.02f, 3154.65f))
        candlestickValues.add(CandlestickValue(3154.78f, 3136.54f, 3147.22f, 3143.70f))
        candlestickValues.add(CandlestickValue(3148.29f, 3123.75f, 3138.31f, 3135.35f))
        candlestickValues.add(CandlestickValue(3143.82f, 3111.38f, 3127.11f, 3127.37f))
        candlestickValues.add(CandlestickValue(3117.61f, 3092.09f, 3114.77f, 3103.04f))
        candlestickValues.add(CandlestickValue(3093.44f, 3067.68f, 3090.07f, 3078.61f))
        candlestickValues.add(CandlestickValue(3084.20f, 3056.56f, 3064.85f, 3080.53f))

        candlestickValues.add(CandlestickValue(3090.82f, 3051.59f, 3078.16f, 3052.78f))
        candlestickValues.add(CandlestickValue(3063.56f, 3016.53f, 3036.79f, 3061.50f))
        candlestickValues.add(CandlestickValue(3090.48f, 3051.87f, 3054.11f, 3083.51f))
        candlestickValues.add(CandlestickValue(3098.91f, 3085.93f, 3085.93f, 3090.22f))
        candlestickValues.add(CandlestickValue(3113.51f, 3060.53f, 3082.87f, 3112.95f))
        candlestickValues.add(CandlestickValue(3119.58f, 3101.30f, 3107.80f, 3104.43f))
        candlestickValues.add(CandlestickValue(3103.43f, 3077.95f, 3082.33f, 3090.13f))
        candlestickValues.add(CandlestickValue(3095.47f, 3081.28f, 3086.70f, 3090.62f))
        candlestickValues.add(CandlestickValue(3103.93f, 3063.14f, 3087.16f, 3075.67f))
        candlestickValues.add(CandlestickValue(3084.23f, 3050.84f, 3069.38f, 3061.94f))

//        candlestickValues.add(CandlestickValue(3064.81f, 3022.30f, 3047.57f, 3064.08f))
//        candlestickValues.add(CandlestickValue(3114.65f, 3052.83f, 3055.34f, 3107.83f))
//        candlestickValues.add(CandlestickValue(3120.65f, 3100.38f, 3101.29f, 3110.06f))
//        candlestickValues.add(CandlestickValue(3143.28f, 3111.56f, 3125.33f, 3117.17f))
//        candlestickValues.add(CandlestickValue(3113.52f, 3097.67f, 3108.41f, 3102.62f))
//        candlestickValues.add(CandlestickValue(3110.38f, 3081.84f, 3094.22f, 3105.54f))
//        candlestickValues.add(CandlestickValue(3105.50f, 3084.83f, 3102.11f, 3091.65f))
//        candlestickValues.add(CandlestickValue(3102.86f, 3078.79f, 3084.54f, 3102.12f))
//        candlestickValues.add(CandlestickValue(3140.77f, 3098.94f, 3101.76f, 3140.32f))
//        candlestickValues.add(CandlestickValue(3153.26f, 3132.82f, 3136.46f, 3150.33f))
//
//        candlestickValues.add(CandlestickValue(3165.91f, 3146.10f, 3147.45f, 3158.39f))
//        candlestickValues.add(CandlestickValue(3164.94f, 3135.31f, 3149.53f, 3139.87f))
//        candlestickValues.add(CandlestickValue(3155.98f, 3131.04f, 3134.01f, 3153.73f))
//        candlestickValues.add(CandlestickValue(3149.16f, 3125.35f, 3146.75f, 3130.66f))
//        candlestickValues.add(CandlestickValue(3137.59f, 3117.08f, 3125.59f, 3132.48f))
//        candlestickValues.add(CandlestickValue(3134.25f, 3117.85f, 3126.37f, 3123.16f))
//        candlestickValues.add(CandlestickValue(3146.77f, 3121.77f, 3122.15f, 3144.37f))
//        candlestickValues.add(CandlestickValue(3150.45f, 3134.61f, 3148.02f, 3140.01f))
//        candlestickValues.add(CandlestickValue(3157.03f, 3132.62f, 3148.98f, 3156.20f))
//        candlestickValues.add(CandlestickValue(3186.97f, 3146.63f, 3152.23f, 3147.44f))
//
//        candlestickValues.add(CandlestickValue(3158.05f, 3118.09f, 3138.43f, 3157.87f))
//        candlestickValues.add(CandlestickValue(3187.88f, 3156.97f, 3157.00f, 3185.43f))
//        candlestickValues.add(CandlestickValue(3193.45f, 3172.45f, 3183.41f, 3191.19f))
//        candlestickValues.add(CandlestickValue(3193.43f, 3170.78f, 3183.63f, 3173.20f))
//        candlestickValues.add(CandlestickValue(3188.77f, 3174.28f, 3174.97f, 3188.06f))
//        candlestickValues.add(CandlestickValue(3193.23f, 3171.57f, 3176.94f, 3192.42f))
//        candlestickValues.add(CandlestickValue(3196.29f, 3177.02f, 3192.00f, 3195.90f))
//        candlestickValues.add(CandlestickValue(3193.06f, 3174.31f, 3192.88f, 3182.80f))
//        candlestickValues.add(CandlestickValue(3207.31f, 3174.70f, 3179.21f, 3207.12f))
//        candlestickValues.add(CandlestickValue(3215.94f, 3188.77f, 3203.86f, 3212.43f))
//
//        candlestickValues.add(CandlestickValue(3219.52f, 3195.29f, 3203.82f, 3217.95f))
//        candlestickValues.add(CandlestickValue(3223.34f, 3203.20f, 3208.45f, 3212.62f))
//        candlestickValues.add(CandlestickValue(3226.90f, 3199.21f, 3201.52f, 3203.04f))
//        candlestickValues.add(CandlestickValue(3215.19f, 3177.92f, 3201.92f, 3197.54f))
//        candlestickValues.add(CandlestickValue(3219.27f, 3190.34f, 3192.36f, 3218.15f))
//        candlestickValues.add(CandlestickValue(3222.97f, 3204.85f, 3212.03f, 3222.41f))
//        candlestickValues.add(CandlestickValue(3230.35f, 3139.50f, 3219.79f, 3176.45f))
//        candlestickValues.add(CandlestickValue(3187.66f, 3150.12f, 3159.72f, 3187.57f))
//        candlestickValues.add(CandlestickValue(3232.93f, 3179.72f, 3181.39f, 3230.97f))
//        candlestickValues.add(CandlestickValue(3246.23f, 3225.42f, 3227.51f, 3244.86f))
//
//        candlestickValues.add(CandlestickValue(3247.70f, 3231.95f, 3236.59f, 3237.97f))
//        candlestickValues.add(CandlestickValue(3261.10f, 3230.07f, 3230.89f, 3250.60f))
//        candlestickValues.add(CandlestickValue(3261.64f, 3233.13f, 3249.13f, 3243.68f))
//        candlestickValues.add(CandlestickValue(3264.85f, 3228.04f, 3244.45f, 3247.66f))
//        candlestickValues.add(CandlestickValue(3251.92f, 3220.63f, 3243.76f, 3249.78f))
//        candlestickValues.add(CandlestickValue(3256.37f, 3232.95f, 3240.16f, 3253.23f))
//        candlestickValues.add(CandlestickValue(3276.94f, 3251.18f, 3252.75f, 3273.03f))
//        candlestickValues.add(CandlestickValue(3292.63f, 3273.50f, 3274.37f, 3292.63f))
//        candlestickValues.add(CandlestickValue(3305.42f, 3282.04f, 3288.52f, 3285.06f))
//        candlestickValues.add(CandlestickValue(3293.37f, 3262.15f, 3279.98f, 3272.92f))
//
//        candlestickValues.add(CandlestickValue(3287.18f, 3261.31f, 3269.32f, 3262.08f))
//        candlestickValues.add(CandlestickValue(3280.10f, 3243.71f, 3257.66f, 3279.45f))
//        candlestickValues.add(CandlestickValue(3285.47f, 3269.65f, 3277.18f, 3281.87f))
//        candlestickValues.add(CandlestickValue(3277.93f, 3263.85f, 3277.81f, 3275.57f))
//        candlestickValues.add(CandlestickValue(3282.52f, 3236.17f, 3269.72f, 3261.75f))
//        candlestickValues.add(CandlestickValue(3245.12f, 3200.75f, 3237.91f, 3208.54f))
//        candlestickValues.add(CandlestickValue(3240.05f, 3206.04f, 3206.04f, 3237.36f))
//        candlestickValues.add(CandlestickValue(3263.59f, 3235.10f, 3235.22f, 3251.26f))
//        candlestickValues.add(CandlestickValue(3248.78f, 3228.87f, 3247.85f, 3246.44f))
//        candlestickValues.add(CandlestickValue(3269.13f, 3251.45f, 3253.85f, 3268.42f))
//
//        candlestickValues.add(CandlestickValue(3275.08f, 3248.08f, 3253.23f, 3268.71f))
//        candlestickValues.add(CandlestickValue(3287.52f, 3270.47f, 3274.58f, 3286.90f))
//        candlestickValues.add(CandlestickValue(3293.47f, 3274.93f, 3287.61f, 3290.22f))
//        candlestickValues.add(CandlestickValue(3299.45f, 3274.43f, 3283.80f, 3287.69f))
//        candlestickValues.add(CandlestickValue(3297.98f, 3266.36f, 3287.95f, 3271.51f))
//        candlestickValues.add(CandlestickValue(3331.90f, 3271.45f, 3271.45f, 3331.52f))
//        candlestickValues.add(CandlestickValue(3375.03f, 3336.12f, 3336.12f, 3362.64f))
//        candlestickValues.add(CandlestickValue(3374.59f, 3354.45f, 3362.06f, 3365.22f))
//        candlestickValues.add(CandlestickValue(3376.64f, 3357.08f, 3361.82f, 3363.62f))
//        candlestickValues.add(CandlestickValue(3367.36f, 3341.14f, 3361.45f, 3349.57f))
//
//        candlestickValues.add(CandlestickValue(3282.52f, 3236.17f, 3269.72f, 3261.75f))
//        candlestickValues.add(CandlestickValue(3245.12f, 3200.75f, 3237.91f, 3208.54f))
//        candlestickValues.add(CandlestickValue(3240.05f, 3206.04f, 3206.04f, 3237.36f))
//        candlestickValues.add(CandlestickValue(3263.59f, 3235.10f, 3235.22f, 3251.26f))
//        candlestickValues.add(CandlestickValue(3248.78f, 3228.87f, 3247.85f, 3246.44f))
//        candlestickValues.add(CandlestickValue(3269.13f, 3251.45f, 3253.85f, 3268.42f))
//        candlestickValues.add(CandlestickValue(3275.08f, 3248.08f, 3253.23f, 3268.71f))
//        candlestickValues.add(CandlestickValue(3287.52f, 3270.47f, 3274.58f, 3286.90f))
//        candlestickValues.add(CandlestickValue(3280.10f, 3243.71f, 3257.66f, 3279.45f))
//        candlestickValues.add(CandlestickValue(3285.47f, 3269.65f, 3277.18f, 3281.87f))
//
//        candlestickValues.add(CandlestickValue(3277.93f, 3263.85f, 3277.81f, 3275.57f))
//        candlestickValues.add(CandlestickValue(3282.52f, 3236.17f, 3269.72f, 3261.75f))
//        candlestickValues.add(CandlestickValue(3245.12f, 3200.75f, 3237.91f, 3208.54f))
//        candlestickValues.add(CandlestickValue(3240.05f, 3206.04f, 3206.04f, 3237.36f))
//        candlestickValues.add(CandlestickValue(3263.59f, 3235.10f, 3235.22f, 3251.26f))
//        candlestickValues.add(CandlestickValue(3248.78f, 3228.87f, 3247.85f, 3246.44f))
//        candlestickValues.add(CandlestickValue(3269.13f, 3251.45f, 3253.85f, 3268.42f))
//        candlestickValues.add(CandlestickValue(3261.10f, 3230.07f, 3230.89f, 3250.60f))
//        candlestickValues.add(CandlestickValue(3261.64f, 3233.13f, 3249.13f, 3243.68f))
//        candlestickValues.add(CandlestickValue(3264.85f, 3228.04f, 3244.45f, 3247.66f))
//
//        candlestickValues.add(CandlestickValue(3251.92f, 3220.63f, 3243.76f, 3249.78f))
//        candlestickValues.add(CandlestickValue(3256.37f, 3232.95f, 3240.16f, 3253.23f))
//        candlestickValues.add(CandlestickValue(3276.94f, 3251.18f, 3252.75f, 3273.03f))
//        candlestickValues.add(CandlestickValue(3292.63f, 3273.50f, 3274.37f, 3292.63f))
//        candlestickValues.add(CandlestickValue(3305.42f, 3282.04f, 3288.52f, 3285.06f))
//        candlestickValues.add(CandlestickValue(3293.37f, 3262.15f, 3279.98f, 3272.92f))
//        candlestickValues.add(CandlestickValue(3287.18f, 3261.31f, 3269.32f, 3262.08f))
//        candlestickValues.add(CandlestickValue(3280.10f, 3243.71f, 3257.66f, 3279.45f))
//        candlestickValues.add(CandlestickValue(3285.47f, 3269.65f, 3277.18f, 3281.87f))
//        candlestickValues.add(CandlestickValue(3280.10f, 3243.71f, 3257.66f, 3279.45f))
//        candlestickValues.add(CandlestickValue(3285.47f, 3269.65f, 3277.18f, 3281.87f))
    }

}
