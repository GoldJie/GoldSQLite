package com.lxj.gold.sqlite_core.parser.model

/**
 * Created by lixinjie on 2019/8/1
 *
 */
class DemoModel {
    //    数据库名称
    val ATTR_DB_NAME = "name"
    //    数据库外存储路径(如果该属性有设置，则说明该数据库不是放在手机系统的内部存储中)
    val ATTR_EXTERNAL_PATH = "external-path"
    //    数据库版本
    val ATTR_VERSION = "version"

    /**
     * 以下四个变量含义对应以上四个常量属性
     */
    private var mDbName: String? = null
    private var mDbVersion: Int = 0
    private var mExternalPath: String? = null
//    private var mDbHelper: DbHelper? = null
    //    数据库中包含的列表
    private var mTableMap: Map<String, TableModel>? = null

    fun getDbName(): String? {
        return mDbName
    }

    fun setDbName(dbName: String) {
        this.mDbName = dbName
    }

    fun getDbVersion(): Int {
        return mDbVersion
    }

    fun setDbVersion(dbVersion: Int) {
        this.mDbVersion = dbVersion
    }

    fun getExternalPath(): String? {
        return mExternalPath
    }

    fun setExternalPath(externalPath: String) {
        this.mExternalPath = externalPath
    }

    fun getTableMap(): Map<String, TableModel>? {
        return mTableMap
    }

    fun setTableMap(tableMap: Map<String, TableModel>) {
        this.mTableMap = tableMap
    }
//
//    fun getDbHelper(): DbHelper? {
//        return mDbHelper
//    }
//
//    fun setDbHelper(dbHelper: DbHelper) {
//        this.mDbHelper = dbHelper
//    }
}