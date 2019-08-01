package com.lxj.gold.sqlite_core.parser

import android.text.TextUtils
import android.util.ArrayMap
import com.lxj.gold.sqlite_core.exception.IllegelConfigXmlFileException
import com.lxj.gold.sqlite_core.parser.model.DbModel
import com.lxj.gold.sqlite_core.parser.model.DbModel.Companion.ATTR_DB_NAME
import com.lxj.gold.sqlite_core.parser.model.DbModel.Companion.ATTR_EXTERNAL_PATH
import com.lxj.gold.sqlite_core.parser.model.DbModel.Companion.ATTR_VERSION
import com.lxj.gold.sqlite_core.parser.model.TableModel
import com.lxj.gold.sqlite_core.parser.model.TableModel.Companion.ATTR_CLASS
import com.lxj.gold.sqlite_core.parser.model.XmlConfigModel.Companion.LABEL_DATABASE
import com.lxj.gold.sqlite_core.parser.model.XmlConfigModel.Companion.LABEL_GOLD_SQLITE
import com.lxj.gold.sqlite_core.parser.model.XmlConfigModel.Companion.LABEL_TABLE
import com.lxj.gold.sqlite_core.utils.CommonUtil
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.util.ArrayList

/**
 * Created by lixinjie on 2019/8/1
 * 解析XML配置文档的处理器，用于SAX解析
 */
class XmlConfigHandler: DefaultHandler() {
    private var isFirstElement = true
    private var mDbModel: DbModel? = null
    private var mTableModel: TableModel? = null
    private var mTableMap: ArrayMap<String, TableModel>? = null
    private var mDbList: ArrayList<DbModel>? = null

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        super.startElement(uri, localName, qName, attributes)
//        进入根元素标签时检查XML配置文件是否符合标准，不符合标准将抛出异常
        if (isFirstElement && checkIsLegalConfigXmlFile(localName)) {
            mDbList = ArrayList()
            isFirstElement = false
        }
        if (mTableMap == null) {
            mTableMap = ArrayMap()
        }

//        解析<database>开始标签
        parseStartDatabaseLabel(localName, attributes)
//        解析<table>开始标签
        parseStartTableLabel(localName, attributes)
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        super.endElement(uri, localName, qName)
//        解析<table>结束标签
        parseEndTableLabel(localName)
//        解析<database>结束标签
        parseEndDatabaseLabel(localName)
    }

    /**
     * 检查XML配置文件是否符合标准
     * @param theFirstElementName   XML文档的首个开始标签名称
     * @return                      是否符合标准
     */
    private fun checkIsLegalConfigXmlFile(theFirstElementName: String?): Boolean{
        if(!TextUtils.isEmpty(theFirstElementName) || LABEL_GOLD_SQLITE == theFirstElementName)
            return true
        else throw IllegelConfigXmlFileException()
    }

    /**
     * 解析<database>开始标签
     * @param labelName     标签名称
     * @param attributes    标签属性
     */
    private fun parseStartDatabaseLabel(labelName: String?, attributes: Attributes?){
        if(!TextUtils.isEmpty(labelName) && LABEL_DATABASE == labelName){
            mDbModel = DbModel()
            val dbName = attributes?.getValue(ATTR_DB_NAME)
            val dbVersion = attributes?.getValue(ATTR_VERSION)
            val dbExternalPath = attributes?.getValue(ATTR_EXTERNAL_PATH)

            mDbModel!!.setDbName(dbName)
            mDbModel!!.setDbVersion(CommonUtil.transformStringToInt(dbVersion))
            mDbModel!!.setExternalPath(dbExternalPath)
        }
    }

    /**
     * 解析<table>开始标签
     * @param labelName     标签名称
     * @param attributes    标签属性
     */
    private fun parseStartTableLabel(labelName: String?, attributes: Attributes?){
        if(!TextUtils.isEmpty(labelName) && LABEL_TABLE == labelName){
            mTableModel = TableModel()
            val tableClass = attributes!!.getValue(ATTR_CLASS)
            val tableName = CommonUtil.getTableNameFromClassPath(tableClass)

            mTableModel!!.setClassName(tableClass)
            mTableModel!!.setTableName(tableName)
        }
    }

    /**
     * 解析<database>结束标签
     * @param labelName     标签名称
     */
    private fun parseEndDatabaseLabel(labelName: String?){
        if (!TextUtils.isEmpty(labelName) && LABEL_DATABASE == labelName) {
            mDbModel!!.setTableMap(mTableMap!!)
            mDbList!!.add(mDbModel!!)
            mTableMap = null
        }
    }

    /**
     * 解析<table>结束标签
     * @param labelName     标签名称
     */
    private fun parseEndTableLabel(labelName: String?){
        if (!TextUtils.isEmpty(labelName) && LABEL_TABLE == labelName) {
            mTableMap!![mTableModel!!.getTableName()] = mTableModel
        }
    }

    /**
     * 获取数据库列表
     */
    fun getDbList(): List<DbModel>? {
        return mDbList
    }
}