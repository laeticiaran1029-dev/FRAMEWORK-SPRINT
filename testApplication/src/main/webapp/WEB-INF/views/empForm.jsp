<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
  <h1>Employés</h1>
  <form action="${pageContext.request.contextPath}/emp/liste" method="get">
    <div id="lignes">
      <div class="ligne">
        <input name="nom" placeholder="Nom">
        <input name="prenom" placeholder="Prenom">
        <input name="age" type="number" placeholder="Age">
      </div>
    </div>
    <button type="button" onclick="ajouter()">+ Ajouter un employé</button>
    <button type="submit">Envoyer</button>
  </form>

  <script>
    function ajouter() {
      var modele = document.querySelector('.ligne');
      var copie = modele.cloneNode(true);
      copie.querySelectorAll('input').forEach(function (i) { i.value = ''; });
      document.getElementById('lignes').appendChild(copie);
    }
  </script>
</body>
</html>
