-- Sandya Rani Prasadam
-- Tejasree Challagundla 

create database part2;
use part2;
-- drop database part2;
CREATE TABLE topping (
    ToppingID INT AUTO_INCREMENT PRIMARY KEY,
    ToppingName VARCHAR(255) NOT NULL,
    ToppingPricePerUnit DECIMAL(10, 2) NOT NULL,
    ToppingCostPerUnit DECIMAL(10, 2) NOT NULL,
    ToppingInventory INT NOT NULL,
    ToppingMinimum INT NOT NULL,
    ToppingSmall DECIMAL(10, 2) NOT NULL,
    ToppingMedium DECIMAL(10, 2) NOT NULL,
    ToppingLarge DECIMAL(10, 2) NOT NULL,
    ToppingXLarge DECIMAL(10, 2) NOT NULL
);

CREATE TABLE baseprice (
    BasePriceID INT AUTO_INCREMENT PRIMARY KEY,
    BasePriceName VARCHAR(255) NOT NULL,
    BasePriceCrust VARCHAR(255) NOT NULL,
    BasePricePrice DECIMAL(10, 2) NOT NULL,
    BasePriceCost DECIMAL(10, 2) NOT NULL
);

CREATE TABLE customer (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerName VARCHAR(255) NOT NULL,
    CustomerPhoneNumber VARCHAR(15) NOT NULL,
    CustomerAptNo VARCHAR(10),
    CustomerStreet VARCHAR(255),
    CustomerCity VARCHAR(255),
    CustomerState VARCHAR(255),
    CustomerPinCode VARCHAR(10)
);

CREATE TABLE ordertable (
    OrderID INT PRIMARY KEY,
    OrderType VARCHAR(255) NOT NULL,
    OrderTimeStamp TIMESTAMP,
    OrderTotalCost DECIMAL(10, 2),
    OrderTotalPrice DECIMAL(10, 2),
    OrderIsComplete BOOLEAN
);

ALTER TABLE ordertable
ADD COLUMN CustomerID INT;
ALTER TABLE ordertable
ADD CONSTRAINT fk_customer
FOREIGN KEY (CustomerID) REFERENCES customer(CustomerID);


ALTER TABLE ordertable
MODIFY OrderID INT AUTO_INCREMENT;

ALTER TABLE ordertable AUTO_INCREMENT = 1;

CREATE TABLE dinein (
    OrderID INT PRIMARY KEY,
    DineInTableNo INT,
    FOREIGN KEY (OrderID) REFERENCES ordertable (OrderID)
);

CREATE TABLE delivery (
    OrderID INT PRIMARY KEY,
    CustomerID INT,
    FOREIGN KEY (OrderID) REFERENCES ordertable (OrderID),
    FOREIGN KEY (CustomerID) REFERENCES customer (CustomerID)
);

CREATE TABLE pickup (
    OrderID INT PRIMARY KEY,
    CustomerID INT,
    FOREIGN KEY (OrderID) REFERENCES ordertable (OrderID),
    FOREIGN KEY (CustomerID) REFERENCES customer (CustomerID)
);


create table pizza(
PizzaID INT AUTO_INCREMENT PRIMARY KEY,
PizzaBasePrice DECIMAL(10, 2) NOT NULL,
PizzaBaseCost DECIMAL(10, 2) NOT NULL,
PizzaState VARCHAR(255) NOT NULL,
OrderID INT,
BasePriceID INT,
FOREIGN KEY (OrderID) REFERENCES ordertable(OrderID),
FOREIGN KEY (BasePriceID) REFERENCES baseprice(BasePriceID)
);

-- ALTER TABLE pizza
-- ADD COLUMN BasePriceID INT;
-- ALTER TABLE ordertable
-- ADD CONSTRAINT fk_baseprice
-- FOREIGN KEY (BasePriceID) REFERENCES baseprice(BasePriceID);


CREATE TABLE discount (
    DiscountID INT AUTO_INCREMENT PRIMARY KEY,
    DiscountName VARCHAR(255) NOT NULL,
    Amount DECIMAL(10, 2) NOT NULL,
    isPercent Boolean
);

CREATE TABLE pizza_topping (
    PizzaID INT,
    ToppingID INT,
    PRIMARY KEY (PizzaID, ToppingID),
    FOREIGN KEY (PizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY (ToppingID) REFERENCES topping(ToppingID),
    Pizza_ToppingExtraTopping Boolean
);

CREATE TABLE pizza_discount (
    PizzaID INT,
    DiscountID INT,
    PRIMARY KEY (PizzaID, DiscountID),
    FOREIGN KEY (PizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY (DiscountID) REFERENCES discount(DiscountID)
);

CREATE TABLE order_discount (
    OrderID INT,
    DiscountID INT,
    PRIMARY KEY (OrderID, DiscountID),
    FOREIGN KEY (OrderID) REFERENCES ordertable (OrderID),
    FOREIGN KEY (DiscountID) REFERENCES discount (DiscountID)
);

