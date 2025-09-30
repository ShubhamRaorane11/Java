import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // NOTE: In production, hardcoded credentials should be replaced by JNDI/Context parameters.

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/electricity_db","root","root123")) {

                String sql = "SELECT role, customer_id FROM users WHERE username=? AND password=?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setString(2, password);

                    try (ResultSet rs = ps.executeQuery()) {
                        if(rs.next()){
                            HttpSession session = request.getSession();
                            session.setAttribute("username", username);
                            session.setAttribute("role", rs.getString("role"));
                            // Store customerId as an Integer object
                            session.setAttribute("customerId", rs.getObject("customer_id")); 

                            if("admin".equals(rs.getString("role"))){
                                response.sendRedirect("adminDashboard.jsp");
                            } else {
                                response.sendRedirect("userDashboard.jsp");
                            }
                        } else {
                            response.sendRedirect("login.jsp?error=1");
                        }
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Login failed due to a server error.");
        }
    }
}