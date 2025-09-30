import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;

@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int billId;
        try {
            billId = Integer.parseInt(request.getParameter("bill_id"));
        } catch (NumberFormatException e) {
            response.sendRedirect("viewBill.jsp?status=invalidBillId");
            return;
        }

        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/electricity_db","root","root123");
            
            // Critical: Start Transaction (Recommended improvement)
            con.setAutoCommit(false); 

            // 1. Insert payment
            String insertPayment = "INSERT INTO payments(bill_id,date,amount,mode) " +
                    "SELECT bill_id,CURDATE(),amount,'Online' FROM bills WHERE bill_id=?";
            try (PreparedStatement ps1 = con.prepareStatement(insertPayment)) {
                ps1.setInt(1, billId);
                ps1.executeUpdate();
            }

            // 2. Update bill status
            String updateBill = "UPDATE bills SET status='paid' WHERE bill_id=?";
            try (PreparedStatement ps2 = con.prepareStatement(updateBill)) {
                ps2.setInt(1, billId);
                ps2.executeUpdate();
            }

            // Commit if both succeed
            con.commit();
            response.sendRedirect("viewBill.jsp?status=paymentSuccess"); 

        } catch(IOException | ClassNotFoundException | SQLException e){
            // Rollback on any failure
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                }
            }
            response.sendRedirect("viewBill.jsp?status=paymentFailed");
        } finally {
            // Reset auto-commit and close connection
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}