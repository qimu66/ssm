package com.caseOne.service.impl;

import com.caseOne.dao.UrlDAO;
import com.caseOne.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlServiceImpl implements UrlService {
    @Autowired
    private UrlDAO urlDAO;

    @Override
    public boolean isRoot(String url, String password) {
        System.out.println(password.length());
        return urlDAO.isPassword(url, password);
    }
}
