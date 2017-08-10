<%--
  Created by IntelliJ IDEA.
  User: gael
  Date: 08/08/2017
  Time: 15:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%

    for (Cookie cookie : request.getCookies()) {
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        Cookie ctxCookie = new Cookie(cookie.getName(), cookie.getValue());
        ctxCookie.setPath(request.getContextPath());
        ctxCookie.setMaxAge(0);
        response.addCookie(ctxCookie);

    }
%>
