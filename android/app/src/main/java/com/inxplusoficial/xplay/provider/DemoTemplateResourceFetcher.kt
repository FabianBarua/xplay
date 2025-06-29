package com.inxplusoficial.xplay.provider

import android.content.Context
import com.inxplusoficial.xplay.Helpers.getAssetFilename
import com.inxplusoficial.xplay.Helpers.isAssetFilename
import com.inxplusoficial.xplay.Helpers.readFileFromAssets
import com.lynx.tasm.core.LynxThreadPool
import com.lynx.tasm.resourceprovider.LynxResourceCallback
import com.lynx.tasm.resourceprovider.LynxResourceRequest
import com.lynx.tasm.resourceprovider.LynxResourceResponse
import com.lynx.tasm.resourceprovider.template.LynxTemplateResourceFetcher
import com.lynx.tasm.resourceprovider.template.TemplateProviderResult
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.util.concurrent.TimeUnit

class DemoTemplateResourceFetcher(context: Context) : LynxTemplateResourceFetcher() {

    private val appContext = context.applicationContext

    private fun requestResource(
        request: LynxResourceRequest,
        callback: Callback<ResponseBody>
    ) {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://example.com/")
            .client(client)
            .callbackExecutor(LynxThreadPool.getBriefIOExecutor())
            .build()

        val templateApi = retrofit.create(TemplateApi::class.java)
        val call = templateApi.getTemplate(request.url)
        call.enqueue(callback)
    }

    override fun fetchTemplate(
        request: LynxResourceRequest?,
        callback: LynxResourceCallback<TemplateProviderResult>
    ) {
        if (request == null) {
            callback.onResponse(
                LynxResourceResponse.onFailed(Throwable("request is null!")) as LynxResourceResponse<TemplateProviderResult>?
            )
            return
        }

        val url = request.url
        if (isAssetFilename(url)) {
            val assetPath = getAssetFilename(url)
            readBundleFromAssets(assetPath, callback)
            return
        }

        requestResource(request, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                response.body()?.let {
                    try {
                        val result = TemplateProviderResult.fromBinary(it.bytes())
                        callback.onResponse(
                            LynxResourceResponse.onSuccess<TemplateProviderResult>(result)
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        callback.onResponse(LynxResourceResponse.onFailed(e) as LynxResourceResponse<TemplateProviderResult>?)
                    }
                } ?: run {
                    callback.onResponse(
                        LynxResourceResponse.onFailed(
                            Throwable("response body is null.")
                        ) as LynxResourceResponse<TemplateProviderResult>?
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                callback.onResponse(LynxResourceResponse.onFailed(throwable) as LynxResourceResponse<TemplateProviderResult>?)
            }
        })
    }

    private fun readBundleFromAssets(
        assetPath: String,
        callback: LynxResourceCallback<TemplateProviderResult>
    ) {
        LynxThreadPool.getBriefIOExecutor().execute {
            val data = readFileFromAssets(appContext, assetPath)
            if (data != null) {
                val result = TemplateProviderResult.fromBinary(data)
                callback.onResponse(
                    LynxResourceResponse.onSuccess<TemplateProviderResult>(result)
                )
            } else {
                callback.onResponse(
                    LynxResourceResponse.onFailed(Throwable("Unable to read file from assets.")) as LynxResourceResponse<TemplateProviderResult>?
                )
            }
        }
    }

    override fun fetchSSRData(
        request: LynxResourceRequest?,
        callback: LynxResourceCallback<ByteArray>
    ) {
        if (request == null) {
            callback.onResponse(
                LynxResourceResponse.onFailed(Throwable("request is null!")) as LynxResourceResponse<ByteArray>?
            )
            return
        }

        requestResource(request, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    response.body()?.let {
                        callback.onResponse(
                            LynxResourceResponse.onSuccess<ByteArray>(it.bytes())
                        )
                    } ?: run {
                        callback.onResponse(
                            LynxResourceResponse.onFailed(
                                Throwable("response body is null.")
                            ) as LynxResourceResponse<ByteArray>?
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    callback.onResponse(LynxResourceResponse.onFailed(e) as LynxResourceResponse<ByteArray>?)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                callback.onResponse(LynxResourceResponse.onFailed(throwable) as LynxResourceResponse<ByteArray>?)
            }
        })
    }
}
