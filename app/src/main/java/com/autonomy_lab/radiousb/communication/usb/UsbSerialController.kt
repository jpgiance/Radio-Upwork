package com.autonomy_lab.radiousb.communication.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Toast
import com.autonomy_lab.radiousb.communication.ProtocolParser
import com.autonomy_lab.radiousb.data.SerialMessage
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.HexDump
import com.hoho.android.usbserial.util.SerialInputOutputManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException


class UsbSerialController(
    private val context: Context,
    private val parser: ProtocolParser
): SerialInputOutputManager.Listener {

    companion object{
        const val ACTION_USB_PERMISSION = "com.autonomy_lab.radiousb.USB_PERMISSION"
        const val BAUD = 115200
        const val VID = 6790
        const val PID = 29987

        const val WRITE_TIMEOUT_MILLIS: Int = 1000
        const val READ_TIMEOUT_MILLIS: Int = 1000
    }

    private var _connected = MutableStateFlow<Boolean>(false)
    val connected: StateFlow<Boolean> get() = _connected

    private var usbDevice: UsbDevice? = null
    private var usbSerialPort: UsbSerialPort? = null
    private var usbIoManager: SerialInputOutputManager? = null

    private val usbManager: UsbManager by lazy {
        context.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private val usbDefaultProber: UsbSerialProber by lazy {
        UsbSerialProber.getDefaultProber()
    }

    private val usbCustomProber: UsbSerialProber by lazy {
        CustomProber.getCustomProber()
    }

    val usbReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {


                    usbDevice?.let { device ->

                        if (usbManager.hasPermission(device)){
                            // connect to device


                            for (i in 0..<device.interfaceCount){
                                val usbInterface = device.getInterface(i)
                                Log.e("TAG", "interface $i with ID: ${usbInterface.id} with endpoint count: ${usbInterface.endpointCount}" )

                                for (j in 0..<usbInterface.endpointCount){
                                    val usbEndpoint = usbInterface.getEndpoint(j)
                                    Log.e("TAG", "-------- endpoint $j: $usbEndpoint with endpoint type: ${usbEndpoint.type} and with endpoint number: ${usbEndpoint.endpointNumber} and and with endpoint address: ${usbEndpoint.address}" )
                                }


                                connect()
                            }

                        }else{
                            // handle permission denied
                            Toast.makeText( context,  "Permission was Denied", Toast.LENGTH_SHORT ).show()

                        }

                    }

                }
            }

        }
    }

    fun connect() {

        usbSerialPort = null
        usbDevice = null

        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList

        if (deviceList.isEmpty()){
            Toast.makeText( context,  "No device Connected", Toast.LENGTH_SHORT ).show()
            return
        }else if (deviceList.size > 1){
            Toast.makeText( context,  "Multiple devices Connected", Toast.LENGTH_SHORT ).show()
            return
        }else if (deviceList.size == 1){
            deviceList.values.forEach { device ->
                Log.e("TAG", "getUsbDevicesConnected: ------- device: ${device.deviceName} with product name: <${device.productName}> with productID: ${device.productId} and vendorID: ${device.vendorId}" )
                Log.e("TAG", "getUsbDevicesConnected: permission is" + usbManager.hasPermission(device) )

            }
        }


        deviceList.values.forEach{ usbDev ->
//            if (usbDev.productId == PID){
//                usbDevice = usbDev
//            }

            usbDevice = usbDev

        }

        if (usbDevice == null){
            Log.e("TAG", "connect: Device with PID: $PID was not found" )
            return
        }


        var mDriver = usbDefaultProber.probeDevice(usbDevice)

        if (mDriver == null){
            Log.e("TAG", "connect: Attempted to connect but Default Prober could not find Driver" )

            mDriver = usbCustomProber.probeDevice(usbDevice)
        }

        if (mDriver == null){
            Log.e("TAG", "connect: Attempted to connect but Custom Prober could not find Driver" )
            return
        }

        if (mDriver.ports.size == 1){
            usbSerialPort = mDriver.ports[0]
        }else if (mDriver.ports.isEmpty()){
            Log.e("TAG", "connect: The Driver found has no ports available" )
            return
        }else{
            Log.e("TAG", "connect: The Driver found has too many ports available" )
            return
        }

        val usbConnection = usbManager.openDevice(mDriver.device)
        if (usbConnection == null && !usbManager.hasPermission(mDriver.device)) {

            usbDevice = mDriver.device

            val intent = Intent(ACTION_USB_PERMISSION)
            intent.setPackage(context.packageName)
            val usbPermissionIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
            usbManager.requestPermission(mDriver.device, usbPermissionIntent)
            return
        }
        if (usbConnection == null) {
            if (!usbManager.hasPermission(mDriver.device)) Log.e("TAG", "connect: connection failed: permission denied" )
            else Log.e("TAG", "connect: connection failed: open failed" )
            return
        }

        try {
            usbSerialPort?.open(usbConnection)
            try {
                usbSerialPort?.setParameters(BAUD, 8, 1, UsbSerialPort.PARITY_NONE)
            } catch (e: UnsupportedOperationException) {
                Log.e("TAG","unsupport setparameters")
            }

            usbIoManager = SerialInputOutputManager(usbSerialPort, this)
            usbIoManager?.start()
            _connected.value = true
            Log.e("TAG"," ------ connected -------${_connected.value}")


        } catch (e: java.lang.Exception) {
            Log.e("TAG"," connection failed: " + e.message)

            disconnect()
        }
    }

    fun disconnect() {
        _connected.value = false
        usbDevice = null
        usbSerialPort = null

        if (usbIoManager != null) {
            usbIoManager?.listener = null
            usbIoManager?.stop()
        }
        usbIoManager = null
        try {
            usbSerialPort?.close()
        } catch (ignored: IOException) {
        }
        usbSerialPort = null
    }

    fun send(str: String) {
        if (!_connected.value) {
            Log.e("TAG"," ------ NOT connected -------")
            return
        }
        try {
            val data = (str + '\n').toByteArray()
            usbSerialPort?.write(data, WRITE_TIMEOUT_MILLIS)
        } catch (e: java.lang.Exception) {
            onRunError(e)
        }
    }

    fun sendBytes(data: ByteArray) {
        if (!_connected.value) {
            Log.e("TAG"," ------ NOT connected ------- ${_connected.value}")
            return
        }
        try {

            usbSerialPort?.write(data, WRITE_TIMEOUT_MILLIS)
        } catch (e: java.lang.Exception) {
            onRunError(e)
        }
    }

    private fun read() {
        if (!_connected.value) {
            Log.e("TAG"," ------ NOT connected -------")
            return
        }
        try {
            val buffer = ByteArray(8192)
            val len = usbSerialPort!!.read(buffer, READ_TIMEOUT_MILLIS)
            receive(buffer.copyOf(len))
        } catch (e: IOException) {
            // when using read with timeout, USB bulkTransfer returns -1 on timeout _and_ errors
            // like connection loss, so there is typically no exception thrown here on error
            Log.e("TAG","connection lost: " + e.message)
            disconnect()
        }
    }

    private fun receive(data: ByteArray) {

        parser.parseBytes(data)
    }

    override fun onNewData(data: ByteArray?) {
        var newData: ByteArray? = null

        data?.let {
            newData = ByteArray(it.size)
            newData?.let { it1 -> System.arraycopy(it, 0, it1, 0, it.size) }
        }


        CoroutineScope(Dispatchers.Main).launch {
            newData?.let { receive(it) }
        }
    }

    override fun onRunError(e: Exception?) {
        Log.e("TAG"," Error ---- : ${e?.message}")

        disconnect()

    }
}