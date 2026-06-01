package com.portfolio.crawlerdata.repository;

import com.portfolio.crawlerdata.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SKU 資料存取層。
 *
 * 對應 FastAPI 版裡用 SQLAlchemy session 直接做的 query；
 * Spring Data JPA 由介面方法名自動產生 SQL，等同把資料存取邏輯交給框架。
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    /** 只查未被軟刪除的 SKU（IS_DELETED = 0）。 */
    @Query("SELECT i FROM Item i WHERE i.isDeleted = 0")
    List<Item> findAllActive();

    /**
     * 依站台查詢未軟刪除的 SKU。
     *
     * 不寫 @Query，純靠方法名讓 Spring Data JPA 自動推導 SQL：
     * findBy + SiteId + And + IsDeleted → WHERE SITE_ID = ? AND IS_DELETED = ?
     */
    List<Item> findBySiteIdAndIsDeleted(String siteId, Integer isDeleted);
}
