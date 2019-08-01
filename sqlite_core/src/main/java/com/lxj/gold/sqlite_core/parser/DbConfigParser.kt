package com.lxj.gold.sqlite_core.parser

import com.lxj.gold.sqlite_core.parser.model.XmlConfigModel
import java.io.InputStream
import javax.xml.parsers.SAXParserFactory

/**
 * Created by lixinjie on 2019/8/1
 * XML配置数据表解析器
 * 解析XML文件中的数据表配置信息，关联数据表与实体类
 */
object DbConfigParser {
    fun parseXmlConfigFile(){

    }

    private fun parseConfigInputStreamToEntity(inputStream: InputStream){
//        创建SAX解析工厂
        var factory = SAXParserFactory.newInstance()
//        创建SAX解析器
        var parser = factory.newSAXParser()
//        获取XML阅读器
        var xmlReader = parser.xmlReader
        var xmlHandler = XmlConfigHandler()
        xmlReader.contentHandler = xmlHandler
    }
}