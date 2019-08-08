package com.lxj.gold.sqlite_core.exception

import android.util.Log
import java.lang.RuntimeException

/**
 * Created by lixinjie on 2019/8/1
 * 无权限操作本地存储异常
 */
class NoStoragePermissionException:
    RuntimeException("No permission about external storage.") {
    private val TAG = NoStoragePermissionException::class.simpleName

    init {
        Log.e(TAG, "No permission about external storage.")
    }
}