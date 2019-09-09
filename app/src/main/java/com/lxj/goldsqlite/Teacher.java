package com.lxj.goldsqlite;

import com.lxj.gold.sqlite_core.annotation.Column;
import com.lxj.gold.sqlite_core.annotation.TableName;

import static com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.COLUMN_TYPE_STRING;

/**
 * Created by lixinjie on 2019/7/11
 */
@TableName(tableName = "Teacher")
public class Teacher extends Person{
    @Column(name = "course", type = COLUMN_TYPE_STRING, length = 6)
    String course;

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
