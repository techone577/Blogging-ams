package com.blogging.ams.service.netty;


import com.blogging.ams.model.constant.NettyHeader;
import com.blogging.ams.model.constant.RedisConstants;
import com.blogging.ams.model.entity.NettyRespEntity;
import com.blogging.ams.model.syncMap.SyncMap;
import com.blogging.ams.support.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author techoneduan
 * @date 2018/12/21
 */
@Service
public class PullNettyService extends AbstractNettyService {

    private static final Logger LOG = LoggerFactory.getLogger(PullNettyService.class);

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void dealRequest (NettyRespEntity resp) {
        SyncMap.put(resp.getRequestId(), resp.getResponse());
        redisUtil.doCache(RedisConstants.INSTANCE_CACHE, resp.getResponse());
    }

    @Override
    public boolean matching (String factor) {
        return NettyHeader.PULL.equals(factor);
    }
}
