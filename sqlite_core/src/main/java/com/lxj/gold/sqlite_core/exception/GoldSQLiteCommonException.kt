package com.lxj.gold.sqlite_core.exception

import android.util.Log
import java.lang.RuntimeException

/**
 * Created by lixinjie on 2019/8/1
 * 通用异常
 */
class GoldSQLiteCommonException: RuntimeException {
    private val TAG = GoldSQLiteCommonException::class.simpleName
    constructor(message: String): super(message){
        Log.e(TAG, message)
    }
}