<html>
<head>
    <title>User Login</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
    <h2>Login</h2>
    
    <form action="login" method="post">
        <label for="username">Username:</label> 
        <input type="text" id="username" name="username" required><br>
        
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required><br>
        
        <input type="submit" value="Login">
    </form>
    
    <% if(request.getParameter("error")!=null){ %>
    <p style="color:red; font-weight: bold;">Invalid Username or Password</p>
    <% } %>
</div>
</body>
</html>