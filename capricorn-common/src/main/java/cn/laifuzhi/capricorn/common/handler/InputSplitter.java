package cn.laifuzhi.capricorn.common.handler;

import cn.laifuzhi.capricorn.common.msg.MsgEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Author: fuzhi.lai
 * Date: 2018/11/17 上午1:39
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
// 私有协议，前四字节是魔数，第五个字节是消息类型(所以最多处理256种消息)，接着四个字节是消息长度
public class InputSplitter extends LengthFieldBasedFrameDecoder {
    private static final int LENGTH_FIELD_OFFSET = 5;
    private static final int LENGTH_FIELD_LENGTH = 4;

    public InputSplitter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //不是魔数开头直接拒绝访问
        if (in.readableBytes() >= Integer.BYTES && in.getInt(in.readerIndex()) != MsgEnum.MAGIC_NUM) {
            ctx.close();
            return null;
        }
        return super.decode(ctx, in);
    }

}
