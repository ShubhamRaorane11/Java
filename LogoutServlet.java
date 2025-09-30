import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Session ko invalidate karein
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // User ko login page par redirect karein
        response.sendRedirect("login.jsp");
    }
}