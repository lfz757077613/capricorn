package cn.laifuzhi.capricorn.server.controller;

import cn.laifuzhi.capricorn.server.model.DeadClient;
import cn.laifuzhi.capricorn.server.service.ClientService;
import cn.laifuzhi.capricorn.server.service.QQNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: fuzhi.lai
 * Date: 2019/2/18 上午11:30
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
@RestController
public class AdminController {
    @Resource
    private ClientService clientService;
    @Resource
    private QQNotifyService qqService;

    @RequestMapping("deadClient")
    public List<DeadClient> getDeadClient() {
        return clientService.getDeadClientList();
    }

    @RequestMapping("notifyQQ")
    public String notifyQQ() {
        qqService.notifyQQ();
        return "ok";
    }
}
