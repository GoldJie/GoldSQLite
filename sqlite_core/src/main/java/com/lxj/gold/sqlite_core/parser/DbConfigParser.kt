package com.lxj.gold.sqlite_core.parser

import android.content.Context
import android.content.res.AssetManager
import com.lxj.gold.sqlite_core.GoldSQLite
import com.lxj.gold.sqlite_core.exception.ParseXmlConfigException
import com.lxj.gold.sqlite_core.exception.XmlConfigFileNotFoundException
import com.lxj.gold.sqlite_core.parser.model.XmlConfigModel
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.CONFIG_XML_FILE_NAME
import org.xml.sax.InputSource
import java.io.IOException
import java.io.InputStream
import javax.xml.parsers.SAXParserFactory

/**
 * Created by lixinjie on 2019/8/1
 * XML配置数据表解析器
 * 解析XML文件中的数据表配置信息，关联数据表与实体类
 */
object DbConfigParser {

    /**
     * 解析数据库的Xml配置文件
     * @param  context      上下文对象
     * @return              Xml配置模型对象
     */
    fun parseXmlConfigFile(context: Context): XmlConfigModel?{
        try {
            return parseConfigInputStreamToEntity(parseConfigXmlToInputStream(context))
        } catch (e: Exception) {
            throw ParseXmlConfigException(e.message.toString())
        }
    }

    /**
     * 解析XML配置文件，转换成输入流
     * @param  context      上下文对象
     * @return              XML文件的输入流
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun parseConfigXmlToInputStream(context: Context): InputStream{
//        获取资源管理器，读取资源中的配置文件
        val assetManager: AssetManager = context.assets
        // todo 后续可能考虑将此处资源文件的Path作为参数暴露出去
        val fileNames: Array<String> = assetManager.list("") as Array<String>
        for(name: String in fileNames){
            if(CONFIG_XML_FILE_NAME == name) return assetManager.open(name, AssetManager.ACCESS_BUFFER)
        }
        throw XmlConfigFileNotFoundException()
    }

    /**
     * 解析XML配置文件，转变成配置Model实体对象
     * @param inputStream   Xml文件流
     * @return              Xml配置Model
     */
    private fun parseConfigInputStreamToEntity(inputStream: InputStream): XmlConfigModel?{
        var xmlConfigModel: XmlConfigModel? = null
        try {
//            创建SAX解析工厂
            val factory = SAXParserFactory.newInstance()
//            创建SAX解析器
            val parser = factory.newSAXParser()
//            获取XML阅读器
            val xmlReader = parser.xmlReader
//            创建XML配置解析器
            val xmlHandler = XmlConfigHandler()
            xmlReader.contentHandler = xmlHandler
//            解析XML配置文件
            xmlReader.parse(InputSource(inputStream))
//            获取解析的数据库模型对象
            val dbModelList = xmlHandler.getDbList()
//            构建Xml配置对象
            xmlConfigModel = dbModelList?.let { XmlConfigModel(it) }
        } catch (e: Exception) {
            throw ParseXmlConfigException(e.message.toString())
        }
        return xmlConfigModel
    }
}