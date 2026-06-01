package com.portfolio.crawlerdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 新增 SKU 的請求 DTO。
 *
 * 對應 FastAPI 版的 Pydantic request schema（schemas/jd_schema.py）。
 * @NotBlank / @Size 等同 Pydantic 的欄位驗證；驗證失敗會由
 * GlobalExceptionHandler 統一轉成 400，行為對齊 FastAPI 的 422。
 */
public class ItemCreateRequest {

    @NotBlank(message = "SKU_ID 不可為空")
    @Size(max = 64, message = "SKU_ID 長度不可超過 64")
    private String skuId;

    @Size(max = 32, message = "SITE_ID 長度不可超過 32")
    private String siteId;

    @Size(max = 255, message = "ITEM_NAME 長度不可超過 255")
    private String itemName;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
