package com.inxplusoficial.xplay

import android.content.Context
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

object helpers {
    fun isAssetFilename(url: String): Boolean {
        return url.startsWith(URL_PREFIX)
    }

    fun getAssetFilename(url: String): String {
        return url.substring(URL_PREFIX.length())
    }

    fun readFileFromAssets(context: Context, fileName: String): ByteArray? {
        val assetManager = context.assets
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        var inputStream: InputStream? = null

        try {
            inputStream = assetManager.open(fileName)
            byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length: Int

            while ((inputStream.read(buffer).also { length = it }) != -1) {
                byteArrayOutputStream.write(buffer, 0, length)
            }

            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                inputStream?.close()
                byteArrayOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}


