package com.lxj.gold.sqlite_core.db

import android.content.Context
import android.util.ArrayMap
import com.lxj.gold.sqlite_core.parser.model.DbModel

/**
 * Created by lixinjie on 2019/8/9
 * 数据库管理类，管理多个数据库
 */
object DbManager {
//    数据库Model对象列表
    private val mDbModelMap: MutableMap<String, DbModel> = ArrayMap()

    fun initDatabase(context: Context){
        Thread(Runnable {

        }).start()
    }
}