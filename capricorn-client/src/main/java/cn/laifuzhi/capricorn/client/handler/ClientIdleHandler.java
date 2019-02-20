package cn.laifuzhi.capricorn.client.handler;

import cn.laifuzhi.capricorn.client.NettyClient;
import cn.laifuzhi.capricorn.client.util.SpringAssist;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Author: fuzhi.lai
 * Date: 2018/10/29 下午5:43
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
public class ClientIdleHandler extends IdleStateHandler {
    public ClientIdleHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }

    //连接断开后发起重连
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel close, start reconnect");
        ctx.executor().execute(() -> SpringAssist.getBean(NettyClient.class).connectWithRetry());
        super.channelInactive(ctx);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info("channel idle");
        ctx.close();
    }
}
