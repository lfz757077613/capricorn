package cn.laifuzhi.capricorn.server.conf;

import cn.laifuzhi.capricorn.common.msg.MsgEnum;
import cn.laifuzhi.capricorn.common.msg.MsgI;
import cn.laifuzhi.capricorn.server.handler.PingMsgHandler;
import com.google.common.collect.Maps;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.Map;

/**
 * Author: fuzhi.lai
 * Date: 2019/2/14 下午6:46
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
@Component
public class MsgHandlerConfig {
    @Resource
    private Map<String, SimpleChannelInboundHandler<? extends MsgI>> handlerMap;

    private Map<MsgEnum, String> Msg2HandlerNameMap = Maps.newHashMap();

    public MsgHandlerConfig() {
        Msg2HandlerNameMap.put(MsgEnum.Ping, PingMsgHandler.class.getSimpleName());
    }

    public @Nullable SimpleChannelInboundHandler<? extends MsgI> getHandlerByMsgEnum(@Nonnull MsgEnum msgEnum) {
        String handlerSimpleName = Msg2HandlerNameMap.get(msgEnum);
        if (StringUtils.isBlank(handlerSimpleName)) {
            return null;
        }
        return handlerMap.get(handlerSimpleName);
    }
}
