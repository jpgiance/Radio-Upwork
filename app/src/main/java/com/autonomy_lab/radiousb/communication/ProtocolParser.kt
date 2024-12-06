package com.autonomy_lab.radiousb.communication

import android.util.Log
import com.autonomy_lab.radiousb.data.SerialMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class ProtocolParser (){


    companion object {
        private const val START_BYTE = 66 // Byte 1
        private const val END_BYTE = 10   // Byte 12
        private const val MESSAGE_LENGTH = 12
    }


    private val _serialMessages = MutableSharedFlow<SerialMessage>(replay = 0)
    val serialMessages: SharedFlow<SerialMessage> get() = _serialMessages

    private var messageBuffer = ByteArray(255)
    private var byteIndex = 0

    fun parseBytes(bytes: ByteArray){



//        val decimals = ("Bytes (Decimal): ${bytes.joinToString(" ") { it.toString() }}")
//        val hex = ("Bytes (Hex): ${bytes.joinToString(" ") { "0x${it.toUByte().toString(16).padStart(2, '0')}" }}")
//        val binaries = ("Bytes (Binary): ${bytes.joinToString(" ") { it.toUByte().toString(2).padStart(8, '0') }}")
//
//        Log.e("TAG", "parseBytes: Starting Parser -----------" )
//        Log.e("TAG", decimals )
//        Log.e("TAG", hex )
//        Log.e("TAG", binaries )


        for (i in bytes.indices){

            if (bytes[i].toInt() == START_BYTE){

                byteIndex = 0
                messageBuffer[byteIndex] = bytes[i]

            }else if (bytes[i].toInt() == END_BYTE){

                if (byteIndex == MESSAGE_LENGTH - 2){
                    messageBuffer[++byteIndex] = bytes[i]
                    parseMessage(messageBuffer.copyOfRange(0, MESSAGE_LENGTH))
                }else{
//                    Log.e("TAG", "parseBytes: ERROR parsing bytes: Reached END of MESSAGE with Wrong Message Size" )
                }

            }else{

                if (byteIndex > -1 && byteIndex < MESSAGE_LENGTH-1){
                    messageBuffer[++byteIndex] = bytes[i]
                }else{
//                    Log.e("TAG", "parseBytes: ERROR parsing bytes: byteIndex out of range" )
                }
            }
        }


    }


    private fun parseMessage(messageBytes: ByteArray) {
        if (messageBytes.size != MESSAGE_LENGTH) return

        // Validate CRC
//        val crc = calculateCRC(messageBytes.copyOfRange(0, MESSAGE_LENGTH - 2))
//        val receivedCRC = messageBytes[10].toInt() and 0xFF // Byte 11 is CRC
//
//        if (crc != receivedCRC) {
//            return null
//        }

        // Extract fields
        val switchPosition = (messageBytes[1].toInt() - '0'.code) * 10 +
                (messageBytes[2].toInt() - '0'.code)

        val band = (messageBytes[3].toInt() - '0'.code) * 100 +
                (messageBytes[4].toInt() - '0'.code) * 10 +
                (messageBytes[5].toInt() - '0'.code)

        val signalLevel = (messageBytes[6].toInt() - '0'.code) * 1000 +
                (messageBytes[7].toInt() - '0'.code) * 100 +
                (messageBytes[8].toInt() - '0'.code) * 10 +
                (messageBytes[9].toInt() - '0'.code)


//        Log.e("TAG", "parseBytes: new Message with position: $switchPosition band: $band signal Level: $signalLevel" )

        CoroutineScope(Dispatchers.Main).launch {
            _serialMessages.emit(SerialMessage(switchPosition, band, signalLevel))
        }

    }


    private fun calculateCRC(data: ByteArray): Int {
        return data.sumOf { it.toInt() and 0xFF } and 0xFF
    }

    // Function to log errors
    private fun logError(error: String) {
        Log.e("TAG", "Error: $error" )
    }
}