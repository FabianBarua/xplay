// Copyright 2025 The Lynx Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.
package com.inxplusoficial.xplay.provider

import android.content.Context
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

// Se você estiver usando a versão Kotlin de Helpers, use HelpersKt
class DemoTemplateResourceFetcher(context: Context) : LynxTemplateResourceFetcher() {
    private val mApplicationContext: Context = context.applicationContext

    // Cria e executa a chamada HTTP com Retrofit
    private fun requestResource(
        request: LynxResourceRequest,
        callback: Callback<ResponseBody>
    ) {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://example.com/") // Essa base é obrigatória pelo Retrofit
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
                LynxResourceResponse.onFailed(
                    Throwable("request is null!")
                )
            )
            return
        }

        val url = request.url
        if (isAssetFilename(url)) {
            val assetPath: String = getAssetFilename(url)
            readBundleFromAssets(assetPath, callback)
            return
        }

        requestResource(request, object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.body() != null) {
                    try {
                        val result =
                            TemplateProviderResult.fromBinary(response.body()!!.bytes())
                        callback.onResponse(LynxResourceResponse.onSuccess(result))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        callback.onResponse(LynxResourceResponse.onFailed(e))
                    }
                } else {
                    callback.onResponse(
                        LynxResourceResponse.onFailed(
                            Throwable("response body is null.")
                        )
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                callback.onResponse(LynxResourceResponse.onFailed(throwable))
            }
        })
    }

    private fun readBundleFromAssets(
        assetPath: String,
        callback: LynxResourceCallback<TemplateProviderResult>
    ) {
        LynxThreadPool.getBriefIOExecutor().execute {
            val data: ByteArray = readFileFromAssets(mApplicationContext, assetPath)
            if (data != null) {
                val result = TemplateProviderResult.fromBinary(data)
                callback.onResponse(
                    LynxResourceResponse.onSuccess(
                        result
                    )
                )
            } else {
                callback.onResponse(
                    LynxResourceResponse.onFailed(
                        Throwable("Unable to read file from assets.")
                    )
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
                LynxResourceResponse.onFailed(
                    Throwable("request is null!")
                )
            )
            return
        }

        requestResource(request, object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    if (response.body() != null) {
                        callback.onResponse(
                            LynxResourceResponse.onSuccess(
                                response.body()!!.bytes()
                            )
                        )
                    } else {
                        callback.onResponse(
                            LynxResourceResponse.onFailed(
                                Throwable("response body is null.")
                            )
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    callback.onResponse(LynxResourceResponse.onFailed(e))
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                callback.onResponse(LynxResourceResponse.onFailed(throwable))
            }
        })
    }
}
