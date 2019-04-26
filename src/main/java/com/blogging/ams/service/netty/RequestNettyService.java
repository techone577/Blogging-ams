package com.blogging.ams.service.netty;



import com.blogging.ams.model.constant.NettyHeader;
import com.blogging.ams.model.entity.NettyRespEntity;
import com.blogging.ams.model.syncMap.SyncMap;
import com.blogging.ams.service.netty.AbstractNettyService;
import org.springframework.stereotype.Service;

/**
 * @author techoneduan
 * @date 2018/12/17
 */
@Service
public class RequestNettyService extends AbstractNettyService {

    @Override
    public void dealRequest (NettyRespEntity respEntity) {
        if (!SyncMap.hasKey(respEntity.getRequestId())) {
            SyncMap.put(respEntity.getRequestId(), respEntity.getResponse());
        }
    }

    @Override
    public boolean matching (String factor) {
        return NettyHeader.REQUEST.equals(factor);
    }
}
