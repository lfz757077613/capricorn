package cn.laifuzhi.capricorn.common.util;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public final class IpUtils {
    // refer to RFC 1918
    // 10/8 prefix
    // 172.16/12 prefix
    // 192.168/16 prefix
    private static Supplier<String> innerIp;

    static {
        innerIp = Suppliers.memoize(() -> {
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                List<String> innerIpList = Lists.newArrayList();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface anInterface = interfaces.nextElement();
                    Enumeration<InetAddress> inetAddresses = anInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (inetAddress.isSiteLocalAddress()) {
                            innerIpList.add(inetAddress.getHostAddress());
                        }
                    }
                }
                log.info("get innerIp:[{}]", JSON.toJSONString(innerIpList));
                //云立方移动机器，会有类似192.168.196.56、172.24.32.166两个符合要求的ip
                if (innerIpList.size() == 0) {
                    log.warn("get innerIp return empty");
                    return StringUtils.EMPTY;
                } else if (innerIpList.size() == 1) {
                    return innerIpList.get(0);
                } else {
                    log.warn("get innerIp duplicate");
                    for (String innerIp : innerIpList) {
                        if (innerIp.startsWith("192.168")) {
                            return innerIp;
                        }
                    }
                    return innerIpList.get(0);
                }
            } catch (SocketException e) {
                log.warn("get innerIp error", e);
                return StringUtils.EMPTY;
            }
        });
    }

    /**
     * 获取当前的内网ip
     */
    public static @Nonnull String getInnerIp() {
        return innerIp.get();
    }
}
