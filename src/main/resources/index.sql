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

-- Orders 인덱스 조건부 생성
# SET @index_exists := (
#     SELECT COUNT(*)
#     FROM INFORMATION_SCHEMA.STATISTICS
#     WHERE table_schema = DATABASE()
#       AND table_name = 'Orders'
#       AND index_name = 'idx_orders_status_created_date'
# );
#
# SET @create_index_orders := IF(
#         @index_exists = 0,
#         'ALTER TABLE Orders ADD INDEX idx_orders_status_created_date (order_status, created_date);',
#         'SELECT "Index already exists for Orders"'
#                             );
#
# PREPARE stmt_orders FROM @create_index_orders;
# EXECUTE stmt_orders;
# DEALLOCATE PREPARE stmt_orders;


-- Recipe 인덱스 조건부 생성: (recipe_servings, recipe_cooking_time)
SET @index_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'Recipe'
      AND index_name = 'idx_recipe_servings_cooking_time'
);

SET @create_index_recipe := IF(
        @index_exists = 0,
        'ALTER TABLE Recipe ADD INDEX idx_recipe_servings_cooking_time (recipe_servings, recipe_cooking_time);',
        'SELECT "Index already exists for Recipe (servings, cooking_time)"'
                            );

PREPARE stmt_recipe FROM @create_index_recipe;
EXECUTE stmt_recipe;
DEALLOCATE PREPARE stmt_recipe;

-- Recipe 인덱스 조건부 생성: (created_date)
SET @index_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'Recipe'
      AND index_name = 'idx_recipe_created_date'
);

SET @create_index_recipe_created := IF(
        @index_exists = 0,
        'ALTER TABLE Recipe ADD INDEX idx_recipe_created_date (created_date);',
        'SELECT "Index already exists for Recipe (created_date)"'
                                    );

PREPARE stmt_recipe_created FROM @create_index_recipe_created;
EXECUTE stmt_recipe_created;
DEALLOCATE PREPARE stmt_recipe_created;