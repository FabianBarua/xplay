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

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lynxView: LynxView = buildLynxView()
        setContentView(lynxView)

        val uri = "http://192.168.100.104:3000/main.lynx.bundle?fullscreen=true";
        lynxView.renderTemplateUrl(uri, "")

        // open switch page
        // startActivity(Intent(this, SwitchActivity::class.java));
    }

    private fun buildLynxView(): LynxView {
        val viewBuilder: LynxViewBuilder = LynxViewBuilder()
        viewBuilder.setTemplateProvider(DemoTemplateProvider())
        viewBuilder.setEnableGenericResourceFetcher(LynxBooleanOption.TRUE)
        viewBuilder.setGenericResourceFetcher(DemoGenericResourceFetcher())
        viewBuilder.setTemplateResourceFetcher(DemoTemplateResourceFetcher(this))
        viewBuilder.setMediaResourceFetcher(DemoMediaResourceFetcher())
        return viewBuilder.build(this)
    }
}