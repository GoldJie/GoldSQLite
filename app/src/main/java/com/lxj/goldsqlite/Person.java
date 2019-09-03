package com.lxj.goldsqlite;

import com.lxj.gold.sqlite_core.annotation.Column;
import com.lxj.gold.sqlite_core.annotation.MainKey;

/**
 * Created by lixinjie on 2019/7/16
 */
public class Person {
    @MainKey
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "sex")
    String sex;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
