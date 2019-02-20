package cn.laifuzhi.capricorn.common.handler;

import cn.laifuzhi.capricorn.common.msg.MsgI;
import cn.laifuzhi.capricorn.common.msg.MsgEnum;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Author: fuzhi.lai
 * Date: 2018/11/17 下午1:54
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
@ChannelHandler.Sharable
public class MyMsgCodec extends MessageToMessageCodec<ByteBuf, MsgI> {

    public static final MyMsgCodec INSTANCE = new MyMsgCodec();

    @Override
    protected void encode(ChannelHandlerContext ctx, MsgI msg, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
        byte[] bytes = JSON.toJSONBytes(msg);
        byteBuf.writeInt(MsgEnum.MAGIC_NUM)
                .writeByte(msg.getMsgEnum().getId())
                .writeInt(bytes.length)
                .writeBytes(bytes);
        out.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 跳过魔数，获得消息类型
        int msgId = in.skipBytes(4).readByte();
        MsgEnum msgEnum = MsgEnum.getById(msgId);
        if (msgEnum == null) {
            log.error("no such msg, msgTypeId:{}", msgId);
            return;
        }
        // 根据数据包长度创建byte数组
        byte[] bytes = new byte[in.readInt()];
        in.readBytes(bytes);
        out.add(JSON.parseObject(bytes, msgEnum.getMsgClass()));
    }
}
