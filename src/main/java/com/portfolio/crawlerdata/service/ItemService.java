package com.portfolio.crawlerdata.service;

import com.portfolio.crawlerdata.dto.ItemCreateRequest;
import com.portfolio.crawlerdata.dto.ItemResponse;
import com.portfolio.crawlerdata.entity.Item;
import com.portfolio.crawlerdata.exception.ResourceNotFoundException;
import com.portfolio.crawlerdata.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SKU 業務邏輯層。
 *
 * 對應 FastAPI 版 services/jd/ 下的服務邏輯：把 Controller 與資料存取分離，
 * 業務規則（軟刪除、找不到時 404）集中在這層。@Transactional 對應
 * SQLAlchemy session 的交易邊界。
 */
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /** GET /v1/item/items —— 查詢所有未軟刪除的 SKU。 */
    @Transactional(readOnly = true)
    public List<ItemResponse> findAll() {
        return itemRepository.findAllActive().stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    /** GET /v1/item/items?siteId=XXX —— 依站台查詢未軟刪除的 SKU。 */
    @Transactional(readOnly = true)
    public List<ItemResponse> findBySite(String siteId) {
        return itemRepository.findBySiteIdAndIsDeleted(siteId, 0).stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    /** GET /v1/item/item/{id} —— 查詢單一 SKU，找不到回 404。 */
    @Transactional(readOnly = true)
    public ItemResponse findById(String skuId) {
        Item item = itemRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("查無 SKU：" + skuId));
        return ItemResponse.from(item);
    }

    /** POST /v1/item/item —— 新增 SKU。 */
    @Transactional
    public ItemResponse create(ItemCreateRequest req) {
        Item item = new Item();
        item.setSkuId(req.getSkuId());
        item.setSiteId(req.getSiteId());
        item.setItemName(req.getItemName());
        item.setIsDeleted(0);
        item.setCreatedAt(LocalDateTime.now());
        return ItemResponse.from(itemRepository.save(item));
    }

    /** PUT /v1/item/item/{id} —— 軟刪除（對齊原專案行為，IS_DELETED = 1）。 */
    @Transactional
    public ItemResponse softDelete(String skuId) {
        Item item = itemRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("查無 SKU：" + skuId));
        item.setIsDeleted(1);
        return ItemResponse.from(itemRepository.save(item));
    }
}
