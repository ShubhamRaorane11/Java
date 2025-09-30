import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;

@WebServlet("/bill")
public class BillServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int customerId;
        int units;

        try {
            // Robust input parsing (Recommended improvement)
            customerId = Integer.parseInt(request.getParameter("customer_id"));
            units = Integer.parseInt(request.getParameter("units"));
        } catch (NumberFormatException e) {
            response.sendRedirect("adminDashboard.jsp?status=billInputError");
            return;
        }

        String month = request.getParameter("month");
        // Rate is hardcoded; should be fetched from DB or config in production
        double rate = 7.5; 
        double amount = units * rate;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/electricity_db","root","root123")) {

                String sql = "INSERT INTO bills(customer_id,month,units,amount,status) VALUES(?,?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, customerId);
                    ps.setString(2, month);
                    ps.setInt(3, units);
                    ps.setDouble(4, amount);
                    ps.setString(5, "unpaid");
                    ps.executeUpdate();
                }
            }
            // Redirect with a success message flag
            response.sendRedirect("adminDashboard.jsp?status=billSuccess"); 
        } catch(SQLIntegrityConstraintViolationException e) {
            // Handle duplicate bill error (customer_id, month UNIQUE constraint)
            response.sendRedirect("adminDashboard.jsp?status=billDuplicateError");
        } catch(Exception e){
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Bill generation failed: " + e.getMessage());
        }
    }
}