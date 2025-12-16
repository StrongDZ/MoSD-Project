#!/bin/sh
# Don't exit on error - we want the app to start even if import has issues
set +e

# Database connection parameters from environment variables
# Support both docker-compose and direct connection formats
# Docker Compose automatically injects environment variables from .env file
DB_URL="${SPRING_DATASOURCE_URL:-jdbc:postgresql://db:5432/mosd-project}"
DB_HOST="${DB_HOST:-$(echo $DB_URL | sed -n 's/.*:\/\/\([^:]*\):.*/\1/p')}"
DB_PORT="${DB_PORT:-$(echo $DB_URL | sed -n 's/.*:\/\/[^:]*:\([^\/]*\)\/.*/\1/p')}"
DB_NAME="${DB_NAME:-$(echo $DB_URL | sed -n 's/.*:\/\/[^\/]*\/\(.*\)/\1/p')}"
DB_USERNAME="${SPRING_DATASOURCE_USERNAME:-${DB_USERNAME:-postgres}}"
DB_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-${DB_PASSWORD:-postgres}}"

# Fallback to default values if parsing failed (matching .env file)
DB_HOST="${DB_HOST:-db}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-mosd-project}"
DB_USERNAME="${DB_USERNAME:-postgres}"
DB_PASSWORD="${DB_PASSWORD:-postgres}"

export PGPASSWORD="$DB_PASSWORD"

echo "=== Database Import Script ==="
echo "DB_HOST: $DB_HOST"
echo "DB_PORT: $DB_PORT"
echo "DB_NAME: $DB_NAME"
echo "DB_USERNAME: $DB_USERNAME"

echo "Waiting for PostgreSQL to be ready..."
RETRIES=30
until pg_isready -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" > /dev/null 2>&1 || [ $RETRIES -eq 0 ]; do
  echo "PostgreSQL is unavailable - sleeping ($RETRIES retries left)"
  RETRIES=$((RETRIES-1))
  sleep 2
done

if [ $RETRIES -eq 0 ]; then
  echo "WARNING: PostgreSQL is not available after waiting. Data import will be skipped."
  echo "The application will start, but database operations may fail until PostgreSQL is ready."
  unset PGPASSWORD
  exit 0
fi

echo "PostgreSQL is ready!"

# Function to execute SQL file
execute_sql() {
    local file=$1
    echo "Executing SQL file: $file"
    if [ ! -f "$file" ]; then
        echo "Warning: SQL file $file not found, skipping..."
        return 0
    fi
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -f "$file" 2>&1 || {
        echo "Warning: Failed to execute $file, continuing..."
        return 0
    }
}

# Function to import CSV file
import_csv() {
    local csv_file=$1
    local table_name=$2
    local columns=$3
    
    echo "Importing CSV: $csv_file -> table: $table_name"
    
    if [ ! -f "$csv_file" ]; then
        echo "Warning: CSV file $csv_file not found, skipping..."
        return 0
    fi
    
    # Use COPY command for efficient import
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" <<EOF
\copy $table_name($columns) FROM '$csv_file' WITH (FORMAT CSV, HEADER true, DELIMITER ',', ENCODING 'UTF8')
EOF
    if [ $? -ne 0 ]; then
        echo "Warning: Failed to import $csv_file, continuing..."
        return 0
    fi
    echo "Successfully imported $csv_file"
}

# Step 1: Create tables (in order of dependencies)
echo "=== Step 1: Creating database tables ==="

# Check if tables already exist (to avoid re-creating)
TABLE_EXISTS=$(psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -tAc "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'hotel');" 2>/dev/null || echo "false")

if [ "$TABLE_EXISTS" != "t" ]; then
    echo "Tables do not exist, creating them..."
    execute_sql "/app/sql/database_create_table.sql"
    execute_sql "/app/sql/review_tables.sql"
    execute_sql "/app/sql/db_2.sql"
else
    echo "Tables already exist, skipping table creation..."
fi

# Step 2: Import data (in order of foreign key dependencies)
echo "=== Step 2: Importing data ==="

# Check if data directory exists
if [ ! -d "/app/data" ]; then
    echo "WARNING: /app/data directory not found. Skipping data import."
    echo "To import data, mount the data directory as a volume or include it in the build context."
    unset PGPASSWORD
    exit 0
fi

