package com.blogging.ams.web.api;

import com.blogging.ams.business.BMBusiness;
import com.blogging.ams.model.dto.AuthReqDTO;
import com.blogging.ams.model.entity.Response;
import com.blogging.ams.support.annotation.Json;
import com.blogging.ams.support.annotation.ServiceInfo;
import com.blogging.ams.support.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author techoneduan
 * @date 2019/5/1
 */

@RestController
@RequestMapping("/BM")
public class BMController {

    private static final Logger LOG = LoggerFactory.getLogger(BMController.class);

    @Autowired
    private BMBusiness bmBusiness;

    @RequestMapping(value = "/queryUsers", method = RequestMethod.POST)
    @ServiceInfo(name = "Blogging.AMS.BMController.queryUsers",description = "查询所有用户")
    public Response queryUsers() {
        LOG.info("（BM）查询用户!");
        Response response = bmBusiness.queryUsers();
        LOG.info("（BM）查询用户出参:{}", JsonUtil.toString(response));
        return response;
    }

    @RequestMapping(value = "/queryUserByName", method = RequestMethod.POST)
    @ServiceInfo(name = "Blogging.AMS.BMController.queryUserByName",description = "查询单个用户")
    public Response queryUsers(@Json AuthReqDTO reqDTO) {
        LOG.info("（BM）查询单个用户入参:{}",JsonUtil.toString(reqDTO));
        Response response = bmBusiness.queryUserByName(reqDTO);
        LOG.info("（BM）查询单个用户出参:{}", JsonUtil.toString(response));
        return response;
    }
}
