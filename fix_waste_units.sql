-- Fix waste units to be compatible with the backend Enum (KG only)
-- This script updates 'CAN' and 'BOTTLE' units to 'KG' in all relevant tables.

-- Update waste_categories table
UPDATE waste_categories 
SET unit = 'KG' 
WHERE unit IN ('CAN', 'BOTTLE', 'Can', 'Bottle');

-- Update collector_report_items table (historical data)
UPDATE collector_report_items 
SET unit_snapshot = 'KG' 
WHERE unit_snapshot IN ('CAN', 'BOTTLE', 'Can', 'Bottle');

-- Update waste_report_items table (historical data)
UPDATE waste_report_items 
SET unit_snapshot = 'KG' 
WHERE unit_snapshot IN ('CAN', 'BOTTLE', 'Can', 'Bottle');