# Check if data has already been imported (optional - can be controlled via env var)
SKIP_IMPORT="${SKIP_DATA_IMPORT:-false}"
if [ "$SKIP_IMPORT" = "true" ]; then
    echo "SKIP_DATA_IMPORT is set to true, skipping data import..."
    unset PGPASSWORD
    exit 0
fi

# Check if database already has data - check multiple tables to be thorough
echo "Checking if database already contains data..."
HOTEL_COUNT=$(psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -tAc "SELECT COUNT(*) FROM hotel;" 2>/dev/null || echo "0")
SHIP_COUNT=$(psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -tAc "SELECT COUNT(*) FROM ship;" 2>/dev/null || echo "0")
COMPANY_COUNT=$(psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -tAc "SELECT COUNT(*) FROM company;" 2>/dev/null || echo "0")

TOTAL_COUNT=$((HOTEL_COUNT + SHIP_COUNT))

if [ "$TOTAL_COUNT" -gt 0 ]; then
    echo "Database already contains data:"
    echo "  - Companies: $COMPANY_COUNT"
    echo "  - Hotels: $HOTEL_COUNT"
    echo "  - Ships: $SHIP_COUNT"
    echo "Total hotel/ship records: $TOTAL_COUNT"
    echo "Skipping data import. Database is not empty."
    unset PGPASSWORD
    exit 0
fi

echo "Database is empty. Proceeding with data import..."

# Import in order of dependencies:
# 1. Base tables (no foreign keys)
echo "=== Importing base data ==="
import_csv "/app/data/company.csv" "company" "company_id,company_name,username,password,role"

# Ensure company_id=0 exists for ships with company_id=0
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" <<EOF
INSERT INTO company (company_id, company_name, username, password, role)
VALUES (0, 'Mixivivu', 'company0', '123456', 'company')
ON CONFLICT (company_id) DO NOTHING;
EOF

import_csv "/app/data/feature.csv" "features" "feature_id,feature_description"

# 2. Hotel data
echo "=== Importing hotel data ==="
import_csv "/app/data/hotel/hotel.csv" "hotel" "hotel_id,hotel_name,total_rooms,company_name,hotel_price,city,address,map_link,thumbnail,company_id"
import_csv "/app/data/hotel/hotel_feature.csv" "hotel_features" "hotel_id,feature_id"
import_csv "/app/data/hotel/hotel_img.csv" "hotel_img" "hotel_id,img_url"

# Import hotel_short_description (CSV has 'data' column, but table expects 'description')
echo "Importing hotel short descriptions..."
if [ -f "/app/data/hotel/hotel_short_description.csv" ]; then
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" <<EOF
CREATE TEMP TABLE hotel_short_desc_temp (
    hotel_id INTEGER,
    block_id INTEGER,
    data VARCHAR
);

\copy hotel_short_desc_temp FROM '/app/data/hotel/hotel_short_description.csv' WITH (FORMAT CSV, HEADER true, DELIMITER ',', ENCODING 'UTF8');

INSERT INTO hotel_short_description (hotel_id, block_id, description)
SELECT hotel_id, block_id, data
FROM hotel_short_desc_temp;

DROP TABLE hotel_short_desc_temp;
EOF
    echo "Successfully imported hotel short descriptions"
fi

import_csv "/app/data/hotel/hotel_long_description.csv" "hotel_long_description" "hotel_id,block_id,type,data"

# 3. Hotel room data
echo "=== Importing hotel room data ==="
import_csv "/app/data/hotel/hotel_room.csv" "hotel_room" "hotel_room_id,hotel_id,room_name,room_price,size,max_persons,bed_type,view"

# Import hotel_room_feature (CSV has 'room_id', but table expects 'hotel_room_id')
echo "Importing hotel room features..."
if [ -f "/app/data/hotel/hotel_room_feature.csv" ]; then
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" <<EOF
\copy hotel_room_features(hotel_room_id, feature_id) FROM '/app/data/hotel/hotel_room_feature.csv' WITH (FORMAT CSV, HEADER true, DELIMITER ',', ENCODING 'UTF8')
EOF
    echo "Successfully imported hotel room features"
fi

import_csv "/app/data/hotel/hotel_room_img.csv" "hotel_room_img" "room_id,img_url"

