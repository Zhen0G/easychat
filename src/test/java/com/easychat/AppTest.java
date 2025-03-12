package com.easychat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest  // ✅ 确保 Spring Boot 正确加载测试环境
class AppTest {

    @Test
    void contextLoads() {
        // ✅ 这个测试只检查 Spring Boot 是否能正确启动
    }
}
