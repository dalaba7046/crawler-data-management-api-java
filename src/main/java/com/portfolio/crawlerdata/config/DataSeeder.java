package com.portfolio.crawlerdata.config;

import com.portfolio.crawlerdata.entity.Item;
import com.portfolio.crawlerdata.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 啟動時塞入假資料，方便 demo / 驗證。
 *
 * 對應原專案 docker 初始化 SQL 建立的 def_sku_list 假資料。
 * 只在 dev profile（H2 in-memory）啟用；正式 MS SQL 走 schema-mssql.sql。
 */
@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final ItemRepository itemRepository;

    public DataSeeder(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) {
        if (itemRepository.count() > 0) {
            return;
        }
        itemRepository.save(new Item("SKU-MSSQL-001", "JD", "示範商品 A"));
        itemRepository.save(new Item("SKU-MSSQL-002", "JD", "示範商品 B"));
        itemRepository.save(new Item("SKU-MSSQL-003", "AMAZON", "示範商品 C"));
    }
}
