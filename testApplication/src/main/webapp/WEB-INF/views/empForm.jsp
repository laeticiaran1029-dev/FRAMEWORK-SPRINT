<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
  <h1>Nouvel employé</h1>
  <form action="${pageContext.request.contextPath}/emp/info" method="get">
    <input name="nom" placeholder="Nom"><br>
    <input name="prenom" placeholder="Prénom"><br>
    <input name="age" type="number" placeholder="Age"><br>
    <button type="submit">Envoyer</button>
  </form>
</body>
</html>
