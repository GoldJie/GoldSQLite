package com.lxj.gold.sqlite_core.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.lxj.gold.sqlite_core.parser.model.DbModel

/**
 * Created by lixinjie on 2019/8/1
 * 数据库辅助类
 */
class DbHelper(context: Context, name: String, version: Int, dbModel: DbModel) :
    SQLiteOpenHelper(context, name, null, version) {

    override fun onCreate(p0: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}