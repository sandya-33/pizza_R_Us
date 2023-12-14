package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";



	
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	
	public static void addOrder(Order o) throws SQLException, IOException 
	{
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 * 
		 */
		Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = DBConnector.make_connection();
		if (o instanceof DineinOrder) {

        String sql = "INSERT INTO ordertable (OrderType, OrderTimeStamp, OrderTotalCost, OrderTotalPrice, OrderIsComplete) VALUES (?, ?, ?, ?, ?)";
        stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, o.getOrderType());
        stmt.setString(2, o.getDate());
        stmt.setDouble(3, o.getBusPrice());
        stmt.setDouble(4, o.getCustPrice());
        stmt.setInt(5, o.getIsComplete());

        stmt.executeUpdate();

        rs = stmt.getGeneratedKeys();
        int orderId = 0;
        if (rs.next()) {
            orderId = rs.getInt(1);
            o.setOrderID(orderId); 
        }
		try (PreparedStatement ps = conn.prepareStatement("INSERT INTO dinein (OrderID, DineInTableNo) VALUES (?, ?)")) {
                ps.setInt(1, orderId);
                ps.setInt(2, ((DineinOrder) o).getTableNum());
                ps.executeUpdate();
            }
	}
	else{
		String sql = "INSERT INTO ordertable (OrderType, OrderTimeStamp, OrderTotalCost, OrderTotalPrice, OrderIsComplete, CustomerID) VALUES (?, ?, ?, ?, ?, ?)";
        stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, o.getOrderType());
        stmt.setString(2, o.getDate());
        stmt.setDouble(3, o.getBusPrice());
        stmt.setDouble(4, o.getCustPrice());
        stmt.setInt(5, o.getIsComplete());
        stmt.setInt(6, o.getCustID());

        stmt.executeUpdate();

        rs = stmt.getGeneratedKeys();
        int orderId = 0;
        if (rs.next()) {
            orderId = rs.getInt(1);
            o.setOrderID(orderId); 
        }
        if (o.getOrderType()=="delivery") {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO delivery (OrderID, CustomerID) VALUES (?, ?)")) {
                ps.setInt(1, orderId);
                ps.setInt(2, o.getCustID());
                ps.executeUpdate();
            }
        } else {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO pickup (OrderID, CustomerID) VALUES (?, ?)")) {
                ps.setInt(1, orderId);
                ps.setInt(2, o.getCustID());
                ps.executeUpdate();
            }
        }
    }
 } finally {
        // Close resources
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }
}
	
	// public static void addPizza(Pizza p) throws SQLException, IOException
	// {
	// 	connect_to_db();
	// 	/*
	// 	 * Add the code needed to insert the pizza into into the database.
	// 	 * Keep in mind adding pizza discounts and toppings associated with the pizza,
	// 	 * there are other methods below that may help with that process.
	// 	 * 
	// 	 */
	// 	Connection conn = null;
    //     PreparedStatement stmt = null;

    //     try {
    //         conn = DBConnector.make_connection();

    //         String sql = "INSERT INTO pizza (PizzaBasePrice, PizzaBaseCost, PizzaState, OrderID) VALUES (?, ?, ?, ?)";
    //         stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

    //         stmt.setDouble(1, p.getBusPrice());
    //         stmt.setDouble(2, p.getCustPrice());
    //         stmt.setString(3, p.getPizzaState());
    //         stmt.setInt(4, p.getOrderID());

    //         stmt.executeUpdate();

    //         ResultSet rs = stmt.getGeneratedKeys();
    //         if (rs.next()) {
    //             int generatedPizzaID = rs.getInt(1);
    //             p.setPizzaID(generatedPizzaID);
    //         }
    //         if (rs != null) rs.close();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         // Optionally rethrow exception or handle it as per your error handling policy
    //     } finally {
    //         // Close resources
    //         if (stmt != null) stmt.close();
    //         if (conn != null) conn.close();
    //     }
    // }


	public static void addPizza(Pizza p) throws SQLException, IOException {
		connect_to_db();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
	
		try {
			conn = DBConnector.make_connection();

			String basePriceSql = "SELECT BasePriceID FROM baseprice WHERE BasePriceName = ? AND BasePriceCrust = ?";
			stmt = conn.prepareStatement(basePriceSql);
			stmt.setString(1, p.getSize()); 
			stmt.setString(2, p.getCrustType()); 
			rs = stmt.executeQuery();
	
			int basePriceId = 0;
			if (rs.next()) {
				basePriceId = rs.getInt("BasePriceID");
			} else {
				throw new SQLException("No base price found for given size and crust");
			}
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			String sql = "INSERT INTO pizza (PizzaBasePrice, PizzaBaseCost, PizzaState, OrderID, BasePriceID) VALUES (?, ?, ?, ?, ?)";
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setDouble(1, p.getBusPrice());
			stmt.setDouble(2, p.getCustPrice());
			stmt.setString(3, p.getPizzaState());
			stmt.setInt(4, p.getOrderID());
			stmt.setInt(5, basePriceId); 
	
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int generatedPizzaID = rs.getInt(1);
				p.setPizzaID(generatedPizzaID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close resources
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}
	
	
	
	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this method will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		connect_to_db();
		/*
		 * This method should do 2 two things.
		 * - update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * - connect the topping to the pizza
		 *   What that means will be specific to your yimplementatinon.
		 * 
		 * Ideally, you should't let toppings go negative....but this should be dealt with BEFORE calling this method.
		 * 
		 */
		Connection conn = null;
        PreparedStatement stmt = null;
		int dd = isDoubled ? 2 : 1;

        try {
            conn = DBConnector.make_connection();
			Statement st=conn.createStatement();

            String ui = "UPDATE topping SET ToppingInventory = ? WHERE ToppingID = ?";
            stmt = conn.prepareStatement(ui);
			int invamt =t.getCurINVT();
            stmt.setInt(1, invamt);
            stmt.setInt(2, t.getTopID());
            stmt.executeUpdate();
            stmt.close();

            // Connect the topping to the pizza
            String pt = "INSERT INTO pizza_topping (PizzaID, ToppingID, Pizza_ToppingExtraTopping) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(pt);
            stmt.setInt(1, p.getPizzaID());
            stmt.setInt(2, t.getTopID());
            stmt.setBoolean(3, isDoubled);
            stmt.executeUpdate();}

    	 catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow exception or handle it as per your error handling policy
        } finally {
            // Close resources
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

	public static void updatePizzaPriceAndCost(int pizzaID, double newPrice, double newCost) throws SQLException, IOException {
		connect_to_db();
	
		try {
			String sql = "UPDATE pizza SET PizzaBasePrice = ?, PizzaBaseCost = ? WHERE PizzaID = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setDouble(1, newPrice);
				stmt.setDouble(2, newCost);
				stmt.setInt(3, pizzaID);
	
				stmt.executeUpdate();

			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle or rethrow the exception as per your application's error handling policy
		} finally {
			if (conn != null) conn.close(); // Close the connection
		}
	}

	public static void updateOrderPriceAndCost(int OrderID, double newPrice, double newCost) throws SQLException, IOException {
		connect_to_db();
	
		try {
			String sql = "UPDATE ordertable SET OrderTotalCost = ?, OrderTotalPrice = ? WHERE OrderID = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setDouble(1, newCost);
				stmt.setDouble(2, newPrice);
				stmt.setInt(3, OrderID);
	
				stmt.executeUpdate();

			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle or rethrow the exception as per your application's error handling policy
		} finally {
			if (conn != null) conn.close(); // Close the connection
		}
	}
	
	
	
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * This method connects a discount with a Pizza in the database.
		 * 
		 * What that means will be specific to your implementatinon.
		 */
		Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnector.make_connection(); 

            String sql = "INSERT INTO pizza_discount (PizzaID, DiscountID) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, p.getPizzaID()); 
            stmt.setInt(2, d.getDiscountID()); 

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow exception or handle it as per your error handling policy
        } finally {
            // Close resources
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }	
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * This method connects a discount with an order in the database
		 * 
		 * You might use this, you might not depending on where / how to want to update
		 * this information in the dabast
		 */
		Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnector.make_connection(); 

            String sql = "INSERT INTO order_discount (OrderID, DiscountID) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, o.getOrderID());
            stmt.setInt(2, d.getDiscountID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow exception or handle it as per your error handling policy
        } finally {
            // Close resources
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
			
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
	
		try {
			String sql = "INSERT INTO customer (CustomerName, CustomerPhoneNumber) VALUES (?, ?);";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			String name= c.getFName()+" "+c.getLName();
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, c.getPhone());
			
			preparedStatement.executeUpdate();
		} finally {
			conn.close();
		}
	}

	public static void completeOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Find the specifed order in the database and mark that order as complete in the database.
		 * 
		 */
		try {

			String updateStatement = "update ordertable set OrderIsComplete = 1 where OrderID = " + o.getOrderID() + " ;";

			PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);

			preparedStatement.executeUpdate();
			String updatePizzaStatement = "update pizza set PizzaState = ?  where OrderID = ?" ;

			PreparedStatement pizzaPreparedStatement = conn.prepareStatement(updatePizzaStatement);
			pizzaPreparedStatement.setString(1,"completed");
			pizzaPreparedStatement.setInt(2,o.getOrderID());

			pizzaPreparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static ArrayList<Order> getOrders(boolean openOnly) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Return an arraylist of all of the orders.
		 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
		 *           == false => return a list of all the orders in the database
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */
		ArrayList<Order> orderslist = new ArrayList<Order>();
		try {

			String ordersql = "SELECT * FROM ordertable o";
			if (openOnly == true){
				ordersql += " where OrderIsComplete ="+false;
			}

			PreparedStatement preparedStatement = conn.prepareStatement(ordersql);

			ResultSet orderRecords = preparedStatement.executeQuery();
			while (orderRecords.next()) {
				int orderID = orderRecords.getInt("OrderID");
				String OrderTimeStamp = orderRecords.getString("OrderTimeStamp");
				int  CustomerID = orderRecords.getInt("CustomerID");
				String OrderType = orderRecords.getString("OrderType");
				int OrderIsComplete = orderRecords.getInt("OrderIsComplete");
				double custPrice = orderRecords.getDouble("OrderTotalPrice");
				double busPrice = orderRecords.getDouble("OrderTotalCost");
				Order o=new Order(orderID,CustomerID, OrderType,OrderTimeStamp,custPrice,busPrice,OrderIsComplete);
				orderslist.add(o);
				o.setPizzaList(getPizzaList(o.getOrderID()));
				o.setDiscountList(getDiscountforOrderList(o.getOrderID()));


			}

			
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return orderslist;
	}



	public static ArrayList<Order> getClosedOrders() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Return an arraylist of all of the orders.
		 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
		 *           == false => return a list of all the orders in the database
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */
		ArrayList<Order> orderslist = new ArrayList<Order>();
		
		try {
			String ordersql = "SELECT * FROM ordertable o where OrderIsComplete =true;";
			PreparedStatement preparedStatement = conn.prepareStatement(ordersql);

			ResultSet orderRecords = preparedStatement.executeQuery();
			while (orderRecords.next()) {
				int orderID = orderRecords.getInt("OrderID");
				String OrderTimeStamp = orderRecords.getString("OrderTimeStamp");
				int  CustomerID = orderRecords.getInt("CustomerID");
				String OrderType = orderRecords.getString("OrderType");
				int OrderIsComplete = orderRecords.getInt("OrderIsComplete");
				double custPrice = orderRecords.getDouble("OrderTotalPrice");
				double busPrice = orderRecords.getDouble("OrderTotalCost");
			
				Order o=new Order(orderID,CustomerID, OrderType,OrderTimeStamp,custPrice,busPrice,OrderIsComplete);
				orderslist.add(o);
				o.setPizzaList(getPizzaList(o.getOrderID()));
				o.setDiscountList(getDiscountforOrderList(o.getOrderID()));
			}

			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return orderslist;
	}



	public static int getLastEnteredCustomerId() throws IOException, SQLException {
		connect_to_db();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int lastCustomerId = -1;

        try {
            conn = DBConnector.make_connection(); 

            String sql = "SELECT MAX(CustomerID) AS LastID FROM customer";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                lastCustomerId = rs.getInt("LastID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow exception or handle it as per your error handling policy
        } finally {
            // Close resources
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        return lastCustomerId;
    }

	
	public static Order getLastOrder()throws SQLException, IOException{
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there should ALWAYS be a "last order"!
		 */
		connect_to_db();
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnector.make_connection(); 
            String sql = "SELECT * FROM ordertable ORDER BY OrderID DESC LIMIT 1";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int orderID = rs.getInt("OrderID");
                int custID = rs.getInt("CustomerID");
                String orderType = rs.getString("OrderType");
                String date = rs.getString("OrderTimeStamp"); 
                double custPrice = rs.getDouble("OrderTotalCost");
                double busPrice = rs.getDouble("OrderTotalPrice");
                int isComplete = rs.getInt("OrderIsComplete");

                return new Order(orderID, custID, orderType, date, custPrice, busPrice, isComplete);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
	}

	public static ArrayList<Order> getOrdersByDate(String date) throws SQLException, IOException{
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *  
		 */
		connect_to_db();
		
		ArrayList<Order> orderslist = new ArrayList<Order>();
		try {

			String ordersql = "select * from ordertable";
			if (date != null) {
				ordersql += " where (OrderTimeStamp >= '" + date + " 00:00:00')";
			}
			ordersql += " order by OrderTimeStamp desc;";
			PreparedStatement preparedStatement = conn.prepareStatement(ordersql);

			ResultSet orderRecords = preparedStatement.executeQuery();
			while (orderRecords.next()) {
				int orderID = orderRecords.getInt("OrderID");
				String OrderTimeStamp = orderRecords.getString("OrderTimeStamp");
				int  CustomerID = orderRecords.getInt("CustomerID");
				String OrderType = orderRecords.getString("OrderType");
				int OrderIsComplete = orderRecords.getInt("OrderIsComplete");
				double custPrice = orderRecords.getDouble("OrderTotalPrice");
				double busPrice = orderRecords.getDouble("OrderTotalCost");
			
				Order o=new Order(orderID,CustomerID, OrderType,OrderTimeStamp,custPrice,busPrice,OrderIsComplete);
				orderslist.add(o);
				o.setPizzaList(getPizzaList(o.getOrderID()));
				o.setDiscountList(getDiscountforOrderList(o.getOrderID()));
			}

			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return orderslist;
		
	}

	public static ArrayList<Pizza> getPizzaList(int orderId) throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database for all the available pizza and 
		 * return them in an arrayList of pizzas.
		 * 
		*/
		ArrayList<Pizza> pizzalist = new ArrayList<Pizza>();
		Pizza p=null;
		try {
		String pizzasql = "SELECT p.pizzaID, b.BasePriceCrust, b.BasePriceName, p.PizzaState, o.OrderTimestamp, p.PizzaBaseCost, p.PizzaBasePrice FROM pizza p JOIN baseprice b ON p.BasePriceID = b.BasePriceID JOIN ordertable o ON p.OrderID = o.OrderID where o.OrderID =" + orderId;
			PreparedStatement preparedStatement = conn.prepareStatement(pizzasql);

			ResultSet pizzaRecords = preparedStatement.executeQuery();
			
			while(pizzaRecords.next()){
				int pizzaId = pizzaRecords.getInt("pizzaID");
				String size = pizzaRecords.getString("BasePriceName");
				String crustType = pizzaRecords.getString("BasePriceCrust");
				String pizzaState = pizzaRecords.getString("PizzaState");
				String pizzaDate = pizzaRecords.getString("OrderTimestamp");
				double custPrice = pizzaRecords.getDouble("PizzaBaseCost");
				double busPrice = pizzaRecords.getDouble("PizzaBasePrice");
				p= new Pizza(pizzaId, size, crustType, orderId, pizzaState, pizzaDate, custPrice, busPrice);
				pizzalist.add(p);
				p.setDiscounts(getDiscountforPizzaList(p.getPizzaID()));

			}
		
		conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return pizzalist;
	}

	public static ArrayList<Discount> getDiscountforPizzaList(int PizzaID) throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database for all the available discounts and 
		 * return them in an arrayList of discounts.
		 * 
		*/
		ArrayList<Discount> Discountlist = new ArrayList<Discount>();
		try {
		String discountsql = "SELECT * FROM discount JOIN pizza_discount ON discount.DiscountID = pizza_discount.DiscountID where pizza_discount.PizzaID=?";
			PreparedStatement preparedStatement = conn.prepareStatement(discountsql);
			preparedStatement.setInt(1, PizzaID);
			ResultSet discountRecords = preparedStatement.executeQuery();
			
			while(discountRecords.next()){
				int discountID = discountRecords.getInt("DiscountID");
				String DiscountName = discountRecords.getString("DiscountName");
				Double amount = discountRecords.getDouble("Amount");
				Boolean isPercent= discountRecords.getBoolean("isPercent");
				Discountlist.add(new Discount(discountID, DiscountName, amount,isPercent));
			}
		conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return Discountlist;
	}

	
	public static ArrayList<Discount> getDiscountforOrderList(int OrderID) throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database for all the available discounts and 
		 * return them in an arrayList of discounts.
		 * 
		*/
		ArrayList<Discount> Discountlist = new ArrayList<Discount>();
		try {
		String discountsql = "SELECT * FROM discount JOIN order_discount ON discount.DiscountID = order_discount.DiscountID where order_discount.OrderID=?";
			PreparedStatement preparedStatement = conn.prepareStatement(discountsql);
			preparedStatement.setInt(1, OrderID);
			ResultSet discountRecords = preparedStatement.executeQuery();
			
			while(discountRecords.next()){
				int discountID = discountRecords.getInt("DiscountID");
				String DiscountName = discountRecords.getString("DiscountName");
				Double amount = discountRecords.getDouble("Amount");
				Boolean isPercent= discountRecords.getBoolean("isPercent");
				Discountlist.add(new Discount(discountID, DiscountName, amount,isPercent));
			}
		conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return Discountlist;
	}
		
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		connect_to_db();
		ArrayList<Discount> discountList = new ArrayList<>();

        try {
			Connection conn = DBConnector.make_connection();
            String query = "SELECT * FROM discount";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("DiscountID"); 
                String description = resultSet.getString("DiscountName");
                double amt = resultSet.getDouble("Amount");
				boolean isit = resultSet.getBoolean("isPercent");

                Discount discount = new Discount(id, description,amt,isit);
                discountList.add(discount);
            }
        } finally {
            conn.close();
        }
		return discountList;
	}

	public static Discount findDiscountByName(String name)throws SQLException, IOException{
		/*
		 * Query the database for a discount using it's name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *  
		 */
		connect_to_db();
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnector.make_connection(); 

            String sql = "SELECT * FROM discount WHERE DiscountName = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int discountID = rs.getInt("DiscountID");
                String discountName = rs.getString("DiscountName");
                double amount = rs.getDouble("Amount");
                boolean isPercent = rs.getBoolean("isPercent");

                return new Discount(discountID, discountName, amount, isPercent);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow exception or handle it as per your error handling policy
            return null;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
	}
	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		connect_to_db();
		ArrayList<Customer> customerslist = new ArrayList<Customer>();
		try {
			Connection conn = DBConnector.make_connection();
			String sql = "SELECT * FROM customer";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);

			ResultSet custRecords = preparedStatement.executeQuery();
			while (custRecords.next()) {
				int custID = custRecords.getInt("CustomerID");
				String fullName = custRecords.getString("CustomerName");
				String[] nameParts = fullName.split(" ");
				String fName = nameParts[0]; 
				String lName = nameParts[1]; 
				
				String phone = custRecords.getString("CustomerPhoneNumber");

				customerslist.add(new Customer(custID, fName, lName, phone));
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		/*
		 * Query the data for all the customers and return an arrayList of all the customers. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		*/
		return customerslist;
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static Customer findCustomerByPhone(String phoneNumber)throws SQLException, IOException {
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *  
		 */
		connect_to_db(); 
		try {
			Connection conn = DBConnector.make_connection();
			String sql = "SELECT * FROM customer WHERE CustomerPhoneNumber = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, phoneNumber);
			ResultSet rs = stmt.executeQuery();
	
			if (rs.next()) {
				int custID = rs.getInt("CustomerID");
				String Name = rs.getString("CustomerName");
				String[] names = Name.split(" ", 2);
				String fName =names[0];
				String lName = names[1];
				String phone = rs.getString("CustomerPhoneNumber");
				return new Customer(custID, fName, lName, phone);
			} else {
				// Customer not found
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
            if (conn != null) {
                conn.close();
            }
		}
	}


	public static ArrayList<Topping> getToppingList() throws SQLException, IOException {
		connect_to_db();
		ArrayList<Topping> toppings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnector.make_connection();

            String sql = "SELECT * FROM topping";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int topID = rs.getInt("ToppingID");
                String topName = rs.getString("ToppingName");
                double perAMT = rs.getDouble("ToppingSmall");
                double medAMT = rs.getDouble("ToppingMedium");
                double lgAMT = rs.getDouble("ToppingLarge");
                double xLAMT = rs.getDouble("ToppingXLarge");
                double custPrice = rs.getDouble("ToppingCostPerUnit");
                double busPrice = rs.getDouble("ToppingPricePerUnit");
                int minINVT = rs.getInt("ToppingMinimum");
                int curINVT = rs.getInt("ToppingInventory");

                Topping topping = new Topping(topID, topName, perAMT, medAMT, lgAMT, xLAMT, custPrice, busPrice, minINVT, curINVT);
                toppings.add(topping);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow exception or handle it as per your error handling policy
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return toppings;
    }

	public static Topping findToppingByName(String name) throws SQLException, IOException {
		/*
		 * Query the database for the topping using it's name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *  
		 */
		connect_to_db(); 
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnector.make_connection();

            String sql = "SELECT * FROM topping WHERE ToppingName = ?"; 
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int topID = rs.getInt("ToppingID");
                String topName = rs.getString("ToppingName");
                double perAMT = rs.getDouble("ToppingSmall");
                double medAMT = rs.getDouble("ToppingMedium");
                double lgAMT = rs.getDouble("ToppingLarge");
                double xLAMT = rs.getDouble("ToppingXLarge");
                double custPrice = rs.getDouble("ToppingPricePerUnit");
                double busPrice = rs.getDouble("ToppingCostPerUnit");
                int minINVT = rs.getInt("ToppingMinimum");
                int curINVT = rs.getInt("ToppingInventory");

                return new Topping(topID, topName, perAMT, medAMT, lgAMT, xLAMT, custPrice, busPrice, minINVT, curINVT);
            } else {
                // Topping not found
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow exception or handle it as per your error handling policy
            return null;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
	}


	public static void addToInventory(Topping t, double quantity) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 * 
		 * */
		try {
			String sql = "UPDATE topping SET ToppingInventory = ToppingInventory+? WHERE ToppingID = ?";
			Connection conn = DBConnector.make_connection();
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setDouble(1, quantity);
			preparedStatement.setInt(2, t.getTopID());
			int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected == 0) {
            System.out.println("Incorrect entry, not an option");
        }
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database fro the base customer price for that size and crust pizza.
		 * 
		*/
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double basePrice = 0.0;

        try {
            conn = DBConnector.make_connection();

            String sql = "SELECT BasePricePrice FROM baseprice WHERE BasePriceName = ? AND BasePriceCrust = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, size);
            stmt.setString(2, crust);
            rs = stmt.executeQuery();

            if (rs.next()) {
                basePrice = rs.getDouble("BasePricePrice");
            }

            return basePrice;
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow exception or handle it as per your error handling policy
            return 0.0;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database fro the base business price for that size and crust pizza.
		 * 
		*/
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double basePrice = 0.0;

        try {
            conn = DBConnector.make_connection();

            String sql = "SELECT BasePriceCost FROM baseprice WHERE BasePriceName = ? AND BasePriceCrust = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, size);
            stmt.setString(2, crust);
            rs = stmt.executeQuery();

            if (rs.next()) {
                basePrice = rs.getDouble("BasePriceCost");
            }

            return basePrice;
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow exception or handle it as per your error handling policy
            return 0.0;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
	}


	public static void printInventory() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Queries the database and prints the current topping list with quantities.
		 *  
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		try {
			String sql = "SELECT ToppingID, ToppingName, ToppingInventory FROM topping order by ToppingID";
			PreparedStatement sqlStatement = conn.prepareStatement(sql);
			ResultSet output = sqlStatement.executeQuery();

			System.out.println("ID " + "\t" + "Name " + "\t" + "\t" +"CurINVT ");
			while (output.next()) {
				System.out.println(output.getString("ToppingID").trim() + "\t" + output.getString("ToppingName").trim() + "\t"  +  output.getString("ToppingInventory").trim());
			}
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void printToppingPopReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
        try {
            String query = "SELECT * FROM ToppingPopularity;"; 
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
			System.out.println("topping     ToppingCount");

            while (rs.next()) {
                String toppingName = rs.getString("Topping");
                int popularityScore = rs.getInt("ToppingCount");
                System.out.println(toppingName + "	" + popularityScore);
            }
        }
		finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByPizza view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		try {
			String Sql = "SELECT * FROM ProfitByPizza";
			PreparedStatement prepared = conn.prepareStatement(Sql);
			ResultSet output = prepared.executeQuery();
			System.out.printf("%-13s  %-14s  %-10s %-30s%n", "Pizza Size", "Pizza Crust", "Profit", "Last Order Date");
			while (output.next()) {

				String size = output.getString("Size");
				String crust = output.getString("Crust");
				Double profit = output.getDouble("Profit");
				String lastorderDate = output.getString("Last Order Date");

				System.out.printf("%-13s  %-14s  %-10s %-30s%n", size, crust, profit, lastorderDate);
			}
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	


	
	public static void printProfitByOrderType() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		try {
			String Sql = "SELECT * FROM ProfitByOrderType";
			PreparedStatement prepared = conn.prepareStatement(Sql);
			ResultSet output = prepared.executeQuery();
			System.out.printf("%-14s  %-14s  %-20s %-20s %-20s%n", "customerType", "OrderMonth", "TotalOrderPrice", "TotalOrderCost","Profit");
			while (output.next()) {

				String OrderType = output.getString("customerType");
				String OrderMonth = output.getString("OrderMonth");
				Double TotalOrderPrice = output.getDouble("TotalOrderPrice");
				String TotalOrderCost = output.getString("TotalOrderCost");
				Double profit = output.getDouble("Profit");

				System.out.printf("%-14s  %-14s  %-20s %-20s %-20s%n", OrderType, OrderMonth, TotalOrderPrice, TotalOrderCost,profit);

			}
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION	
	}
	
	
	
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
	/*
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. This is an example of how to interact with 
		 * your database from Java.  It's used in the model solution for this project...so the code works!
		 * 
		 * OF COURSE....this code would only work in your application if the table & field names match!
		 *
		 */

		 connect_to_db();

		/* 
		 * an example query using a constructed string...
		 * remember, this style of query construction could be subject to sql injection attacks!
		 * 
		 */
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String customerName = null;

        try {
            conn = DBConnector.make_connection();

            String sql = "SELECT CustomerName FROM customer WHERE CustomerID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, CustID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                customerName = rs.getString("CustomerName");
            }
			if(customerName==null)
			{
				customerName="INSTORE customer";
			}

            return customerName;
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow exception or handle it as per your error handling policy
            return null;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
	}

	/*
	 * The next 3 private methods help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		if(getYear(dateOfOrder) > year)
			return true;
		else if(getYear(dateOfOrder) < year)
			return false;
		else
		{
			if(getMonth(dateOfOrder) > month)
				return true;
			else if(getMonth(dateOfOrder) < month)
				return false;
			else
			{
				if(getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}


}