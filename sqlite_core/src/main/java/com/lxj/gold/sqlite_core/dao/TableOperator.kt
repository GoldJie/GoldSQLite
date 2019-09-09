package com.lxj.gold.sqlite_core.dao

import android.os.Parcel
import android.os.Parcelable
import com.lxj.gold.sqlite_core.dao.crud.operation.ABaseDbOperation

/**
 * Created by lixinjie on 2019/8/1
 * 数据表DAO操作类
 * 用于限制对单表的并发操作
 * @param tableName    数据表名
 */
class TableOperator(internal var tableName: String?): Parcelable{
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tableName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TableOperator> {
        override fun createFromParcel(parcel: Parcel): TableOperator {
            return TableOperator(parcel)
        }

        override fun newArray(size: Int): Array<TableOperator?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * 线程池执行数据操作
     * @param baseDbOperation   数据库操作对象
     */
    fun execute(baseDbOperation: ABaseDbOperation) = DaoExecutor.execute(baseDbOperation)
}