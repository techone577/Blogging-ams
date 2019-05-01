package com.blogging.ams.service;

import com.blogging.ams.model.entity.UserInfoEntity;
import com.blogging.ams.persistence.UserInfoEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    public void insertUser(UserInfoEntity entity) {
        userInfoEntityMapper.insertSelective(entity);
    }

    /**
     * 查询所有用户
     */
    public List<UserInfoEntity> queryUsers() {
        return userInfoEntityMapper.selectUsers();
    }

    /**
     * 删除用户
     */
    public void delUser(String name) {
        userInfoEntityMapper.delByName(name);
    }

    /**
     * 更新用户信息
     */
    public void updateByMemberId(UserInfoEntity entity){
        userInfoEntityMapper.updateByMemberId(entity);
    }

    /**
     * 更新用户登录时间
     */
    public void updateActiveTimeByMemberId(String memberId,Date activeTime){
        userInfoEntityMapper.updateActiveTimeByMemberId(memberId,activeTime);
    }
}
