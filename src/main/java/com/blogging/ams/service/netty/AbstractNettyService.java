package com.blogging.ams.service.netty;


import com.blogging.ams.model.entity.NettyRespEntity;
import com.blogging.ams.support.strategy.MatchingBean;


/**
 * @author techoneduan
 * @date 2018/12/17
 */
public abstract class AbstractNettyService implements MatchingBean<String> {

    public abstract void dealRequest (NettyRespEntity resp);

}
