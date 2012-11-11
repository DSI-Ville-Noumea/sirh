<%nc.mairie.technique.BasicProcess processErr = (nc.mairie.technique.BasicProcess)request.getSession().getAttribute("process"); 

if (processErr !=null && processErr.getTransaction() != null && processErr.getTransaction().isErreur()) {
try {
	out.println("<TR>");
	out.println("<TD align=\"center\">");
	out.println("  <FIELDSET style=\"border-color : red red red red;\">");
	out.println("  <TABLE border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
	out.println("  <TBODY>");
	out.println("    <TR>");
	
	String message = processErr.getTransaction().getMessageErreur();
	out.println("        <TD width=\"30\" class=\"sigp2-titre\">");
	
	if (message.toUpperCase().startsWith("ERR")) {
		out.println("		<IMG src=\"images/stop.gif\" width=\"20\" height=\"20\" border=\"0\">");
	} else { 
		out.println("		<IMG src=\"images/info.gif\" width=\"20\" height=\"20\" border=\"0\">");
	}
	out.println("      </TD>");
	out.println("        <TD valign=\"middle\" class=\"sigp2-titre\">"+message+"</TD>");
	out.println("      </TR>");
	out.println("  </TBODY>");
	out.println("</TABLE>");
	out.println("  </FIELDSET>");
	out.println("  </TD>");
	out.println("</TR>");
} catch (java.io.IOException e) {
	//CHAIS PAS QUOI LANCER
}
}%>
