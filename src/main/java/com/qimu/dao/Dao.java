package com.qimu.dao;

import com.qimu.pojo.User;
import org.apache.ibatis.annotations.Select;

public interface Dao {
    @Select("select * from smbms_user where id=#{id} ")
    User findUser(Integer id);
}
