package com.autonomy_lab.radiousb.data

data class BarInfo(
    var show: Boolean = true,
    var alarmOn: Boolean = false,
    var alarmState: Boolean = false,
    var alarmValue: Float = 0f,
    var maxValue: Float = Float.MAX_VALUE,
    var normalizedValue: Float = 0f,
    var bandName: String,
    var bandFrequency: Float,
    val valueList: MutableList<Pair<Float,Long>> = mutableListOf(),
    var lastValue:Float = 0f,
    var maxBarValue:Int = 600
)
