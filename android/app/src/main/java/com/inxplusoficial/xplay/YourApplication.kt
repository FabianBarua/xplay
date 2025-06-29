package com.inxplusoficial.xplay

import android.content.Context
import android.app.Application

import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.memory.PoolConfig
import com.facebook.imagepipeline.memory.PoolFactory
import com.lynx.service.devtool.LynxDevToolService
import com.lynx.service.http.LynxHttpService
import com.lynx.service.image.LynxImageService
import com.lynx.service.log.LynxLogService
import com.lynx.tasm.service.LynxServiceCenter

import com.lynx.tasm.LynxEnv

class LynxInitializer() : Application() {
    override fun onCreate() {
        super.onCreate()
        initLynxService(applicationContext)
        initLynxEnv(this)
    }

    fun initLynxService(applicationContext: Context) {
        // init Fresco which is needed by LynxImageService
        val factory = PoolFactory(PoolConfig.newBuilder().build())
        val builder = ImagePipelineConfig.newBuilder(applicationContext).setPoolFactory(factory)
        Fresco.initialize(applicationContext, builder.build())

        LynxServiceCenter.inst().registerService(LynxImageService.getInstance())
        LynxServiceCenter.inst().registerService(LynxLogService)
        LynxServiceCenter.inst().registerService(LynxHttpService)
        // register devtool service
        LynxServiceCenter.inst().registerService(LynxDevToolService)
    }

    fun initLynxEnv(app: Application) {
        LynxEnv.inst().init(
            app,
            null,
            null,
            null
        )
        // Turn on Lynx Debug
        LynxEnv.inst().enableLynxDebug(true)
        // Turn on Lynx DevTool
        LynxEnv.inst().enableDevtool(true)
        // Turn on Lynx LogBox
        LynxEnv.inst().enableLogBox(true)
    }
}
