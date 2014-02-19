<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Agents homonymes</TITLE>

<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 

<SCRIPT language="JavaScript">
//afin de s�lectionner un �l�ment dans une liste
function executeBouton(nom)
{
document.formu.elements[nom].click();
}
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.agent.OeAGENTHomonyme"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
		    <legend class="sigp2Legend">Agents homonymes</legend>
		    <br/>
			<span class="sigp2">D'autres agents nomm�s <b><%=process.getAgentCourant().getNomUsage()%>
			<%=process.getAgentCourant().getPrenom()%> </b>ont �t� trouv�s dans la	base.<BR/>
			Si l'un d'entre eux correspond � celui que vous souhaitiez cr�er, merci de le s�lectionner.<BR/>
			Dans le cas contraire, vous pouvez cr�er un nouvel agent.<BR/>
</span> <BR />
			<span style="margin-left:5px;position:relative;width:65px;">Matricule</span>
			<span style="position:relative;width:412px;">Nom</span>
			<span style="position:relative;width:412px;">Pr�nom</span>
			<span style="position:relative;width:90px;">Date naissance</span>
		
			<span class="sigp2-titre">
				<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_AGENT_HOMONYME() %>" size="10" style="width:1000px;font-family : monospace;">
					<%=process.forComboHTML(process.getVAL_LB_AGENT_HOMONYME(), process.getVAL_LB_AGENT_HOMONYME_SELECT()) %>
				</SELECT>
			</span>
			<BR/><BR/>
		</FIELDSET>
		<BR/>

		<FIELDSET class="sigp2Fieldset" style="text-align:center;width:1030px">
			<%if (process.isCreation()){ %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Cr�er" name="<%=process.getNOM_PB_CREER_AGT_HOMONYME()%>">
			<%}else{ %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_CREER_AGT_HOMONYME()%>">
			<%} %>
			<INPUT type="submit" class="sigp2-Bouton-100" value="S�lectionner" name="<%=process.getNOM_PB_RECUP_AGENT_SELECTIONNE()%>">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		</FIELDSET>
	</FORM>
</BODY>
</HTML>