<%@ page import="com.weblab2.weblab2.TableRow" %>
<%@ page errorPage="error.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Web Lab 2</title>

    <link rel="stylesheet" href="static/css/MainPage.css">
</head>

<body>

<header>
    <div class="name-and-group">
        Поленов Кирилл Александрович
        <br>
        PЗ213
    </div>

    <div class="title-and-variant">
        <div class="title">
            Web Lab 2
        </div>

        <div class="variant">
            Var 291830
        </div>
    </div>
</header>

<main>
    <table class="res-table">
        <thead id="table-head">
        <tr>
            <th>
                X
            </th>
            <th>
                Y
            </th>
            <th>
                R
            </th>
            <th>
                Результат
            </th>
        </tr>
        </thead>

        <% Object resultRow = request.getAttribute("newRow"); %>

        <tbody>
        <% if (resultRow != null) {
            TableRow newRow = (TableRow) resultRow;
        %>
            <tr>
                <td>
                    <%= newRow.getX() %>
                </td>

                <td>
                    <%= newRow.getY() %>
                </td>

                <td>
                    <%= newRow.getR() %>
                </td>

                <td>
                    <%= newRow.getResult() ? "Hit" : "Miss" %>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <br>
    <div class="home-link">
        <a href="index.jsp">На главную</a>
    </div>
</main>

<footer>
    <small> bilyardvmetro Porno Entertainment Inc. Enterprise Copyright. All Rights Reserved.</small>
</footer>

</body>
</html>
