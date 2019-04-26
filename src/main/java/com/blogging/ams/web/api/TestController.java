package com.blogging.ams.web.api;

import com.blogging.ams.model.entity.Response;
import com.blogging.ams.support.annotation.ServiceInfo;
import com.blogging.ams.support.bsp.ServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author techoneduan
 * @date 2018/12/13
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ServiceClient caller;

    @RequestMapping(value = "/test")
    @ServiceInfo(name = "Blogging.AMS.TestController.test", description = "ams测试方法注册")
    public Response test (Integer s) {
        return null;
    }

}
