<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
  <h1>Profil</h1>
  <p>Connecté en tant que : <%= session.getAttribute("user") %>
    (rôle : <%= session.getAttribute("role") %>)</p>
  <a href="${pageContext.request.contextPath}/emp/logout">Se déconnecter</a>
</body>
</html>
