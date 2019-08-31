package com.lxj.gold.sqlite_core

import android.app.Application
import android.os.Handler
import android.os.Looper

/**
 * Created by lixinjie on 2019/8/2
 * 数据库框架总的入口操作类
 */
object GoldSQLite {
    private var mContext: Application? = null
//    主线程的Handler
    private val mMainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    /**
     * 初始化
     * @param application   App级别的上下文
     */
    fun init(application: Application) {
        mContext = application
    }

    /**
     * 获取Context上下文对象
     */
    fun getContext(): Application = mContext!!

    /**
     * 获取主线程Handler，用于线程切换
     */
    fun getMainThreadHandler(): Handler = mMainThreadHandler
}