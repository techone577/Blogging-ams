package com.blogging.ams.persistence;

import com.blogging.ams.model.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Param;

public interface UserInfoEntityMapper {
    int insert(UserInfoEntity record);

    int insertSelective(UserInfoEntity record);

    UserInfoEntity selectByMemberId(@Param("memberId") String memberId);

    UserInfoEntity selectByName(@Param("name") String name);
}