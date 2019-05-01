package com.blogging.ams.web.api;

import com.blogging.ams.business.AuthBusiness;
import com.blogging.ams.model.dto.AuthReqDTO;
import com.blogging.ams.model.dto.RegisterReqDTO;
import com.blogging.ams.model.dto.SessionDTO;
import com.blogging.ams.model.dto.UserInfoModifyDTO;
import com.blogging.ams.model.entity.Response;
import com.blogging.ams.model.entity.UserInfoEntity;
import com.blogging.ams.model.enums.ErrorCodeEnum;
import com.blogging.ams.support.annotation.Json;
import com.blogging.ams.support.annotation.ServiceInfo;
import com.blogging.ams.support.exception.UnifiedException;
import com.blogging.ams.support.utils.JsonUtil;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author techoneduan
 * @date 2019/4/26
 */

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthBusiness authBusiness;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ServiceInfo(name = "Blogging.AMS.AuthController.login", description = "登录")
    public Response loginAuth(@Json AuthReqDTO dto) {
        LOG.info("用户登录入参:{}", JsonUtil.toString(dto));
        Response response = authBusiness.loginAuth(dto);
        LOG.info("用户登录出参:{}", JsonUtil.toString(response));
        return response;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ServiceInfo(name = "Blogging.AMS.AuthController.register", description = "注册")
    @RequiresRoles("root")
    public Response register(@Json RegisterReqDTO dto) {
        LOG.info("注册用户入参:{}", JsonUtil.toString(dto));
        Response response = authBusiness.register(dto);
        LOG.info("注册用户出参:{}", JsonUtil.toString(response));
        return response;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ServiceInfo(name = "Blogging.AMS.AuthController.logout", description = "登出")
    public Response logout(@Json AuthReqDTO reqDTO) {
        LOG.info("用户登出入参!");
        Response response = authBusiness.logout(reqDTO);
        LOG.info("用户登出出参:{}", JsonUtil.toString(response));
        return response;
    }

    @RequestMapping(value = "/unauth", method = RequestMethod.POST)
    public Response unauth() {
        throw new UnifiedException(ErrorCodeEnum.NEED_RELOGIN);
    }

    @RequestMapping(value = "/stateJudge", method = RequestMethod.POST)
    @ServiceInfo(name = "Blogging.AMS.AuthController.stateJudge", description = "状态、权限认证")
    public Response stateJudge(@Json SessionDTO dto) {
        LOG.info("权限认证入参:{}", JsonUtil.toString(dto));
        Response response = authBusiness.authCertify(dto);
        LOG.info("权限认证出参:{}", JsonUtil.toString(response));
        return response;
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    @ServiceInfo(name = "Blogging.AMS.AuthController.delUser", description = "删除用户")
    public Response delUser(@Json AuthReqDTO dto) {
        LOG.info("删除用户入参:{}", JsonUtil.toString(dto));
        Response response = authBusiness.deleteUser(dto);
        LOG.info("删除用户出参:{}", JsonUtil.toString(response));
        return response;
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @ServiceInfo(name = "Blogging.AMS.AuthController.modify", description = "修改信息")
    public Response modify(@Json UserInfoModifyDTO dto) {
        LOG.info("用户信息修改入参:{}", JsonUtil.toString(dto));
        Response response = authBusiness.modifyUserInfo(dto);
        LOG.info("用户信息修改出参:{}", JsonUtil.toString(response));
        return response;
    }
}
