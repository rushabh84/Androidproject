<%-- 
    Document   : dashboard
    Created on : Apr 13, 2017, 9:53:20 PM
    Author     : Rush
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        
        <h1>Welcome to me Dashboard Analytics Page</h1>
        <br><br>
        <%
            
            Map<Integer, List> dashDisplay = (Map)request.getAttribute("populationmap"); //because request.getAttribute returns an Object so convert it to Map
            ArrayList <String> analytics = (ArrayList)request.getAttribute("analyticsoperation");
        %><table style="width: 80%">
            <tr>
                <th>number</th>
                <th>User-Agent</th>
                <th>Host</th>
                <th>Time-android request</th>
                <th>Year-parameter</th>
                <th>Age-parameter</th>
                <th>Total objects returned by API</th>
                <th>Objects returned to Android</th>
                <th>Total Population</th>
                <th>Female Population</th>
                <th>Male Population</th>
            </tr>
        <%
            for(Integer keys: dashDisplay.keySet()){%>
            <tr>
                <th><%=keys+1%></th>
                <th><%=dashDisplay.get(keys).get(0)%></th>
                 <th><%=dashDisplay.get(keys).get(1)%></th>
                 <th><%=dashDisplay.get(keys).get(2)%></th>
                 <th><%=dashDisplay.get(keys).get(3)%></th>
                 <th><%=dashDisplay.get(keys).get(4)%></th>
                 <th><%=dashDisplay.get(keys).get(5)%></th>
                <th><%=dashDisplay.get(keys).get(6)%></th>
                <th><%=dashDisplay.get(keys).get(7)%></th>
                <th><%=dashDisplay.get(keys).get(8)%></th>
                <th><%=dashDisplay.get(keys).get(9)%></th>
                
            </tr><%}%>
        </table>
        <br><br>
    <hi>Analytics performed on the log information in the above table</h1>
    <table style="width: 80%">
        <%
            for(String s: analytics){%>
            <tr>
                <th><%=s%></th>
            </tr><%}%>
    </table>
    
    </body>
</html>
