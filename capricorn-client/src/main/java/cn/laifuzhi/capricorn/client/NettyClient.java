package cn.laifuzhi.capricorn.client;

import cn.laifuzhi.capricorn.client.handler.AllMsgHandler;
import cn.laifuzhi.capricorn.client.handler.ClientIdleHandler;
import cn.laifuzhi.capricorn.client.handler.ExceptionHandler;
import cn.laifuzhi.capricorn.client.handler.HeartBeatHandler;
import cn.laifuzhi.capricorn.common.handler.InputSplitter;
import cn.laifuzhi.capricorn.common.handler.MyMsgCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Author: fuzhi.lai
 * Date: 2018/10/29 下午4:58
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
public class NettyClient {
    private final AtomicInteger reconnectCount = new AtomicInteger();
    private final AtomicBoolean isConnecting = new AtomicBoolean();
    private final Bootstrap bootstrap;

    @Value("${capricorn.server.ip}")
    private String serverIp;
    @Value("${capricorn.server.port}")
    private int serverPort;
    @Value("${capricorn.reconnectPeriod.seconds}")
    private long reconnectPeriodSeconds;

    @Resource
    private HeartBeatHandler heartBeatHandler;
    @Resource
    private AllMsgHandler allMsgHandler;
    @Resource
    private ExceptionHandler exceptionHandler;

    public NettyClient() {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap().group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        // 60s没有服务端数据流入认为连接假死(就是收不到pong)，关闭channel
                        p.addLast(new ClientIdleHandler(60, 0, 0));
                        p.addLast(new InputSplitter());
                        p.addLast(MyMsgCodec.INSTANCE);
                        p.addLast(heartBeatHandler);
                        p.addLast(allMsgHandler);
                        p.addLast(exceptionHandler);
                    }
                });
        Runtime.getRuntime().addShutdownHook(new Thread(workGroup::shutdownGracefully));
        this.bootstrap = bootstrap;
    }

    // 连接超时时间小于重试时间或者重复调用会引发多次连接
    public synchronized void connectWithRetry() {
        //已经在连接中了，直接返回
        if (!isConnecting.compareAndSet(false, true)) {
            log.error("duplicate connecting");
            return;
        }
        bootstrap.connect(serverIp, serverPort).addListener((ChannelFutureListener) future -> {
            reconnectCount.incrementAndGet();
            if (future.isSuccess()) {
                log.info("connect done after connect {} times", reconnectCount);
                reconnectCount.set(0);
                //反之重复连接
            } else {
                future.channel().eventLoop().schedule(this::connectWithRetry, reconnectPeriodSeconds, TimeUnit.SECONDS);
                log.error("connect fail {} times, cause:{}", reconnectCount, future.cause().getMessage());
            }
            isConnecting.set(false);
        });
    }
}
