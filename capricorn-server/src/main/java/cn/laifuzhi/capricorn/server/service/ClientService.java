package cn.laifuzhi.capricorn.server.service;

import cn.laifuzhi.capricorn.server.conf.ClientConfig;
import cn.laifuzhi.capricorn.server.model.ClientInfo;
import cn.laifuzhi.capricorn.server.model.ClientMonitor;
import cn.laifuzhi.capricorn.server.model.ClientStatus;
import cn.laifuzhi.capricorn.server.model.DeadClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: fuzhi.lai
 * Date: 2019/2/15 下午2:06
 * Create by Intellij idea
 */
/*
                       _oo0oo_
                      o8888888o
                      88" . "88
                      (| -_- |)
                      0\  =  /0
                    ___/`---'\___
                  .' \\|     |// '.
                 / \\|||  :  |||// \
                / _||||| -:- |||||- \
               |   | \\\  -  /// |   |
               | \_|  ''\---/''  |_/ |
               \  .-\__  '-'  ___/-. /
             ___'. .'  /--.--\  `. .'___
          ."" '<  `.___\_<|>_/___.' >' "".
         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
         \  \ `_.   \_ __\ /__ _/   .-` /  /
     =====`-.____`.___ \_____/___.-`___.-'=====
                       `=---='
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

               佛祖保佑         永无BUG
*/
@Slf4j
@Service
public class ClientService {
    private Map<String, ClientMonitor> ip2MonitorMap = Maps.newConcurrentMap();
    @Value("${capricorn.dead.seconds}")
    private long deadSecond;

    @Resource
    private ClientConfig clientConfig;

    public boolean recordAliveChannel(@Nonnull String innerIp, @Nonnull Channel channel) {
        ClientInfo clientInfo = clientConfig.getInfoByInnerIp(innerIp);
        if (clientInfo == null) {
            log.error("recordAliveChannel INNER_IP:[{}] not support", innerIp);
            return false;
        }
        ClientMonitor clientMonitor = ip2MonitorMap.get(innerIp);
        if (clientMonitor == null) {
            clientMonitor = new ClientMonitor();
            clientMonitor.setInfo(clientInfo);
            ip2MonitorMap.put(innerIp, clientMonitor);
        }
        clientMonitor.setChannel(channel);// 重置channel
        clientMonitor.setLastPingTs(System.currentTimeMillis());// 记录收到ping的时间
        return true;
    }

    public @Nonnull List<DeadClient> getDeadClientList() {
        Set<String> innerIpSet = clientConfig.getAllInnerIpSet();
        List<DeadClient> result = Lists.newArrayList();
        for (String innerIp : innerIpSet) {
            ClientMonitor clientMonitor = ip2MonitorMap.get(innerIp);
            //从没收到过ping
            if (clientMonitor == null) {
                DeadClient deadClient = new DeadClient();
                deadClient.setClientInfo(clientConfig.getInfoByInnerIp(innerIp));
                deadClient.setClientStatus(ClientStatus.NO_DETECTED.getDesc());
                result.add(deadClient);
                continue;
            }
            long noPingSecond = (System.currentTimeMillis() - clientMonitor.getLastPingTs()) / 1000;
            if (noPingSecond >= deadSecond) {
                DeadClient deadClient = new DeadClient();
                deadClient.setClientInfo(clientMonitor.getInfo());
                deadClient.setClientStatus(ClientStatus.DEAD.getDesc());
                deadClient.setDeadMinutes(noPingSecond / 60);
                result.add(deadClient);
            }
        }
        return result;
    }
}
