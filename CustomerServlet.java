import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String meter = request.getParameter("meter_no");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        
        // Use part of the name for the default username/password
        String defaultUsername = name.toLowerCase().split(" ")[0]; 
        String defaultPassword = "12345"; // Default password
        
        Connection con = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/electricity_db","root","root123");
            
            // --- CRITICAL: Start Transaction ---
            con.setAutoCommit(false); 
            int customerId = -1;

            // 1. INSERT INTO customers and retrieve the generated ID
            String sqlCustomer = "INSERT INTO customers(name,meter_no,email,phone) VALUES(?,?,?,?)";
            try (PreparedStatement psCust = con.prepareStatement(sqlCustomer, Statement.RETURN_GENERATED_KEYS)) {
                psCust.setString(1, name);
                psCust.setString(2, meter);
                psCust.setString(3, email);
                psCust.setString(4, phone);
                psCust.executeUpdate();

                // Retrieve the auto-generated customer_id
                try (ResultSet rs = psCust.getGeneratedKeys()) {
                    if (rs.next()) {
                        customerId = rs.getInt(1);
                    }
                }
            }

            // 2. INSERT INTO users, linking it with the retrieved customerId
            if (customerId != -1) {
                String sqlUser = "INSERT INTO users(username,password,role,customer_id) VALUES(?,?,?,?)";
                try (PreparedStatement psUser = con.prepareStatement(sqlUser)) {
                    psUser.setString(1, defaultUsername);
                    psUser.setString(2, defaultPassword);
                    psUser.setString(3, "user"); // Role is always 'user'
                    psUser.setInt(4, customerId);
                    psUser.executeUpdate();
                }
            } else {
                throw new SQLException("Failed to retrieve generated customer ID.");
            }
            
            // --- Commit if both succeeded ---
            con.commit();
            // *** UPDATED REDIRECT: Passing the customerId to the JSP ***
            response.sendRedirect("adminDashboard.jsp?status=customerSuccess&newUsername=" + defaultUsername + "&newCustomerId=" + customerId); 

        } catch(SQLIntegrityConstraintViolationException e) {
            // Rollback and handle unique constraint violations
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) {}
            }
            if (e.getMessage().contains("meter_no")) {
                 response.sendRedirect("adminDashboard.jsp?status=meterError");
            } else if (e.getMessage().contains("username")) {
                 response.sendRedirect("adminDashboard.jsp?status=userExistsError");
            } else {
                 response.sendRedirect("adminDashboard.jsp?status=dbError");
            }
        } catch(IOException | ClassNotFoundException | SQLException e){
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) {}
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Customer creation failed: " + e.getMessage());
        } finally {
            if (con != null) {
                try { 
                    con.setAutoCommit(true);
                    con.close(); 
                } catch (SQLException e) {}
            }
        }
    }
}