package com.lxj.gold.sqlite_core.exception

import android.util.Log
import java.lang.RuntimeException

/**
 * Created by lixinjie on 2019/8/1
 * Xml配置文件未找到异常
 */
class XmlConfigFileNotFoundException:
    RuntimeException("The config XML file is not found. Please check your config file.") {
    private val TAG = XmlConfigFileNotFoundException::class.simpleName

    init {
        Log.e(TAG, "The config XML file is not found. Please check your config file.")
    }
}