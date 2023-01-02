package com.caseOne.service;

import org.springframework.transaction.annotation.Transactional;

public interface UrlService {
    boolean isRoot(String url,String password);
}
