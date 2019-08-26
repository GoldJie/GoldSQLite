package com.lxj.gold.sqlite_core.dao.crud.provider

import android.net.Uri
import com.lxj.gold.sqlite_core.utils.CommonUtil
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.DISTINCT
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_AUTHORITY
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_SCHEME
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.ROW_ID
import java.lang.StringBuilder

/**
 * Created by lixinjie on 2019/8/26
 * Uri处理者，负责生成、解析Uri
 */
object UriHandler {

    /**
     * 组合访问ContentProvider的Uri
     * @param dbName        数据库名
     * @param tableName     数据表名
     * @param operationType 数据操作的类型
     * @return              对应操作的URI对象
     */
    fun combineOperationUri(dbName: String, tableName: String, operationType: Int): Uri
        = Uri.parse("$OPERATION_SCHEME$OPERATION_AUTHORITY/$dbName/$tableName#$operationType")

    /**
     * 组合带参数的ContentProvider访问Uri
     * @param dbName        数据库名称
     * @param tableName     数据表名称
     * @param operationType 数据操作的类型
     * @param parmsMap      参数映射表
     * @return              带参数的Uri
     */
    fun combineOperationUriWithParms(dbName: String, tableName: String,
                                     operationType: Int, parmsMap: Map<String, String>): Uri{
        if(parmsMap.isNotEmpty()){
//            组合Uri协议、权限、路径
            val uriStr = "$OPERATION_SCHEME$OPERATION_AUTHORITY/$dbName/$tableName"
            val uriBuilder = StringBuilder(uriStr)
            var index = 0
//            组合Uri参数
            parmsMap.forEach{
                if(index == 0){
                    uriBuilder.append("?")
                }
                uriBuilder.append(it.key).append("=").append(it.value)
                if(index != parmsMap.size -1){
                    uriBuilder.append("&")
                }
                index++
            }
//            fragment部分拼接数据操作类型
            uriBuilder.append("#").append(operationType)
            return Uri.parse(uriBuilder.toString())
        }else {
            return combineOperationUri(dbName, tableName, operationType)
        }
    }
    /**
     * 从Uri中获取数据库名称
     * @param uri   传递给ContentProvider的Uri
     * @return      数据库名称
     */
    fun getDbNameFromUri(uri: Uri): String = uri.path?.split("/")?.get(1).toString()

    /**
     * 从Uri中获取数据表名称
     * @param uri   传递给ContentProvider的Uri
     * @return      数据表名称
     */
    fun getTableNameFromUri(uri: Uri): String = uri.lastPathSegment.toString()

    /**
     * 从插入操作返回的Uri中获取插入的行Id
     * @param uri   插入操作执行结束后返回的行Id
     * @return      int形式的插入行Id
     */
    fun getRowIdFromInsertUri(uri: Uri): Int = CommonUtil.transformStringToInt(uri.getQueryParameter(ROW_ID))

    /**
     * 从Uri中获取参数：是否去除重复行标识
     * @param uri   查询操作的Uri
     * @return      是否去除重复行
     */
    fun getDistinctFromUri(uri: Uri): Boolean = uri.getBooleanQueryParameter(DISTINCT, false)

    /**
     * 从Uri中获取参数，一般是语句中的子句或者其他特定关键字的内容
     * @param uri           Uri
     * @param key           获取参数的Key
     * @return              内容
     */
    fun getParmFromUri(uri: Uri, key: String): String = uri.getQueryParameter(key)?:""
}