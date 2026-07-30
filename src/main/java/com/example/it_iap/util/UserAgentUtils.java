package com.example.it_iap.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserAgentUtils {
    private UserAgentAnalyzer uaa;

    @PostConstruct
    public void init() {
        try {
            this.uaa = UserAgentAnalyzer
                    .newBuilder()
                    .hideMatcherLoadStats()
                    .withCache(1000)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi khởi tạo UserAgentAnalyzer: ", e);
        }
    }

    public DeviceInfo parseUserAgent(String userAgentString) {
        if (userAgentString == null || userAgentString.isBlank()) {
            return new DeviceInfo("Unknown", "Unknown", "Unknown");
        }

        try {
            if (uaa == null) {
                return new DeviceInfo("Unknown", "Unknown", "Unknown");
            }
            UserAgent agent = uaa.parse(userAgentString);
            String deviceClass = agent.getValue(UserAgent.DEVICE_CLASS);
            String osName = agent.getValue(UserAgent.OPERATING_SYSTEM_NAME_VERSION_MAJOR);
            String browserName = agent.getValue(UserAgent.AGENT_NAME_VERSION_MAJOR);

            return new DeviceInfo(
                    deviceClass != null ? deviceClass : "Unknown",
                    osName != null ? osName : "Unknown",
                    browserName != null ? browserName : "Unknown"
            );
        } catch (Exception e) {
            log.warn("Lỗi phân tích User-Agent: {}", e.getMessage());
            return new DeviceInfo("Unknown", "Unknown", "Unknown");
        }
    }

    public String getLocation(String ip) {
        if (ip == null || ip.isBlank() || "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) 
                || ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.16.")) {
            return "Localhost (Dev)";
        }
        return "Unknown";
    }

    public record DeviceInfo(String deviceType, String osName, String browserName) {}
}
