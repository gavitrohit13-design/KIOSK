-- =========================================================================
-- RPG AUTO PRINT - Comprehensive Database Schema (PostgreSQL)
-- Matches Production Multi-tier Architecture (Web Kiosk, Desktop, Mobile)
-- =========================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. USERS & ROLES
CREATE TYPE user_role AS ENUM ('ADMIN', 'STAFF', 'CUSTOMER');

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    full_name VARCHAR(120),
    role user_role DEFAULT 'CUSTOMER',
    password_hash VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. SHOPS & BILLING CONFIGURATION
CREATE TABLE shops (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    shop_name VARCHAR(150) NOT NULL DEFAULT 'RPG AUTO PRINT',
    tagline VARCHAR(200) DEFAULT 'Scan • Upload • Pay • Print',
    address TEXT,
    phone VARCHAR(25),
    whatsapp VARCHAR(25),
    email VARCHAR(120),
    gst_number VARCHAR(30),
    upi_id VARCHAR(80),
    bank_details TEXT,
    business_hours VARCHAR(120),
    bw_price_per_page NUMERIC(8, 2) DEFAULT 10.00,
    color_price_per_page NUMERIC(8, 2) DEFAULT 20.00,
    gst_percentage NUMERIC(5, 2) DEFAULT 18.00,
    discount_percentage NUMERIC(5, 2) DEFAULT 0.00,
    service_charge NUMERIC(8, 2) DEFAULT 2.00,
    min_order_amount NUMERIC(8, 2) DEFAULT 10.00,
    auto_print_enabled BOOLEAN DEFAULT TRUE,
    default_printer_id VARCHAR(80),
    language VARCHAR(10) DEFAULT 'en',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. HARDWARE PRINTERS
CREATE TYPE printer_type AS ENUM ('LASER', 'INKJET', 'THERMAL', 'PLOTTER');
CREATE TYPE printer_conn AS ENUM ('USB', 'NETWORK', 'WIFI');
CREATE TYPE printer_status AS ENUM ('ONLINE', 'OFFLINE', 'PRINTING', 'OUT_OF_PAPER', 'PAUSED');

CREATE TABLE printers (
    id VARCHAR(80) PRIMARY KEY,
    shop_id UUID NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    name VARCHAR(120) NOT NULL,
    type printer_type DEFAULT 'LASER',
    connection_type printer_conn DEFAULT 'NETWORK',
    ip_address VARCHAR(100),
    color_supported BOOLEAN DEFAULT FALSE,
    duplex_supported BOOLEAN DEFAULT TRUE,
    supported_sizes VARCHAR(50)[] DEFAULT ARRAY['A4', 'LEGAL'],
    status printer_status DEFAULT 'ONLINE',
    paper_level_percent INT DEFAULT 100,
    ink_level_percent INT DEFAULT 100,
    queue_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. PRINT ORDERS & JOBS
CREATE TYPE order_status AS ENUM ('PENDING', 'ACCEPTED', 'PRINTING', 'COMPLETED', 'CANCELLED');
CREATE TYPE payment_status AS ENUM ('PENDING', 'PAID', 'FAILED', 'REFUNDED');
CREATE TYPE payment_method AS ENUM ('UPI', 'QR', 'GPAY', 'PHONEPE', 'PAYTM', 'RAZORPAY', 'CASH');

CREATE TABLE orders (
    id VARCHAR(50) PRIMARY KEY, -- e.g. ORD-102938
    shop_id UUID REFERENCES shops(id) ON DELETE SET NULL,
    customer_id UUID REFERENCES users(id) ON DELETE SET NULL,
    customer_name VARCHAR(120),
    customer_phone VARCHAR(25),
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    storage_path TEXT,
    page_count INT NOT NULL DEFAULT 1,
    copies INT NOT NULL DEFAULT 1,
    color_mode VARCHAR(20) DEFAULT 'BW', -- 'BW' or 'COLOR'
    duplex_mode VARCHAR(20) DEFAULT 'SINGLE', -- 'SINGLE' or 'DOUBLE'
    paper_size VARCHAR(20) DEFAULT 'A4', -- 'A4', 'A3', 'LEGAL'
    page_range VARCHAR(50) DEFAULT 'ALL',
    orientation VARCHAR(20) DEFAULT 'PORTRAIT',
    scale_mode VARCHAR(30) DEFAULT 'FIT_TO_PAGE',
    print_quality VARCHAR(30) DEFAULT 'NORMAL',
    subtotal NUMERIC(10, 2) NOT NULL,
    gst_amount NUMERIC(10, 2) DEFAULT 0.00,
    service_charge NUMERIC(10, 2) DEFAULT 0.00,
    discount_amount NUMERIC(10, 2) DEFAULT 0.00,
    total_amount NUMERIC(10, 2) NOT NULL,
    payment_method payment_method DEFAULT 'UPI',
    payment_status payment_status DEFAULT 'PENDING',
    payment_txn_id VARCHAR(120),
    order_status order_status DEFAULT 'PENDING',
    assigned_printer_id VARCHAR(80) REFERENCES printers(id) ON DELETE SET NULL,
    is_auto_printed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. PAYMENTS & INVOICES
CREATE TABLE invoices (
    id VARCHAR(60) PRIMARY KEY, -- INV-2026-XXXX
    order_id VARCHAR(50) NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    invoice_number VARCHAR(60) UNIQUE NOT NULL,
    customer_name VARCHAR(120),
    subtotal NUMERIC(10, 2) NOT NULL,
    cgst NUMERIC(10, 2) DEFAULT 0.00,
    sgst NUMERIC(10, 2) DEFAULT 0.00,
    total_amount NUMERIC(10, 2) NOT NULL,
    payment_mode VARCHAR(30),
    payment_reference VARCHAR(120),
    generated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. AUDIT & ACTIVITY LOGS
CREATE TABLE activity_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id VARCHAR(50) REFERENCES orders(id) ON DELETE CASCADE,
    action VARCHAR(80) NOT NULL,
    details JSONB,
    performed_by VARCHAR(120) DEFAULT 'SYSTEM',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- INDEXES FOR ULTRA-FAST QUEUE QUERIES
CREATE INDEX idx_orders_status ON orders(order_status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_printers_status ON printers(status);
