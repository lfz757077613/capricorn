package cn.laifuzhi.capricorn.client.util;

import com.google.common.util.concurrent.MoreExecutors;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author: fuzhi.lai
 * Date: 2018/11/12 下午11:27
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
@Component
@Slf4j(topic = "directMemReporter")
public final class DirectMemReporter {
    private static final String THREAD_NAME = "directMemReporter";
    private AtomicLong directMem = new AtomicLong();
    private ScheduledExecutorService executor = MoreExecutors.getExitingScheduledExecutorService(
            new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory(THREAD_NAME)),
            10,
            TimeUnit.SECONDS);

    @Value("${capricorn.directMemReporterPeriod.seconds}")
    private long periodSeconds;

    public DirectMemReporter() {
        try {
            Field field = ReflectionUtils.findField(PlatformDependent.class, "DIRECT_MEMORY_COUNTER");
            Objects.requireNonNull(field).setAccessible(true);
            directMem = (AtomicLong) field.get(PlatformDependent.class);
        } catch (Exception e) {
            log.error("client create directMemReporter error", e);
        }
    }

    public void startReport() {
        executor.scheduleAtFixedRate(
                () -> log.info("client direct memory size:{}b, max:{}", directMem.get(), PlatformDependent.maxDirectMemory())
                , periodSeconds
                , periodSeconds
                , TimeUnit.SECONDS);
    }
}
