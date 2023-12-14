-- Sandya Rani Prasadam
-- Tejasree Challagundla 

-- View ToppingPopularity:
use part2;
CREATE view ToppingPopularity AS
SELECT t.ToppingName as Topping,
    SUM(CASE WHEN pt.PizzaID IS NOT NULL THEN (CASE WHEN pt.Pizza_ToppingExtraTopping = TRUE THEN 2 ELSE 1 END) ELSE 0 END) AS ToppingCount
FROM topping t
Left JOIN pizza_topping pt ON t.ToppingID = pt.ToppingID
GROUP BY t.ToppingName
ORDER BY ToppingCount DESC;

SELECT * FROM ToppingPopularity;

-- drop table ToppingPopularity;


CREATE view ProfitByPizza AS
SELECT
    bp.BasePriceName AS Size,
    bp.BasePriceCrust AS Crust,
    SUM(p.PizzaBasePrice - p.PizzaBaseCost) AS Profit,
    MAX(o.ordertimestamp) AS 'Last Order Date'
FROM
    pizza p
JOIN
    baseprice bp ON p.BasePriceID = bp.BasePriceID
JOIN
    ordertable o ON p.OrderID = o.OrderID
GROUP BY
    bp.BasePriceName, bp.BasePriceCrust
ORDER BY
    Profit DESC;

SELECT * FROM ProfitByPizza;
-- drop table ProfitByPizza;

CREATE view ProfitByOrderType AS
SELECT OrderType AS customerType, DATE_FORMAT (OrderTimestamp, '%c/%Y') AS OrderMonth,
SUM(OrderTotalPrice) AS TotalOrderPrice,
SUM(OrderTotalCost) AS TotalOrderCost,
SUM(OrderTotalPrice)-SUM(OrderTotalCost) AS Profit FROM ordertable GROUP BY CustomerType, OrderMonth
UNION
SELECT '', 'Grand Total',SUM(OrderTotalPrice), SUM(OrderTotalCost), SUM(OrderTotalPrice)-SUM(OrderTotalCost) FROM ordertable;

SELECT * FROM ProfitByOrderType;
-- drop table ProfitByOrderType;



