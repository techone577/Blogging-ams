package com.blogging.ams.business;

import com.blogging.ams.model.dto.AuthReqDTO;
import com.blogging.ams.model.entity.Response;
import com.blogging.ams.model.entity.UserInfoEntity;
import com.blogging.ams.model.enums.ErrorCodeEnum;
import com.blogging.ams.service.UserService;
import com.blogging.ams.support.exception.UnifiedException;
import com.blogging.ams.support.utils.IdGenerator;
import com.blogging.ams.support.utils.ResponseBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

/**
 * @author techoneduan
 * @date 2019/4/26
 */

@Component
public class AuthBusiness {

    private static final Logger LOG = LoggerFactory.getLogger(AuthBusiness.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SessionDAO sessionDAO;

    public Response loginAuth(AuthReqDTO reqDTO) {
        if (null == reqDTO || StringUtils.isBlank(reqDTO.getName())
                || StringUtils.isBlank(reqDTO.getPassword()))
            throw new UnifiedException(ErrorCodeEnum.PARAM_ILLEGAL_ERROR);
        UsernamePasswordToken token = new UsernamePasswordToken(reqDTO.getName(), reqDTO.getPassword());
        Subject subject = SecurityUtils.getSubject();
        mutualLogin(reqDTO.getName());
        try {
            subject.login(token);
//          put("token", subject.getSession().getId());

        } catch (IncorrectCredentialsException e) {
            throw new UnifiedException(ErrorCodeEnum.WRONG_PASSWORD_ERROR);
        } catch (LockedAccountException e) {
            throw new UnifiedException(ErrorCodeEnum.USER_LOCKED_ERROR);
        } catch (AuthenticationException e) {
            throw new UnifiedException(ErrorCodeEnum.USER_NOT_EXIST_ERROR);
        } catch (UnifiedException e) {
            LOG.info("ue:{}", e);
        }
        return ResponseBuilder.build(true, "登录成功");

    }

    /**
     * 互斥登录
     * @param name
     */
    public void mutualLogin(String name){
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        if(null != sessions) {
            sessions.stream().forEach(i->{
                Subject s = new Subject.Builder().session(i).buildSubject();
                if(name.equals(s.getPrincipal()) && s.isAuthenticated()){
                    s.logout();
                    sessionDAO.delete(i);
                }
            });
        }
    }

    /**
     * 添加新管理员 只有root用户可以添加
     *
     * @param reqDTO
     * @return
     */
    public Response register(AuthReqDTO reqDTO) {
        if (null == reqDTO || StringUtils.isBlank(reqDTO.getName())
                || StringUtils.isBlank(reqDTO.getPassword()))
            throw new UnifiedException(ErrorCodeEnum.PARAM_ILLEGAL_ERROR);
        UserInfoEntity entity = userService.queryByName(reqDTO.getName());
        if (null != entity)
            throw new UnifiedException(ErrorCodeEnum.USER_ALREADY_EXIST_ERROR);
        entity = new UserInfoEntity() {
            {
                setMemberId(IdGenerator.generateMemberId());
                setNickName(reqDTO.getName());
                setDelFlag(0);
                setAddTime(new Date());
                setSalt(IdGenerator.geerateSalt());
            }
        };
        String password = new SimpleHash("MD5", reqDTO.getPassword(), entity.getSalt(), 1024).toHex();
        entity.setPassword(password);
        userService.insertUser(entity);
        return ResponseBuilder.build(true, "添加成功");
    }

    /**
     * 登出
     *
     * @return
     */
    public Response logout() {
        Subject currentUser = SecurityUtils.getSubject();
        LOG.info("用户登出:{}", (String) currentUser.getPrincipal());
        if (currentUser.isAuthenticated()) {
            currentUser.logout();
        }
        return ResponseBuilder.build(true, "登出成功");
    }

}
