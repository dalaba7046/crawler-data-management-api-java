package com.portfolio.crawlerdata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * SKU 主檔資料表實體。
 *
 * 對應 FastAPI 版的 models/jd_model.py 的 Items（SQLAlchemy ORM model）。
 * JPA @Entity / @Table 等同 SQLAlchemy 的 Base + __tablename__。
 *
 * 對齊資料表：dbo.def_sku_list（見 src/main/resources/db/schema-mssql.sql）
 */
@Entity
@Table(name = "def_sku_list")
public class Item {

    /** SKU 主鍵，對應原專案 SKU_ID。 */
    @Id
    @Column(name = "SKU_ID", length = 64, nullable = false)
    private String skuId;

    /** 來源站台代碼，如 JD / Amazon。 */
    @Column(name = "SITE_ID", length = 32)
    private String siteId;

    /** 商品名稱。 */
    @Column(name = "ITEM_NAME", length = 255)
    private String itemName;

    /** 軟刪除旗標：0 有效、1 已刪除。對應原專案 PUT 軟刪除行為。 */
    @Column(name = "IS_DELETED", nullable = false)
    private Integer isDeleted = 0;

    /** 建立時間。 */
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    public Item() {
    }

    public Item(String skuId, String siteId, String itemName) {
        this.skuId = skuId;
        this.siteId = siteId;
        this.itemName = itemName;
        this.isDeleted = 0;
        this.createdAt = LocalDateTime.now();
    }

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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
