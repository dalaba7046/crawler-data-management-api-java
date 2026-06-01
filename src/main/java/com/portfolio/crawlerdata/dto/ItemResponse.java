package com.portfolio.crawlerdata.dto;

import com.portfolio.crawlerdata.entity.Item;

import java.time.LocalDateTime;

/**
 * SKU 回應 DTO。
 *
 * 對應 FastAPI 版的 Pydantic response schema：把 Entity 轉成對外輸出格式，
 * 避免直接把 JPA Entity 暴露給前端（等同 Pydantic 的 orm_mode / from_attributes）。
 */
public class ItemResponse {

    private String skuId;
    private String siteId;
    private String itemName;
    private Integer isDeleted;
    private LocalDateTime createdAt;

    public static ItemResponse from(Item item) {
        ItemResponse r = new ItemResponse();
        r.skuId = item.getSkuId();
        r.siteId = item.getSiteId();
        r.itemName = item.getItemName();
        r.isDeleted = item.getIsDeleted();
        r.createdAt = item.getCreatedAt();
        return r;
    }

    public String getSkuId() {
        return skuId;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getItemName() {
        return itemName;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
