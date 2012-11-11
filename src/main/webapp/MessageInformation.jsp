<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE></TITLE>
<LINK rel="stylesheet" href="theme/sigp2.css" type="text/css">
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
</HEAD>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" class="sigp2-BODY"><jsp:useBean class="nc.mairie.commun.process.MessageInformation" id="process" scope="session"></jsp:useBean>
<%String message = process.getVAL_ST_MESSAGE();%>
<TABLE border="0" width="100%" height="100%" style="text-align : center;">
  <TBODY>
    <TR>
      <TD align="center">
      <FORM name="formu" method="POST">
      <TABLE border="1" width="590" class="TableauEcran">
        <COL span="1" align="center" valign="middle">
        <TBODY>
          <TR>
            <TD align="center"><% if (message.toUpperCase().startsWith("INF")) { %> <FONT size="6">Message d'information</FONT> <%} else { %> <FONT size="6">Message d'erreur</FONT> <%}%></TD>
          </TR>
          <TR>
            <TD align="center">
            <TABLE border="0">
              <TBODY>
                <TR>
                  <TD width="30"><% if (message.toUpperCase().startsWith("INF")) { %> <IMG src="images/info.gif" width="32" height="32" border="0"> <%} else { %> <IMG src="images/stop.gif" width="32" height="32" border="0"> <%}%></TD>
                  <TD valign="middle"><%=message%></TD>
                </TR>
              </TBODY>
            </TABLE>
            </TD>
          </TR>
          <TR>
            <TD align="center"><INPUT type="submit" name="<%=process.getNOM_PB_OK()%>" value="OK" class="Bouton">
								<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>"></TD>
          </TR>
        </TBODY>
      </TABLE>
      </FORM>
      </TD>
    </TR>
  </TBODY>
</TABLE>
</BODY>
</HTML>