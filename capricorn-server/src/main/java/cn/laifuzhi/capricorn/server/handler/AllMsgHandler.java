package cn.laifuzhi.capricorn.server.handler;

import cn.laifuzhi.capricorn.common.msg.MsgI;
import cn.laifuzhi.capricorn.server.conf.MsgHandlerConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Author: fuzhi.lai
 * Date: 2018/11/17 下午2:08
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
@Component("AllMsgHandler")
@ChannelHandler.Sharable
public class AllMsgHandler extends SimpleChannelInboundHandler<MsgI> {
    @Resource
    private MsgHandlerConfig handlerConfig;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgI msg) throws Exception {
        SimpleChannelInboundHandler<? extends MsgI> handler = handlerConfig.getHandlerByMsgEnum(msg.getMsgEnum());
        if (handler == null) {
            log.error("server not support msg:{}", msg.getMsgEnum());
            return;
        }
        handler.channelRead(ctx, msg);
    }
}
