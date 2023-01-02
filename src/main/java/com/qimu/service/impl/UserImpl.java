package com.qimu.service.impl;

import com.qimu.dao.impl.DaoImpl;
import com.qimu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserImpl implements UserService {
    @Autowired
    private DaoImpl dao;

    @Override
    public void save() {
        System.out.println("service>>>");
        dao.save();
    }
}
