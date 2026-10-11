<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, config.model.Emp" %>
<html>
<body>
  <h1>Liste des employés</h1>
  <ul>
  <%
      List<Emp> emps = (List<Emp>) request.getAttribute("emps");
      if (emps != null) {
          for (int i = 0; i < emps.size(); i++) {
              Emp e = emps.get(i);
  %>
     <li>Employé <%= i + 1 %> : <%= e.getNom() %> <%= e.getPrenom() %>, <%= e.getAge() %> ans
    <% if (e.getDepartement() != null) { %>
      — département <%= e.getDepartement().getNom() %> (<%= e.getDepartement().getVille() %>)
    <% } %>
</li>
<%
          }
      }
%>

  </ul>
</body>
</html>
