<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
  <h1>Employés</h1>
  <form action="${pageContext.request.contextPath}/emp/liste" method="post">
    <div id="lignes"></div>
    <button type="button" onclick="ajouter()">+ Ajouter un employé</button>
    <button type="submit">Envoyer</button>
  </form>

  <script>
    var n = 0;
    function ajouter() {
      var d = document.createElement('div');
      d.innerHTML =
  '<input name="emps[' + n + '].nom" placeholder="Nom"> ' +
  '<input name="emps[' + n + '].prenom" placeholder="Prénom"> ' +
  '<input name="emps[' + n + '].age" type="number" placeholder="Age"> ' +
  '<input name="emps[' + n + '].departement.nom" placeholder="Département"> ' +
  '<input name="emps[' + n + '].departement.ville" placeholder="Ville">';

      document.getElementById('lignes').appendChild(d);
      n++;
    }
    ajouter();
  </script>
</body>
</html>
