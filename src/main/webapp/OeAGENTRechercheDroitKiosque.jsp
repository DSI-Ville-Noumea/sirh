<%@page import="nc.mairie.gestionagent.dto.AgentWithServiceDto"%>
<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTRechercheDroitKiosque" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Sélection d'agents</TITLE>
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
		<SCRIPT language="JavaScript">
			//afin de sélectionner un élément dans une liste
			function executeBouton(nom)
			{
				document.formu.elements[nom].click();
			}
		</SCRIPT>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple">
		<%@ include file="BanniereErreur.jsp"%>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Sélection d'un agent">
				<LEGEND class="sigp2Legend">Sélection d'agents</LEGEND>
										
				<BR/><BR/>
				<INPUT type="hidden" id="service" size="4" name="SERVICE_CACHE" value="" class="sigp2-saisie">
				<INPUT type="hidden" id="idServiceADS" size="4" name="ENCORE_CACHE" value="" class="sigp2-saisie">
														
				<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
				<%=process.getTreeAgent() %>
				<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
				
	            <BR/><BR/>
	            <TABLE border="0" style="text-align : center;" cellpadding="0" cellspacing="0">
					<TBODY>
						<TR>
							<TD>
								<INPUT type="submit" value="Valider" class="sigp2-Bouton-100" accesskey="A" name="<%=process.getNOM_PB_OK()%>">
								<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" accesskey="B" name="<%=process.getNOM_PB_ANNULER()%>">
							</TD>
						</TR>
					</TBODY>
	            </TABLE>
	            <BR>
			</FIELDSET>
		</FORM>
	</BODY>
</HTML>