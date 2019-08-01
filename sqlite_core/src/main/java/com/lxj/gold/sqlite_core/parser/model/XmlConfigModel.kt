package com.lxj.gold.sqlite_core.parser.model

/**
 * Created by lixinjie on 2019/8/1
 * XML配置文件对应的实体类
 */
data class XmlConfigModel(var mDbList: List<DbModel>) {
    companion object {
//        XML配置文件根元素标签名称
        val LABEL_GOLD_SQLITE = "gold-sqlite"
//        数据库标签名称
        val LABEL_DATABASE = "database"
//        数据表标签名称
        val LABEL_TABLE = "table"
    }
}