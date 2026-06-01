-- ============================================================
-- MS SQL Server 初始化腳本（對齊京城 JD 的 MS SQL 要求）
-- 對應 FastAPI 版 docker 初始化 SQL 建立的 def_sku_list + 假資料
--
-- 使用方式（mssql profile 前先在 SQL Server 跑一次）：
--   sqlcmd -S localhost -U sa -P <password> -d crawler_data -i schema-mssql.sql
-- ============================================================

IF OBJECT_ID(N'dbo.def_sku_list', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.def_sku_list (
        SKU_ID      VARCHAR(64)  NOT NULL PRIMARY KEY,
        SITE_ID     VARCHAR(32)  NULL,
        ITEM_NAME   NVARCHAR(255) NULL,
        IS_DELETED  INT          NOT NULL DEFAULT 0,
        CREATED_AT  DATETIME2    NULL DEFAULT SYSDATETIME()
    );
END;
GO

-- 假資料（存在則略過）
IF NOT EXISTS (SELECT 1 FROM dbo.def_sku_list WHERE SKU_ID = 'SKU-MSSQL-001')
    INSERT INTO dbo.def_sku_list (SKU_ID, SITE_ID, ITEM_NAME, IS_DELETED) VALUES
        ('SKU-MSSQL-001', 'JD',     N'示範商品 A', 0),
        ('SKU-MSSQL-002', 'JD',     N'示範商品 B', 0),
        ('SKU-MSSQL-003', 'AMAZON', N'示範商品 C', 0);
GO
