<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.carriere.Grade"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.metier.Const"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des grades</TITLE>
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
		<SCRIPT language="JavaScript">
		//afin de sélectionner un élément dans une liste
		function executeBouton(nom)
		{
			document.formu.elements[nom].click();
		}

		// afin de mettre le focus sur une zone précise
		function setfocus(nom)
		{
		if (document.formu.elements[nom] != null)
		document.formu.elements[nom].focus();
		}
		//function pour changement couleur arriere plan ligne du tableau
		function SelectLigne(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById(i).className="";
			} 
		 document.getElementById(id).className="selectLigne";
		}
		//function pour changement couleur arriere plan ligne du tableau
		function SelectLigneGrille(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById(i).className="";
			} 
		 document.getElementById(id).className="selectLigne";
		}
		
		</SCRIPT>
		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEGrade" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<br/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Liste des grilles</legend>
		   	<br/>					
			<span style="margin-left: 5px;">
			<%if(process.getCalculPaye().equals("")){ %>
				<INPUT title="créer" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_GRILLE()%>">
			<%} %>
			</span>
			<span style="margin-left: 30px;">Nom</span>
			<span style="margin-left: 320px;">Actif</span>
			<br/>
			<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
				<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceGrille = 0;
							if (process.getListeGrille()!=null){
								for (int i = 0;i<process.getListeGrille().size();i++){
									Grade grille = (Grade) process.getListeGrille().get(i);
							%>
									<tr id="<%=indiceGrille%>" onmouseover="SelectLigneGrille(<%=indiceGrille%>,<%=process.getListeGrille().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:50px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_GRILLE(indiceGrille)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_GRILLE(indiceGrille)%>">
										<%if(process.getCalculPaye().equals("")){ %>
											<%if(grille.getCodeActif().equals("A")){ %>
				    						<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_GRILLE(indiceGrille)%>">
				    						<%} %>
				    					<%}%>
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:345px;text-align: left;"><%=process.getVAL_ST_LIB_GRADE_GRILLE(indiceGrille)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_ACTIF_GRADE_GRILLE(indiceGrille)%></td>
									</tr>
									<%
									indiceGrille++;
								}
							}%>
				</table>	
			</div>		
		</FIELDSET>
		
		<INPUT type="submit" style="display:none" name="<%=process.getNOM_PB_SELECT_GRADE_GENERIQUE()%>" value="x">
		
		<% if (!Const.CHAINE_VIDE.equals(process.getVAL_ST_ACTION_GRILLE())) { %>
			<% if (process.ACTION_CREATION_GRILLE.equals(process.getVAL_ST_ACTION_GRILLE())&& !process.ACTION_CREATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE())) { %>
				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION_GRILLE()%></legend>
					<table>
						<tr>
							<td width="130px">
								<span class="sigp2Mandatory">Grade générique :</span>
							</td>
							<td>
								<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_GRADE_GENERIQUE() %>" onchange='executeBouton("<%=process.getNOM_PB_SELECT_GRADE_GENERIQUE()%>")'>
									<%=process.forComboHTML(process.getVAL_LB_GRADE_GENERIQUE(), process.getVAL_LB_GRADE_GENERIQUE_SELECT()) %>
								</SELECT>
							</td>
						</tr>
						<tr>
							<td>
								<span class="sigp2Mandatory">Grade :</span>
							</td>
							<td>
								<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_GRADE() %>" size="50" type="text" value="<%= process.getVAL_EF_GRADE() %>" />
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_GRILLE()%>">
								<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
							</td>
						</tr>
					</table>
				</FIELDSET>
					
			<% } else { %>
					
				<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
					
					<legend class="sigp2Legend">Liste des grades</legend>
					
					<%if(process.getListeGrade().size()!=0){ %>
					<br/>
				    <span style="margin-left: 80px;">Code</span>
					<span style="margin-left: 15px;">Libellé</span>
					<span style="margin-left: 340px;">IBA</span>
					<span style="margin-left: 80px;">Grade suivant</span>
					<br/>
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceGrade = 0;
							if (process.getListeGrade()!=null){
								for (int i = 0;i<process.getListeGrade().size();i++){
									Grade grade = (Grade)process.getListeGrade().get(i);
							%>
									<tr id="<%=indiceGrade%>" onmouseover="SelectLigne(<%=indiceGrade%>,<%=process.getListeGrade().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_GRADE(indiceGrade)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_GRADE(indiceGrade)%>">
											<%if(grade.getCodeActif().equals("A") && process.getCalculPaye().equals("")){ %>
					    						<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_GRADE(indiceGrade)%>">
						    					<%if(indiceGrade==process.getListeGrade().size()-1){ %>
												<INPUT title="créer grade suivant" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_GRADE_SUIVANT(indiceGrade)%>">
												<%} %>	
											<%} %>							
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: left;"><%=process.getVAL_ST_CODE_GRADE(indiceGrade)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:380px;text-align: left;"><%=process.getVAL_ST_LIB_GRADE(indiceGrade)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:100px;text-align: left;"><%=process.getVAL_ST_IBA_GRADE(indiceGrade)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_GRADE_SUIVANT(indiceGrade)%></td>
									</tr>
									<%
									indiceGrade++;
								}
							}%>
						</table>	
						</div>	
						<%} %>
				<BR/><BR/>
					<% if (process.ACTION_CREATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ||
							process.ACTION_MODIFICATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ||
								process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE())){  %>
								
						<table>
							<tr>
								<td width="130px;">
									<span class="sigp2Mandatory">Grade générique :</span>
								</td>
								<td>
									<SELECT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_LB_GRADE_GENERIQUE() %>" >
										<%=process.forComboHTML(process.getVAL_LB_GRADE_GENERIQUE(), process.getVAL_LB_GRADE_GENERIQUE_SELECT()) %>
									</SELECT>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2Mandatory">Grade :</span>
								</td>
								<td>
									<INPUT class="sigp2-saisie" disabled="disabled" maxlength="50" name="<%= process.getNOM_EF_GRADE() %>" size="50" type="text" value="<%= process.getVAL_EF_GRADE() %>" />
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2">Classe:</span>
								</td>
								<td>
									<SELECT class="sigp2-saisie" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> name="<%= process.getNOM_LB_CLASSE() %>">
										<%=process.forComboHTML(process.getVAL_LB_CLASSE(), process.getVAL_LB_CLASSE_SELECT()) %>
									</SELECT>
								</td>
							</tr>
							<tr>
								<td>
								<span class="sigp2">Echelon :</span>
								</td>
								<td>
									<SELECT class="sigp2-saisie" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> name="<%= process.getNOM_LB_ECHELON() %>">
										<%=process.forComboHTML(process.getVAL_LB_ECHELON(), process.getVAL_LB_ECHELON_SELECT()) %>
									</SELECT>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2Mandatory">Code grade :</span>
								</td>
								<td>
									<INPUT class="sigp2-saisiemajuscule" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> maxlength="4" name="<%= process.getNOM_EF_CODE_GRADE() %>" size="10" type="text" value="<%= process.getVAL_EF_CODE_GRADE() %>" <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>/>
								</td>
							</tr>
							<% if(process.ACTION_MODIFICATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE())){ %>
							<tr>
								<td>
									<span class="sigp2">Code grade suivant :</span>
								</td>
								<td>
									<INPUT class="sigp2-saisiemajuscule" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> maxlength="4" name="<%= process.getNOM_EF_CODE_GRADE_SUIVANT() %>" size="10" type="text" value="<%= process.getVAL_EF_CODE_GRADE_SUIVANT() %>" <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>/>
								</td>
							</tr>
							<%} %>
							<tr>
								<td>
									<span class="sigp2Mandatory">Montant forfait :</span>
								</td>
								<td>
									<INPUT class="sigp2-saisie" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> maxlength="9" name="<%= process.getNOM_EF_MONTANT_FORFAIT() %>" size="10" type="text" value="<%= process.getVAL_EF_MONTANT_FORFAIT() %>" <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>/>	
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2Mandatory">Montant prime :</span>
								</td>
								<td>
									<INPUT class="sigp2-saisie" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> maxlength="7" name="<%= process.getNOM_EF_MONTANT_PRIME() %>" size="10" type="text" value="<%= process.getVAL_EF_MONTANT_PRIME() %>" <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>/>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2Mandatory">IBA :</span>
								</td>
								<td>
									<SELECT class="sigp2-saisie" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> name="<%= process.getNOM_LB_BAREME() %>" <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>>
										<%=process.forComboHTML(process.getVAL_LB_BAREME(), process.getVAL_LB_BAREME_SELECT()) %>
									</SELECT>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2Mandatory">Code grille:</span>
								</td>
								<td>
									<input type="radio" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> <%= process.forRadioHTML(process.getNOM_RG_CODE_GRILLE(), process.getNOM_RB_NC()) %> <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>><span class="sigp2Mandatory">NC</span> 
									<input type="radio" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> <%= process.forRadioHTML(process.getNOM_RG_CODE_GRILLE(), process.getNOM_RB_FR()) %> <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>><span class="sigp2Mandatory">FR</span>  
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2Mandatory">ACC:</span>
								</td>
								<td>
									<input type="radio" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> <%= process.forRadioHTML(process.getNOM_RG_ACC(), process.getNOM_RB_OUI()) %> <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>><span class="sigp2Mandatory">Oui</span> 
									<input type="radio" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> <%= process.forRadioHTML(process.getNOM_RG_ACC(), process.getNOM_RB_NON()) %> <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>><span class="sigp2Mandatory">Non</span>  
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2Mandatory">BM:</span>
								</td>
								<td>
									<input type="radio" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> <%= process.forRadioHTML(process.getNOM_RG_BM(), process.getNOM_RB_OUI()) %> <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>><span class="sigp2Mandatory">Oui</span>  
									<input type="radio" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> <%= process.forRadioHTML(process.getNOM_RG_BM(), process.getNOM_RB_NON()) %> <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>><span class="sigp2Mandatory">Non</span> 
								</td>
							</tr>
							<tr>
								<td>
								<span class="sigp2">Motif avct :</span>
								</td>
								<td>
									<SELECT class="sigp2-saisie" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> name="<%= process.getNOM_LB_MOTIF_AVCT() %>">
										<%=process.forComboHTML(process.getVAL_LB_MOTIF_AVCT(), process.getVAL_LB_MOTIF_AVCT_SELECT()) %>
									</SELECT>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2Mandatory">Durée min.:</span>
								</td>
								<td>
									<INPUT class="sigp2-saisie" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> maxlength="2" name="<%= process.getNOM_EF_DUREE_MIN() %>" size="2" type="text" value="<%= process.getVAL_EF_DUREE_MIN() %>" <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>/>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2Mandatory">Durée moy.:</span>
								</td>
								<td>
									<INPUT class="sigp2-saisie" maxlength="2" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> name="<%= process.getNOM_EF_DUREE_MOY() %>" size="2" type="text" value="<%= process.getVAL_EF_DUREE_MOY() %>" <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>/>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2Mandatory">Durée max.:</span>
								</td>
								<td>
									<INPUT class="sigp2-saisie" maxlength="2" <%=process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE()) ? "disabled='disabled'" : "" %> name="<%= process.getNOM_EF_DUREE_MAX() %>" size="2" type="text" value="<%= process.getVAL_EF_DUREE_MAX() %>" <%=MairieUtils.getDisabled(request, process.getNomEcran()) %>/>
								</td>
							</tr>
						
						<% if (process.ACTION_MODIFICATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE())||process.ACTION_CREATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE())){  %>
							<tr>
								<td colspan="2" align="center">
									<INPUT type="submit" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2-Bouton-100") %>" value="Valider" name="<%=process.getNOM_PB_VALIDER_GRADE()%>">
									<INPUT type="submit" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2-Bouton-100") %>" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
								</td>
							</tr>
						<% } else if (process.ACTION_CONSULTATION_GRADE.equals(process.getVAL_ST_ACTION_GRADE())){  %>
							<tr>
								<td colspan="2" align="center">
						    		<INPUT type="submit" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2-Bouton-100") %>" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
								</td>
							</tr>
						<% }  %>						
						</table>
					<%} %>						
				</FIELDSET>
				</FORM>
			<% } %>
		<% } %>
		<BR/>
		
	</BODY>
</HTML>