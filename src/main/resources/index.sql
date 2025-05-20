-- Brand 인덱스 조건부 생성
SET @index_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'Brand'
      AND index_name = 'idx_brand_name_fulltext'
);

SET @create_index_brand := IF(
        @index_exists = 0,
        'ALTER TABLE Brand ADD FULLTEXT INDEX idx_brand_name_fulltext (name) WITH PARSER ngram;',
        'SELECT "Index already exists for Brand"'
                           );

PREPARE stmt_brand FROM @create_index_brand;
EXECUTE stmt_brand;
DEALLOCATE PREPARE stmt_brand;

-- Item 인덱스 조건부 생성
SET @index_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'Item'
      AND index_name = 'idx_item_name_fulltext'
);

SET @create_index_item := IF(
        @index_exists = 0,
        'ALTER TABLE Item ADD FULLTEXT INDEX idx_item_name_fulltext (item_name);',
        'SELECT "Index already exists for Item"'
                          );

PREPARE stmt_item FROM @create_index_item;
EXECUTE stmt_item;
DEALLOCATE PREPARE stmt_item;
