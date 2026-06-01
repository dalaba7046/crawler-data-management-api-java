package com.portfolio.crawlerdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 應用程式進入點。
 *
 * 對應 FastAPI 版的 app/main.py：
 *   FastAPI()                 -> @SpringBootApplication
 *   include_router(prefix=)   -> 各 Controller 的 @RequestMapping
 *   @app.get("/")             -> 下方 root() 健康檢查
 *
 * MongoDB 預設關閉自動設定，避免本機沒有 Mongo 時無法啟動；
 * 需要時於 application.properties 設定 app.mongo.enabled=true 並移除排除設定。
 */
@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoReactiveAutoConfiguration.class
})
@RestController
public class CrawlerDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerDataApplication.class, args);
    }

    /** 對應 FastAPI 的 @app.get("/") 根路由健康檢查。 */
    @GetMapping("/")
    public Object root() {
        return java.util.Map.of(
                "service", "crawler-data-management-api",
                "stack", "Spring Boot + MS SQL",
                "status", "ok"
        );
    }
}
