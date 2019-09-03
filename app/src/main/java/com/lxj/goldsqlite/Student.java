package com.lxj.goldsqlite;

import com.lxj.gold.sqlite_core.annotation.Column;
import com.lxj.gold.sqlite_core.annotation.TableName;

/**
 * Created by lixinjie on 2019/7/10
 */
@TableName(tableName = "Student")
public class Student extends Person{
    @Column(name = "classz", length = 10)
    String classz;
    @Column(name = "grade")
    int grade;
    @Column(name = "family")
    String family;

    public String getClassz() {
        return classz;
    }

    public void setClassz(String classz) {
        this.classz = classz;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }
}
