<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
    <head>
        <title>Meal list</title>
        <style>
            table {
                border-collapse: collapse;
                border: 2px solid #000000;
            }
            td, th {
                padding: 10px;
                border: 2px solid #000000;
            }
            .red {
                color: red;
            }
            .green {
                color: green;
            }
        </style>
    </head>
    <body>
        <h3><a href="index.html">Home</a></h3>
        <hr>
        <h3>Meals</h3>
        <a href="meals?action=edit">Add Meal</a>
        <table>
            <thead>
                <tr>
                    <th>Date And Time</th>
                    <th>Description</th>
                    <th>Calories</th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <jsp:useBean id="meals" scope="request" type="java.util.List<ru.javawebinar.topjava.model.MealTo>"/>
                <jsp:useBean id="formatter" scope="request" type="java.time.format.DateTimeFormatter"/>
                <c:forEach var="meal" items="${meals}">
                    <tr class="${meal.excess ? 'red' : 'green'}">
                        <td>${formatter.format(meal.dateTime)}</td>
                        <td>${meal.description}</td>
                        <td>${meal.calories}</td>
                        <td><a href="meals?action=edit&mealId=${meal.id}">Update</a></td>
                        <td><a href="meals?action=delete&mealId=${meal.id}">Delete</a></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>
