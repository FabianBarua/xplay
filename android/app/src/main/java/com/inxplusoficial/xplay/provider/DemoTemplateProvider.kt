// Copyright 2024 The Lynx Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.
package com.inxplusoficial.xplay.provider

import com.lynx.tasm.provider.AbsTemplateProvider
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class DemoTemplateProvider : AbsTemplateProvider() {
    override fun loadTemplate(url: String, callback: Callback) {
        val retrofit = Retrofit.Builder().baseUrl("https://example.com/").build()

        val templateApi = retrofit.create(TemplateApi::class.java)

        val call = templateApi.getTemplate(url)

        call.enqueue(object : retrofit2.Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    if (response.body() != null) {
                        callback.onSuccess(response.body()!!.bytes())
                    } else {
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    callback.onFailed(e.toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                callback.onFailed(throwable.message)
            }
        })
    }
}
