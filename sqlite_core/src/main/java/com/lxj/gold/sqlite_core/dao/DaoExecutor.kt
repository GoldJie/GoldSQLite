package com.lxj.gold.sqlite_core.dao

import com.lxj.gold.sqlite_core.dao.crud.operation.ABaseDbOperation
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by lixinjie on 2019/8/26
 * 数据库Dao操作执行者
 */
object DaoExecutor {
    private var mDaoThreadPool: ExecutorService = ThreadPoolExecutor(0, 5, 10,
        TimeUnit.SECONDS,
        ArrayBlockingQueue<Runnable>(10),
        ThreadPoolExecutor.DiscardPolicy())

    /**
     * 执行DAO操作
     * @param operation   基本数据操作对象
     */
    fun execute(operation: ABaseDbOperation){
        mDaoThreadPool.execute {
            operation.doExecute()
        }
    }
}