<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
  <h1>Zone protégée par rôle</h1>
  <p><%= session.getAttribute("user") %> (rôle : <%= session.getAttribute("role") %>)</p>
</body>
</html>
