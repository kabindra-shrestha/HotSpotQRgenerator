package com.amitgupta.hotspotqrgenerator

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.android.dx.stock.ProxyBuilder
import kotlinx.android.synthetic.main.activity_start_hotspot.*
import java.io.File
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method


class StartHotSpotActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_hotspot)

        start_hotspot_btn.setOnClickListener {
            startHotspot(this)
        }
    }

    fun startHotspot(context: Context) {
        val outputDir: File = context.getCacheDir()
        val proxy: Any
        try {
            proxy = ProxyBuilder.forClass(OnStartTetheringCallbackClass())
                .dexCache(outputDir)
                .handler { proxy, method, args ->
                    when (method.getName()) {
                        "onTetheringStarted" -> Log.e("", "invoke: started")
                        "onTetheringFailed" -> Log.e("", "invoke: failed")
                        else -> ProxyBuilder.callSuper(proxy, method, args)
                    }
                    null
                }.build()
            val mConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val method: Method = mConnectivityManager.javaClass.getDeclaredMethod(
                "startTethering",
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType, OnStartTetheringCallbackClass(),
                Handler::class.java
            )
            method.invoke(mConnectivityManager, 0, false, proxy, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun OnStartTetheringCallbackClass(): Class<*>? {
        try {
            return Class.forName("android.net.ConnectivityManager\$OnStartTetheringCallback")
        } catch (e: ClassNotFoundException) {
            Log.e("", "OnStartTetheringCallbackClass error: $e")
            e.printStackTrace()
        }
        return null
    }
}