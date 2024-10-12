<%@ page import="java.util.List" %>
<%@ page import="com.weblab2.weblab2.TableRow" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
    <div class="coords-and-inputs-container">
        <div class="inputs">
            <form id="coords-form" method="post">
                <div class="x-input">
                    <h5>Выберите X</h5>
                    <br>
                    X:
                    <label class="x-selector">
                        <select id="x-selector">
                            <option class="x-var" value="">Select X</option>
                            <option class="x-var" value="-3">-3</option>
                            <option class="x-var" value="-2">-2</option>
                            <option class="x-var" value="-1">-1</option>
                            <option class="x-var" value="0">0</option>
                            <option class="x-var" value="1">1</option>
                            <option class="x-var" value="2">2</option>
                            <option class="x-var" value="3">3</option>
                            <option class="x-var" value="4">4</option>
                            <option class="x-var" value="5">5</option>
                        </select>
                    </label>
                </div>

                <div class="y-input">
                    <h5>Введите Y</h5>
                    <br>
                    <label>
                        Y:
                        <input id="YInput" type="text" placeholder="from -5 to 3" maxlength="6"/>
                    </label>
                </div>

                <div class="r-input">
                    <h5>Введите R</h5>
                    <br>

                    R:
                    <input class="r-checkbox" type="checkbox" id="r1" value="1">
                    <label for="r1">1</label>

                    <input class="r-checkbox" type="checkbox" id="r2" value="2">
                    <label for="r2">2</label>

                    <input class="r-checkbox" type="checkbox" id="r3" value="3">
                    <label for="r3">3</label>

                    <input class="r-checkbox" type="checkbox" id="r4" value="4">
                    <label for="r4">4</label>

                    <input class="r-checkbox" type="checkbox" id="r5" value="5">
                    <label for="r5">5</label>
                </div>

                <button id="submitButton" type="submit" form="coords-form"
<%--                        onmouseover="document.body.style.backgroundImage='url(https://preview.redd.it/name-you-favorite-movie-but-add-getting-jonkled-by-jonkler-v0-38cm2dx9f63d1.jpeg?auto=webp&s=1b99067f243c076da1e8b2df2e0fa0071dcdb721)'"--%>
<%--                        onmouseout="document.body.style.backgroundImage=''"--%>
                        onclick="play()">
                    Submit
                </button>
            </form>
        </div>

        <div class="coordinate-plane">
            <canvas id="coordinate-plane"></canvas>
        </div>
    </div>

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

        <% List<TableRow> tableRows = (List<TableRow>) application.getAttribute("rows"); %>

        <tbody>
        <% if (tableRows != null) {
            for (int i = tableRows.size() - 1; i >= 0 ; i--) { %>
                <tr>
                    <td>
                        <%= tableRows.get(i).getX() %>
                    </td>

                    <td>
                        <%= tableRows.get(i).getY() %>
                    </td>

                    <td>
                        <%= tableRows.get(i).getR() %>
                    </td>

                    <td>
                        <%= tableRows.get(i).getResult() ? "Hit" : "Miss" %>
                    </td>
                </tr>
            <% } %>
        <% } %>
        </tbody>
    </table>
</main>

<footer>
    <small> bilyardvmetro Porno Entertainment Inc. Enterprise Copyright. All Rights Reserved.</small>
</footer>

<script src="static/js/canvas.js"></script>
<script src="static/js/sendRequest.js"></script>

<audio id="52">
    <source src="static/audio/52.mp3" type="audio/mp3"/>
</audio>

<script>
    function play() {
        const audio = document.getElementById("52");
        audio.play();
    }
</script>

</body>
</html>