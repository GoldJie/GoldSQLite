package com.lxj.gold.sqlite_core.parser.model

import com.lxj.gold.sqlite_core.db.DbHelper

/**
 * Created by lixinjie on 2019/8/1
 * XML配置标签对应的实体：数据库
 */
class DbModel {
    companion object {
//        数据库名称
        const val ATTR_DB_NAME = "name"
//        数据库外存储路径(如果该属性有设置，则说明该数据库不是放在手机系统的内部存储中)
        const val ATTR_EXTERNAL_PATH = "external-path"
//        数据库版本
        const val ATTR_VERSION = "version"
    }

    /**
     * 以下四个变量含义对应以上四个常量属性
     */
    private var mDbName: String? = null
    private var mDbVersion: Int = 0
    private var mExternalPath: String? = null
    private var mDbHelper: DbHelper? = null
//    数据库中包含的列表
    private var mTableMap: Map<String, TableModel>? = null

    fun getDbName(): String? = mDbName

    fun setDbName(dbName: String?) {
        this.mDbName = dbName
    }

    fun getDbVersion(): Int = mDbVersion

    fun setDbVersion(dbVersion: Int) {
        this.mDbVersion = dbVersion
    }

    fun getExternalPath(): String? = mExternalPath

    fun setExternalPath(externalPath: String?) {
        this.mExternalPath = externalPath
    }

    fun getTableMap(): Map<String, TableModel>? = mTableMap

    fun setTableMap(tableMap: Map<String, TableModel>?) {
        this.mTableMap = tableMap
    }

    fun getDbHelper(): DbHelper? = mDbHelper

    fun setDbHelper(dbHelper: DbHelper?) {
        this.mDbHelper = dbHelper
    }
}