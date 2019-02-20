package cn.laifuzhi.capricorn.server;

import cn.laifuzhi.capricorn.common.handler.InputSplitter;
import cn.laifuzhi.capricorn.common.handler.MyMsgCodec;
import cn.laifuzhi.capricorn.server.handler.AllMsgHandler;
import cn.laifuzhi.capricorn.server.handler.ExceptionHandler;
import cn.laifuzhi.capricorn.server.handler.ServerIdleHandler;
import cn.laifuzhi.capricorn.server.service.QQNotifyService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Author: fuzhi.lai
 * Date: 2018/10/22 下午11:12
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
public class NettyServer {

    private final ServerBootstrap serverBootstrap;

    @Value("${capricorn.server.port}")
    private int serverPort;

    @Resource
    private AllMsgHandler allMsgHandler;
    @Resource
    private QQNotifyService qqNotifyService;
    @Resource
    private ExceptionHandler exceptionHandler;// 放在最后


    public NettyServer() {
        //只监听一个端口，bossGroup只设置一个线程就可以
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
                .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        // 60s收不到client心跳认为连接假死(也就是收不到ping)，关闭channel
                        p.addLast(new ServerIdleHandler(60, 0, 0));
                        p.addLast(new InputSplitter());
                        p.addLast(MyMsgCodec.INSTANCE);
                        p.addLast(allMsgHandler);
                        p.addLast(exceptionHandler);
                    }
                });
    }

    public synchronized void bind() {
        serverBootstrap.bind(serverPort).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("bind port {} success", serverPort);
                qqNotifyService.startNotifySchedule();
            } else {
                log.error("bind fail cause:", future.cause());
            }
        });
    }
}
