package cn.laifuzhi.capricorn.server.util;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;


/**
 * Author: fuzhi.lai
 * Date: 2019/2/18 下午8:55
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
public final class AttrKeyUtil {
    public static final AttributeKey<String> INNER_IP = AttributeKey.valueOf("INNER_IP");

    //非正常连接的关闭(不是魔数开头的)，或者还没收到ping的时候，innerIp会是空
    public static @Nonnull String getInnerIp(@Nonnull Channel channel) {
        String innerIp = StringUtils.EMPTY;
        if (channel.hasAttr(AttrKeyUtil.INNER_IP)) {
            innerIp = channel.attr(AttrKeyUtil.INNER_IP).get();
        }
        return innerIp;
    }
}
