// Copyright 2025 The Lynx Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.
package com.inxplusoficial.xplay.provider

import com.lynx.tasm.base.LLog
import com.lynx.tasm.resourceprovider.LynxResourceCallback
import com.lynx.tasm.resourceprovider.LynxResourceRequest
import com.lynx.tasm.resourceprovider.LynxResourceResponse
import com.lynx.tasm.resourceprovider.generic.LynxGenericResourceFetcher
import com.lynx.tasm.resourceprovider.generic.StreamDelegate
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class DemoGenericResourceFetcher : LynxGenericResourceFetcher() {
    override fun fetchResource(
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

        LLog.i(TAG, "fetchResource: " + request.url)
        val retrofit = Retrofit.Builder().baseUrl("https://example.com/").build()
        val templateApi = retrofit.create(TemplateApi::class.java)
        val call = templateApi.getTemplate(request.url)
        call.enqueue(object : Callback<ResponseBody?> {
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
                            LynxResourceResponse.onFailed(Throwable("response body is null."))
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

    override fun fetchResourcePath(
        request: LynxResourceRequest, callback: LynxResourceCallback<String>
    ) {
        callback.onResponse(
            LynxResourceResponse.onFailed(Throwable("fetchResourcePath not supported."))
        )
    }

    override fun fetchStream(request: LynxResourceRequest, delegate: StreamDelegate) {
        delegate.onError("fetchStream not supported.")
    }

    override fun cancel(request: LynxResourceRequest) {}

    companion object {
        const val TAG: String = "DemoGenericResourceFetcher"
    }
}
