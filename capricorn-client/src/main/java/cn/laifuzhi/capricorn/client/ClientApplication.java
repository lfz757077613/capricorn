package cn.laifuzhi.capricorn.client;

import cn.laifuzhi.capricorn.client.util.DirectMemReporter;
import cn.laifuzhi.capricorn.client.util.SpringAssist;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
        SpringAssist.getBean(NettyClient.class).connectWithRetry();
        SpringAssist.getBean(DirectMemReporter.class).startReport();
    }
}
