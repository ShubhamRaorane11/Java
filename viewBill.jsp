<%@ page import="java.sql.*" %>
<%@ page import="java.text.DecimalFormat" %>
<html>
<head>
    <title>Your Bills</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
    <h2>Your Bills</h2>

    <%
    // Security Check
    String username = (String)session.getAttribute("username");
    Object customerIdObj = session.getAttribute("customerId");
    
    if(username == null || customerIdObj == null || !(customerIdObj instanceof Integer)){
        response.sendRedirect("login.jsp");
        return;
    }

    Integer customerId = (Integer)customerIdObj;
    
    // Check for payment status feedback
    String status = request.getParameter("status");
    if (status != null) {
        if (status.equals("paymentSuccess")) {
            %> <p class="success-message">Payment successful! Bill status updated.</p> <%
        } else if (status.equals("paymentFailed")) {
            %> <p class="error-message">Payment failed due to a system error. Please try again.</p> <%
        }
    }
    
    // For formatting currency (Recommended improvement)
    DecimalFormat df = new DecimalFormat("#.00");

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/electricity_db","root","root123");
        ps = con.prepareStatement(
            "SELECT bill_id, month, units, amount, status FROM bills WHERE customer_id=?"
        );
        ps.setInt(1,customerId);
        rs = ps.executeQuery();
    %>

    <table border="1">
    <tr><th>Bill ID</th><th>Month</th><th>Units</th><th>Amount (?)</th><th>Status</th><th>Action</th></tr>
    <%
        while(rs.next()){
    %>
    <tr>
    <td><%=rs.getInt("bill_id")%></td>
    <td><%=rs.getString("month")%></td>
    <td><%=rs.getInt("units")%></td>
    <td><%=df.format(rs.getDouble("amount"))%></td>
    <td class="<%=rs.getString("status")%>-status"><%=rs.getString("status")%></td>
    <td>
    <% if("unpaid".equals(rs.getString("status"))){ %>
    <form action="payment" method="post">
    <input type="hidden" name="bill_id" value="<%=rs.getInt("bill_id")%>"/>
    <input type="submit" value="Pay" class="pay-button">
    </form>
    <% } else { %>
    Paid
    <% } %>
    </td>
    </tr>
    <%
        }
    %>
    </table>

    <%
    } catch(Exception e){
        e.printStackTrace();
        %> <p class="error-message">Could not fetch bills due to a database error.</p> <%
    } finally {
        // Essential: Close resources (Recommended improvement)
        try { if (rs != null) rs.close(); } catch (SQLException e) { /* log */ }
        try { if (ps != null) ps.close(); } catch (SQLException e) { /* log */ }
        try { if (con != null) con.close(); } catch (SQLException e) { /* log */ }
    }
    %>
    <br>
    <p class="dashboard-link"><a href="userDashboard.jsp">Go Back to Dashboard</a></p>
</div>
</body>
</html>