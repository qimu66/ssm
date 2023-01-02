package com.caseOne;
import com.caseOne.config.UrlConfig;
import com.caseOne.service.UrlService;
import com.qimu.config.UserConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestUrl {
    @Autowired
    private UrlService urlService;

    @Test
    public void testUrl() {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(UrlConfig.class);
        UrlService bean = ctx.getBean(UrlService.class);
        boolean root1 = bean.isRoot("https:www.qimu.com", "root ");
        System.out.println(root1);
//        boolean root = urlService.isRoot("https:www.qimu.com", "root");
//        System.out.println(root);
    }
}
