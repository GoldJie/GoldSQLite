package com.lxj.gold.sqlite_core.exception

import android.util.Log
import java.lang.RuntimeException

/**
 * Created by lixinjie on 2019/8/1
 * 非法的XML配置文件异常
 */
class IllegelConfigXmlFileException :
    RuntimeException("The config XML file is illegal. Please check your config file.") {
    private val TAG = IllegelConfigXmlFileException::class.simpleName

    init {
        Log.e(TAG, "The config XML file is illegal. Please check your config file.")
    }
}