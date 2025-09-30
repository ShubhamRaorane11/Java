<html>
<head>
    <title>User Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
    <%
    // Basic security check
    if (session.getAttribute("role") == null || !session.getAttribute("role").equals("user")) {
        response.sendRedirect("login.jsp");
        return;
    }
    String username = (String)session.getAttribute("username");
    %>
    <h2>Welcome, <%= username %></h2>
    <p class="dashboard-link"><a href="viewBill.jsp">View Bills</a></p>
    <p><a href="logout">Logout</a></p>
</div>
</body>
</html>