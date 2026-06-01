package com.portfolio.crawlerdata;

import com.portfolio.crawlerdata.dto.ItemResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Item API 整合測試（以 dev profile + H2 跑）。
 *
 * 驗證原專案四條路由的行為：列表、查單筆、新增（201）、軟刪除、
 * 以及查不到時回 404、驗證失敗回 400。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ItemApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootHealthCheck() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void listSeededItems() throws Exception {
        mockMvc.perform(get("/v1/item/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createThenGet() throws Exception {
        String body = "{\"skuId\":\"SKU-TEST-100\",\"siteId\":\"JD\",\"itemName\":\"測試商品\"}";
        mockMvc.perform(post("/v1/item/item")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.skuId").value("SKU-TEST-100"));

        mockMvc.perform(get("/v1/item/item/SKU-TEST-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.siteId").value("JD"));
    }

    @Test
    void getMissingReturns404() throws Exception {
        mockMvc.perform(get("/v1/item/item/NO-SUCH-SKU"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBlankSkuReturns400() throws Exception {
        String body = "{\"skuId\":\"\",\"siteId\":\"JD\"}";
        mockMvc.perform(post("/v1/item/item")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listBySiteReturnsOnlyActiveItemsOfThatSite() throws Exception {
        // 用獨立站台代碼，避免與種子資料 / 其他測試互相污染
        String site = "SITE-FILTER-TEST";
        createItem("SKU-FILTER-A", site);
        createItem("SKU-FILTER-B", site);
        createItem("SKU-FILTER-DEL", site);

        // 把其中一筆軟刪除——站台查詢「不應」再撈到它
        mockMvc.perform(put("/v1/item/item/SKU-FILTER-DEL"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/item/items").param("siteId", site))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].skuId", hasItem("SKU-FILTER-A")))
                .andExpect(jsonPath("$[*].skuId", hasItem("SKU-FILTER-B")))
                // 守住軟刪除規則：少了 AndIsDeleted=0 這條測試就會紅
                .andExpect(jsonPath("$[*].skuId", not(hasItem("SKU-FILTER-DEL"))));
    }

    private void createItem(String skuId, String siteId) throws Exception {
        String body = "{\"skuId\":\"" + skuId + "\",\"siteId\":\"" + siteId + "\"}";
        mockMvc.perform(post("/v1/item/item")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void softDeleteSetsFlag() throws Exception {
        String body = "{\"skuId\":\"SKU-TEST-200\",\"siteId\":\"JD\"}";
        mockMvc.perform(post("/v1/item/item")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/v1/item/item/SKU-TEST-200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDeleted").value(1));
    }
}
