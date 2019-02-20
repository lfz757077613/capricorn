package cn.laifuzhi.capricorn.client.handler;

import cn.laifuzhi.capricorn.common.msg.PingMsg;
import cn.laifuzhi.capricorn.common.util.IpUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Author: fuzhi.lai
 * Date: 2019/2/14 下午5:35
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
@ChannelHandler.Sharable
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private static final PingMsg pingMsg = new PingMsg();

    @Value("${capricorn.heartBeatPeriod.seconds}")
    private long seconds;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        scheduleSendHeartBeat(ctx, seconds);
        super.channelActive(ctx);
    }

    private void scheduleSendHeartBeat(ChannelHandlerContext ctx, long period) {
        if (ctx.channel().isActive()) {
            ctx.writeAndFlush(pingMsg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            ctx.executor().schedule(() -> scheduleSendHeartBeat(ctx, period), period, TimeUnit.SECONDS);
        }
    }
}
