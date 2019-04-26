package com.blogging.ams.service;

import com.blogging.ams.model.entity.UserInfoEntity;
import com.blogging.ams.persistence.UserInfoEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author techoneduan
 * @date 2019/4/26
 */
@Service
public class UserService {

    @Autowired
    private UserInfoEntityMapper userInfoEntityMapper;

    public UserInfoEntity queryByMemberId(String memberId) {
        return userInfoEntityMapper.selectByMemberId(memberId);
    }

    public UserInfoEntity queryByName(String name) {
        return userInfoEntityMapper.selectByName(name);
    }

    public void insertUser(UserInfoEntity entity){
        userInfoEntityMapper.insertSelective(entity);
    }
}
