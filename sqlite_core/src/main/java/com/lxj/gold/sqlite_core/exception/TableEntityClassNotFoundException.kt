package com.lxj.gold.sqlite_core.exception

import android.util.Log
import java.lang.RuntimeException

/**
 * Created by lixinjie on 2019/8/1
 * 数据表映射实体类未找到异常
 */
class TableEntityClassNotFoundException:
    RuntimeException("The table entity is not found. Please check your entities.") {
    private val TAG = TableEntityClassNotFoundException::class.simpleName

    init {
        Log.e(TAG, "The table entity is not found. Please check your entities.")
    }
}