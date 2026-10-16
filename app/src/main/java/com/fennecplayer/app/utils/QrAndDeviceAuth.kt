// Fennec-player-main/app/src/main/java/com/fennecplayer/app/utils/QrAndDeviceAuth.kt

package com.fennecplayer.app.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

object QrAndDeviceAuth {
    fun generateQRCode(text: String, width: Int = 512, height: Int = 512): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, width, height)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
