package cn.laifuzhi.capricorn.common.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: fuzhi.lai
 * Date: 2018/11/17 上午2:13
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
@Getter
@AllArgsConstructor
public enum MsgEnum {

    Ping(1, "ping消息", PingMsg.class),
    Pong(2, "pong消息", PongMsg.class),
    ;
    public static final int MAGIC_NUM = 0xdeadbeaf;

    private int id;
    private String desc;
    private Class<?> msgClass;

    private static final Map<Integer, MsgEnum> Id2EnumMap = new HashMap<>();

    static {
        for (MsgEnum msgEnum : values()) {
            Id2EnumMap.put(msgEnum.getId(), msgEnum);
        }
    }

    public static MsgEnum getById(int id) {
        return Id2EnumMap.get(id);
    }
}
