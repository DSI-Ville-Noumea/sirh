<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Historique des affectations</TITLE>

<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 

<SCRIPT language="JavaScript">
		//afin de sélectionner un élément dans une liste
		function executeBouton(nom)
		{
		document.formu.elements[nom].click();
		}
		//function pour changement couleur arriere plan ligne du tableau
		function SelectLigne(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById(i).className="";
			} 
		 document.getElementById(id).className="selectLigne";
		}
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.agent.OeAGENTEmploisAffHisto"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Historique des affectations d'un agent</legend>
				    <br/>
				    <span style="position:relative;width:5px;"></span>
				    <span style="position:relative;width:60px;text-align:left;">Matricule</span>
					<span style="position:relative;width:300px;text-align:left;">Service</span>
					<span style="position:relative;width:75px;text-align: center;">Réf. arrêté</span>
					<span style="position:relative;width:80px;text-align: center;">Date début</span>
					<span style="position:relative;width:85px;text-align: center;">Date fin</span>
					<span style="position:relative;text-align:left;">Code école</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceHistoAff = 0;
							if (process.getListeHistoAffectation()!=null){
								for (int i = 0;i<process.getListeHistoAffectation().size();i++){
							%>
									<tr id="<%=indiceHistoAff%>" onmouseover="SelectLigne(<%=indiceHistoAff%>,<%=process.getListeHistoAffectation().size()%>)">										
										<td class="sigp2NewTab-liste" style="position:relative;width:60px;text-align: left;"><%=process.getVAL_ST_MATR(indiceHistoAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:300px;text-align: left;"><%=process.getVAL_ST_SERV(indiceHistoAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:75px;text-align: right;"><%=process.getVAL_ST_REF_ARR(indiceHistoAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceHistoAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_DATE_FIN(indiceHistoAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_CODE_ECOLE(indiceHistoAff)%></td>
									</tr>
									<%
									indiceHistoAff++;
								}
							}%>
						</table>	
						</div>	
			<BR/>
			<div style="text-align: center;">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Retour" name="<%=process.getNOM_PB_OK()%>">
			</div>	
		</FIELDSET>
	</FORM>
<%} %>
</BODY>
</HTML>