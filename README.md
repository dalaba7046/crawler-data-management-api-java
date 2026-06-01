# Crawler Data Management API（Spring Boot + MS SQL 版）

以 Spring Boot 3 層架構建立的後端 API，管理爬蟲落地後的 SKU 主檔資料。本專案由原本的 FastAPI 版（[crawler-data-management-api](https://github.com/dalaba7046/crawler-data-management-api)）改寫為 Java / Spring Boot，主資料庫採用 Microsoft SQL Server。

重點不在爬蟲本身，而是練習資料進入資料庫後，如何透過 Controller、Service、Repository 分層與 DTO 驗證，提供穩定、可維護的查詢與管理能力。

## 技術棧

- Java 11
- Spring Boot 2.7（Web、Data JPA、Validation）
- Microsoft SQL Server（主資料庫，JDBC）
- H2 in-memory（dev profile，本機免裝資料庫即可啟動驗證）
- MongoDB（對應原專案 raw-items 原始爬蟲文件查詢，預設關閉）
- springdoc-openapi（Swagger UI，對應 FastAPI 內建 /docs）
- Maven、Docker / Docker Compose
- Vue 3 + Vite（前端儀表板）

## 分層架構

```
Controller  →  Service  →  Repository  →  Entity / Database
 (路由/HTTP)   (業務邏輯)   (資料存取)      (JPA 對應資料表)
   DTO 驗證                  Spring Data JPA
```

```
src/main/java/com/portfolio/crawlerdata/
├── CrawlerDataApplication.java   進入點 + 根路由健康檢查
├── controller/ItemController.java   REST 端點（/v1/item）
├── service/ItemService.java         業務邏輯（軟刪除、404）
├── repository/ItemRepository.java   Spring Data JPA
├── entity/Item.java                 對應 def_sku_list 資料表
├── dto/
│   ├── ItemCreateRequest.java       請求驗證（對應 Pydantic）
│   └── ItemResponse.java            回應格式
├── exception/                       全域例外處理（404 / 400）
└── config/
    ├── WebConfig.java               CORS（對應 FastAPI CORSMiddleware）
    └── DataSeeder.java              dev 假資料
src/main/resources/
├── application.properties           預設 dev profile，port 8000
├── application-dev.properties       H2 in-memory
├── application-mssql.properties     連真正的 MS SQL Server
└── db/schema-mssql.sql              MS SQL 建表 + 假資料
```

## API 路由（與 FastAPI 版一一對應）

| Method | Path | 說明 |
|---|---|---|
| GET | `/` | 健康檢查 |
| GET | `/v1/item/items` | 查詢所有未軟刪除的 SKU |
| GET | `/v1/item/items?siteId={siteId}` | 依站台篩選未軟刪除的 SKU |
| GET | `/v1/item/item/{id}` | 查詢指定 SKU；查無回 404 |
| POST | `/v1/item/item` | 新增 SKU；驗證失敗回 400 |
| PUT | `/v1/item/item/{id}` | 軟刪除（IS_DELETED = 1） |

## 執行方式

### 方式一：本機免裝資料庫（最快，用 H2）

需求：JDK 11、Maven。

```bash
mvn spring-boot:run
```

啟動後：

- API：http://localhost:8000
- Swagger UI：http://localhost:8000/swagger-ui.html
- H2 console：http://localhost:8000/h2-console

快速驗證：

```bash
curl http://localhost:8000/v1/item/items
curl http://localhost:8000/v1/item/item/SKU-MSSQL-001
curl -X POST http://localhost:8000/v1/item/item \
  -H "Content-Type: application/json" \
  -d '{"skuId":"SKU-MSSQL-004","siteId":"JD","itemName":"新商品"}'
curl -X PUT http://localhost:8000/v1/item/item/SKU-MSSQL-004
```

### 方式二：連真正的 MS SQL Server

1. 在 SQL Server 跑一次建表腳本：

```bash
sqlcmd -S localhost -U sa -P <password> -d crawler_data -i src/main/resources/db/schema-mssql.sql
```

2. 以 mssql profile 啟動（連線資訊可用環境變數覆寫）：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mssql
```

### 方式三：Docker Compose 一鍵起 MS SQL + API + 前端

```bash
docker compose up --build
```

- API：http://localhost:8000
- 前端：http://localhost:5173

### 前端（Vue 3）

```bash
cd frontend
npm install
npm run dev
```

開啟 http://localhost:5173。

## 測試

```bash
mvn test
```

`ItemApiIntegrationTest` 以 H2 驗證四條路由、依站台篩選（含排除軟刪除）、404、與驗證失敗（400）行為。

## 從 FastAPI 版對映過來的對照

| FastAPI 版 | Spring Boot 版 |
|---|---|
| `FastAPI()` / `@app.get` | `@SpringBootApplication` / `@RestController` |
| `include_router(prefix="/v1/item")` | `@RequestMapping("/v1/item")` |
| SQLAlchemy ORM model | JPA `@Entity` |
| SQLAlchemy session query | Spring Data JPA `Repository` |
| Pydantic schema | DTO + `javax.validation` |
| HTTPException(404) | `ResourceNotFoundException` + `@RestControllerAdvice` |
| CORSMiddleware | `WebConfig` CORS |
| 內建 `/docs` | springdoc Swagger UI |
| PostgreSQL/MySQL/Mongo | MS SQL（主）+ H2（dev）+ Mongo（選用） |

## 備註

學習與 side project 用途，不宣稱 production-ready。
