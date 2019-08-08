package com.lxj.gold.sqlite_core.exception

import android.util.Log
import java.lang.RuntimeException

/**
 * Created by lixinjie on 2019/8/1
 * 通用异常
 */
class GoldSQLiteCommonException(message: String) : RuntimeException(message) {
    private val TAG = GoldSQLiteCommonException::class.simpleName

    init {
        Log.e(TAG, message)
    }
}