# 4. Ship data
echo "=== Importing ship data ==="
# Import ship using temp table to handle CSV parsing issues with commas in text fields
echo "Importing ships..."
if [ -f "/app/data/ship/ship.csv" ]; then
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" <<EOF
CREATE TEMP TABLE ship_temp (
    ship_id INTEGER,
    ship_name VARCHAR,
    launch INTEGER,
    cabin INTEGER,
    shell VARCHAR,
    trip VARCHAR,
    company_name VARCHAR,
    ship_price INTEGER,
    address VARCHAR,
    map_link VARCHAR,
    thumbnail VARCHAR,
    company_id INTEGER
);

\copy ship_temp FROM '/app/data/ship/ship.csv' WITH (FORMAT CSV, HEADER true, DELIMITER ',', ENCODING 'UTF8');

-- Insert ships, handling company_id=0 by using company_id=0 (which we ensure exists)
INSERT INTO ship (ship_id, ship_name, launch, cabin, shell, trip, company_name, ship_price, address, map_link, thumbnail, company_id)
SELECT 
    ship_id, 
    ship_name, 
    launch, 
    cabin, 
    shell, 
    trip, 
    company_name, 
    ship_price, 
    address, 
    map_link, 
    thumbnail, 
    COALESCE(NULLIF(company_id, 0), 0) as company_id
FROM ship_temp
WHERE ship_id IS NOT NULL;

DROP TABLE ship_temp;
EOF
    if [ $? -eq 0 ]; then
        echo "Successfully imported ships"
    else
        echo "Warning: Failed to import ships, continuing..."
    fi
fi

import_csv "/app/data/ship/ship_feature.csv" "ship_features" "ship_id,feature_id"
import_csv "/app/data/ship/ship_img.csv" "ship_img" "ship_id,img_url"

# Import ship_short_description (CSV has 'data' column, but table expects 'description')
echo "Importing ship short descriptions..."
if [ -f "/app/data/ship/ship_short_description.csv" ]; then
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" <<EOF
CREATE TEMP TABLE ship_short_desc_temp (
    ship_id INTEGER,
    block_id INTEGER,
    data VARCHAR
);

\copy ship_short_desc_temp FROM '/app/data/ship/ship_short_description.csv' WITH (FORMAT CSV, HEADER true, DELIMITER ',', ENCODING 'UTF8');

INSERT INTO ship_short_description (ship_id, block_id, description)
SELECT ship_id, block_id, data
FROM ship_short_desc_temp;

DROP TABLE ship_short_desc_temp;
EOF
    echo "Successfully imported ship short descriptions"
fi

import_csv "/app/data/ship/ship_long_description.csv" "ship_long_description" "ship_id,block_id,type,data"

# 5. Ship room data
echo "=== Importing ship room data ==="
import_csv "/app/data/ship/ship_room.csv" "ship_room" "ship_room_id,ship_id,room_name,size,max_persons,room_price"

# Import ship_room_feature (CSV has 'room_id', but table expects 'ship_room_id')
echo "Importing ship room features..."
if [ -f "/app/data/ship/ship_room_feature.csv" ]; then
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" <<EOF
\copy ship_room_features(ship_room_id, feature_id) FROM '/app/data/ship/ship_room_feature.csv' WITH (FORMAT CSV, HEADER true, DELIMITER ',', ENCODING 'UTF8')
EOF
    echo "Successfully imported ship room features"
fi

import_csv "/app/data/ship/ship_room_img.csv" "ship_room_img" "room_id,img_url"

# Import review sample data (optional) - only if hotels/ships exist
if [ -f "/app/sql/review_sample_data.sql" ]; then
    # Check if we have hotels or ships before importing reviews
    HOTEL_COUNT_AFTER=$(psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -tAc "SELECT COUNT(*) FROM hotel;" 2>/dev/null || echo "0")
    SHIP_COUNT_AFTER=$(psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -tAc "SELECT COUNT(*) FROM ship;" 2>/dev/null || echo "0")
    
    if [ "$HOTEL_COUNT_AFTER" -gt 0 ] || [ "$SHIP_COUNT_AFTER" -gt 0 ]; then
        echo "Importing review sample data..."
        echo "Found $HOTEL_COUNT_AFTER hotels and $SHIP_COUNT_AFTER ships, proceeding with review import..."
        execute_sql "/app/sql/review_sample_data.sql"
    else
        echo "Skipping review sample data import: no hotels or ships found in database."
        echo "Reviews require existing hotels/ships to reference."
    fi
fi

echo "=== Data import completed successfully! ==="

unset PGPASSWORD
