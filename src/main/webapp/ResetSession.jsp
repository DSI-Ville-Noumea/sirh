<%--
  Created by IntelliJ IDEA.
  User: gael
  Date: 08/08/2017
  Time: 15:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Cookie sessionCookie = new Cookie("JSESSIONID", null);
    sessionCookie.setMaxAge(0);
    sessionCookie.setPath("/");
    response.addCookie(sessionCookie);
%>
