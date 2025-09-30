<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
    <h2>Admin Dashboard</h2>
    
    <% 
    // --- 1. Security Check ---
    if (session.getAttribute("role") == null || !session.getAttribute("role").equals("admin")) {
        response.sendRedirect("login.jsp");
        return;
    } 
    
    // --- 2. Status/Feedback Logic ---
    String status = request.getParameter("status");
    String newUsername = request.getParameter("newUsername"); 
    String newCustomerId = request.getParameter("newCustomerId"); // <-- Retrieves the Customer ID
    
    if (status != null) {
        if (status.equals("customerSuccess")) {
            %> 
            <p class="success-message">
                  Customer and User created successfully! <br>
                **Customer ID:** <b><%= newCustomerId %></b> (Use this ID for generating bills!) <br>
                **Default Login:** Username: <b><%= newUsername %></b>, Password: <b>12345</b>
            </p> 
            <%
        } else if (status.equals("meterError")) {
            %> <p class="error-message">? Error: Meter number already exists.</p> <%
        } else if (status.equals("userExistsError")) {
            %> <p class="error-message">? Error: Default username ('<%= newUsername %>') already taken. Try a different customer name.</p> <%
        } else if (status.equals("billSuccess")) {
            %> <p class="success-message">? Bill generated successfully!</p> <%
        } else if (status.equals("billInputError")) {
            %> <p class="error-message">? Error: Customer ID or Units must be valid numbers.</p> <%
        } else if (status.equals("billDuplicateError")) {
            %> <p class="error-message">? Error: A bill already exists for this customer and month.</p> <%
        } else if (status.equals("dbError")) {
            %> <p class="error-message">? Error: Database constraint violation (check inputs).</p> <%
        }
    }
    %>

    
    <h3>Add Customer</h3>
    <form action="customer" method="post">
        <label for="name">Name:</label> <input type="text" id="name" name="name" required><br>
        <label for="meter_no">Meter No:</label> <input type="text" id="meter_no" name="meter_no" required><br>
        <label for="email">Email:</label> <input type="email" id="email" name="email" required><br>
        <label for="phone">Phone:</label> <input type="text" id="phone" name="phone"><br>
        <input type="submit" value="Add Customer">
    </form>

    
    <h3>Generate Bill</h3>
    <form action="bill" method="post">
        <label for="customer_id">Customer ID:</label> <input type="number" id="customer_id" name="customer_id" required><br>
        <label for="month">Month:</label> <input type="text" id="month" name="month" placeholder="e.g., April 2025" required><br>
        <label for="units">Units:</label> <input type="number" id="units" name="units" required><br>
        <input type="submit" value="Generate Bill">
    </form>

    
    <p><a href="logout">Logout</a></p>

</div>
</body>
</html>