package com.caseOne.dao.impl;

import com.caseOne.dao.UrlDAO;
import org.springframework.stereotype.Repository;

@Repository
public class UrlDaoImpl implements UrlDAO {
    @Override
    public boolean isPassword(String url, String password) {
        return password.equals("root");
    }
}
