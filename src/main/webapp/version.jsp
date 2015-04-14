<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="java.net.InetAddress"%>
sirh.version=${version}<br/>
sirh.hostaddress=<%=InetAddress.getLocalHost().getHostAddress() %><br/>
sirh.canonicalhostname=<%=InetAddress.getLocalHost().getCanonicalHostName() %><br/>
sirh.hostname=<%=InetAddress.getLocalHost().getHostName() %><br/>
sirh.tomcat.version=<%= application.getServerInfo() %><br/>
sirh.tomcat.catalina_base=<%= System.getProperty("catalina.base") %><br/>


<% 
HttpSession theSession = request.getSession( false );

// print out the session id
if( theSession != null ) {
  //pw.println( "<BR>Session Id: " + theSession.getId() );
  synchronized( theSession ) {
    // invalidating a session destroys it
    theSession.invalidate();
    //pw.println( "<BR>Session destroyed" );
  }
}
%>
	
