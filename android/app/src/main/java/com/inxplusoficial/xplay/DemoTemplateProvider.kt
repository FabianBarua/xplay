package com.inxplusoficial.xplay

import android.content.Context
import com.lynx.tasm.provider.AbsTemplateProvider
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class DemoTemplateProvider(context: Context) : AbsTemplateProvider() {

    private var mContext: Context = context.applicationContext

    override fun loadTemplate(uri: String, callback: Callback) {
        Thread {
            try {
                val inputStream: InputStream = if (uri.startsWith("http")) {
                    val url = URL(uri)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    connection.inputStream
                } else {
                    mContext.assets.open(uri)
                }

                ByteArrayOutputStream().use { byteArrayOutputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while ((inputStream.read(buffer).also { length = it }) != -1) {
                        byteArrayOutputStream.write(buffer, 0, length)
                    }
                    callback.onSuccess(byteArrayOutputStream.toByteArray())
                }

            } catch (e: IOException) {
                callback.onFailed(e.message)
            }
        }.start()
    }
}
