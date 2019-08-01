package com.lxj.gold.sqlite_core.exception

import android.util.Log
import java.lang.RuntimeException

/**
 * Created by lixinjie on 2019/8/1
 * 解析数据库配置Xml文件IO异常
 */
class ParseXmlConfigException: RuntimeException{
    private val TAG = ParseXmlConfigException::class.simpleName
    constructor(message: String): super(message){
        Log.e(TAG, "Parsing XML configuration file has an exception. Exception message: $message")
    }
}