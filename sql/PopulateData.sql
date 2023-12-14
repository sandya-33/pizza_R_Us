-- Sandya Rani Prasadam
-- Tejasree Challagundla
use part2;


-- DISCOUNT TABLE

INSERT INTO discount(DiscountName, Amount, isPercent) 
VALUES("Employee", 15.0, true), 
 ("Lunch Special Medium", 1.0, false),
 ("Lunch Special Large", 2.0, false),
 ("Specialty Pizza", 1.50, false),
 ("Happy Hour", 10.0, true),
 ("Gameday Special", 20.0, true);
 
 -- select * from discpunt;
 
 -- BASE PRICE TABLE
-- SET SQL_SAFE_UPDATES = 0;
-- delete from discount;
-- SET SQL_SAFE_UPDATES = 1;

INSERT INTO baseprice(BasePriceName,BasePriceCrust,BasePricePrice,BasePriceCost) 
VALUES('Small', 'Thin', 3.0, 0.5),
('Small', 'Original', 3.0, 0.75),
('Small', 'Pan', 3.5, 1.0),
('Small', 'Gluten-Free', 4.0, 2.0),
('Medium', 'Thin', 5.0, 1.0),
('Medium', 'Original', 5.0, 1.5),
('Medium', 'Pan', 6.0, 2.25),
('Medium', 'Gluten-Free', 6.25, 3.0),
('Large', 'Thin', 8.0, 1.25),
('Large', 'Original', 8.0, 2.0),
('Large', 'Pan', 9.0, 3.0),
('Large', 'Gluten-Free', 9.5, 4.0),
('XLarge', 'Thin', 10.0, 2.0),
('XLarge', 'Original', 10.0, 3.0),
('XLarge', 'Pan', 11.5, 4.5),
('XLarge', 'Glutan-Free', 12.5, 6.0);

 -- select * from baseprice;

-- TOPPINGS TABLE

INSERT INTO topping
(ToppingName,
ToppingPricePerUnit,
ToppingCostPerUnit,
ToppingInventory,
ToppingMinimum,
ToppingSmall,
ToppingMedium,
ToppingLarge,
ToppingXLarge
)
VALUES('Pepperoni',1.25,0.2,100,50,2,2.75,3.5,4.5),
('Sausage', 1.25, 0.15, 100, 50, 2.5, 3, 3.5, 4.25),
 ('Ham', 1.5, 0.15, 78, 25, 2, 2.5, 3.25, 4),
 ('Chicken', 1.75, 0.25, 56, 25, 1.5, 2, 2.25, 3),
 ('Green Pepper', 0.5, 0.02, 79, 25, 1, 1.5, 2, 2.5),
 ('Onion', 0.5, 0.02, 85, 25, 1, 1.5, 2, 2.75),
 ('Roma Tomato', 0.75, 0.03, 86, 10, 2, 3, 3.5, 4.5),
 ('Mushrooms', 0.75, 0.1, 52, 50, 1.5, 2, 2.5, 3),
 ('Black Olives', 0.6, 0.1, 39, 25, 0.75, 1, 1.5, 2),
 ('Pineapple', 1, 0.25, 15, 0, 1, 1.25, 1.75, 2),
 ('Jalapenos', 0.5, 0.05, 64, 0, 0.5, 0.75, 1.25, 1.75),
 ('Banana Peppers', 0.5, 0.05, 36, 0, 0.6, 1, 1.3, 1.75),
 ('Regular Cheese',0.5,0.12,250,50,2,3.5,5,7),
 ('Four Cheese Blend',1, 0.15, 150, 25, 2, 3.5, 5, 7),
 ('Feta Cheese', 1.5, 0.18, 75, 0, 1.75, 3, 4, 5.5),
 ('Goat Cheese', 1.5, 0.2, 54, 0, 1.6, 2.75, 4, 5.5),
 ('Bacon',1.5,0.25,89,0,1,1.5,2,3);

 -- select * from topping;
 
 -- customer table
  
insert into customer(CustomerName,CustomerPhoneNumber,CustomerAptNo,CustomerStreet,CustomerCity,CustomerState,CustomerPinCode)
values
('Andrew Wilkes-Krier','864-254-5861',null,'115 Party Blvd', 'Anderson', 'SC', 29621),
('Matt Engers','864-474-9953',null,null,null,null,null),
('Frank Turner','864-232-8944',null,'6745 Wessex St', 'Anderson', 'SC', 29621),
('Milo Auckerman ','864-878-5679', null,'8879 Suburban Home', 'Anderson', 'SC', 29621);

 -- ORDER 1
insert into ordertable(OrderID,
OrderType,
OrderTimestamp,
OrderTotalCost,
OrderTotalPrice,
OrderIsComplete
)
values
(1,"dinein",'2023-03-05 12:03:00',3.68,20.75,TRUE);

