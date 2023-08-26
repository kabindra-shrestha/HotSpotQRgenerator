package com.kabindra.hotspotqrgenerator.base

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

typealias InflateActivity<T> = (LayoutInflater) -> T

abstract class BaseActivity<viewBinding : ViewBinding>(
    private val inflate: InflateActivity<viewBinding>
) : AppCompatActivity() {

    private lateinit var _viewBinding: viewBinding
    val views get() = _viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")

        super.onCreate(savedInstanceState)

        _viewBinding = inflate(layoutInflater)
        setContentView(views.root)
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    /**
     * add code to handle fragment within the activity
     */
}