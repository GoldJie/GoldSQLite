package com.lxj.gold.sqlite_core.exception

import android.util.Log
import java.lang.RuntimeException

/**
 * Created by lixinjie on 2019/8/1
 * 解析数据库配置Xml文件IO异常
 */
class ParseXmlConfigException(message: String) : RuntimeException(message) {
    private val TAG = ParseXmlConfigException::class.simpleName

    init {
        Log.e(TAG, "Parsing XML configuration file has an exception. Exception message: $message")
    }
}