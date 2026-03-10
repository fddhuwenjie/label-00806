package com.fooddelivery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

@SpringBootApplication
@MapperScan("com.fooddelivery.mapper")
public class FoodDeliveryApplication {

    private static final Logger log = LoggerFactory.getLogger(FoodDeliveryApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FoodDeliveryApplication.class, args);
        printStartupInfo(context);
    }

    private static void printStartupInfo(ConfigurableApplicationContext context) {
        try {
            Environment env = context.getEnvironment();
            String port = env.getProperty("server.port", "8080");
            String contextPath = env.getProperty("server.servlet.context-path", "");
            String hostAddress = InetAddress.getLocalHost().getHostAddress();

            log.info("\n----------------------------------------------------------");
            log.info("  ✓ Startup Success - 微信外卖平台后端服务启动成功!");
            log.info("----------------------------------------------------------");
            log.info("  Backend API:    http://localhost:{}{}", port, contextPath);
            log.info("  Backend API:    http://{}:{}{}", hostAddress, port, contextPath);
            log.info("  Swagger UI:     http://localhost:{}{}/swagger-ui.html", port, contextPath);
            log.info("  Health Check:   http://localhost:{}{}/actuator/health", port, contextPath);
            log.info("----------------------------------------------------------\n");
        } catch (Exception e) {
            log.warn("获取启动信息时发生异常: {}", e.getMessage());
        }
    }
}
