<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
    <head>
        <title>Meal</title>
        <style>
            td {
                padding: 10px;
            }
        </style>
    </head>
    <body>
        <h3><a href="index.html">Home</a></h3>
        <hr>
        <h3>Edit meal</h3>
        <form method="POST" action='meals' name="formEditMeal">
            <input type="hidden", name="mealId" value="<c:out value="${meal.id}"/>"/>
            <table>
                <tr>
                    <td>DateTime: </td>
                    <td><input type="datetime-local" name="dateTime" value="<c:out value="${meal.dateTime}"/>"/></td>
                </tr>
                <tr>
                    <td>Description: </td>
                    <td><input type="text" name="description" value="<c:out value="${meal.description}"/>"/></td>
                </tr>
                <tr>
                    <td>Calories: </td>
                    <td><input type="text" name="calories" value="<c:out value="${meal.calories}"/>"/></td>
                </tr>
            </table>
            <input type="submit" value="Save"/>
            <button onclick="window.history.back()" type="button">Cancel</button>
            </form>
    </body>
</html>
