package com.inxplusoficial.xplay

import android.content.Context
import java.io.ByteArrayOutputStream
import java.io.IOException

object Helpers {

    private const val URL_PREFIX = "assets://"

    fun isAssetFilename(url: String): Boolean {
        return url.startsWith(URL_PREFIX)
    }

    fun getAssetFilename(url: String): String {
        return url.removePrefix(URL_PREFIX)
    }

    fun readFileFromAssets(context: Context, fileName: String): ByteArray? {
        return try {
            context.assets.open(fileName).use { inputStream ->
                val buffer = ByteArray(1024)
                val output = ByteArrayOutputStream()
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    output.write(buffer, 0, length)
                }
                output.toByteArray()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
