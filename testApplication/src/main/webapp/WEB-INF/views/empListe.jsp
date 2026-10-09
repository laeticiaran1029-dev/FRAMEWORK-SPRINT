<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<html>
<body>
    <h1>Liste des employés</h1>
    <ul>
    <%
        List<String> noms = (List<String>) request.getAttribute("nom");
        List<String> prenoms = (List<String>) request.getAttribute("prenom");
        List<Integer> ages = (List<Integer>) request.getAttribute("age");
        for (int i = 0; i < noms.size(); i++) {
    %>
       <li>Employé <%= i + 1 %> : <%= noms.get(i) %> <%= prenoms.get(i) %>, <%= ages.get(i) %> ans</li>
    <%
        }
    %>
    </ul>
</body>
</html>
