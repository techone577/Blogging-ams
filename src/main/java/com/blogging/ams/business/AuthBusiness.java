package com.blogging.ams.business;

import com.blogging.ams.model.dto.AuthReqDTO;
import com.blogging.ams.model.dto.RegisterReqDTO;
import com.blogging.ams.model.dto.SessionDTO;
import com.blogging.ams.model.dto.UserInfoModifyDTO;
import com.blogging.ams.model.entity.Response;
import com.blogging.ams.model.entity.UserInfoEntity;
import com.blogging.ams.model.enums.AuthEnum;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        //构造token返回头
        addJsessionIdForCookieEnable(subject.getSession().getId().toString());
        //互斥登陆
        mutualLogin(reqDTO.getName(), subject.getSession());
        try {
            subject.login(token);

        } catch (IncorrectCredentialsException e) {
            throw new UnifiedException(ErrorCodeEnum.WRONG_PASSWORD_ERROR);
        } catch (LockedAccountException e) {
            throw new UnifiedException(ErrorCodeEnum.USER_LOCKED_ERROR);
        } catch (AuthenticationException e) {
            throw new UnifiedException(ErrorCodeEnum.USER_NOT_EXIST_ERROR);
        } catch (UnifiedException e) {
            LOG.info("ue:{}", e);
        }
        UserInfoEntity entity = userService.queryByName(reqDTO.getName());
        userService.updateActiveTimeByMemberId(entity.getMemberId(), new Date());
        return ResponseBuilder.build(true, "登录成功");

    }

    /**
     * 互斥登录
     *
     * @param name
     */
    public void mutualLogin(String name, Session session) {
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        String sessionId = session.getId().toString();
        if (null != sessions) {
            sessions.stream().forEach(i -> {
                Subject s = new Subject.Builder().session(i).buildSubject();
                //同一个浏览器 || session下的用户 不管是否是同一个账户，保持之前的登录状态
                if (s.isAuthenticated() && i.getId().toString().equals(sessionId)) {
                    //直接赋予登录状态 前端路由控制跳转 未登录不需删除当前创建的session
                    throw new UnifiedException(ErrorCodeEnum.ALREADY_LOGIN_ERROR);
                }
            });
            //注意验证顺序 先验证同一浏览器
            sessions.stream().forEach(i -> {
                Subject s = new Subject.Builder().session(i).buildSubject();
                //不是同一个session 同一个用户 踢出另一个
                if (name.equals(s.getPrincipal()) && s.isAuthenticated() && !i.getId().toString().equals(sessionId)) {
                    //使当前session保持登录状态
                    s.logout();
                    sessionDAO.delete(i);
                }
            });
        }
    }

    /**
     * 添加返回头避免cookie被禁用
     */
    private void addJsessionIdForCookieEnable(String jsessionId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();
        response.addHeader("ams-JSESSIONID", jsessionId);
    }

    /**
     * 添加新管理员 只有root用户可以添加
     *
     * @param reqDTO
     * @return
     */
    public Response register(RegisterReqDTO reqDTO) {
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
                setPermission(StringUtils.isBlank(reqDTO.getPermission()) ? AuthEnum.GUEST.getValue() : reqDTO.getPermission());
                setDelFlag(0);
                setAddTime(new Date());
                setActiveTime(new Date());
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
    public Response logout(AuthReqDTO reqDTO) {
        Subject currentUser = SecurityUtils.getSubject();
        String userName = (String) currentUser.getPrincipal();
        logoutFrontCheck(userName, currentUser.getSession());
        LOG.info("用户登出:{}", userName);
        if (currentUser.isAuthenticated()) {
            currentUser.logout();
        }
        return ResponseBuilder.build(true, "登出成功");
    }

    /**
     * 登出前置检查
     *
     * @param name
     * @param session
     */
    public void logoutFrontCheck(String name, Session session) {
        if (StringUtils.isBlank(name))
            throw new UnifiedException(ErrorCodeEnum.ALREADY_LOGOUT_ERROR);
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        List<Session> sessionList = sessions.stream().filter(i -> {
            Subject s = new Subject.Builder().session(i).buildSubject();
            if (null == s) {
                sessionDAO.delete(i);
                return false;
            }
            if (StringUtils.isBlank((String) s.getPrincipal())) {
                s.logout();
                sessionDAO.delete(i);
                return false;
            } else {
                return ((String) s.getPrincipal()).equals(name);
            }
        }).collect(Collectors.toList());
        if (sessionList.size() > 1) {
            LOG.info("存在多个相同登录账户，全部退出");
            sessionList.forEach(i -> {
                Subject s = new Subject.Builder().session(i).buildSubject();
                s.logout();
                sessionDAO.delete(i);
            });
            throw new UnifiedException(ErrorCodeEnum.MULTI_USER_EXISTERROR);
        }
        if (sessionList.size() == 0)
            throw new UnifiedException(ErrorCodeEnum.ALREADY_LOGOUT_ERROR);
        if (sessionList.size() == 1
                && !sessionList.get(0).getId().toString().equals(session.getId().toString()))
            throw new UnifiedException(ErrorCodeEnum.JSESSIONID_NOT_MATCH_ERROR);
    }

    /**
     * 验证
     *
     * @param dto
     * @return
     */
    public Response authCertify(SessionDTO dto) {
        /**
         * 登录验证
         */
        Subject user = SecurityUtils.getSubject();
        Session session = user.getSession();
        //useless
        if (StringUtils.isBlank(dto.getSessionId()) || !session.getId().toString().equals(dto.getSessionId()))
            throw new UnifiedException(ErrorCodeEnum.JSESSIONID_NOT_MATCH_ERROR);
        if (!user.isAuthenticated())
            throw new UnifiedException(ErrorCodeEnum.NEED_RELOGIN);
        /**
         * 权限验证
         */
        String permission = "";
        if (StringUtils.isNotBlank(dto.getAuthority())) {
            permission = dto.getAuthority();
        } else {
            permission = AuthEnum.GUEST.getValue();
        }
        if (user.isPermitted(AuthEnum.ROOT.getValue()))
            return ResponseBuilder.build(true, "验证成功");
        if (!user.isPermitted(permission)) {
            throw new UnifiedException(ErrorCodeEnum.PERMISSION_DENIED_ERROR);
        }
        return ResponseBuilder.build(true, "验证成功");
    }


    /**
     * 用户删除
     *
     * @param reqDTO
     * @return
     */
    public Response deleteUser(AuthReqDTO reqDTO) {
        if (null == reqDTO || StringUtils.isBlank(reqDTO.getName()))
            throw new UnifiedException(ErrorCodeEnum.PARAM_ILLEGAL_ERROR);
        Subject user = SecurityUtils.getSubject();
        if (!user.isAuthenticated())
            throw new UnifiedException(ErrorCodeEnum.NEED_RELOGIN);
        if(user.getPrincipal().equals(reqDTO.getName()))
            throw new UnifiedException(ErrorCodeEnum.PERMISSION_DENIED_ERROR,"不能注销自己");
        UserInfoEntity userInfoEntity = userService.queryByName(reqDTO.getName());
        if (null == userInfoEntity)
            throw new UnifiedException(ErrorCodeEnum.USER_NOT_EXIST_ERROR);
        if (AuthEnum.ROOT.getValue().equals(userInfoEntity.getPermission()))
            throw new UnifiedException(ErrorCodeEnum.PERMISSION_DENIED_ERROR, "root用户无法删除");
        userService.delUser(reqDTO.getName());
        return ResponseBuilder.build(true, "删除成功");
    }

    /**
     * 用户信息修改
     *
     * @param dto
     * @return
     */
    public Response modifyUserInfo(UserInfoModifyDTO dto) {
        if (null == dto || StringUtils.isBlank(dto.getMemberId()))
            throw new UnifiedException(ErrorCodeEnum.PARAM_ILLEGAL_ERROR);
        UserInfoEntity userInfoEntity = userService.queryByMemberId(dto.getMemberId());
        if (null == userInfoEntity)
            throw new UnifiedException(ErrorCodeEnum.USER_NOT_EXIST_ERROR);
        String password = "";
        if (StringUtils.isNotBlank(dto.getPassword())) {
            password = new SimpleHash("MD5", dto.getPassword(), userInfoEntity.getSalt(), 1024).toHex();
        }
        UserInfoEntity entity = new UserInfoEntity();
        entity.setPermission(dto.getPermission());
        entity.setNickName(dto.getName());
        entity.setPassword(password);
        entity.setMemberId(dto.getMemberId());
        updateFrontCheck(userInfoEntity, entity);
        userService.updateByMemberId(entity);
        return ResponseBuilder.build(true, "修改成功");
    }

    private void updateFrontCheck(UserInfoEntity old, UserInfoEntity fresh) {
        boolean permissionChange = StringUtils.isNotBlank(fresh.getPermission()) && fresh.getPermission().equals(old.getPermission());
        boolean passwordChange = StringUtils.isNotBlank(fresh.getPassword()) && fresh.getPassword().equals(old.getPassword());
        boolean nameChange = StringUtils.isNotBlank(fresh.getNickName()) && fresh.getNickName().equals(old.getNickName());

        if (permissionChange && passwordChange && nameChange)
            throw new UnifiedException(ErrorCodeEnum.USER_INFO_NOT_CHANGE_ERROR);
    }
}
