<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<html>
<body>
    <h1>Liste des produits</h1>

    <ul>
    <%
        List<String> maListe = (List<String>) request.getAttribute("maListe");
        if (maListe != null) {
            for (String item : maListe) {
    %>
        <li><%= item %></li>
    <%
            }
        }
    %>
    </ul>

    <a href="${pageContext.request.contextPath}/">Retour à l'accueil</a>
</body>
</html>
