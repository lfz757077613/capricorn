package cn.laifuzhi.capricorn.server.conf;

import cn.laifuzhi.capricorn.common.util.StringAssist;
import cn.laifuzhi.capricorn.server.model.ClientInfo;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: fuzhi.lai
 * Date: 2019/2/15 下午2:22
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
@Component
public class ClientConfig{
    private static final String fileName = "client-config.properties";

    private Map<String, ClientInfo> clientInfoMap = Collections.emptyMap();

    public ClientConfig() {
        try {
            URL fileUrl = getClass().getResource(File.separator + fileName);
            Map<String, ClientInfo> newConfigMap = Resources.readLines(fileUrl, Charsets.UTF_8, new LineProcessor<Map<String, ClientInfo>>() {
                Map<String, ClientInfo> result = Maps.newHashMap();
                @Override
                public boolean processLine(String line) throws IOException {
                    List<String> list = StringAssist.splitComma(line);
                    if (list.size() != 4) {
                        log.error("process config line error, line:{}", line);
                        return true;
                    }
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.setTypeName(list.get(0));
                    clientInfo.setHostName(list.get(1));
                    clientInfo.setSshAddress(list.get(2));
                    clientInfo.setInnerIp(list.get(3));
                    result.put(clientInfo.getInnerIp(), clientInfo);
                    return true;
                }

                @Override
                public Map<String, ClientInfo> getResult() {
                    return result;
                }
            });
            clientInfoMap = Collections.unmodifiableMap(newConfigMap);
        } catch (Exception e) {
            log.error("create ClientConfig error", e);
        }
    }

    public @Nullable ClientInfo getInfoByInnerIp(String innerIp) {
        return clientInfoMap.get(innerIp);
    }

    public @Nonnull Set<String> getAllInnerIpSet() {
        return clientInfoMap.keySet();
    }
}
