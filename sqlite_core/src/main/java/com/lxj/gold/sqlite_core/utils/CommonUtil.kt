package com.lxj.gold.sqlite_core.utils

import android.content.ContentValues
import android.database.Cursor
import android.text.TextUtils
import com.lxj.gold.sqlite_core.annotation.TableName
import com.lxj.gold.sqlite_core.exception.GoldSQLiteCommonException
import com.lxj.gold.sqlite_core.exception.TableEntityClassNotFoundException
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.sql.Blob

/**
 * Created by lixinjie on 2019/8/1
 * 公共的工具类
 */
object CommonUtil {
    /**
     * 将String转换为int
     * @param integerStr    数字字符串
     * @return              int类型数字
     */
    fun transformStringToInt(integerStr: String?): Int =
        if (!TextUtils.isEmpty(integerStr) && TextUtils.isDigitsOnly(integerStr))
            Integer.valueOf(integerStr ?: "-1") else -1

    /**
     * Blob转换为Bytes数组
     * @param blob  Blob对象
     * @return      Bytes数组
     */
    fun transformBlobToBytes(blob: Blob): ByteArray?{
        var bytes: ByteArray? = null
        var input: InputStream? = null
        var inputBuffered: BufferedInputStream? = null
        var output: ByteArrayOutputStream? =null

        try {
            input = blob.binaryStream
            inputBuffered = BufferedInputStream(input)
            output = ByteArrayOutputStream(1024)
            val temp = ByteArray(1024)
            var size: Int
            do{
                size = inputBuffered.read(temp)
                output.write(temp, 0, size)
            }while (size != -1)

            bytes = output.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException(e.message?: "fun transformBlobToBytes(..) has error.")
        } finally {
            inputBuffered?.close()
            input?.close()
            output?.close()
        }

        return bytes
    }

    /**
     * 解析映射表，组合ContentValues
     * @param valuesMap     数据表字段数值映射表
     */
    fun combineContentValues(valuesMap: Map<String, Any>): ContentValues{
        val contentValues = ContentValues()
        for((key, value) in valuesMap){
            when(value){
                is Short -> contentValues.put(key, value)
                is Int -> contentValues.put(key, value)
                is Long -> contentValues.put(key, value)
                is Float -> contentValues.put(key, value)
                is Double -> contentValues.put(key, value)
                is String -> contentValues.put(key, value)
                is Blob -> contentValues.put(key, transformBlobToBytes(value))
            }
        }
        return contentValues
    }

    /**
     * 关闭游标(Kotlin化后这玩意儿好像没用了)
     * @param cursor 游标对象
     */
    fun closeCursor(cursor: Cursor?){
        cursor?.close()
    }

    /**
     * 从数据表对应的实体类路径中获取数据表名称
     * @param classPath     数据表实体类绝对路径（完整包名）
     * @return              数据表名
     */
    fun getTableNameFromClassPath(classPath: String?): String =
        if(!TextUtils.isEmpty(classPath)) getTableNameFromClass(classPath?.let { Class.forName(it) } as Class<*>)
        else throw TableEntityClassNotFoundException()

    /**
     * 从数据表对应实体类类型中获取数据表名
     * @param tableClass    数据表对应实体类类名
     * @return              数据表名
     */
    fun getTableNameFromClass(tableClass: Class<*>): String{
        var tableName = ""
//        从注解中获取数据表名
        if (tableClass.isAnnotationPresent(TableName::class.java)) {
            val annotation = tableClass.getAnnotation(TableName::class.java)
            tableName = annotation?.tableName?: ""
        }
        if(TextUtils.isEmpty(tableName)){
            throw TableEntityClassNotFoundException()
        }
        return tableName
    }
}