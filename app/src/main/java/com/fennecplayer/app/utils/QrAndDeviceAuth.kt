package com.fennecplayer.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.util.UUID
import kotlin.random.Random

object QrAndDeviceAuth {

    private const val PREFS_NAME = "fennec_device_prefs"

    // توليد أو استرجاع Device ID (بصيغة MAC تشبه 1d:50:b8:2b:09:6f) و Device Key
    fun getOrCreateDeviceCredentials(context: Context): Pair<String, String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var deviceId = prefs.getString("device_id", null)
        var deviceKey = prefs.getString("device_key", null)

        if (deviceId == null || deviceKey == null) {
            val uuidBytes = UUID.randomUUID().toString().replace("-", "").take(12)
            deviceId = uuidBytes.chunked(2).joinToString(":").lowercase()
            deviceKey = Random.nextInt(100000, 999999).toString()

            prefs.edit()
                .putString("device_id", deviceId)
                .putString("device_key", deviceKey)
                .apply()
        }
        return Pair(deviceId, deviceKey)
    }

    // توليد صورة QR Code تحتوي على رابط التفعيل المدمج به Device ID و Device Key
    fun generateActivationQrBitmap(portalBaseUrl: String, deviceId: String, deviceKey: String, size: Int = 360): Bitmap {
        val fullUrl = "$portalBaseUrl?device_id=$deviceId&device_key=$deviceKey"
        val bitMatrix = QRCodeWriter().encode(fullUrl, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
}
