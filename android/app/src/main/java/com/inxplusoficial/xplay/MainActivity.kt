package com.inxplusoficial.xplay

import android.app.Activity
import android.os.Bundle
import com.lynx.tasm.LynxView
import com.lynx.tasm.LynxViewBuilder

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lynxView: LynxView = buildLynxView()
        setContentView(lynxView)

        val uri = "http://169.254.83.107:3000/main.lynx.bundle?fullscreen=true";
        lynxView.renderTemplateUrl(uri, "")

        // open switch page
        // startActivity(Intent(this, SwitchActivity::class.java));
    }

    private fun buildLynxView(): LynxView {
        val viewBuilder: LynxViewBuilder = LynxViewBuilder()
        viewBuilder.setTemplateProvider(DemoTemplateProvider(this))
        return viewBuilder.build(this)
    }
}