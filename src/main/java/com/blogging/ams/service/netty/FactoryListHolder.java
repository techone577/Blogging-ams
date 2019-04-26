package com.blogging.ams.service.netty;

import com.blogging.ams.service.netty.AbstractNettyService;
import com.blogging.ams.support.strategy.FactoryList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author techoneduan
 * @date 2018/12/17
 */

@Component
public class FactoryListHolder {
    @Autowired
    private FactoryList<AbstractNettyService, String> nettyService;

    public FactoryList<AbstractNettyService, String> getNettyService () {
        return nettyService;
    }
}
