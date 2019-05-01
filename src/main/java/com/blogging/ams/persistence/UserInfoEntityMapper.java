package com.blogging.ams.persistence;

import com.blogging.ams.model.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserInfoEntityMapper {
    int insert(UserInfoEntity record);

    int insertSelective(UserInfoEntity record);

    UserInfoEntity selectByMemberId(@Param("memberId") String memberId);

    UserInfoEntity selectByName(@Param("name") String name);

    List<UserInfoEntity> selectUsers();

    void delByName(@Param("name") String name);

    void updateByMemberId(UserInfoEntity entity);

    void updateActiveTimeByMemberId(@Param("memberId") String memberId, @Param("activeTime") Date activeTime);
}