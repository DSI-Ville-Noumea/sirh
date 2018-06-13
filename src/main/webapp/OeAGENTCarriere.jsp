<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.metier.carriere.Grade"%>
<%@page import="nc.mairie.technique.Services"%>
<HTML>
	
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTCarriere" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<TITLE>Gestion des carrières</TITLE>
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
		<link rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" charset="utf-8" type="text/css">
		<script type="text/javascript" src="js/jquery-1.6.2.min.js" charset="utf-8"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.core.js" charset="utf-8"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.widget.js" charset="utf-8"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.position.js" charset="utf-8"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.autocomplete.js" charset="utf-8"></script>
		
		<SCRIPT language="JavaScript" charset="utf-8">
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
		
		</SCRIPT>		
		<%
		ArrayList<Grade> listeGrades = process.getListeGrade();
		
		String res = 	"<script language=\"javascript\" charset=\"utf-8\">\n"+
				"var availableGrades = new Array(\n";
		
		for (int i = 0; i < listeGrades.size(); i++){
			String iban = "";
			Grade grade = (Grade) listeGrades.get(i);
			if(Services.estNumerique(grade.getIban())){
				iban =Services.lpad(grade.getIban(), 7, "0");					
			}else{
				iban = grade.getIban();					
			}
			res+= "   \""+grade.getCodeGrade()+" "+grade.getLibGrade()+" "+iban+"\"";
			if (i+1 < listeGrades.size())
				res+=",\n";
			else	res+="\n";
		}
		
		res+=")</script>";
		%>
		<%=res%>
		<SCRIPT type="text/javascript" charset="utf-8">
			$(document).ready(function(){
				$("#listeGrades").autocomplete({source:availableGrades
				},{select: function(event, ui) { test(ui); }});
			});		
			function test(ui){
   			document.getElementById('listeGrades').value=ui.item.value;   			
   			executeBouton('NOM_PB_SELECT_GRADE');
			}	
		</SCRIPT>
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des carrières de l'agent</legend>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td>
								    <%if(process.getCalculPaye().equals("")){ %>
								    <INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>">
								    <%}%>
								</td>
								<td align="center" width="45px;">Code grade</td>
								<td align="center" width="45px;">Type contrat</td>
								<td align="center" width="45px;">Base horaire</td>
								<td align="center" width="65px;">IBA</td>
								<td align="center" width="45px;">INA</td>
								<td align="center" width="45px;">INM</td>
								<td align="center" width="85px;">Début</td>
								<td align="center" width="85px;">Fin</td>
								<td align="center" width="75px;">Ref. arrêté</td>
								<td>Statut</td>
							</tr>
							<%
							int indiceCarr = 0;
							if (process.getListeCarriere()!=null){
								for (int i = 0;i<process.getListeCarriere().size();i++){
							%>
									<tr id="<%=indiceCarr%>" onmouseover="SelectLigne(<%=indiceCarr%>,<%=process.getListeCarriere().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceCarr)%>">
											<%if(process.getCalculPaye().equals("")){ %>
				    						<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceCarr)%>">
				    						<%}%>
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceCarr)%>">				
										<%if(indiceCarr==process.getListeCarriere().size()-1){ %>
											<%if(process.getCalculPaye().equals("")){ %>
				    						<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceCarr)%>">
				    						<%}%>
										<%} %>
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_GRADE(indiceCarr)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_TYPE_CONTRAT(indiceCarr)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_BASE_HORAIRE(indiceCarr)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:65px;text-align: center;"><%=process.getVAL_ST_IBA(indiceCarr)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_INA(indiceCarr)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_INM(indiceCarr)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:85px;text-align: center;"><%=process.getVAL_ST_DEBUT(indiceCarr)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:85px;text-align: center;"><%=process.getVAL_ST_FIN(indiceCarr)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:75px;text-align: right;"><%=process.getVAL_ST_REF_ARR(indiceCarr)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;">&nbsp;<%=process.getVAL_ST_STATUT(indiceCarr)%></td>
									</tr>
									<%
									indiceCarr++;
								}
							}%>
						</table>	
						</div>	
				<BR/><BR/>
				<div style="text-align: center;">
					<INPUT type="submit" class="sigp2-Bouton-100" value="Avct Prev." name="<%=process.getNOM_PB_AVANCEMENT_PREV()%>">
				</div>		
				</FIELDSET>
				
				<BR/>
				
				
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>

		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
			<table>
				<tr>
					<td width="200px;">
						<span class="sigp2Mandatory">Statut :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_STATUTS() %>" onchange='executeBouton("<%=process.getNOM_PB_SELECT_STATUT()%>")' >
							<%=process.forComboHTML(process.getVAL_LB_STATUTS(), process.getVAL_LB_STATUTS_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<span class="<%= process.gradeObligatoire ? "sigp2Mandatory" : "sigp2" %>">Grade :</span>
					</td>
					<td>
						<INPUT id="listeGrades" class="sigp2-saisie" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_GRADE() %>"  style="margin-right:10px;width:350px" type="text" value="<%= process.getVAL_EF_GRADE() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Filière :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_ST_FILIERE() %>" size="70" type="text"  readonly="readonly" value="<%= process.getVAL_ST_FILIERE() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">IBA :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="7" name="<%= process.getNOM_EF_IBA() %>" size="7" type="text" <%= !process.saisieIba() ? "readonly='readonly'" : "" %> value="<%= process.getVAL_EF_IBA() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">INA :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_ST_INA() %>" size="4" type="text"  readonly="readonly" value="<%= process.getVAL_ST_INA() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">INM :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_ST_INM() %>" size="4" type="text"  readonly="readonly" value="<%= process.getVAL_ST_INM() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de début :</span>
					</td>
					<td>
						<%if(process.getCarriereCourante()!=null && !process.getCarriereCourante().isActive()&& !process.getCarriereCourante().getCodeCategorie().equals("8")){ %>
							<input class="sigp2-saisie" disabled="disabled"  maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>">				
						<%}else{ %>
							<input id="<%=process.getNOM_EF_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>" onblur='executeBouton("<%=process.getNOM_PB_INIT_TYPE_CONTRAT()%>")'>
							<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');">
						<%} %>
					</td>
				</tr>
				<%if(process.showDateFin){ %>
				<tr>
					<td>
						<span class="sigp2">Date de fin :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_FIN() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN()%>', 'dd/mm/y');">	
					</td>
				</tr>
				<%} %>
				<tr>
					<td>
						<span class="sigp2">Type de contrat :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CDICDD() %>">
							<%=process.forComboHTML(process.getVAL_LB_CDICDD(), process.getVAL_LB_CDICDD_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Régime :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_REGIMES() %>">
							<%=process.forComboHTML(process.getVAL_LB_REGIMES(), process.getVAL_LB_REGIMES_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Base horaire :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_BASE_HORAIRE() %>">
							<%=process.forComboHTML(process.getVAL_LB_BASE_HORAIRE(), process.getVAL_LB_BASE_HORAIRE_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Base règlement :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_BASE_REGLEMENT() %>">
							<%=process.forComboHTML(process.getVAL_LB_BASE_REGLEMENT(), process.getVAL_LB_BASE_REGLEMENT_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">ACC a-m-j :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ACC_ANNEES() %>" size="5" type="text" value="<%= process.getVAL_EF_ACC_ANNEES() %>">
						<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ACC_MOIS() %>" size="5" type="text" value="<%= process.getVAL_EF_ACC_MOIS() %>">
						<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ACC_JOURS() %>" size="5" type="text" value="<%= process.getVAL_EF_ACC_JOURS() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">BM a-m-j:</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_BM_ANNEES() %>" size="5" type="text" value="<%= process.getVAL_EF_BM_ANNEES() %>">
						<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_BM_MOIS() %>" size="5" type="text" value="<%= process.getVAL_EF_BM_MOIS() %>">
						<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_BM_JOURS() %>" size="5" type="text" value="<%= process.getVAL_EF_BM_JOURS() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Ref. arrêté :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="6" name="<%= process.getNOM_EF_REF_ARR() %>" size="6" type="text" value="<%= process.getVAL_EF_REF_ARR() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Date arrêté :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_ARR()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_ARR() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_ARR() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_ARR()%>', 'dd/mm/y');">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Motif :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIFS() %>">
							<%=process.forComboHTML(process.getVAL_LB_MOTIFS(), process.getVAL_LB_MOTIFS_SELECT()) %>
						</SELECT>	
					</td>
				</tr>
			</table>
			
			<% } else if(process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) || process.getVAL_ST_ACTION().equals(process.ACTION_VISUALISATION)) { %>
			<div>
		    	<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_VISUALISATION)) {%>
					<FONT color='red'>Veuillez valider votre choix.</FONT>
					<BR/>
				<% } %>
		    	<BR/>
		    	<table>
		    		<tr>
		    			<td width="150px;">
		    				<span class="sigp2">Statut : </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_ST_STATUT()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Grade: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_ST_GRADE()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Filière: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_ST_FILIERE()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">IBA: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_EF_IBA()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">INA: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_ST_INA()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">INM: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_ST_INM()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Date de début: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DEBUT()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Date de fin: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_FIN()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Type de contrat: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_ST_CDICDD()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Regime: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_ST_REGIME()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Base horaire: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_ST_HORAIRE()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Base règlement: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_ST_REGLEMENT()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">ACC a-m-j: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_EF_ACC_ANNEES()%></span>
							<span class="sigp2-saisie"><%=process.getVAL_EF_ACC_MOIS()%></span>
							<span class="sigp2-saisie"><%=process.getVAL_EF_ACC_JOURS()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">BM  a-m-j: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_EF_BM_ANNEES()%></span>
							<span class="sigp2-saisie"><%=process.getVAL_EF_BM_MOIS()%></span>
							<span class="sigp2-saisie"><%=process.getVAL_EF_BM_JOURS()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Ref. arrêté: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_EF_REF_ARR()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Date arrêté: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_ARR()%></span>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td>
							<span class="sigp2">Motif: </span>
		    			</td>
		    			<td>
							<span class="sigp2-saisie"><%=process.getVAL_ST_MOTIF()%></span>
		    			</td>
		    		</tr>
		    	</table>
		</div>
		<%} else if(process.getVAL_ST_ACTION().equals(process.ACTION_REOUVERTURE)) { %>
			<FONT color="red"> Réouverture de la carrière précédente ?</FONT>
		<% }else if(process.getVAL_ST_ACTION().equals(process.ACTION_AVCT_PREV)){ %>
			<div>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Grade: </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_GRADE()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Filière: </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_FILIERE()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nouv. IBA: </span>
				<span class="sigp2-saisie"><%=process.getVAL_EF_IBA()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nouv. INA: </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_INA()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nouv. INM: </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_INM()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Date du prochain grade : </span>
				<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DEBUT()%></span>				
				<%if(process.showAccBM){ %>
					<BR/><BR/>
					<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nouv. Grade: </span>
					<span class="sigp2-saisie"><%=process.getVAL_ST_NOUV_GRADE()%></span>
					<BR/><BR/>
					<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nouv. Grade Gen.: </span>
					<span class="sigp2-saisie"><%=process.getVAL_ST_NOUV_GRADE_GEN()%></span>
					<BR/><BR/>
					<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nouv. Classe: </span>
					<span class="sigp2-saisie"><%=process.getVAL_ST_NOUV_CLASSE()%></span>
					<BR/><BR/>
					<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nouv. Echelon: </span>
					<span class="sigp2-saisie"><%=process.getVAL_ST_NOUV_ECHELON()%></span>
					<BR/><BR/>
					<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nouv. ACC a-m-j: </span>
					<span class="sigp2-saisie"><%=process.getVAL_EF_ACC_ANNEES()%></span>
					<span class="sigp2-saisie"><%=process.getVAL_EF_ACC_MOIS()%></span>
					<span class="sigp2-saisie"><%=process.getVAL_EF_ACC_JOURS()%></span>
					<BR/><BR/>
					<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nouv. BM  a-m-j: </span>
					<span class="sigp2-saisie"><%=process.getVAL_EF_BM_ANNEES()%></span>
					<span class="sigp2-saisie"><%=process.getVAL_EF_BM_MOIS()%></span>
					<span class="sigp2-saisie"><%=process.getVAL_EF_BM_JOURS()%></span>
				<%} %>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Type avancement : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_TYPE_AVCT()%></span>
			</div>
			<% }else if(process.getVAL_ST_ACTION().equals(process.ACTION_AVCT_PREV_CC)){ %>
			<div>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Montant prime: </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_GRADE()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Date début prime : </span>
				<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DEBUT()%></span>		
			</div>
		<%} %>
			<BR/>
			<div style="width:100%; text-align:center;">
				<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_VISUALISATION) && !process.getVAL_ST_ACTION().equals(process.ACTION_AVCT_PREV)) { %>
					<INPUT type="submit"
						class="sigp2-Bouton-100" value="<%= process.getVAL_ST_ACTION().equals(process.ACTION_REOUVERTURE) ? "Oui" : "Valider" %>"
						name="<%=process.getNOM_PB_VALIDER()%>">
				<% } %>
					<INPUT type="submit"
						class="sigp2-Bouton-100" value="<%= process.getVAL_ST_ACTION().equals(process.ACTION_REOUVERTURE) ? "Non" : "Annuler" %>"
						name="<%=process.getNOM_PB_ANNULER()%>">
			</div>
		</FIELDSET>
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SELECT_GRADE()%>" value="x">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SELECT_STATUT()%>" value="x">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_INIT_TYPE_CONTRAT()%>" value="x">
		
		</FORM>
		<% } %>
<%} %>	
	</BODY>
</HTML>