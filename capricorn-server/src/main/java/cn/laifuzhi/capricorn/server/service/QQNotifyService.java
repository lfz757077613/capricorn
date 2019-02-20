package cn.laifuzhi.capricorn.server.service;

import cn.laifuzhi.capricorn.common.util.StringAssist;
import cn.laifuzhi.capricorn.server.model.DeadClient;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Author: fuzhi.lai
 * Date: 2019/2/18 下午3:25
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
@Service
public class QQNotifyService {
    private static final String THREAD_NAME = "QQNotify";
    private static final String CLOUDCUBE_URL = "xxxx";
    private static final String KEY = "xxx";

    private ScheduledExecutorService executor = MoreExecutors.getExitingScheduledExecutorService(
            new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory(THREAD_NAME)),
            10,
            TimeUnit.SECONDS);

    private static final Dispatcher dispatcher = new Dispatcher();
    static {
        dispatcher.setMaxRequests(200);
        dispatcher.setMaxRequestsPerHost(200);
    }

    private final static OkHttpClient okhttpClient = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .dispatcher(dispatcher)
            .connectionPool(new ConnectionPool(10, 10, TimeUnit.SECONDS))
            .followRedirects(false)
            .followSslRedirects(false)
            .hostnameVerifier((s, sslSession) -> true)
            .build();

    @Value("${capricorn.qqNotify.seconds}")
    private int notifyPeriod;

    @Resource
    private ClientService clientService;

    public void startNotifySchedule() {
        executor.scheduleAtFixedRate(this::notifyQQ, notifyPeriod, notifyPeriod, TimeUnit.SECONDS);
    }

    public void notifyQQ() {
        Set<String> deadClientAddrList = Sets.newHashSet();
        for (DeadClient deadClient : clientService.getDeadClientList()) {
            deadClientAddrList.add(deadClient.getClientInfo().getSshAddress());
        }
        if (!CollectionUtils.isEmpty(deadClientAddrList)) {
            StringAssist.joinComma(deadClientAddrList);
            FormBody formBody = new FormBody.Builder(Charsets.UTF_8)
                    .add("addr", StringAssist.joinComma(deadClientAddrList))
                    .add("key", KEY)
                    .add("content", "网络不通")
                    .build();
            Request request = new Request.Builder()
                    .url(CLOUDCUBE_URL)
                    .post(formBody)
                    .build();
            okhttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.error("notify qq error, deadClient:{}", JSON.toJSONString(deadClientAddrList), e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()
                            && response.body() != null
                            && StringUtils.equalsIgnoreCase(response.body().string(), "ok")) {
                        log.info("notify qq success, deadClient:{}", JSON.toJSONString(deadClientAddrList));
                        return;
                    }
                    log.error("notify qq fail, deadClient:{}", JSON.toJSONString(deadClientAddrList));
                }
            });
        }
    }
}
