-- brand 인덱스 조건부 생성
SET @index_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'brand'
      AND index_name = 'idx_brand_name_fulltext'
);

SET @create_index_brand := IF(
        @index_exists = 0,
        'ALTER TABLE brand ADD FULLTEXT INDEX idx_brand_name_fulltext (name) WITH PARSER ngram;',
        'SELECT "Index already exists for brand"'
                           );

PREPARE stmt_brand FROM @create_index_brand;
EXECUTE stmt_brand;
DEALLOCATE PREPARE stmt_brand;

-- item 인덱스 조건부 생성
SET @index_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'item'
      AND index_name = 'idx_item_name_fulltext'
);

SET @create_index_item := IF(
        @index_exists = 0,
        'ALTER TABLE item ADD FULLTEXT INDEX idx_item_name_fulltext (item_name);',
        'SELECT "Index already exists for item"'
                          );

PREPARE stmt_item FROM @create_index_item;
EXECUTE stmt_item;
DEALLOCATE PREPARE stmt_item;

-- recipe 인덱스 조건부 생성: (recipe_servings, recipe_cooking_time)
SET @index_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'recipe'
      AND index_name = 'idx_recipe_servings_cooking_time'
);

SET @create_index_recipe := IF(
        @index_exists = 0,
        'ALTER TABLE recipe ADD INDEX idx_recipe_servings_cooking_time (recipe_servings, recipe_cooking_time);',
        'SELECT "Index already exists for recipe (servings, cooking_time)"'
                            );

PREPARE stmt_recipe FROM @create_index_recipe;
EXECUTE stmt_recipe;
DEALLOCATE PREPARE stmt_recipe;

-- recipe 인덱스 조건부 생성: (created_date)
SET @index_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = 'recipe'
      AND index_name = 'idx_recipe_created_date'
);

SET @create_index_recipe_created := IF(
        @index_exists = 0,
        'ALTER TABLE recipe ADD INDEX idx_recipe_created_date (created_date);',
        'SELECT "Index already exists for recipe (created_date)"'
                                    );

PREPARE stmt_recipe_created FROM @create_index_recipe_created;
EXECUTE stmt_recipe_created;
DEALLOCATE PREPARE stmt_recipe_created;