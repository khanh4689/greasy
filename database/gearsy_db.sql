CREATE TABLE Users (
    UserID SERIAL PRIMARY KEY,
    Email VARCHAR(255) UNIQUE NOT NULL,
    Phone VARCHAR(20),
    Password VARCHAR(255) NOT NULL,
    Name VARCHAR(100),
    Avatar VARCHAR(255),
    Address TEXT,
    Role VARCHAR(10) CHECK (Role IN ('Guest','User','Admin','Staff','Block')) DEFAULT 'User',
    Status VARCHAR(10) CHECK (Status IN ('Active','Inactive')) DEFAULT 'Active',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Suppliers (
    SupplierID SERIAL PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    ContactEmail VARCHAR(255),
    ContactPhone VARCHAR(20),
    Address TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Categories (
    CategoryID SERIAL PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    SupplierID INTEGER REFERENCES Suppliers(SupplierID),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Products (
    ProductID SERIAL PRIMARY KEY,
    CategoryID INTEGER REFERENCES Categories(CategoryID),
    Name VARCHAR(100) NOT NULL,
    Price DECIMAL(12,2) NOT NULL,
    Stock INTEGER NOT NULL CHECK (Stock >= 0),
    Description TEXT,
    Images VARCHAR(255),
    CompatibilitySpecs TEXT,
    IsHidden BOOLEAN DEFAULT FALSE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Orders (
    OrderID SERIAL PRIMARY KEY,
    UserID INTEGER REFERENCES Users(UserID),
    TotalAmount DECIMAL(12,2) NOT NULL,
    OrderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Status VARCHAR(10) CHECK (Status IN ('Pending','Shipped','Delivered','Cancelled')) DEFAULT 'Pending',
    ShippingAddress TEXT,
    ShippingFee DECIMAL(10,2),
    IsHidden BOOLEAN DEFAULT FALSE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Order_Details (
    OrderItemID SERIAL PRIMARY KEY,
    OrderID INTEGER REFERENCES Orders(OrderID) ON DELETE CASCADE,
    ProductID INTEGER REFERENCES Products(ProductID),
    Quantity INTEGER NOT NULL CHECK (Quantity > 0),
    UnitPrice DECIMAL(12,2) NOT NULL
);

CREATE TABLE Carts (
    CartID SERIAL PRIMARY KEY,
    UserID INTEGER UNIQUE NOT NULL REFERENCES Users(UserID),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Cart_Items (
    CartItemID SERIAL PRIMARY KEY,
    CartID INTEGER REFERENCES Carts(CartID) ON DELETE CASCADE,
    ProductID INTEGER REFERENCES Products(ProductID),
    Quantity INTEGER NOT NULL CHECK (Quantity > 0)
);

CREATE TABLE Payments (
    PaymentID SERIAL PRIMARY KEY,
    OrderID INTEGER REFERENCES Orders(OrderID),
    PaymentMethod VARCHAR(10) CHECK (PaymentMethod IN ('VNPay','COD')) NOT NULL,
    PaymentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PaymentStatus VARCHAR(10) CHECK (PaymentStatus IN ('Pending','Completed','Failed')) DEFAULT 'Pending'
);

CREATE TABLE Reviews (
    ReviewID SERIAL PRIMARY KEY,
    UserID INTEGER REFERENCES Users(UserID),
    ProductID INTEGER REFERENCES Products(ProductID),
    Rating INTEGER CHECK (Rating BETWEEN 1 AND 5),
    Comment TEXT,
    Status VARCHAR(10) CHECK (Status IN ('Approved','Hidden','Pending')) DEFAULT 'Pending',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Favorites (
    FavoriteID SERIAL PRIMARY KEY,
    UserID INTEGER REFERENCES Users(UserID),
    ProductID INTEGER REFERENCES Products(ProductID),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(UserID, ProductID)
);

CREATE TABLE Promotions (
    PromotionID SERIAL PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    DiscountPercent DECIMAL(5,2) CHECK (DiscountPercent BETWEEN 0 AND 100),
    StartDate TIMESTAMP,
    EndDate TIMESTAMP,
    Status VARCHAR(10) CHECK (Status IN ('Active','Inactive')) DEFAULT 'Active'
);

CREATE TABLE Product_Promotions (
    ProductPromotionID SERIAL PRIMARY KEY,
    ProductID INTEGER REFERENCES Products(ProductID),
    PromotionID INTEGER REFERENCES Promotions(PromotionID)
);

CREATE TABLE Coupons (
    CouponID SERIAL PRIMARY KEY,
    Code VARCHAR(50) UNIQUE NOT NULL,
    DiscountPercent DECIMAL(5,2) CHECK (DiscountPercent BETWEEN 0 AND 100),
    MinOrderAmount DECIMAL(12,2) DEFAULT 0,
    MaxUses INTEGER DEFAULT 1,
    UsedCount INTEGER DEFAULT 0,
    StartDate TIMESTAMP,
    EndDate TIMESTAMP,
    Status VARCHAR(10) CHECK (Status IN ('Active','Inactive')) DEFAULT 'Active'
);

CREATE TABLE User_Coupons (
    UserCouponID SERIAL PRIMARY KEY,
    UserID INTEGER REFERENCES Users(UserID),
    CouponID INTEGER REFERENCES Coupons(CouponID),
    UsedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    OrderID INTEGER REFERENCES Orders(OrderID)
);

CREATE TABLE Banners (
    BannerID SERIAL PRIMARY KEY,
    ImageURL VARCHAR(255),
    Content TEXT,
    Position VARCHAR(50),
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Favorites
ALTER TABLE Favorites
DROP CONSTRAINT Favorites_userid_fkey,
ADD FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE;

ALTER TABLE Favorites
DROP CONSTRAINT Favorites_productid_fkey,
ADD FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE;

-- Product_Promotions
ALTER TABLE Product_Promotions
DROP CONSTRAINT Product_Promotions_productid_fkey,
ADD FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE;

ALTER TABLE Product_Promotions
DROP CONSTRAINT Product_Promotions_promotionid_fkey,
ADD FOREIGN KEY (PromotionID) REFERENCES Promotions(PromotionID) ON DELETE CASCADE;

-- User_Coupons
ALTER TABLE User_Coupons
DROP CONSTRAINT User_Coupons_userid_fkey,
ADD FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE;

ALTER TABLE User_Coupons
DROP CONSTRAINT User_Coupons_couponid_fkey,
ADD FOREIGN KEY (CouponID) REFERENCES Coupons(CouponID) ON DELETE CASCADE;

-- Cart_Items
ALTER TABLE Cart_Items
DROP CONSTRAINT Cart_Items_cartid_fkey,
ADD FOREIGN KEY (CartID) REFERENCES Carts(CartID) ON DELETE CASCADE;

ALTER TABLE Cart_Items
DROP CONSTRAINT Cart_Items_productid_fkey,
ADD FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE;

-- Order_Details (đã có)
-- Payments
ALTER TABLE Payments
DROP CONSTRAINT Payments_orderid_fkey,
ADD FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE;

-- Truy vấn sản phẩm theo danh mục
CREATE INDEX idx_products_category ON Products(CategoryID);

-- Truy vấn đơn hàng theo người dùng
CREATE INDEX idx_orders_user ON Orders(UserID);

-- Truy vấn đánh giá theo sản phẩm
CREATE INDEX idx_reviews_product ON Reviews(ProductID);

-- Truy vấn giỏ hàng của user
CREATE INDEX idx_carts_user ON Carts(UserID);

-- Truy vấn chi tiết giỏ hàng theo cart
CREATE INDEX idx_cart_items_cart ON Cart_Items(CartID);

-- Truy vấn đơn hàng của chi tiết đơn hàng
CREATE INDEX idx_order_details_order ON Order_Details(OrderID);

-- Truy vấn mã khuyến mãi theo sản phẩm
CREATE INDEX idx_product_promotions_product ON Product_Promotions(ProductID);

-- Truy vấn mã coupon của người dùng
CREATE INDEX idx_user_coupons_user ON User_Coupons(UserID);

-- Truy vấn đơn hàng trong payments
CREATE INDEX idx_payments_order ON Payments(OrderID);

CREATE OR REPLACE FUNCTION decrease_stock_after_order()
RETURNS TRIGGER AS $$
BEGIN
  -- Kiểm tra tồn kho đủ
  IF (SELECT Stock FROM Products WHERE ProductID = NEW.ProductID) < NEW.Quantity THEN
    RAISE EXCEPTION 'Not enough stock for product ID %', NEW.ProductID;
  END IF;

  -- Trừ tồn kho
  UPDATE Products
  SET Stock = Stock - NEW.Quantity
  WHERE ProductID = NEW.ProductID;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

INSERT INTO Users (Email, Phone, Password, Name, Avatar, Address, Role, Status, CreatedAt) VALUES
('john.doe@example.com', '555-0101', '$2b$10$K8Zx9z3f9v7Y6Xb2Q4W7.u9T5z8Y6X7W8Q9T0U1V2W3X4Y5Z6A7B8', 'John Doe', 'avatars/1.jpg', '123 Main St, New York, NY', 'User', 'Active', '2025-06-01 10:00:00'),
('jane.smith@example.com', '555-0102', '$2b$10$L9A0B1C2D3E4F5G6H7I8J9.K0L1M2N3O4P5Q6R7S8T9U0V1W2X3Y4', 'Jane Smith', 'avatars/1.jpg', '456 Oak Ave, Los Angeles, CA', 'Admin', 'Active', '2025-06-02 11:00:00'),
('alice.brown@example.com', '555-0103', '$2b$10$M0N1O2P3Q4R5S6T7U8V9W0.X1Y2Z3A4B5C6D7E8F9G0H1I2J3K4L5', 'Alice Brown', NULL, '789 Pine Rd, Chicago, IL', 'Staff', 'Active', '2025-06-03 12:00:00'),
('bob.jones@example.com', '555-0104', '$2b$10$N1O2P3Q4R5S6T7U8V9W0X1.Y2Z3A4B5C6D7E8F9G0H1I2J3K4L5M6', 'Bob Jones', 'avatars/1.jpg', '321 Elm St, Houston, TX', 'User', 'Inactive', '2025-06-04 13:00:00'),
('emma.wilson@example.com', '555-0105', '$2b$10$O2P3Q4R5S6T7U8V9W0X1Y2.Z3A4B5C6D7E8F9G0H1I2J3K4L5M6N7', 'Emma Wilson', NULL, '654 Cedar Ln, Miami, FL', 'Guest', 'Active', '2025-06-05 14:00:00'),
('david.martin@example.com', '555-0106', '$2b$10$P3Q4R5S6T7U8V9W0X1Y2Z3.A4B5C6D7E8F9G0H1I2J3K4L5M6N7O8', 'David Martin', 'avatars/1.jpg', '987 Birch Dr, Seattle, WA', 'User', 'Active', '2025-06-06 15:00:00'),
('sarah.taylor@example.com', '555-0107', '$2b$10$Q4R5S6T7U8V9W0X1Y2Z3A4.B5C6D7E8F9G0H1I2J3K4L5M6N7O8P9', 'Sarah Taylor', NULL, '147 Maple St, Boston, MA', 'Staff', 'Active', '2025-06-07 16:00:00'),
('michael.lee@example.com', '555-0108', '$2b$10$R5S6T7U8V9W0X1Y2Z3A4B5.C6D7E8F9G0H1I2J3K4L5M6N7O8P9Q0', 'Michael Lee', 'avatars/1.jpg', '258 Walnut Ave, San Francisco, CA', 'User', 'Active', '2025-06-08 17:00:00'),
('linda.moore@example.com', '555-0109', '$2b$10$S6T7U8V9W0X1Y2Z3A4B5C6.D7E8F9G0H1I2J3K4L5M6N7O8P9Q0R1', 'Linda Moore', NULL, '369 Spruce Ct, Denver, CO', 'Block', 'Inactive', '2025-06-09 18:00:00'),
('chris.evans@example.com', '555-0110', '$2b$10$T7U8V9W0X1Y2Z3A4B5C6D7.E8F9G0H1I2J3K4L5M6N7O8P9Q0R1S2', 'Chris Evans', 'avatars/1.jpg', '741 Oakwood Dr, Phoenix, AZ', 'User', 'Active', '2025-06-10 19:00:00');
INSERT INTO Suppliers (Name, ContactEmail, ContactPhone, Address, CreatedAt, UpdatedAt) VALUES
('TechTrend Innovations', 'contact@techtrend.com', '555-0201', '100 Industrial Way, San Jose, CA', '2025-06-01 09:00:00', '2025-06-01 09:00:00'),
('GadgetWorld Ltd', 'info@gadgetworld.com', '555-0202', '200 Tech Park, Austin, TX', '2025-06-02 09:00:00', '2025-06-02 09:00:00'),
('ElectroMart', 'support@electromart.com', '555-0203', '300 Circuit Dr, Boston, MA', '2025-06-03 09:00:00', '2025-06-03 09:00:00'),
('FutureTech Supplies', 'sales@futuretech.com', '555-0204', '400 Silicon Rd, Seattle, WA', '2025-06-04 09:00:00', '2025-06-04 09:00:00'),
('SmartGear Co', 'info@smartgear.com', '555-0205', '500 Innovation St, Chicago, IL', '2025-06-05 09:00:00', '2025-06-05 09:00:00'),
('TechBit', 'contact@techbit.com', '555-0206', '600 Tech Ave, New York, NY', '2025-06-06 09:00:00', '2025-06-06 09:00:00'),
('GizmoZone', 'support@gizmozone.com', '555-0207', '700 Gadget Ln, Miami, FL', '2025-06-07 09:00:00', '2025-06-07 09:00:00'),
('ElectroHub', 'sales@electrohub.com', '555-0208', '800 Circuit Ct, Denver, CO', '2025-06-08 09:00:00', '2025-06-08 09:00:00'),
('InnovateTech', 'info@innovatetech.com', '555-0209', '900 Tech Dr, Houston, TX', '2025-06-09 09:00:00', '2025-06-09 09:00:00'),
('NextGen Supplies', 'contact@nextgen.com', '555-0210', '1000 Future Rd, Los Angeles, CA', '2025-06-10 09:00:00', '2025-06-10 09:00:00');
INSERT INTO Categories (Name, SupplierID, CreatedAt, UpdatedAt) VALUES
('Smartphones', 1, '2025-06-01 10:00:00', '2025-06-01 10:00:00'),
('Laptops', 2, '2025-06-02 10:00:00', '2025-06-02 10:00:00'),
('Tablets', 3, '2025-06-03 10:00:00', '2025-06-03 10:00:00'),
('CPU', 4, '2025-06-04 10:00:00', '2025-06-04 10:00:00'),
('Ram', 5, '2025-06-05 10:00:00', '2025-06-05 10:00:00'),
('Audio Devices', 6, '2025-06-06 10:00:00', '2025-06-06 10:00:00'),
('Cameras', 7, '2025-06-07 10:00:00', '2025-06-07 10:00:00'),
('Gaming Consoles', 8, '2025-06-08 10:00:00', '2025-06-08 10:00:00'),
('Smart Home', 9, '2025-06-09 10:00:00', '2025-06-09 10:00:00'),
('Networking', 10, '2025-06-10 10:00:00', '2025-06-10 10:00:00');
INSERT INTO Products (CategoryID, Name, Price, Stock, Description, Images, CompatibilitySpecs, IsHidden, CreatedAt, UpdatedAt) VALUES
(1, 'Realme C61', 699.99, 50, 'Latest 5G smartphone with 128GB storage', 'images/1.jpg', 'Android 13, 6.5" display', FALSE, '2025-06-01 11:00:00', '2025-06-01 11:00:00'),
(1, 'Samsung Galaxy A06 5G', 799.99, 30, 'Premium smartphone with 256GB storage', 'images/2.jpg', 'iOS 16, 6.7" display', FALSE, '2025-06-02 11:00:00', '2025-06-02 11:00:00'),
(2, 'Laptop HP 245 G10', 1299.99, 20, 'High-performance laptop with 16GB RAM', 'images/3.jpg', 'Windows 11, i7 processor', FALSE, '2025-06-03 11:00:00', '2025-06-03 11:00:00'),
(2, 'Laptop Gaming Acer Nitro V ANV15-51-58AN', 999.99, 25, 'Lightweight laptop with 8GB RAM', 'images/4.jpg', 'macOS Ventura, M2 chip', FALSE, '2025-06-04 11:00:00', '2025-06-04 11:00:00'),
(3, 'Samsung Galaxy Tab A9+', 499.99, 40, '10.2" tablet with 64GB storage', 'images/5.jpg', 'iPadOS 16, A13 Bionic', FALSE, '2025-06-05 11:00:00', '2025-06-05 11:00:00'),
(4, 'Intel Core Ultra 9', 29.99, 100, 'Fast wireless charger for smartphones', 'images/6.jpg', 'Qi-compatible', FALSE, '2025-06-06 11:00:00', '2025-06-06 11:00:00'),
(5, 'RAM Kingston Fury Beast RGB', 199.99, 60, 'Fitness tracker with heart rate monitor', 'images/7.jpg', 'Compatible with Android/iOS', FALSE, '2025-06-07 11:00:00', '2025-06-07 11:00:00'),
(6, 'Wireless Earbuds', 89.99, 80, 'True wireless earbuds with noise cancellation', 'images/8.jpg', 'Bluetooth 5.0', FALSE, '2025-06-08 11:00:00', '2025-06-08 11:00:00'),
(7, 'DSLR Camera', 599.99, 15, '24MP camera with 18-55mm lens', 'images/9.jpg', 'Canon EF mount', FALSE, '2025-06-09 11:00:00', '2025-06-09 11:00:00'),
(8, 'Gaming Console X', 499.99, 10, 'Next-gen gaming console with 1TB storage', 'images/10.jpg', '4K, 120fps', FALSE, '2025-06-10 11:00:00', '2025-06-10 11:00:00');
INSERT INTO Orders (UserID, TotalAmount, OrderDate, Status, ShippingAddress, ShippingFee, IsHidden, CreatedAt, UpdatedAt) VALUES
(1, 729.98, '2025-06-11 10:00:00', 'Pending', '123 Main St, New York, NY', 29.99, FALSE, '2025-06-11 10:00:00', '2025-06-11 10:00:00'),
(2, 1299.99, '2025-06-12 10:00:00', 'Shipped', '456 Oak Ave, Los Angeles, CA', 0.00, FALSE, '2025-06-12 10:00:00', '2025-06-12 10:00:00'),
(3, 529.98, '2025-06-13 10:00:00', 'Delivered', '789 Pine Rd, Chicago, IL', 29.99, FALSE, '2025-06-13 10:00:00', '2025-06-13 10:00:00'),
(4, 199.99, '2025-06-14 10:00:00', 'Cancelled', '321 Elm St, Houston, TX', 15.00, FALSE, '2025-06-14 10:00:00', '2025-06-14 10:00:00'),
(5, 89.99, '2025-06-15 10:00:00', 'Pending', '654 Cedar Ln, Miami, FL', 10.00, FALSE, '2025-06-15 10:00:00', '2025-06-15 10:00:00'),
(6, 1599.98, '2025-06-16 10:00:00', 'Shipped', '987 Birch Dr, Seattle, WA', 49.99, FALSE, '2025-06-16 10:00:00', '2025-06-16 10:00:00'),
(7, 599.99, '2025-06-17 10:00:00', 'Delivered', '147 Maple St, Boston, MA', 0.00, FALSE, '2025-06-17 10:00:00', '2025-06-17 10:00:00'),
(8, 999.99, '2025-06-18 10:00:00', 'Pending', '258 Walnut Ave, San Francisco, CA', 25.00, FALSE, '2025-06-18 10:00:00', '2025-06-18 10:00:00'),
(9, 29.99, '2025-06-19 10:00:00', 'Cancelled', '369 Spruce Ct, Denver, CO', 5.00, FALSE, '2025-06-19 10:00:00', '2025-06-19 10:00:00'),
(10, 499.99, '2025-06-20 10:00:00', 'Shipped', '741 Oakwood Dr, Phoenix, AZ', 20.00, FALSE, '2025-06-20 10:00:00', '2025-06-20 10:00:00');
INSERT INTO Order_Details (OrderID, ProductID, Quantity, UnitPrice) VALUES
(1, 1, 1, 699.99),
(2, 3, 1, 1299.99),
(3, 5, 1, 499.99),
(4, 7, 1, 199.99),
(5, 8, 1, 89.99),
(6, 2, 2, 799.99),
(7, 9, 1, 599.99),
(8, 4, 1, 999.99),
(9, 6, 1, 29.99),
(10, 10, 1, 499.99);
INSERT INTO Carts (UserID, CreatedAt) VALUES
(1, '2025-06-01 12:00:00'),
(2, '2025-06-02 12:00:00'),
(3, '2025-06-03 12:00:00'),
(4, '2025-06-04 12:00:00'),
(5, '2025-06-05 12:00:00'),
(6, '2025-06-06 12:00:00'),
(7, '2025-06-07 12:00:00'),
(8, '2025-06-08 12:00:00'),
(9, '2025-06-09 12:00:00'),
(10, '2025-06-10 12:00:00');
INSERT INTO Cart_Items (CartID, ProductID, Quantity) VALUES
(1, 1, 2),
(2, 3, 1),
(3, 5, 3),
(4, 7, 1),
(5, 8, 2),
(6, 2, 1),
(7, 9, 1),
(8, 4, 2),
(9, 6, 5),
(10, 10, 1);
INSERT INTO Payments (OrderID, PaymentMethod, PaymentDate, PaymentStatus) VALUES
(1, 'VNPay', '2025-06-11 10:05:00', 'Pending'),
(2, 'COD', '2025-06-12 10:05:00', 'Completed'),
(3, 'VNPay', '2025-06-13 10:05:00', 'Completed'),
(4, 'VNPay', '2025-06-14 10:05:00', 'Failed'),
(5, 'COD', '2025-06-15 10:05:00', 'Pending'),
(6, 'VNPay', '2025-06-16 10:05:00', 'Completed'),
(7, 'COD', '2025-06-17 10:05:00', 'Completed'),
(8, 'VNPay', '2025-06-18 10:05:00', 'Pending'),
(9, 'COD', '2025-06-19 10:05:00', 'Failed'),
(10, 'VNPay', '2025-06-20 10:05:00', 'Completed');
INSERT INTO Reviews (UserID, ProductID, Rating, Comment, Status, CreatedAt, UpdatedAt) VALUES
(1, 1, 5, 'Great phone, fast delivery!', 'Approved', '2025-06-11 11:00:00', '2025-06-11 11:00:00'),
(2, 3, 4, 'Good laptop but a bit pricey.', 'Approved', '2025-06-12 11:00:00', '2025-06-12 11:00:00'),
(3, 5, 3, 'Tablet is okay, battery life could be better.', 'Pending', '2025-06-13 11:00:00', '2025-06-13 11:00:00'),
(4, 7, 5, 'Love the smartwatch, very stylish!', 'Approved', '2025-06-14 11:00:00', '2025-06-14 11:00:00'),
(5, 8, 4, 'Earbuds have great sound quality.', 'Approved', '2025-06-15 11:00:00', '2025-06-15 11:00:00'),
(6, 2, 5, 'Amazing smartphone, highly recommend!', 'Pending', '2025-06-16 11:00:00', '2025-06-16 11:00:00'),
(7, 9, 4, 'Camera takes great photos.', 'Approved', '2025-06-17 11:00:00', '2025-06-17 11:00:00'),
(8, 4, 3, 'Laptop is good but overheats sometimes.', 'Hidden', '2025-06-18 11:00:00', '2025-06-18 11:00:00'),
(9, 6, 5, 'Charger works perfectly.', 'Approved', '2025-06-19 11:00:00', '2025-06-19 11:00:00'),
(10, 10, 4, 'Console is awesome, but games are expensive.', 'Approved', '2025-06-20 11:00:00', '2025-06-20 11:00:00');
INSERT INTO Favorites (UserID, ProductID, CreatedAt) VALUES
(1, 1, '2025-06-01 13:00:00'),
(2, 3, '2025-06-02 13:00:00'),
(3, 5, '2025-06-03 13:00:00'),
(4, 7, '2025-06-04 13:00:00'),
(5, 8, '2025-06-05 13:00:00'),
(6, 2, '2025-06-06 13:00:00'),
(7, 9, '2025-06-07 13:00:00'),
(8, 4, '2025-06-08 13:00:00'),
(9, 6, '2025-06-09 13:00:00'),
(10, 10, '2025-06-10 13:00:00');
INSERT INTO Promotions (Name, DiscountPercent, StartDate, EndDate, Status) VALUES
('Summer Sale', 20.00, '2025-06-01 00:00:00', '2025-06-30 23:59:59', 'Active'),
('Back to School', 15.00, '2025-08-01 00:00:00', '2025-09-15 23:59:59', 'Inactive'),
('Black Friday', 30.00, '2025-11-25 00:00:00', '2025-11-30 23:59:59', 'Inactive'),
('Cyber Monday', 25.00, '2025-12-01 00:00:00', '2025-12-02 23:59:59', 'Inactive'),
('New Year Deal', 10.00, '2025-01-01 00:00:00', '2025-01-15 23:59:59', 'Inactive'),
('Spring Flash', 15.00, '2025-03-01 00:00:00', '2025-03-31 23:59:59', 'Inactive'),
('Tech Fest', 20.00, '2025-06-15 00:00:00', '2025-06-20 23:59:59', 'Active'),
('Holiday Special', 35.00, '2025-12-15 00:00:00', '2025-12-31 23:59:59', 'Inactive'),
('Mid-Year Sale', 25.00, '2025-07-01 00:00:00', '2025-07-15 23:59:59', 'Inactive'),
('Clearance Sale', 40.00, '2025-06-10 00:00:00', '2025-06-30 23:59:59', 'Active');
INSERT INTO Product_Promotions (ProductID, PromotionID) VALUES
(1, 1),
(2, 1),
(3, 2),
(4, 2),
(5, 3),
(6, 4),
(7, 5),
(8, 7),
(9, 8),
(10, 10);
INSERT INTO Coupons (Code, DiscountPercent, MinOrderAmount, MaxUses, UsedCount, StartDate, EndDate, Status) VALUES
('SUMMER25', 25.00, 100.00, 100, 10, '2025-06-01 00:00:00', '2025-06-30 23:59:59', 'Active'),
('SCHOOL15', 15.00, 200.00, 50, 5, '2025-08-01 00:00:00', '2025-09-15 23:59:59', 'Inactive'),
('BF30', 30.00, 300.00, 200, 50, '2025-11-25 00:00:00', '2025-11-30 23:59:59', 'Inactive'),
('CM25', 25.00, 150.00, 150, 20, '2025-12-01 00:00:00', '2025-12-02 23:59:59', 'Inactive'),
('NY10', 10.00, 50.00, 100, 0, '2025-01-01 00:00:00', '2025-01-15 23:59:59', 'Inactive'),
('SPRING20', 20.00, 100.00, 80, 15, '2025-03-01 00:00:00', '2025-03-31 23:59:59', 'Inactive'),
('TECH15', 15.00, 200.00, 60, 10, '2025-06-15 00:00:00', '2025-06-20 23:59:59', 'Active'),
('HOLIDAY35', 35.00, 500.00, 50, 5, '2025-12-15 00:00:00', '2025-12-31 23:59:59', 'Inactive'),
('MIDYEAR25', 25.00, 250.00, 100, 25, '2025-07-01 00:00:00', '2025-07-15 23:59:59', 'Inactive'),
('CLEAR40', 40.00, 300.00, 200, 30, '2025-06-10 00:00:00', '2025-06-30 23:59:59', 'Active');
INSERT INTO User_Coupons (UserID, CouponID, UsedAt, OrderID) VALUES
(1, 1, '2025-06-11 10:10:00', 1),
(2, 2, '2025-06-12 10:10:00', 2),
(3, 3, '2025-06-13 10:10:00', 3),
(4, 4, '2025-06-14 10:10:00', 4),
(5, 5, '2025-06-15 10:10:00', 5),
(6, 6, '2025-06-16 10:10:00', 6),
(7, 7, '2025-06-17 10:10:00', 7),
(8, 8, '2025-06-18 10:10:00', 8),
(9, 9, '2025-06-19 10:10:00', 9),
(10, 10, '2025-06-20 10:10:00', 10);
INSERT INTO User_Coupons (UserID, CouponID, UsedAt, OrderID) VALUES
(1, 1, '2025-06-11 10:10:00', 1),
(2, 2, '2025-06-12 10:10:00', 2),
(3, 3, '2025-06-13 10:10:00', 3),
(4, 4, '2025-06-14 10:10:00', 4),
(5, 5, '2025-06-15 10:10:00', 5),
(6, 6, '2025-06-16 10:10:00', 6),
(7, 7, '2025-06-17 10:10:00', 7),
(8, 8, '2025-06-18 10:10:00', 8),
(9, 9, '2025-06-19 10:10:00', 9),
(10, 10, '2025-06-20 10:10:00', 10);
INSERT INTO Banners (ImageURL, Content, Position, IsActive, CreatedAt, UpdatedAt) VALUES
('banners/summer.jpg', 'Summer Sale: Up to 25% Off!', 'Homepage Top', TRUE, '2025-06-01 14:00:00', '2025-06-01 14:00:00'),
('banners/laptop.jpg', 'New Laptops Arrived!', 'Category Page', TRUE, '2025-06-02 14:00:00', '2025-06-02 14:00:00'),
('banners/tablet.jpg', 'Get Your Tablet Today!', 'Homepage Middle', TRUE, '2025-06-03 14:00:00', '2025-06-03 14:00:00'),
('banners/watch.jpg', 'Smartwatches at Great Prices', 'Sidebar', TRUE, '2025-06-04 14:00:00', '2025-06-04 14:00:00'),
('banners/earbuds.jpg', 'Wireless Earbuds Sale', 'Footer', TRUE, '2025-06-05 14:00:00', '2025-06-05 14:00:00'),
('banners/camera.jpg', 'Capture Moments with Our Cameras', 'Homepage Bottom', FALSE, '2025-06-06 14:00:00', '2025-06-06 14:00:00'),
('banners/console.jpg', 'Gaming Consoles on Sale', 'Category Page', TRUE, '2025-06-07 14:00:00', '2025-06-07 14:00:00'),
('banners/charger.jpg', 'Fast Chargers for All Devices', 'Sidebar', TRUE, '2025-06-08 14:00:00', '2025-06-08 14:00:00'),
('banners/smarthome.jpg', 'Smart Home Devices Discount', 'Homepage Top', TRUE, '2025-06-09 14:00:00', '2025-06-09 14:00:00'),
('banners/network.jpg', 'Boost Your Network Today', 'Footer', FALSE, '2025-06-10 14:00:00', '2025-06-10 14:00:00');