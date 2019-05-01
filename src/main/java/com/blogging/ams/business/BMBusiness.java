package com.blogging.ams.business;

import com.blogging.ams.model.dto.AuthReqDTO;
import com.blogging.ams.model.dto.BMUserQueryRespDTO;
import com.blogging.ams.model.entity.Response;
import com.blogging.ams.model.entity.UserInfoEntity;
import com.blogging.ams.model.enums.ErrorCodeEnum;
import com.blogging.ams.service.UserService;
import com.blogging.ams.support.exception.UnifiedException;
import com.blogging.ams.support.utils.DateUtils;
import com.blogging.ams.support.utils.ResponseBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author techoneduan
 * @date 2019/5/1
 */
@Component
public class BMBusiness {

    private static final Logger LOG = LoggerFactory.getLogger(BMBusiness.class);

    @Autowired
    private UserService userService;

    public Response queryUsers() {
        List<UserInfoEntity> entities = userService.queryUsers();
        if (null == entities || entities.size() == 0)
            return ResponseBuilder.build(true, null);
        List<BMUserQueryRespDTO> respDTOS = buildUserQueryRespDTO(entities);
        return ResponseBuilder.build(true, respDTOS);
    }

    private List<BMUserQueryRespDTO> buildUserQueryRespDTO(List<UserInfoEntity> entities) {
        List<BMUserQueryRespDTO> dtos = new ArrayList<>();
        entities.stream().forEach(i -> {
            BMUserQueryRespDTO respDTO = new BMUserQueryRespDTO() {
                {
                    setUserId(i.getMemberId());
                    setUserName(i.getNickName());
                    setActiveTime(DateUtils.formatDateTime(i.getActiveTime()));
                    setAddTime(DateUtils.formatDateTime(i.getAddTime()));
                    setPermission(i.getPermission());
                    setUpdateTime(DateUtils.formatDateTime(i.getUpdateTime()));
                }
            };
            dtos.add(respDTO);
        });
        return dtos;
    }

    public Response queryUserByName(AuthReqDTO reqDTO) {
        String name = "";
        if (StringUtils.isBlank(reqDTO.getName())) {
            Subject subject = SecurityUtils.getSubject();
            name = (String) subject.getPrincipal();
        } else {
            name = reqDTO.getName();
        }
        if (StringUtils.isBlank(name))
            throw new UnifiedException(ErrorCodeEnum.NEED_RELOGIN);
        UserInfoEntity entity = userService.queryByName(name);
        BMUserQueryRespDTO respDTO = new BMUserQueryRespDTO() {
            {
                setUserId(entity.getMemberId());
                setUserName(entity.getNickName());
                setAddTime(DateUtils.formatDateTime(entity.getAddTime()));
                setUpdateTime(DateUtils.formatDateTime(entity.getUpdateTime()));
                setActiveTime(DateUtils.formatDateTime(entity.getActiveTime()));
                setPermission(entity.getPermission());
            }
        };
        return ResponseBuilder.build(true, respDTO);
    }
}
