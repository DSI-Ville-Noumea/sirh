<%@page import="java.net.InetAddress"%>
sirh.version=${version}
sirh.localhost.hostaddress=<%=InetAddress.getLocalHost().getHostAddress() %>
sirh.localhost.canonicalhostname=<%=InetAddress.getLocalHost().getCanonicalHostName() %>
sirh.localhost.hostname=<%=InetAddress.getLocalHost().getHostName() %>
tomcat.version=<%= application.getServerInfo() %>
tomcat.catalina_base=<%= System.getProperty("catalina.base") %>

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
	
