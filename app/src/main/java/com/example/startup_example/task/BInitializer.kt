package com.example.startup_example.task

import android.content.Context
import androidx.startup.Initializer

/**
 * Author : wangbo
 * Date : 2022/7/15
 * Function : TODO 请在这里输入文件用途
 * Desc : TODO 请在这里输入文件描述
 */
class BInitializer : Initializer<SdkB> {
    override fun create(context: Context): SdkB {
        Thread.sleep(1000)
        return SdkB.create(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(AInitializer::class.java)
    }
}