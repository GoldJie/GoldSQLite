package com.lxj.gold.sqlite_core.db

/**
 * Created by lixinjie on 2019/8/9
 * 数据库创建、更新状态监听器接口
 */
interface DbStateListener {
    /**
     * 数据库创建
     * @param dbName    数据库名称
     */
    fun onCreate(dbName: String)

    /**
     * 数据库更新
     * @param dbName        数据库名称
     * @param oldVersion    旧版本号
     * @param newVersion    新版本号
     */
    fun onUpgrate(dbName: String, oldVersion: Int, newVersion: Int)
}