insert into dinein(OrderID,DineInTableNo) values(1,21);

insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(20.75,3.68,"completed",1,9);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(1,13,TRUE);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(1,1,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(1,2,False);

insert into pizza_discount(PizzaID,DiscountID)values(1,3);

-- ORDER 2
 insert into ordertable(OrderID,
OrderType,
OrderTimestamp,
OrderTotalCost,
OrderTotalPrice,
OrderIsComplete
)
values
(2,"dinein",'2023-04-03 12:05:00',4.63,19.78,TRUE);

insert into dinein(OrderID,DineInTableNo) values(2,4);

insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(12.85,3.23,"completed",2,7);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(2,15,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(2,9,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(2,7,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(2,8,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(2,12,False);

insert into pizza_discount(PizzaID,DiscountID)values(2,2);
insert into pizza_discount(PizzaID,DiscountID)values(2,4);

insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(6.93,1.40,"completed",2,2);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(3,13,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(3,4,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(3,12,False);

-- ORDER 3
 insert into ordertable(OrderID,
OrderType,
OrderTimestamp,
OrderTotalCost,
OrderTotalPrice,
OrderIsComplete
)
values
(3,"pickup",'2023-03-03 21:30:00',19.8,89.28,TRUE);

insert into pickup(OrderID,CustomerID) values(3,1);

insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(14.88,3.30,"completed",3,10);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(4,13,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(4,1,False);

-- -- hereeeee

insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(14.88,3.30,"completed",3,10);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(5,13,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(5,1,False);
-- --
insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(14.88,3.30,"completed",3,10);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(6,13,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(6,1,False);
-- --
insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(14.88,3.30,"completed",3,10);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(7,13,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(7,1,False);
-- --
insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(14.88,3.30,"completed",3,10);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(8,13,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(8,1,False);
-- --
insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(14.88,3.30,"completed",3,10);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(9,13,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(9,1,False);

-- Order 4

insert into ordertable(OrderID,
OrderType,
OrderTimestamp,
OrderTotalCost,
OrderTotalPrice,
OrderIsComplete
)
values
(4,"delivery",'2023-04-20 19:11:00',23.62,86.19,TRUE);

insert into delivery(OrderID,CustomerID)values(4,1);

insert into order_discount(OrderId,DiscountID) values(4,6);
-- Pizza 1
insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(27.94,9.19,"completed",4,14);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(10,1,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(10,2,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(10,14,False);

-- --Pizza 2

insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(31.50,6.25,"completed",4,14);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(11,3,True);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(11,10,True);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(11,14,False);

insert into pizza_discount(PizzaID,DiscountID)values(11,4);

-- --Pizza 3

insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(26.75,8.18,"completed",4,14);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(12,4,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(12,17,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(12,14,False);

-- ORDER 5

insert into ordertable(OrderID,
OrderType,
OrderTimestamp,
OrderTotalCost,
OrderTotalPrice,
OrderIsComplete
)
values
(5,"pickup",'2023-03-02 17:30:00',7.88,27.45,TRUE);

insert into pickup(OrderID,CustomerID) values(5,2);

insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(27.45,7.88,"completed",5,16);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(13,5,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(13,6,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(13,7,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(13,8,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(13,9,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(13,16,False);

insert into pizza_discount(PizzaID,DiscountID)values(13,4);

-- ORDER 6

insert into ordertable(OrderID,
OrderType,
OrderTimestamp,
OrderTotalCost,
OrderTotalPrice,
OrderIsComplete
)
values
(6,"delivery",'2023-03-02 18:17:00',4.24,25.81,TRUE);

insert into delivery(OrderID,CustomerID)values(6,3);

-- Sandya Rani Prasadam
-- Tejasree Challagundla 

insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(25.81,4.24,"completed",6,9);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(14,4,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(14,5,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(14,6,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(14,8,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(14,14,True);

-- ORDER 7

insert into ordertable(OrderID,
OrderType,
OrderTimestamp,
OrderTotalCost,
OrderTotalPrice,
OrderIsComplete
)
values
(7,"delivery",'2023-04-13 20:32:00',6.00,37.25,TRUE);

insert into delivery(OrderID,CustomerID)values(7,4);

insert into order_discount(OrderId,DiscountID) values(7,1);

-- Pizza 1

insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(18.00,2.75,"completed",7,9);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(15,14,True);

-- Pizza 2
insert into pizza(PizzaBasePrice,PizzaBaseCost,PizzaState,OrderID,BasePriceID)
values(19.25,3.25,"completed",7,9);

insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(16,13,False);
insert into pizza_topping(PizzaID,ToppingID,Pizza_ToppingExtraTopping) values(16,1,True);




