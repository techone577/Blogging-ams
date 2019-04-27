package com.blogging.ams.config.shiro;

import com.blogging.ams.model.entity.UserInfoEntity;
import com.blogging.ams.model.enums.ErrorCodeEnum;
import com.blogging.ams.service.UserService;
import com.blogging.ams.support.exception.UnifiedException;
import com.blogging.ams.support.utils.JsonUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;


public class AMSShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserService userInfoService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals){
        String username = (String) super.getAvailablePrincipal(principals);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserInfoEntity entity = userInfoService.queryByName(username);
        if(null == entity)
            throw new UnifiedException(ErrorCodeEnum.USER_NOT_EXIST_ERROR);
        Set<String> roles = new HashSet<String>() {
            {
                add(entity.getPermission());
            }
        };
        authorizationInfo.setRoles(roles);
        roles.forEach(role -> {
            Set<String> permissions = new HashSet<String>(){
                {
                    add(entity.getPermission());
                }
            };
            authorizationInfo.addStringPermissions(permissions);
        });
        return authorizationInfo;
    }

    /**
     * 身份认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        String username = (String) token.getPrincipal();
        //可以根据实际情况做缓存，Shiro时间间隔机制，2分钟内不会重复执行该方法
        UserInfoEntity userInfo = userInfoService.queryByName(username);
        if (null == userInfo)
            throw new UnifiedException(ErrorCodeEnum.USER_NOT_EXIST_ERROR);
        String credentials = userInfo.getPassword();
        String salt = userInfo.getSalt();
        ByteSource credentialsSalt = ByteSource.Util.bytes(salt);
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                username, //用户名
                credentials, //密码
                credentialsSalt,
                getName()  //realm name
        );
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("USER_SESSION", JsonUtil.toString(userInfo));
        return authenticationInfo;
    }

}