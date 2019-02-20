package cn.laifuzhi.capricorn.common.msg;

import cn.laifuzhi.capricorn.common.util.IpUtils;
import lombok.Data;

/**
 * Author: fuzhi.lai
 * Date: 2019/2/14 下午5:27
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
@Data
public class PingMsg implements MsgI {
    private String innerIp = IpUtils.getInnerIp();

    @Override
    public MsgEnum getMsgEnum() {
        return MsgEnum.Ping;
    }
}
