package com.autonomy_lab.radiousb.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autonomy_lab.radiousb.communication.ProtocolParser
import com.autonomy_lab.radiousb.communication.usb.UsbSerialController
import com.autonomy_lab.radiousb.data.BarInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val usbController:UsbSerialController,
    private val parser: ProtocolParser
): ViewModel() {

    private var updateJob: Job? = null

    private var _bufferData = MutableStateFlow<List<BarInfo>>(emptyList())

    private var _barInfoList = MutableStateFlow<List<BarInfo>>(emptyList())
    val barInfoList: StateFlow<List<BarInfo>> get() = _barInfoList

    private var _normalizeValuesOn = MutableStateFlow<Boolean>(false)
    val normalizeValuesOn: StateFlow<Boolean> get() = _normalizeValuesOn

    private var _maxHoldOn = MutableStateFlow<Boolean>(false)
    val maxHoldOn: StateFlow<Boolean> get() = _maxHoldOn



    init {
        _bufferData.value = List(20){index ->  BarInfo(bandName = "Name $index", bandFrequency = 0f)}

        viewModelScope.launch {

            _bufferData
                .sample(100) // Emit updates every 16ms (approx 60 FPS)
                .collect { bufferedData ->
                    filterOldData()
                    calculateAverage()
                    _barInfoList.value = bufferedData
                }
        }

        viewModelScope.launch {

            parser.serialMessages.collect(){ message ->


                if (message.switchPosition < 1 || message.switchPosition > _bufferData.value.size) return@collect

                val position = message.switchPosition-1

                _bufferData.update { list->

                    list.toMutableList().also { mutableList ->

                        val infoBar = mutableList[position]

                        infoBar.lastValue = message.signalLevel.toFloat()
                        val now = System.currentTimeMillis()
                        val newPair = Pair(infoBar.lastValue, now)
                        val newAlarmState = if (infoBar.lastValue < infoBar.alarmValue) true else false
                        val newMaxValue = if (infoBar.lastValue < infoBar.maxValue) infoBar.lastValue else infoBar.maxValue


                        mutableList[position] = infoBar.copy(
                            valueList = (infoBar.valueList + newPair).toMutableList(),
                            alarmState = newAlarmState,
                            maxValue = newMaxValue,
                            bandFrequency = message.band.toFloat()
                        )

                    }
                }

            }
        }
    }

    fun isUsbConnected():StateFlow<Boolean>{
        return usbController.connected
    }


    private fun startTest(){
        stopTest()

        updateJob = viewModelScope.launch {
            while (isActive) { // Ensure the coroutine runs while active


//                _bufferData.value = _bufferData.value.map { barInfo ->
//
//                    barInfo.lastValue = Random.nextFloat()
//                    val now = System.currentTimeMillis()
//                    val newPair = Pair(barInfo.lastValue, now)
//                    val newAlarmState = if (barInfo.lastValue > barInfo.alarmValue) true else false
//                    val newMaxValue = if (barInfo.lastValue > barInfo.maxValue) barInfo.lastValue else barInfo.maxValue
//                    barInfo.copy(
//                        valueList = (barInfo.valueList + newPair).toMutableList(),
//                        alarmState = newAlarmState,
//                        maxValue = newMaxValue,
//                    )
//
//                }

//                Log.e("TAG", "startTest: size of position 0 is: ${_bufferData.value[0].valueList.size}" )

                if (usbController.connected.value){

                    val combined = byteArrayOf(
                        66,
                        ('0'..'1').random().code.toByte(), ('0'..'9').random().code.toByte(),
                        '1'.code.toByte(), '0'.code.toByte(), '2'.code.toByte(),
                        ('0'..'9').random().code.toByte(), ('0'..'9').random().code.toByte(), ('0'..'9').random().code.toByte(), ('0'..'9').random().code.toByte(),
                        90.toByte(),
                        10
                    )
                    usbController.sendBytes(combined)
                }else{
                    stopTest()
                }

                delay(20) // Wait in ms
            }
        }

    }

    private fun filterOldData(){
        _bufferData.update { list ->
            list.map { barInfo ->
                barInfo.copy(
                    valueList = barInfo.valueList
                        .filter { it.second > System.currentTimeMillis() - 30_000L }
                        .toMutableList()
                )
            }
        }
    }

    private fun calculateAverage(){

        _bufferData.update { list ->
            list.map { barInfo ->

                var average = 0f

                if (!barInfo.valueList.isEmpty()){
                    val floatValues = barInfo.valueList.map { it.first }
                    val sum = floatValues.sum()
                    average = sum/floatValues.size
                }
                barInfo.copy(
                    normalizedValue = average
                )
            }
        }

    }

    private fun stopTest(){
        updateJob?.cancel()
        updateJob = null
    }

    fun startStop(){

        if (usbController.connected.value){
            usbController.disconnect()
        }else{
            usbController.connect()
        }

//        if (usbController.connected.value){
//            updateJob?.let {
//                if (it.isActive) {
//                    stopTest()
//                    usbController.disconnect()
//                }
//            }?: run{
//                startTest()
//            }
//        }else{
//            updateJob?.let {
//                if (it.isActive) {
//                    usbController.connect()
//                    startTest()
//                }
//            }?: run{
//                usbController.connect()
//                startTest()
//            }
//        }

    }

    fun alarmOnOff(position: Int){

        _bufferData.update { list->

            list.toMutableList().also { mutableList ->
                val infoBar = mutableList[position]
                mutableList[position] = infoBar.copy(alarmOn = !infoBar.alarmOn)
            }
        }
    }

    fun normalizeValuesOnOff(){
//        _normalizeValuesOn.value = !_normalizeValuesOn.value

        if (_normalizeValuesOn.value){
            _normalizeValuesOn.value = false
        }else{
            _normalizeValuesOn.value = true

            if (_maxHoldOn.value){
                _maxHoldOn.value = false
            }
        }
    }

    fun maxHoldOnOff(){
//        _maxHoldOn.value = !_maxHoldOn.value

        if (_maxHoldOn.value){
            _maxHoldOn.value = false
        }else{
            _maxHoldOn.value = true

            if (_normalizeValuesOn.value){
                _normalizeValuesOn.value = false
            }
        }
    }

    fun showHide(position: Int){

        _bufferData.update { list->

            list.toMutableList().also { mutableList ->
                val infoBar = mutableList[position]
                mutableList[position] = infoBar.copy(show = !infoBar.show)
            }
        }
    }

    fun toggleAllShow(){
        _bufferData.update { list->

            list.map { bar->
                bar.copy(show = true)
            }
        }
    }

    fun updateAlarmSetting(position: Int, newValue: Float){
        _bufferData.update { list->

            list.toMutableList().also { mutableList ->
                val infoBar = mutableList[position]
                mutableList[position] = infoBar.copy(alarmValue = newValue)
            }
        }
    }

    fun updateMaxBarValueSetting(position: Int, newValue: Int){
        _bufferData.update { list->

            list.toMutableList().also { mutableList ->
                val infoBar = mutableList[position]
                mutableList[position] = infoBar.copy(maxBarValue = newValue)
            }
        }
    }



}