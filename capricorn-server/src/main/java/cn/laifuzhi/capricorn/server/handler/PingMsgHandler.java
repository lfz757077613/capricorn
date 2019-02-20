package cn.laifuzhi.capricorn.server.handler;

import cn.laifuzhi.capricorn.common.msg.PingMsg;
import cn.laifuzhi.capricorn.common.msg.PongMsg;
import cn.laifuzhi.capricorn.server.service.ClientService;
import cn.laifuzhi.capricorn.server.util.AttrKeyUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Author: fuzhi.lai
 * Date: 2018/11/17 下午2:35
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
@Component("PingMsgHandler")
@ChannelHandler.Sharable
public class PingMsgHandler extends SimpleChannelInboundHandler<PingMsg> {
    private static final PongMsg pongMsg = new PongMsg();

    @Resource
    private ClientService monitorService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingMsg msg) throws Exception {
        ctx.channel().attr(AttrKeyUtil.INNER_IP).set(msg.getInnerIp());
        if (monitorService.recordAliveChannel(msg.getInnerIp(), ctx.channel())) {
            ctx.writeAndFlush(pongMsg);
        }
    }
}
