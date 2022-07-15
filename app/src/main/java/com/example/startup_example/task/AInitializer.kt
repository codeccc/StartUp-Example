package com.example.startup_example.task

import android.content.Context
import androidx.startup.Initializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Author : wangbo
 * Date : 2022/7/15
 * Function : TODO 请在这里输入文件用途
 * Desc : TODO 请在这里输入文件描述
 */
class AInitializer : Initializer<SdkA> {
    override fun create(context: Context): SdkA {
        Thread.sleep(500)
        return SdkA.create(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}