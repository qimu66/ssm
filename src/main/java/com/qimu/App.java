package com.qimu;

import com.qimu.config.JdbcConfig;
import com.qimu.config.UserConfig;
import com.qimu.dao.Dao;
import com.qimu.pojo.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;

public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(UserConfig.class);
//        DaoImpl bean = ctx.getBean(DaoImpl.class);
//        bean.save();
        JdbcConfig bean1 = ctx.getBean(JdbcConfig.class);
        DataSource dataSource = bean1.dataSource();
        System.out.println(dataSource);
        Dao bean = ctx.getBean(Dao.class);
        User user = bean.findUser(3);
        System.out.println(user);
    }
}
