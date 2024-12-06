package com.autonomy_lab.radiousb

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.autonomy_lab.radiousb.communication.usb.UsbSerialController
import com.autonomy_lab.radiousb.communication.usb.UsbSerialController.Companion.ACTION_USB_PERMISSION
import com.autonomy_lab.radiousb.ui.navigation.ScreenNavigator
import com.autonomy_lab.radiousb.ui.theme.RadioUSBTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var usbController: UsbSerialController


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter(ACTION_USB_PERMISSION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(usbController.usbReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(usbController.usbReceiver, filter)
        }


//        enableEdgeToEdge()
        setContent {
            RadioUSBTheme {

                ScreenNavigator()

            }
        }
    }


    override fun onDestroy() {
        unregisterReceiver(usbController.usbReceiver)
        super.onDestroy()
    }
}

