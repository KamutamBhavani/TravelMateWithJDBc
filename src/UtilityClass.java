import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class UtilityClass {

    private static final int maxattempts = 5;

    public static void main(String[] args) throws SQLException {
        while (true) {
            System.out.print("Enter your choice: ");
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("UserRegistration");
                    Registration();
                    break;
                case 2:
                    System.out.println("LockAccount");
                    for (int i = 1; i <= maxattempts; i++) {
                        System.out.println("enter the email");
                        String email = sc.next();
                        System.out.println("enter the password");
                        String password = sc.next();
                        if(authenticateUser(email,password)) {
                            System.out.println("user logged in successful");
                            break;
                        }

                    }
                    break;
                case 3:
                    System.out.println("PlanJourney");

                    System.out.println("enter the userid");
                    int userId = sc.nextInt();
                    if (isUserIdValid(userId)) {
                        System.out.println("user exists");
                    } else {
                        System.out.println("Error: User ID " + userId + " does not exist in the system. Please register first.");
                    }
                    userWantToShow();
                    System.out.print("Enter the Route ID from the above options: ");
                    int selectedRouteId = sc.nextInt();
                    if (isRouteIdValid(selectedRouteId)) {
                        System.out.println("Route ID " + selectedRouteId + " is valid. Proceeding with booking...");
                    } else {
                        System.out.println("Invalid Route ID. Please select a valid option.");
                    }
                    System.out.print("Enter Source: ");
                    String source = sc.next();
                    System.out.print("Enter Destination: ");
                    String destination = sc.next();
                    if (!sourceChecking(source, destination)) {
                        System.out.println("NoValid route from " + source + " to " + destination + ". Proceeding...");
                        return;
                    }

                    planJourney(userId, source, destination);

                    break;
                case 4:
                    System.out.println("RescheduleJourney");
                    System.out.println("enter your bookingid");
                    int BookingId = sc.nextInt();
                    if (bookingidCheck(BookingId)) {
                        rescheduleDate(BookingId);
                    } else {
                        System.out.println("you are booking id does not exists:");
                    }
                    break;
                case 5:
                    System.out.println("press true if you want to perfrom any other operations otherwise press false");
                    boolean perfromanyotherop = sc.nextBoolean();
                    if (perfromanyotherop) {
                        displayOperations();
                    } else {
                        System.out.println("Thank you for choosing us:)");
                        System.exit(0);
                    }
                    break;
                default:
                    System.out.println("please enter the valid choices");

            }
        }
    }

    private static void displayOperations() {
        System.out.println("connection is sucessfull");
        System.out.println("\nMain Menu:");
        System.out.println("Showing the logo ABC TRAVELS");
        System.out.println("1. Admin User Registration");
        System.out.println("2. Lock Account");
        System.out.println("3. Plan Journey");
        System.out.println("4. RescheduleJourney");
        System.out.println("5. Perform any other Operations");
    }

    public class DBConnection {
        private static final String URL = "jdbc:mysql://localhost:3306/abctravels";
        private static final String USER = "root";
        private static final String PASSWORD = "Bhavani@456";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
        private static void Registration() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("enter your id: ");
            int id = scanner.nextInt();
            System.out.print("Enter First Name: ");
            String firstName = scanner.next();
            System.out.print("Enter Last Name: ");
            String lastName = scanner.next();
            System.out.print("Enter Mobile Number: ");
            String mobileNumber = scanner.next();
            System.out.print("Enter Gender: ");
            String gender = scanner.next();
            System.out.print("Enter Email ID: ");
            String email = scanner.next();
            System.out.print("Enter Password: ");
            String password = scanner.next();
            int failedcount = 0;
            String accountstatus = "Active";
            try {
                Connection con = DBConnection.getConnection();
                String query = "INSERT INTO admin_users (id,first_name, last_name, mobile_number, gender, email,password,failed_count,account_status) VALUES (?,?, ?, ?, ?, ?, ?,?,?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, id);
                ps.setString(2, firstName);
                ps.setString(3, lastName);
                ps.setString(4, mobileNumber);
                ps.setString(5, gender);
                ps.setString(6, email);
                ps.setString(7, password);
                ps.setInt(8, failedcount);
                ps.setString(9, accountstatus);
                ps.executeUpdate();
                System.out.println("Admin User Registered Successfully!");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        //lock account
        private static boolean authenticateUser(String email, String password) {
            try (Connection con = DBConnection.getConnection()) {


                String query = "SELECT email, password, failed_count, account_status FROM admin_users WHERE email = ?";
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, email);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String dbPassword = rs.getString("password");
                            int failedCount = rs.getInt("failed_count");
                            String accountStatus = rs.getString("account_status");


                            if ("LOCKED".equalsIgnoreCase(accountStatus)) {
                                System.out.println("Your account is locked. Please contact support.");

                            }


                            if (dbPassword.equals(password)) {

                                System.out.println("User logged in successfully.");
                                resetFailedCount(con, email);
                                // return;
                            } else {

                                incrementFailedCount(con, email, failedCount);
                            }
                        } else {

                            System.out.println("Invalid email. Please try again.");
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            }
            return false;
        }

        private static void resetFailedCount(Connection con, String email) throws SQLException {
            String resetQuery = "UPDATE admin_users SET failed_count = 0 WHERE email = ?";
            try (PreparedStatement ps = con.prepareStatement(resetQuery)) {
                ps.setString(1, email);
                ps.executeUpdate();
            }
        }

        private static void incrementFailedCount(Connection con, String email, int currentFailedCount) throws SQLException {
            currentFailedCount++;

            if (currentFailedCount > 5) {
                // Lock the account
                String lockQuery = "UPDATE admin_users SET failed_count = ?, account_status = 'LOCKED' WHERE email = ?";
                try (PreparedStatement ps = con.prepareStatement(lockQuery)) {
                    ps.setInt(1, currentFailedCount);
                    ps.setString(2, email);
                    ps.executeUpdate();
                }
                System.out.println("Too many failed attempts. Your account has been locked.");
            } else {

                String updateQuery = "UPDATE admin_users SET failed_count = ? WHERE email = ?";
                try (PreparedStatement ps = con.prepareStatement(updateQuery)) {
                    ps.setInt(1, currentFailedCount);
                    ps.setString(2, email);
                    ps.executeUpdate();
                }
                System.out.println("Incorrect password. " + (maxattempts - currentFailedCount) + " attempts remaining.");
            }
        }

        private static void planJourney(int UserId, String source, String destination) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("if user exists in the database you can proceed with booking");

            System.out.println("enter your booking id");
            int bookingid = scanner.nextInt();
            LocalDate travelDate = date();
            System.out.print("Enter Number of Passengers: ");
            int passengers = scanner.nextInt();

            double baseFare = getBaseFare(source, destination);
            double fare = baseFare * passengers;


            if (travelDate.getDayOfWeek() == DayOfWeek.SATURDAY || travelDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                fare += 200;
            }


            double gst = fare * 0.18;
            double totalFare = fare + gst;

            try (Connection conn = DBConnection.getConnection()) {
                String query = "INSERT INTO journey_bookings (User_Id,BookingId, source, destination, traveldate, passengers, totalfare) VALUES ( ?, ?, ?, ?, ?,?,?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, UserId);
                ps.setInt(2, bookingid);
                ps.setString(3, source);
                ps.setString(4, destination);
                ps.setDate(5, Date.valueOf(travelDate));
                ps.setInt(6, passengers);
                ps.setDouble(7, totalFare);
                ps.executeUpdate();
                System.out.println("Booking confirmed! Total Fare: " + totalFare);
            } catch (SQLException e) {
                System.out.println("Error during journey booking: " + e.getMessage());
            }
        }

        private static boolean isRouteIdValid(int selectedRouteId) {
            String query = "SELECT 1 FROM journeyroutes WHERE routeid = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setInt(1, selectedRouteId);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }

            } catch (SQLException e) {
                System.out.println("Error validating Route ID: " + e.getMessage());
            }
            return false;
        }

        public static boolean sourceChecking(String source, String destination) {
            String query = "SELECT 1 FROM journeyroutes WHERE source = ? AND destination = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, source);
                ps.setString(2, destination);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }

            } catch (SQLException e) {
                System.out.println("Error validating route: " + e.getMessage());
            }

            return false;
        }

        public static Double getBaseFare(String source, String destination) {
            String query = "SELECT fare FROM journeyroutes WHERE source = ? AND destination = ?";
            Double fare = null;

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, source);
                ps.setString(2, destination);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        fare = rs.getDouble("fare"); // Retrieve the fare
                    }
                }

            } catch (SQLException e) {
                System.out.println("Error retrieving fare: " + e.getMessage());
            }

            return fare;
        }

        private static LocalDate date() {

            Scanner sc = new Scanner(System.in);

            LocalDate travelDate = null;
            while (travelDate == null) {
                System.out.print("Enter your travel date (yyyy-MM-dd): ");
                String dateInput = sc.nextLine();

                try {

                    travelDate = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));


                    LocalDate today = LocalDate.now();


                    if (travelDate.isBefore(today)) {
                        System.out.println("Error: Travel date cannot be in the past. Please enter a valid date.");
                        travelDate = null;
                    }

                } catch (DateTimeParseException e) {

                    System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
                }
            }

            System.out.println("Travel date confirmed: " + travelDate);

            return travelDate;
        }

        public static boolean isUserIdValid(int userId) {

            String query = "SELECT 1 FROM admin_users WHERE id = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();

                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }

            return false;
        }

        private static void userWantToShow() {
            String query = "SELECT routeid, source, destination, fare, availableseats FROM journeyroutes";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                System.out.println("Available Journey Options:");
                while (rs.next()) {
                    int routeId = rs.getInt("routeid");
                    String source = rs.getString("source");
                    String destination = rs.getString("destination");
                    double fare = rs.getDouble("fare");
                    int availableSeats = rs.getInt("availableseats");
                    System.out.printf("%-10d %-15s %-15s %-10.2f %-10d\n", routeId, source, destination, fare, availableSeats);


                }

            } catch (SQLException e) {
                System.out.println("Error retrieving journey options: " + e.getMessage());
            }
        }

        private static void rescheduleDate(int bookingid) {
            LocalDate newDateInput = date();
            String query = "UPDATE journey_bookings SET traveldate = ? WHERE BookingId = ?";

            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setDate(1, java.sql.Date.valueOf(newDateInput));
                ps.setInt(2, bookingid);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Travel date successfully updated to: " + newDateInput);
                } else {
                    System.out.println("Error: Failed to update the travel date.");
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }

        private static boolean bookingidCheck(int bookingid) {
            String query = "select BookingId from journey_bookings where BookingId=?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setInt(1, bookingid);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();

                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
            return false;
        }
    }
