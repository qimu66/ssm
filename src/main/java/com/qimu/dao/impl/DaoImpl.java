package com.qimu.dao.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class DaoImpl {
    @Value("${jdbc.password}")
    private String name;

    public void save() {
        System.out.println("dao>>>>>>>>>>>");
        System.out.println(name + "name");
    }

}
