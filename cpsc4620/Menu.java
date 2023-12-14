package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the methods for each of the menu options.
 * 
 * This file should not need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove methods as you see necessary. But you MUST have all of the menu methods (including exit!)
 * 
 * Simply removing menu methods because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 */

public class Menu {

	public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws SQLException, IOException {

		System.out.println("Welcome to Pizzas-R-Us!");
		
		int menu_option = 0;

		// present a menu of options and take their selection
		
		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
			case 1:// enter order
				EnterOrder();
				break;
			case 2:// view customers
				viewCustomers(); // done________________________
				break;
			case 3:// enter customer
				EnterCustomer(); // done_________________________
				break;
			case 4:// view order
				// open/closed/date
				ViewOrders();
				break;
			case 5:// mark order as complete
				MarkOrderAsComplete();
				break;
			case 6:// view inventory levels
				ViewInventoryLevels(); //done_________________________
				break;
			case 7:// add to inventory
				AddInventory(); //done_______________________________
				break;
			case 8:// view reports
				PrintReports(); //done_______________________________
				break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException 
	{

		/*
		 * EnterOrder should do the following:
		 * 
		 * Ask if the order is delivery, pickup, or dinein
		 *   if dine in....ask for table number
		 *   if pickup...
		 *   if delivery...
		 * 
		 * Then, build the pizza(s) for the order (there's a method for this)
		 *  until there are no more pizzas for the order
		 *  add the pizzas to the order
		 *
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * return to menu
		 * 
		 * make sure you use the prompts below in the correct order!
		 */

		 // User Input Prompts...

		System.out.println("Is this order for: \n1.) Dine-in\n2.) Pick-up\n3.) Delivery\nEnter the number of your choice:");
		int ot = Integer.parseInt(reader.readLine());
		String ordertype=null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Order o=null;
		ArrayList<Pizza> pizzasList = new ArrayList<>();
		ArrayList<Discount> discountList = new ArrayList<>();
		String dateNow = dateFormat.format(date);
		int idn;
		Customer cust=null;
		if(ot==1) ordertype="dinein";
		else if(ot==2){
				 ordertype="pickup";
			}
			else {
				ordertype="delivery";
		}
		if(ot==1){
			System.out.println("What is the table number for this order?");
			int tablenum = Integer.parseInt(reader.readLine());
			o =new DineinOrder(0,0,dateNow,0.0,0.0,0,tablenum);
			//System.out.println(o);

		}
		else{
			System.out.println("Is this order for an existing customer? Answer y/n: ");
            String existingCustomer = reader.readLine();
			if (existingCustomer.equals("y")) {
                System.out.println("Here's a list of the current customers: ");
				ArrayList<Customer> customer = DBNinja.getCustomerList();
            if (customer != null && !customer.isEmpty()) {
                for (Customer c : customer) {
                    System.out.println(c.toString());
                }
            }
			System.out.println("Which customer is this order for? Enter ID Number:");
			idn=Integer.parseInt(reader.readLine());
			boolean isValidId = false;
			for (Customer c : customer) {
				if (c.getCustID() == idn) {
					isValidId = true;
					break;
				}
			}
			if(!isValidId){
			System.out.println("ERROR: I don't understand your input for: Is this order an existing customer?");
			System.out.println("Here's a list of the current customers: ");
            if (customer != null && !customer.isEmpty()) {
                for (Customer c : customer) {
                    System.out.println(c.toString());
                }
            }
			idn=Integer.parseInt(reader.readLine());
			}
			if(ot==3){
				System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
				String apt=reader.readLine();
				System.out.println("What is the Street for this order? (e.g., Smile Street)");
				String street=reader.readLine();
				System.out.println("What is the City for this order? (e.g., Greenville)");
				String city=reader.readLine();
				System.out.println("What is the State for this order? (e.g., SC)");
				String state=reader.readLine();
				System.out.println("What is the Zip Code for this order? (e.g., 20605)");
				String code=reader.readLine();
				Connection conn = null;
				PreparedStatement stmt = null;

				try {
					conn = DBConnector.make_connection();


					String sql = "UPDATE customer SET CustomerAptNo = ?, CustomerStreet = ?, CustomerCity = ?, CustomerState = ?, CustomerPinCode = ? WHERE CustomerID = ?";
					stmt = conn.prepareStatement(sql);

					stmt.setString(1, apt);
					stmt.setString(2, street);
					stmt.setString(3, city);
					stmt.setString(4, state);
					stmt.setString(5, code);
					stmt.setInt(6, idn);

					// Execute the update
					stmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
					// Optionally rethrow exception or handle it as per your error handling policy
				} finally {
					// Close resources
					if (stmt != null) stmt.close();
					if (conn != null) conn.close();
				}
			}

		}

		else{
			EnterCustomer();
			idn= DBNinja.getLastEnteredCustomerId();
			if(ot==3){
				System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
				String apt=reader.readLine();
				System.out.println("What is the Street for this order? (e.g., Smile Street)");
				String street=reader.readLine();
				System.out.println("What is the City for this order? (e.g., Greenville)");
				String city=reader.readLine();
				System.out.println("What is the State for this order? (e.g., SC)");
				String state=reader.readLine();
				System.out.println("What is the Zip Code for this order? (e.g., 20605)");
				String code=reader.readLine();
				Connection conn = null;
				PreparedStatement stmt = null;

				try {
					conn = DBConnector.make_connection();


					String sql = "UPDATE customer SET CustomerAptNo = ?, CustomerStreet = ?, CustomerCity = ?, CustomerState = ?, CustomerPinCode = ? WHERE CustomerID = ?";
					stmt = conn.prepareStatement(sql);

					stmt.setString(1, apt);
					stmt.setString(2, street);
					stmt.setString(3, city);
					stmt.setString(4, state);
					stmt.setString(5, code);
					stmt.setInt(6, idn);

					// Execute the update
					stmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
					// Optionally rethrow exception or handle it as per your error handling policy
				} finally {
					// Close resources
					if (stmt != null) stmt.close();
					if (conn != null) conn.close();
				}

			}
		}
		o = new Order(0,idn, ordertype, dateNow, 0.0, 0.0, 0);
		}

		DBNinja.addOrder(o);

    	System.out.println("Let's build a pizza!");
		int pizzaOption = 0;

		while(pizzaOption != -1){
		Pizza p=buildPizza(o.getOrderID());
		pizzasList.add(p);
		o.addPizza(p);
		System.out.println("Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
		pizzaOption = Integer.parseInt(reader.readLine());
		}

		double TotalOrderPrice = 0.0;
		double TotalOrderCost = 0.0;
		for(int x=0; x<o.getPizzaList().size(); x++) {
			TotalOrderPrice += o.getPizzaList().get(x).getCustPrice();
			TotalOrderCost += o.getPizzaList().get(x).getBusPrice();
		}
		o.setCustPrice(TotalOrderPrice);
		o.setBusPrice(TotalOrderCost);

		int discountOption = 0;
		System.out.println("Do you want to add discounts to this order? Enter y/n");
		String option = reader.readLine();
		if(option.equals("y")){
			while (discountOption != -1) {
				try {
					ArrayList<Discount> d = DBNinja.getDiscountList();
				for (Discount discount : d) {
					System.out.println(discount.toString());
				}
					System.out.println("Which Order Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding discounts:");
					discountOption =Integer.parseInt(reader.readLine());
					if (discountOption != -1) {
						o.addDiscount(d.get(discountOption-1));
						DBNinja.useOrderDiscount(o, d.get(discountOption-1));
						// System.out.println("debug Discount we are using");
						// System.out.println(d.get(discountOption-1));
						discountList.add(d.get(discountOption-1));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}

	o.setPizzaList(pizzasList);
	o.setDiscountList(discountList);
	//System.out.println(pizzasList);
	//System.out.println(discountList);
		
	DBNinja.updateOrderPriceAndCost(o.getOrderID(),o.getCustPrice(),o.getBusPrice());
		
 	System.out.println("Finished adding order...Returning to menu...");
	}
	
	
	public static void viewCustomers() throws SQLException, IOException 
	{
		/*
		 * Simply print out all of the customers from the database. 
		 */
		Connection conn = DBConnector.make_connection();
		if(conn != null) {
			ArrayList<Customer> customersList = null;
			customersList=DBNinja.getCustomerList();
			for(Customer customer:customersList){
				System.out.println(customer.toString());
			}

		
		}
		conn.close();	
	}

	public static void EnterCustomer() throws SQLException, IOException 
	{
		/*
		 * Ask for the name of the customer:
		 *   First Name <space> Last Name
		 * 
		 * Ask for the  phone number.
		 *   (##########) (No dash/space)
		 * 
		 * Once you get the name and phone number, add it to the DB
		 */
		
		// User Input Prompts...

		System.out.println("Please Enter the Customer name (First Name <space> Last Name):");
		String name = reader.readLine();
		String[] names = name.split(" ", 2);
		String Fname= names[0];
		String Lname=names[1];
		System.out.println("What is this customer's phone number (#########) (No dash/space):");
		String phn = reader.readLine();
		Customer newCustomer = new Customer(0, Fname,Lname, phn);
		try {
            DBNinja.addCustomer(newCustomer);
        } catch (Exception e) {
            e.printStackTrace();
        }	

	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException 
		{
		//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		/*  
		* This method allows the user to select between three different views of the Order history:
		* The program must display:
		* a.	all open orders
		* b.	all completed orders 
		* c.	all the orders (open and completed) since a specific date (inclusive)
		* 
		* After displaying the list of orders (in a condensed format) must allow the user to select a specific order for viewing its details.  
		* The details include the full order type information, the pizza information (including pizza discounts), and the order discounts.
		* 
		*/ 
		System.out.println("Would you like to:\n(a) display all orders [open or closed]\n(b) display all open orders\n(c) display all completed [closed] orders\n(d) display orders since a specific date");
		String optionString = reader.readLine();

		Connection conn = DBConnector.make_connection();
		ArrayList<Order> ordersList = null;
		ArrayList<Pizza> pizzasList = null;
		ArrayList<Discount> discountList = null;
		
		try{
		switch (optionString) {
			case "a":// all orders
				if(conn != null) {
					ordersList=DBNinja.getOrders(false);
					for(Order order:ordersList){
						System.out.println(order.toSimplePrint());
					}
				}
				System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
				String IDString = reader.readLine();
				if(Integer.parseInt(IDString)==-1){
					break;
				}
				int orderID = Integer.parseInt(IDString);
				Map<Integer, Order> resultMap = ordersList.stream().collect(Collectors.toMap(x -> x.getOrderID(), x -> x));
				Order id = resultMap.get(orderID);
				System.out.println(id.toString());
				discountList = id.getDiscountList();
				if (discountList == null || discountList.isEmpty()) {
					System.out.println("NO ORDER DISCOUNTS");
				} else {
					for (Discount d : discountList) {
						System.out.println("ORDER DISCOUNTS: " + d.getDiscountName());
					}
				}
				
				pizzasList = id.getPizzaList();
				for (Pizza p: pizzasList){
					System.out.println(p.toString());
					discountList = p.getDiscounts();
				for (Discount d: discountList){
					System.out.println("PIZZA DISCOUNTS: "+d.getDiscountName());
				}
			}


			break;
			case "b":// open orders
				if(conn != null) {
					ordersList=DBNinja.getOrders(true);
					for(Order order:ordersList){
						System.out.println(order.toSimplePrint());
					}
				}
				System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
				String IDString2 = reader.readLine();
				if(Integer.parseInt(IDString2)==-1){
					break;
				}
				int orderID2 = Integer.parseInt(IDString2);
				Map<Integer, Order> resultMap2 = ordersList.stream().collect(Collectors.toMap(x -> x.getOrderID(), x -> x));
				Order id2 = resultMap2.get(orderID2);
				System.out.println(id2.toString());
				discountList = id2.getDiscountList();
				if (discountList == null || discountList.isEmpty()) {
					System.out.println("NO ORDER DISCOUNTS");
				} else {
					for (Discount d : discountList) {
						System.out.println("ORDER DISCOUNTS: " + d.getDiscountName());
					}
				}
				
				pizzasList = id2.getPizzaList();
				for (Pizza p: pizzasList){
					System.out.println(p.toString());
					discountList = p.getDiscounts();
				for (Discount d: discountList){
					System.out.println("PIZZA DISCOUNTS: "+d.getDiscountName());
				}
			}
			break;
			case "c":// closed orders
				if(conn != null) {
					ordersList=DBNinja.getClosedOrders();
					for(Order order:ordersList){
						System.out.println(order.toSimplePrint());
					}
				}
				System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
				String IDString3 = reader.readLine();
				if(Integer.parseInt(IDString3)==-1){
					break;
				}
				int orderID3 = Integer.parseInt(IDString3);
				Map<Integer, Order> resultMap3 = ordersList.stream().collect(Collectors.toMap(x -> x.getOrderID(), x -> x));
				Order id3 = resultMap3.get(orderID3);
				System.out.println(id3.toString());
				discountList = id3.getDiscountList();
				if (discountList == null || discountList.isEmpty()) {
					System.out.println("NO ORDER DISCOUNTS");
				} else {
					for (Discount d : discountList) {
						System.out.println("ORDER DISCOUNTS: " + d.getDiscountName());
					}
				}
				
				pizzasList = id3.getPizzaList();
				for (Pizza p: pizzasList){
					System.out.println(p.toString());
					discountList = p.getDiscounts();
				for (Discount d: discountList){
					System.out.println("PIZZA DISCOUNTS: "+d.getDiscountName());
				}
			}
				break;
			case "d":// orders on specific dates
				System.out.println("Enter the date in YYYY-MM-DD format: ");
				String dateString = reader.readLine();
	
				if(conn != null) {
					ordersList=DBNinja.getOrdersByDate(dateString);
					for(Order order:ordersList){
						System.out.println(order.toSimplePrint());
					}
				}
				System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
				String IDString4 = reader.readLine();
				if(Integer.parseInt(IDString4)==-1){
					break;
				}
				int orderID4 = Integer.parseInt(IDString4);
				Map<Integer, Order> resultMap4 = ordersList.stream().collect(Collectors.toMap(x -> x.getOrderID(), x -> x));
				Order id4 = resultMap4.get(orderID4);
				System.out.println(id4.toString());
				discountList = id4.getDiscountList();
				if (discountList == null || discountList.isEmpty()) {
					System.out.println("NO ORDER DISCOUNTS");
				} else {
					for (Discount d : discountList) {
						System.out.println("ORDER DISCOUNTS: " + d.getDiscountName());
					}
				}
				
				pizzasList = id4.getPizzaList();
				for (Pizza p: pizzasList){
					System.out.println(p.toString());
					discountList = p.getDiscounts();
				for (Discount d: discountList){
					System.out.println("PIZZA DISCOUNTS: "+d.getDiscountName());
				}
			}
				break;
			}
		} catch (IOException e) {
			System.out.println("A NullPointerException occurred: " + e.getMessage());
			// Handle the exception or log it
		} 
}

public static void MarkOrderAsComplete() throws SQLException, IOException 
	{
		/*
		 * All orders that are created through java (part 3, not the orders from part 2) should start as incomplete
		 * 
		 * When this method is called, you should print all of the "opoen" orders marked
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 * 
		 */
		// User Input Prompts...
		// System.out.println("Incorrect entry, not an option");
		ArrayList<Order> orders = DBNinja.getOrders(true);
		try{
		if(orders.size()!=0) {
			for (Order order : orders) {
				System.out.println(order.toSimplePrint());
			}
			//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


			System.out.println("Which order would you like mark as complete? Enter the OrderID: ");
			Integer orderId = Integer.parseInt(reader.readLine());
			Order order = null;
			int index = 0;
			for (int i = 0; i < orders.size(); i++) {
				if (orders.get(i).getOrderID() == orderId) {
					index = i;
				}
			}
			order = orders.get(index);
			DBNinja.completeOrder(order);
		}
		else{
			System.out.println("There are no open orders currently... returning to menu...");
		}
	}catch (IOException e) {
			System.out.println("A NullPointerException occurred: " + e.getMessage());
			// Handle the exception or log it
		}
	}


	public static void ViewInventoryLevels() throws SQLException, IOException 
	{
		/*
		 * Print the inventory. Display the topping ID, name, and current inventory
		*/
		DBNinja.printInventory();
	}



	public static void AddInventory() throws SQLException, IOException 
	{
		/*
		 * This should print the current inventory and then ask the user which topping (by ID) they want to add more to and how much to add
		 */
		
		ViewInventoryLevels();
		// User Input Prompts...
		System.out.println("Which topping do you want to add inventory to? Enter the number: ");
		String input = reader.readLine();
		int ToppingID = Integer.parseInt(input);
		System.out.println("How many units would you like to add? ");
		String inputunit = reader.readLine();
		int InvUnits = Integer.parseInt(inputunit);
		Topping t = new Topping(ToppingID, null, 0, 0, 0, 0, 0, 0, 0, 0);
        DBNinja.addToInventory(t, InvUnits);

		// System.out.println("Incorrect entry, not an option");
	
	}

	// A method that builds a pizza. Used in our add new order method
	public static Pizza buildPizza(int orderID) throws SQLException, IOException 
	{
		
		/*
		 * This is a helper method for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */

		Pizza ret = null;
		
		// User Input Prompts...
		System.out.println("What size is the pizza?");
		System.out.println("1."+DBNinja.size_s);
		System.out.println("2."+DBNinja.size_m);
		System.out.println("3."+DBNinja.size_l);
		System.out.println("4."+DBNinja.size_xl);
		System.out.println("Enter the corresponding number: ");
		try{
		int ot = Integer.parseInt(reader.readLine());
		String psize = null;
		if(ot == 1) {psize = "Small";}
		else if(ot == 2) {psize = "Medium";}
		else if(ot == 3) {psize = "Large";}
		else if(ot == 4) {psize = "X-Large";}

		System.out.println("What crust for this pizza?");
		System.out.println("1."+DBNinja.crust_thin);
		System.out.println("2."+DBNinja.crust_orig);
		System.out.println("3."+DBNinja.crust_pan);
		System.out.println("4."+DBNinja.crust_gf);
		System.out.println("Enter the corresponding number: ");
		int op = Integer.parseInt(reader.readLine());
		String pcrust = null;
		if(op == 1) pcrust = "Thin";
		else if(op == 2) pcrust = "Original";
		else if(op == 3) pcrust = "Pan";
		else if(op == 4) pcrust = "Gluten-free";


		double custp = DBNinja.getBaseCustPrice(psize, pcrust);
		double basec = DBNinja.getBaseBusPrice(psize, pcrust);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String dateentry = dateFormat.format(date);

		ret = new Pizza(0,psize, pcrust, orderID, "Completed", dateentry, custp, basec);
		DBNinja.addPizza(ret);
		int opt = 0;
		boolean isDouble = false;
		ArrayList<Topping> toppinglist = new ArrayList<>();
		ArrayList<Discount> pdiscountlist = new ArrayList<>();
		ArrayList<Topping> topping = new ArrayList<>(DBNinja.getToppingList());
		double invamt=0.0;

		while(opt != -1) {
			System.out.println("Available Toppings:" );
			DBNinja.printInventory();
			System.out.println("Which topping do you want to add? Please enter the ToppingID. Enter -1 to stop adding toppings:");
			opt = Integer.parseInt(reader.readLine());
			if (opt != -1) {
				Topping selected = topping.get(opt-1);
				System.out.println("Do you want to add extra topping? Enter y/n");
				String option = reader.readLine();
				if(psize=="Small"){invamt=selected.getPerAMT();}
				else if(psize=="Medium"){invamt=selected.getMedAMT();}
				else if(psize=="Large"){invamt=selected.getLgAMT();}
				else if(psize=="X-Large"){invamt=selected.getXLAMT();}
				if (option.equals("y")) {
					isDouble = true;
					invamt *= 2;
				}
				if (selected.getCurINVT() >= invamt) {
				int amtt=(int)Math.round(selected.getCurINVT()-invamt);
				selected.setCurINVT(amtt);
				DBNinja.useTopping(ret, topping.get(opt-1), isDouble);
				toppinglist.add(topping.get(opt-1));
				ret.addToppings(topping.get(opt-1), isDouble);


				
			}
			else{
			System.out.println("We don't have enough of that topping to add it...");
			continue;
		}


		}
		}
		System.out.println("Do you want to add discounts to this Pizza? Enter y/n?");
		String s = reader.readLine();
		if(s.equals("y")) {
			int disc = 0;
			while (disc != -1) {
				ArrayList<Discount> discount = DBNinja.getDiscountList();
				for (Discount d : discount) {
					System.out.println(d.toString());
				}
			System.out.println("Which Pizza Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
			disc=Integer.parseInt(reader.readLine());
			if (disc != -1) {
				ret.addDiscounts(discount.get(disc-1));
				pdiscountlist.add(discount.get(disc-1));
				DBNinja.usePizzaDiscount(ret, discount.get(disc-1));
			}
		}
	
			}
		// 	System.out.println("debug1......");
		// System.out.println(ret.getCustPrice());
		// System.out.println(ret.getBusPrice());
		ret.setToppings(toppinglist);
		ret.setDiscounts(pdiscountlist);
		
		DBNinja.updatePizzaPriceAndCost(ret.getPizzaID(),ret.getCustPrice(),ret.getBusPrice());

		}catch(IOException e){
			System.out.println("A NullPointerException occurred: " + e.getMessage());
			// Handle the exception or log it
		}
		return ret;
	}
	
	
	public static void PrintReports() throws SQLException, NumberFormatException, IOException
	{
		/*
		 * This method asks the use which report they want to see and calls the DBNinja method to print the appropriate report.
		 * 
		 */

		// User Input Prompts...
		System.out.println("Which report do you wish to print? Enter\n(a) ToppingPopularity\n(b) ProfitByPizza\n(c) ProfitByOrderType:");
		String res=reader.readLine();
		if(res.equals("a")){
			DBNinja.printToppingPopReport();
		}
		else if(res.equals("b")) {
			DBNinja.printProfitByPizzaReport();
		}
		else if(res.equals("c")){
			DBNinja.printProfitByOrderType();

		}
		else{
		System.out.println("I don't understand that input... returning to menu...");}

	}

	//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
	// DO NOT EDIT ANYTHING BELOW HERE, THIS IS NEEDED TESTING.
	// IF YOU EDIT SOMETHING BELOW, IT BREAKS THE AUTOGRADER WHICH MEANS YOUR GRADE WILL BE A 0 (zero)!!

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	/*
	 * autograder controls....do not modiify!
	 */

	public final static String autograder_seed = "6f1b7ea9aac470402d48f7916ea6a010";

	
	private static void autograder_compilation_check() {

		try {
			Order o = null;
			Pizza p = null;
			Topping t = null;
			Discount d = null;
			Customer c = null;
			ArrayList<Order> alo = null;
			ArrayList<Discount> ald = null;
			ArrayList<Customer> alc = null;
			ArrayList<Topping> alt = null;
			double v = 0.0;
			String s = "";

			DBNinja.addOrder(o);
			DBNinja.addPizza(p);
			DBNinja.useTopping(p, t, false);
			DBNinja.usePizzaDiscount(p, d);
			DBNinja.useOrderDiscount(o, d);
			DBNinja.addCustomer(c);
			DBNinja.completeOrder(o);
			alo = DBNinja.getOrders(false);
			o = DBNinja.getLastOrder();
			alo = DBNinja.getOrdersByDate("01/01/1999");
			ald = DBNinja.getDiscountList();
			d = DBNinja.findDiscountByName("Discount");
			alc = DBNinja.getCustomerList();
			c = DBNinja.findCustomerByPhone("0000000000");
			alt = DBNinja.getToppingList();
			t = DBNinja.findToppingByName("Topping");
			DBNinja.addToInventory(t, 1000.0);
			v = DBNinja.getBaseCustPrice("size", "crust");
			v = DBNinja.getBaseBusPrice("size", "crust");
			DBNinja.printInventory();
			DBNinja.printToppingPopReport();
			DBNinja.printProfitByPizzaReport();
			DBNinja.printProfitByOrderType();
			s = DBNinja.getCustomerName(0);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}


}


