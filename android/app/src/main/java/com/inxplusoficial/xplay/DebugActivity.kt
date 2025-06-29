package com.inxplusoficial.xplay

import android.app.Activity
import android.os.Bundle
import com.inxplusoficial.xplay.provider.DemoGenericResourceFetcher
import com.inxplusoficial.xplay.provider.DemoMediaResourceFetcher
import com.inxplusoficial.xplay.provider.DemoTemplateProvider
import com.inxplusoficial.xplay.provider.DemoTemplateResourceFetcher
import com.lynx.tasm.LynxBooleanOption
import com.lynx.tasm.LynxView
import com.lynx.tasm.LynxViewBuilder
import com.lynx.tasm.TemplateData

class DebugActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lynxView = buildLynxView()
        setContentView(lynxView)
        val url = intent.getStringExtra("url")
        if (url != null) {
            lynxView.renderTemplateUrl(url, TemplateData.empty())
        }
    }

    private fun buildLynxView(): LynxView {
        val viewBuilder = LynxViewBuilder()
        viewBuilder.setTemplateProvider(DemoTemplateProvider())
        viewBuilder.setEnableGenericResourceFetcher(LynxBooleanOption.TRUE)
        viewBuilder.setGenericResourceFetcher(DemoGenericResourceFetcher())
        viewBuilder.setTemplateResourceFetcher(DemoTemplateResourceFetcher(this))
        viewBuilder.setMediaResourceFetcher(DemoMediaResourceFetcher())
        return viewBuilder.build(this)
    }
}