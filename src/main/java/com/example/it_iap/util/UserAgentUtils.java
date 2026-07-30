package com.example.it_iap.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.InetAddress;

@Component
@Slf4j
public class UserAgentUtils {
    private UserAgentAnalyzer uaa;
    private DatabaseReader dbReader; // [THÊM MỚI] MaxMind GeoIP Reader

    @PostConstruct
    public void init() {
        // 1. Khởi tạo UserAgentAnalyzer (Giữ nguyên của bạn)
        try {
            this.uaa = UserAgentAnalyzer
                    .newBuilder()
                    .hideMatcherLoadStats()
                    .withCache(1000)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi khởi tạo UserAgentAnalyzer: ", e);
        }

        // 2. [THÊM MỚI] Khởi tạo MaxMind GeoIP Database
        try {
            ClassPathResource resource = new ClassPathResource("geoip/GeoLite2-City.mmdb");
            if (resource.exists()) {
                InputStream database = resource.getInputStream();
                this.dbReader = new DatabaseReader.Builder(database).build();
                log.info("Nạp file MaxMind GeoIP thành công!");
            } else {
                log.warn("Không tìm thấy file GeoLite2-City.mmdb tại src/main/resources/geoip/");
            }
        } catch (Exception e) {
            log.error("Lỗi khi khởi tạo GeoIP DatabaseReader: ", e);
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

            // [SỬA TẠI ĐÂY] Xử lý trường hợp bị dính "Windows ??" hoặc chứa dấu "??"
            if (osName != null && (osName.contains("??") || osName.contains("Windows"))) {
                // Thử lấy phiên bản đầy đủ (thường YAUAA sẽ trả ra "Windows 10" hoặc "Windows 10.0")
                String fullOsVersion = agent.getValue(UserAgent.OPERATING_SYSTEM_NAME_VERSION);

                if (fullOsVersion != null && !fullOsVersion.contains("??") && !fullOsVersion.equalsIgnoreCase("Unknown")) {
                    osName = fullOsVersion;
                } else {
                    // Fallback an toàn nhất cho tất cả các bản Windows hiện tại
                    osName = "Windows 10/11";
                }
            }

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

    // [ĐÃ SỬA] Hàm getLocation hoàn chỉnh tra cứu vị trí thực tế
    public String getLocation(String ip) {
        if (ip == null || ip.isBlank() || "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)
                || ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.16.")) {
            return "Localhost (Dev)";
        }

        if (dbReader == null) {
            return "Unknown";
        }

        try {
            // MaxMind tự động hỗ trợ cả IPv4 và IPv6 qua InetAddress
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = dbReader.city(ipAddress);

            String city = response.getCity() != null ? response.getCity().getName() : null;
            String country = response.getCountry() != null ? response.getCountry().getName() : null;

            if (city != null && !city.isBlank() && country != null && !country.isBlank()) {
                return city + ", " + country;
            } else if (country != null && !country.isBlank()) {
                return country;
            }
        } catch (Exception e) {
            log.debug("Không thể tra cứu vị trí cho IP {}: {}", ip, e.getMessage());
        }

        return "Unknown";
    }

    public record DeviceInfo(String deviceType, String osName, String browserName) {}
}