package com.portfolio.crawlerdata.controller;

import com.portfolio.crawlerdata.dto.ItemCreateRequest;
import com.portfolio.crawlerdata.dto.ItemResponse;
import com.portfolio.crawlerdata.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * SKU REST 端點。
 *
 * 對應 FastAPI 版 routers/item_api.py，掛在 /v1/item prefix 下；
 * 路由與原專案一一對應：
 *   GET    /v1/item/items          查所有 SKU
 *   GET    /v1/item/item/{id}      查單一 SKU
 *   POST   /v1/item/item           新增 SKU
 *   PUT    /v1/item/item/{id}      軟刪除 SKU
 */
@RestController
@RequestMapping("/v1/item")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public List<ItemResponse> listItems(@RequestParam(required = false) String siteId) {
        return siteId == null ? itemService.findAll() : itemService.findBySite(siteId);
    }

    @GetMapping("/item/{skuId}")
    public ItemResponse getItem(@PathVariable String skuId) {
        return itemService.findById(skuId);
    }

    @PostMapping("/item")
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemCreateRequest request) {
        ItemResponse created = itemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/item/{skuId}")
    public ItemResponse softDeleteItem(@PathVariable String skuId) {
        return itemService.softDelete(skuId);
    }
}
