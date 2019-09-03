package com.lxj.gold.sqlite_core.dao.crud.operation

import com.lxj.gold.sqlite_core.dao.TableOperator

/**
 * Created by lixinjie on 2019/8/26
 * 数据库基本操作对象抽象类,继承于Callable
 * @param proTableOperator 指定数据表的操作者
 */
abstract class ABaseDbOperation (val proTableOperator: TableOperator){
    val TAG = ABaseDbOperation::class.simpleName

    /**
     * 开始在线程池中执行的DAO操作
     */
    abstract fun doExecute()

    /**
     * DAO操作结束回调接口
     */
    interface OnDaoFinishedCallback<T: Any>{
        fun onResultReturn(result: T)
    }
}