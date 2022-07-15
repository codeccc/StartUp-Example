package com.example.startup_example.task

import android.content.Context
import android.util.Log

/**
 * Author : wangbo
 * Date : 2022/7/15
 * Function : TODO 请在这里输入文件用途
 * Desc : TODO 请在这里输入文件描述
 */
object SdkA {

    init {
        Log.d("StartUp", this.javaClass.simpleName + "被初始化了")
    }

    fun create(context: Context):SdkA {
        return this
    }
}