package com.lxj.gold.sqlite_core.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import com.lxj.gold.sqlite_core.annotation.Check
import com.lxj.gold.sqlite_core.annotation.Column
import com.lxj.gold.sqlite_core.annotation.MainKey
import com.lxj.gold.sqlite_core.annotation.Unique
import com.lxj.gold.sqlite_core.exception.GoldSQLiteCommonException
import com.lxj.gold.sqlite_core.exception.TableEntityClassNotFoundException
import com.lxj.gold.sqlite_core.parser.model.DbModel
import com.lxj.gold.sqlite_core.parser.model.TableModel
import com.lxj.gold.sqlite_core.utils.CommonUtil
import org.jetbrains.annotations.NotNull
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.sql.Blob
import java.util.*

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

    /**
     * 根据实体类映射关系创建数据库表
     * @param db                数据库对象
     * @param dbModel           DatabaseModel对象
     */
    fun createTables(db: SQLiteDatabase, dbModel: DbModel) {
//        获取数据库模型中的数据表映射
        dbModel.getTableMap()?.let {
            if(it.isNotEmpty()){
                createTables(db, it)
            }
        }
    }

    /**
     * 根据实体类映射关系创建数据库表
     * @param db                数据库对象
     * @param tableModelMap     数据模型映射表
     */
    fun createTables(db: SQLiteDatabase, tableModelMap: Map<String, TableModel>) {
        try {
            if (tableModelMap.isNotEmpty()) {
//                遍历数据表模型列表
                for((_, tableModel) in tableModelMap){
//                    获取数据对应实体类名称（完整包名）
                    tableModel.getClassName()?.run {
//                        获取数据对应实体类类型
                        val classType = Class.forName(this)
//                        获取数据表名
                        tableModel.getTableName()?.let {
//                            创建数据表
                            createTable(db, it, classType)
                        }
                    }
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            throw TableEntityClassNotFoundException()
        }
    }

    /**
     * 根据表名创建数据库表
     * @param db            数据库对象
     * @param tableName     数据表名
     */
    fun createTable(db: SQLiteDatabase, tableName: String, tableClass: Class<*>){
//        创建StringBuilder用于构造SQL语句
        val strBuilder = StringBuilder()
        strBuilder.append("create table if not exists ").append(tableName).append(" (")
//        获取数据库字段列表
        val fieldsList = transformFieldToColumn(tableClass)
        fieldsList.forEach {
//            获取字段上声明的注解
            it.getAnnotation(Column::class.java)?.run {
//                若注解声明的数据表列类型为空，则通过实体类字段本身类型去获取
                val columnType =
                    if (TextUtils.isEmpty(type)) {
                        getColumnType(it.type)
                    } else {
                        type
                    }
                strBuilder.append(name).append(" ").append(columnType)
//                注解长度有设置则添加上类型长度
                if (length != 0) {
                    strBuilder.append("($length)")
                }
            }

//             实体类中作为主键的字段为int类型时，需要设置主键自增
            if (it.isAnnotationPresent(MainKey::class.java) && (it.type == Integer.TYPE || it.type == Int::class.java)) {
                strBuilder.append(" primary key autoincrement")
            } else if (it.isAnnotationPresent(MainKey::class.java)) {
                strBuilder.append(" primary key")
            } else {
//                数据表约束操作
                if (it.isAnnotationPresent(Unique::class.java)) {
                    strBuilder.append(" unique")
                }
                if (it.isAnnotationPresent(NotNull::class.java)) {
                    strBuilder.append(" not null")
                }
                it.getAnnotation(Check::class.java)?.run {
                    val checkStr = value
                    if (!TextUtils.isEmpty(checkStr)) {
                        strBuilder.append(" check(").append(checkStr).append(")")
                    }
                }
            }
            strBuilder.append(", ")
        }
//         删除末尾的","和空格
        strBuilder.delete(strBuilder.length - 2, strBuilder.length - 1)
        strBuilder.append(")")

//         执行SQL语句创建表格
        try {
            Log.d(TAG, "create table [$tableName]")
            db.execSQL(strBuilder.toString())
        } catch (e: Exception) {
            throw GoldSQLiteCommonException("create table[$tableName] error: " + e.message)
        }
    }

    /**
     * 不影响原数据表数据的情况下更新数据表
     * @param db            数据库对象
     * @param dbModel       数据表名
     */
    fun upgrateTables(db: SQLiteDatabase, dbModel: DbModel) {
//        获取数据库模型中的数据表映射
        dbModel.getTableMap()?.let {
            if(it.isNotEmpty()){
                upgrateTables(db, it)
            }
        }
    }

    /**
     * 不影响原数据表数据的情况下更新数据表
     * @param db                数据库对象
     * @param tableModelMap     数据模型映射表
     */
    fun upgrateTables(db: SQLiteDatabase, tableModelMap: Map<String, TableModel>) {
        try {
            if (tableModelMap.isNotEmpty()) {
//                遍历数据表模型列表
                for((_, tableModel) in tableModelMap){
//                    获取数据对应实体类名称（完整包名）
                    tableModel.getClassName()?.let {
//                        获取数据对应实体类类型
                        val classType = Class.forName(it)
//                        更新数据表
                        upgrateTable(db, it, classType)
                    }
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            throw TableEntityClassNotFoundException()
        }
    }

    /**
     * 不影响原数据表数据的情况下更新数据表
     * @param db            数据库对象
     * @param tableName     数据表名
     * @param tableClass    数据表实体类类型
     */
    fun upgrateTable(db: SQLiteDatabase, tableName: String, tableClass: Class<*>){
//        标识是否将原表名更改为临时表
        var ifAlterTableName = false
        var oldCursor: Cursor? = null
        var newCursor: Cursor? = null

        try {
//            开启事务
            db.beginTransaction()
//        　　表格存在则更新旧表
            if(isExist(db, tableName)){
                oldCursor = db.rawQuery("select * from $tableName", null)
                oldCursor?.let {
//                    标识旧表是否为空
                    var isOldTableDataNull = true
                    if(oldCursor.count > 0){
                        isOldTableDataNull = false
                    }
//                    将旧表更名为临时表，用于中转原表数据
                    db.execSQL("alter table $tableName rename to _temp_$tableName")
                    ifAlterTableName = true

//                    创建新表
                    createTable(db, tableName, tableClass)
//                    旧表存在数据则转移数据
                    if(!isOldTableDataNull){
//                        查询新表（为了获取新表字段集合）
                        newCursor = db.rawQuery("select * from $tableName", null)
                            ?: throw GoldSQLiteCommonException("Creating new table is failed when the table is on updating. ")

//                         获取新表中所有的字段名
                        val newColumnNamesArray = newCursor?.columnNames?: arrayOf("")
//                         获取旧表中所有的字段名
                        val oldColumnNamesArray = oldCursor.columnNames
//                         数据插入语句
                        val insertStr = StringBuilder("insert into $tableName")
//                         新表字段
                        val newColumns = StringBuilder("(")
//                         旧表字段
                        val oldColumns = StringBuilder()

//                        将对应旧表的字段与新表字段匹配
                        for (i in oldColumnNamesArray.indices) {
                            for (newColumnName in newColumnNamesArray) {
                                newColumnName?.run {
                                    if (this == oldColumnNamesArray[i]) {
                                        newColumns.append(oldColumnNamesArray[i])
                                        oldColumns.append(oldColumnNamesArray[i])
                                        if (i != oldColumnNamesArray.size - 1) {
                                            newColumns.append(",")
                                            oldColumns.append(",")
                                        }
                                    }
                                }
                            }
                        }
//                        组合插入语句
                        newColumns.append(")")
                        insertStr.append(newColumns.toString())
                            .append(" select ")
                            .append(oldColumns.toString())
                            .append(" from _temp_")
                            .append(tableName)
//                        将旧表数据插入新表
                        db.execSQL(insertStr.toString())
                    }
//                    删除旧表
                    dropTable(db, "_temp_$tableName")
                }
            }else {
//                表格不存在则是新表，直接创建
                createTable(db, tableName, tableClass)
            }
//            关闭事务
            db.setTransactionSuccessful()
            db.endTransaction()
        } catch (e: Exception) {
//            有异常且临时表已经变更过名称的情况下，将旧表名称改回去
            if (ifAlterTableName) {
                db.execSQL("alter table _temp_$tableName rename to $tableName")
            }
            throw GoldSQLiteCommonException(e.message?: "fun upgrateTable(..) has error. ")
        } finally {
            CommonUtil.closeCursor(oldCursor)
            CommonUtil.closeCursor(oldCursor)
        }
    }

    /**
     * 根据实体类映射关系删除数据表
     * @param db            数据库对象
     * @param tableModel    TableModel对象
     */
    fun dropTable(db: SQLiteDatabase, tableModel: TableModel){
        dropTable(db, CommonUtil.getTableNameFromClassPath(tableModel.getClassName()))
    }

    /**
     * 根据表名删除数据库表
     * @param db            数据库对象
     * @param tableName     数据表名
     */
    fun dropTable(db: SQLiteDatabase, tableName: String){
        db.execSQL("drop table if exists $tableName")
        Log.d(TAG, "dropTable[$tableName]")
    }

    /**
     * 将数据表实体类转换成<数据表列名，字段对象>形式的映射表
     * @param tableEntity   数据表实体类
     * @return             <数据表列名，字段对象>形式的映射表
     */
    fun transformEntityToValuesMap(tableEntity: Any): ArrayMap<String, Any>? {
        val valuesMap = ArrayMap<String, Any>()
        try {
//            获取与数据表列属性有映射关系的数据表实体类字段列表
            val fieldList = transformFieldToColumn(tableEntity::class.java)
            fieldList.forEach {
                it.isAccessible = true
                it.getAnnotation(Column::class.java)?.run {
//                    获取数据表字段名
                    val columnName = name
                    valuesMap[columnName] = it.get(tableEntity)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Transforming entity has error: " + e.message)
            throw GoldSQLiteCommonException("IllegalAccessException: " + e.message)
        }
        return valuesMap
    }

    /**
     * 将Cursor对象转换成对应的数据表实体类
     * @param cursor        Cursor对象
     * @param tableClass    数据表实体类类类型
     * @param <T>           泛型声明数据表实体类
     * @return              数据表实体类
     */
    fun <T> transformCursorToEntity(cursor: Cursor, tableClass: Class<T>): T?{
        var entity: T? = null
        try {
            val constructor: Constructor<T> = tableClass.getConstructor()
//            构造函数新建数据表实体类
            entity = constructor.newInstance()
//            获取与数据表列属性有映射关系的数据表实体类字段列表
            val fieldList = transformFieldToColumn(tableClass)
            fieldList.forEach {
                it.getAnnotation(Column::class.java)?.run {
//                    数据表字段名
                    val columnName = name
//                数据表字段类型
//                若注解声明的数据表列类型为空，则通过实体类字段本身类型去获取
                    val columnType =
                        if (TextUtils.isEmpty(type)) {
                            getColumnType(it.type)
                        } else {
                            type
                        }
                    it.isAccessible = true
//                    根据不同数据表字段类型来设置实体成员变量
                    it.set(entity, with(cursor){
                        val index = getColumnIndex(columnName)
                        when(columnType){
                            TYPE_TEXT -> getString(index)
                            TYPE_BIG_INT -> getLong(index)
                            TYPE_INTEGER -> getInt(index)
                            TYPE_INT -> getShort(index)
                            TYPE_FLOAT -> getFloat(index)
                            TYPE_DOUBLE -> getDouble(index)
                            TYPE_BLOB -> getBlob(index)
                            else -> getString(index)
                        }
                    })
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Transforming cursor has error: " + e.message)
            throw GoldSQLiteCommonException(e.message ?: "Transforming cursor has error.")
        }
        return entity
    }

    /**
     * 获取与数据表列属性有映射关系的数据表实体类字段列表
     * @param tableClass    单个实体类映射类型
     * @return              声明为数据表字段的Field列表
     */
    fun transformFieldToColumn(tableClass: Class<*>): List<Field>{
        val filedsMap = ArrayMap<String, Field>()
        var localTableClass = tableClass
//        tableClass.let {
//            遍历实体类的所有字段属性
            while (Object::class.java != localTableClass){
                for(field in localTableClass.declaredFields){
//                    如果字段没有被注解Column注解则跳过
                    if(!field.isAnnotationPresent(Column::class.java)) continue
//                    获取声明的注解
                    val column: Column? = field.getAnnotation(Column::class.java)
//                    字段与列名建立映射关系
                    val columnKey = column?.name ?: ""
                    if(columnKey.isNotEmpty() && !filedsMap.containsKey(columnKey)){
                        filedsMap[columnKey] = field
                    }
                }
//                沿着继承关系树一一获取父类字段
                localTableClass = localTableClass.superclass as Class<*>
            }
//        }
        val fieldsList = ArrayList<Field>()
//        遍历建立了映射关系后的列名集合
        for((_, field) in filedsMap){
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
     * 获取列（字段）类型
     * @param fieldType   字段类型
     * @return            列类型，默认返回text
     */
    fun getColumnType(fieldType: Class<*>): String{
        if(fieldType == String::class.java){
            return TYPE_TEXT
        }
        if(Integer.TYPE == fieldType || Integer::class.java == fieldType){
            return TYPE_INTEGER
        }
        if(java.lang.Long.TYPE == fieldType || Long::class.java == fieldType){
            return TYPE_BIG_INT
        }
        if(java.lang.Float.TYPE == fieldType || Float::class.java == fieldType){
            return TYPE_FLOAT
        }
        if(java.lang.Short.TYPE == fieldType || Short::class.java == fieldType){
            return TYPE_INT
        }
        if(java.lang.Double.TYPE == fieldType || Double::class.java == fieldType){
            return TYPE_DOUBLE
        }
        if(Blob::class.java == fieldType){
            return TYPE_BLOB
        }
        return TYPE_TEXT
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
            cursor?.run {
                moveToFirst()
                while (moveToNext()){
//                    遍历出表名
                    val name: String = getString(0)
                    if(name == tableName) return true
                }
            }
        } catch (e: Exception) {
            throw GoldSQLiteCommonException(e.message.toString())
        } finally {
//            关闭游标
            CommonUtil.closeCursor(cursor)
        }
        return false
    }
}