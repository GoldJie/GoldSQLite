package com.lxj.gold.sqlite_core.db

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.ArrayMap
import com.lxj.gold.sqlite_core.annotation.Column
import com.lxj.gold.sqlite_core.annotation.MainKey
import com.lxj.gold.sqlite_core.exception.GoldSQLiteCommonException
import com.lxj.gold.sqlite_core.utils.CommonUtil
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.util.ArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

/**
 * Created by lixinjie on 2019/8/2
 * 数据库表辅助类
 */
object TableHelper {
    private val TAG: String = TableHelper.javaClass.simpleName

//    SQLite数据库支持的字段类型
    private val TYPE_TEXT = "text"
    private val TYPE_INT = "int"
    private val TYPE_BIG_INT = "bigint"
    private val TYPE_INTEGER = "integer"
    private val TYPE_FLOAT = "float"
    private val TYPE_DOUBLE = "double"
    private val TYPE_BLOB = "blob"

    fun <T> transformCursorToEntity(cursor: Cursor?, tableClass: Class<T>?): T?{
        var entity: T? = null
        var constructor: Constructor<T>? = tableClass?.getConstructor()
        entity = constructor?.newInstance()
        var fieldList:
        for(field: Field in )
        return entity
    }

    /**
     * 获取与数据表列属性有映射关系的数据表实体类字段列表
     * @param tableClass    单个实体类映射类型
     * @return              声明为数据表字段的Field列表
     */
    fun transformFieldToColumn(tableClass: Class<*>): List<Field>{
        var filedsMap: ArrayMap<String, Field> = ArrayMap()
        var localTableClass = tableClass
//        tableClass.let {
//            遍历实体类的所有字段属性
            while (Object::class != localTableClass){
                for(field: Field in localTableClass.declaredFields){
//                    如果字段没有被注解Column注解则跳过
                    if(!field.isAnnotationPresent(Column::class.java)) continue
//                    获取声明的注解
                    val column: Column? = field.getAnnotation(Column::class.java)
//                    字段与列名建立映射关系
                    val columnKey: String = column?.name ?: ""
                    if(!TextUtils.isEmpty(columnKey) && !filedsMap.containsKey(columnKey)){
                        filedsMap[columnKey] = field
                    }
                }
//                沿着继承关系树一一获取父类字段
                localTableClass = localTableClass.superclass
            }
//        }
        val fieldsList = ArrayList<Field>()
//        遍历建立了映射关系后的列名集合
        for((_: String, field: Field) in filedsMap){
            field?.let {
//                如果字段注解是MainKey，说明是主键，放在List中第一位
                if(field.isAnnotationPresent(MainKey::class.java)){
                    fieldsList[0] = field
                }else{
                    fieldsList.add(field)
                }
            }
        }
        return fieldsList
    }

    /**
     * 判断某一表格是否存在.
     * @param db            数据库对象
     * @param tableName     数据表名称
     * @return              数据表是否存在
     */
    fun isExist(db: SQLiteDatabase, tableName: String): Boolean{
        var cursor: Cursor? = null
        try {
//            查找数据库内默认记录数据表的索引表
            cursor = db.rawQuery("select name from sqlite_master where type='table' ", null)
            cursor?.let {
                cursor.moveToFirst()
                while (cursor.moveToNext()){
//                    遍历出表名
                    val name: String = cursor.getString(0)
                    if(name == tableName) return true
                }
            }
        } catch (e: Exception) {
            throw GoldSQLiteCommonException(e.message.toString())
        } finally {
//            关闭游标
            CommonUtil.cloaseCursor(cursor)
        }
        return false
    }
}