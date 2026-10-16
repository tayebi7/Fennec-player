package com.fennecplayer.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.Settings
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.security.MessageDigest

object QrAndDeviceAuth {

    fun getVirtualMacAddress(context: Context): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "fennec001"
        val digest = MessageDigest.getInstance("MD5").digest(androidId.toByteArray())
        return digest.take(6).joinToString(":") { "%02x".format(it) }
    }

    fun getDeviceKey(context: Context): String {
        val mac = getVirtualMacAddress(context).replace(":", "")
        val hash = mac.hashCode().let { if (it < 0) -it else it }
        return (hash % 900000 + 100000).toString()
    }

    fun generateQrBitmap(content: String, size: Int = 400): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
}
