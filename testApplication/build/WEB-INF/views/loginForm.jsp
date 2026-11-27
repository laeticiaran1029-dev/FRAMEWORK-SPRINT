<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
  <h1>Connexion</h1>
  <form action="${pageContext.request.contextPath}/emp/login" method="post">
    <input name="login" placeholder="Identifiant">
    <select name="role">
      <option value="prof">prof</option>
      <option value="etudiant">etudiant</option>
      <option value="visiteur">visiteur</option>
    </select>
    <button type="submit">Se connecter</button>
  </form>
</body>
</html>
