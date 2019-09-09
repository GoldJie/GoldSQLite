package com.lxj.gold.sqlite_core.db

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.text.TextUtils
import android.util.ArrayMap
import androidx.core.content.ContextCompat
import com.lxj.gold.sqlite_core.GoldSQLite
import com.lxj.gold.sqlite_core.dao.TableOperator
import com.lxj.gold.sqlite_core.exception.GoldSQLiteCommonException
import com.lxj.gold.sqlite_core.exception.NoStoragePermissionException
import com.lxj.gold.sqlite_core.parser.DbConfigParser
import com.lxj.gold.sqlite_core.parser.model.DbModel
import java.io.File

/**
 * Created by lixinjie on 2019/8/9
 * 数据库管理类，管理多个数据库
 */
object DbManager {
//    数据库Model对象列表
    private val mDbModelMap: MutableMap<String, DbModel> = ArrayMap()

    /**
     * 初始化数据库（异步初始化）
     */
    fun initDatabase(context: Context){
        Thread(Runnable {
//            解析XML配置文件，获取xml配置对象
            val xmlConfigModel = DbConfigParser.parseXmlConfigFile(context)
            xmlConfigModel?.let {
//                获取数据库列表
                val dbModelList = it.mDbList
                if(dbModelList.isNotEmpty()){
//                   遍历初始化数据库配置
                   for(dbModel in dbModelList) {
                       dbModel.getDbName()?.run {
                           mDbModelMap[this] = dbModel
                       }
                   }
                }
            }
        }).start()
    }

    /**
     * 组合dbName，检查是否有外存路径，有则获取外存路径拼接数据库地址
     * @param dbName            数据库名称
     * @param relativePath      外存相对路径
     * @return                  最终数据库名称结果
     */
    private fun combineDbName(dbName: String, relativePath: String?) : String {
        return if(!TextUtils.isEmpty(relativePath)){
            val absolutePath = combineExternalPath(relativePath)
            "$absolutePath/$dbName.db"
        } else {
            dbName
        }
    }

    /**
     * 组合并创建外存路径
     * @param relativePath      外存相对路径
     * @return                  外存绝对路径
     */
    private fun combineExternalPath(relativePath: String?): String?{
//        检查本地存储读写权限
        val permissionStr = ContextCompat.checkSelfPermission(GoldSQLite.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionStr != PackageManager.PERMISSION_GRANTED) throw NoStoragePermissionException()

        var absolutePath: String? = null
        if(!TextUtils.isEmpty(relativePath)){
            absolutePath = Environment.getExternalStorageDirectory().absolutePath + relativePath
            File(absolutePath).run {
//                如果外存绝对路径不存在则创建文件夹
                if(!exists()){
                    mkdir()
                }
            }
        }
        return absolutePath
    }

    /**
     * 通过数据库名称获取DbModel对象
     * @param dbName    数据库名称
     * @return          数据库模型对象
     */
    private fun getDbModelByName(dbName: String): DbModel{
        return  mDbModelMap[dbName] ?: throw GoldSQLiteCommonException("No such a Database model.")
    }

    /**
     * 获取指定名称的数据库db对象
     * @param dbName    db名称
     * @return          db对象
     */
    fun getSQLiteDb(dbName: String): SQLiteDatabase{
        getDbModelByName(dbName).run {
            var dbHelper = getDbHelper()
            if(dbHelper == null){
//                若为空则创建数据库辅助类
                val externalPath = getExternalPath()
//                组合数据库名称结果
                val combileDbName = combineDbName(dbName, externalPath)
//                创建数据库辅助类
                dbHelper = DbHelper(GoldSQLite.getContext(), combileDbName, getDbVersion(), this)
                setDbHelper(dbHelper)
            }
            return dbHelper.writableDatabase
        }
    }

    /**
     * 通过数据表名获取对应的数据表操作者对象
     * @param dbName        数据库名
     * @param tableName     需要获取数据操作对象的数据表名
     */
    fun getTableOperator(dbName: String, tableName: String): TableOperator{
        getDbModelByName(dbName).getTableMap()?.let {
                if(it.isNotEmpty()){
                    val tableModel =  it[tableName]
                    var tableOperator = tableModel?.getTableOperator()
                    tableOperator?.run {
                        return this
                    }
                    tableOperator = TableOperator(tableName)
                    tableModel?.setTableOperator(tableOperator)
                    return  tableOperator
                }
            }
        throw GoldSQLiteCommonException("Can't get the TableOperator from table model.")
    }
}