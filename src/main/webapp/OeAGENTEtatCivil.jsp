<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<!-- Sample JSP file -->
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR"
	content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Etat civil d'un agent</TITLE>
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
<SCRIPT language="javascript" src="js/GestionMenu.js"></SCRIPT>
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

</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTEtatCivil" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="changerMenuHaut('Module_agent_donneesPerso');window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp"%>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
			<legend class="sigp2Legend">Etat civil</legend>
			<table>
				<tr>
					<td>
						<span class="sigp2Mandatory" style="width:60px;margin-right:10px">Collectivité :</span>
					</td>
					<td>
						<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_COLLECTIVITE() %>" style="width:200px;">
							<%=process.forComboHTML(process.getVAL_LB_COLLECTIVITE(), process.getVAL_LB_COLLECTIVITE_SELECT())%>
						</SELECT>
					</td>
				</tr>
			</table>
			<table>
				<tr>
					<td width="65px;" valign="bottom">
						<span class="sigp2-saisie">
							<%
							String photo = process.getVAL_ST_PHOTO().equals("") ? "aucune": process.getVAL_ST_PHOTO();
							%>
							<IMG src="<%=photo%>" width="60" height="75"
								border="0" align="middle" alt="<%=photo%>">
						</span>
					</td>
					<td width="60px;" align="left">
						<span class="sigp2Mandatory">Civilité :</span>
					</td>
					<td width="90px;" align="left">
						<SELECT onchange='executeBouton("<%=process.getNOM_PB_CIVILITE() %>")' <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> class="sigp2-saisie" style="width:50px;" name="<%= process.getNOM_LB_CIVILITE() %>">
							<%=process.forComboHTML(process.getVAL_LB_CIVILITE(), process.getVAL_LB_CIVILITE_SELECT())%>
						</SELECT>
					</td>
					<td width="90px;" align="left">
						<span class="sigp2Mandatory">Prénom :</span>
					</td>
					<td width="190px;">
						<INPUT class="sigp2-saisie" size="30" maxlength="50" type="text" name="<%= process.getNOM_EF_PRENOM() %>" value="<%= process.getVAL_EF_PRENOM() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					</td>
					<td width="120px;" align="left">
						<span class="sigp2Mandatory">Nom patronymique :</span>
					</td>
					<td align="left">
						<INPUT class="sigp2-saisie" size="30" maxlength="50" type="text" name="<%= process.getNOM_EF_NOM_PATRONYMIQUE() %>" value="<%= process.getVAL_EF_NOM_PATRONYMIQUE() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
						<span class="sigp2">Sexe :</span>
					</td>
					<td>
						<span class="sigp2-statique" ><%=process.getVAL_ST_SEXE()%></span>
					</td>
					<td>
						<span class="sigp2">Prénom d'usage :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" size="30" maxlength="50" type="text" name="<%= process.getNOM_EF_PRENOM_USAGE() %>" value="<%= process.getVAL_EF_PRENOM_USAGE() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					</td>
					<td>
						<span class="sigp2">Nom marital :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" size="30" maxlength="50" type="text" name="<%= process.getNOM_EF_NOM_MARITAL() %>" value="<%= process.getVAL_EF_NOM_MARITAL() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>
						<span class="sigp2">Nom d'usage :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" size="30" maxlength="50" type="text" name="<%= process.getNOM_EF_NOM_USAGE() %>" value="<%= process.getVAL_EF_NOM_USAGE() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>				
					</td>
				</tr>
			</table>
			<BR/>
			<table>
				<tr>
					<td width="65px;">&nbsp;</td>
					<td width="180px;">
						<span class="sigp2Mandatory"> Situation familiale : </span>
					</td>
					<td width="170px;">
						<span class="sigp2-saisie">
							<SELECT style="width: 90px;" onchange='executeBouton("<%=process.getNOM_PB_SITUATION() %>")'  <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> class="sigp2-saisie" name="<%= process.getNOM_LB_SITUATION() %>">
								<%=process.forComboHTML(process.getVAL_LB_SITUATION(), process.getVAL_LB_SITUATION_SELECT())%>
							</SELECT>
						</span>
					</td>
					<td width="180px;">				
						<span class="sigp2Mandatory"> Nationalité : </span>
					</td>
					<td>
						<span class="sigp2-saisie">
							<SELECT style="width: 90px;" onchange='executeBouton("<%=process.getNOM_PB_NATIONALITE() %>")' <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> class="sigp2-saisie" name="<%= process.getNOM_LB_NATIONALITE() %>">
								<%=process.forComboHTML(process.getVAL_LB_NATIONALITE(), process.getVAL_LB_NATIONALITE_SELECT())%>
							</SELECT>
						</span>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
						<span class="sigp2Mandatory"> Date de naissance : </span>
					</td>
					<td>
						<span>
							<INPUT id="<%=process.getNOM_EF_DATE_NAISSANCE()%>" class="sigp2-saisie" maxlength="10" style="width: 90px;"	name="<%= process.getNOM_EF_DATE_NAISSANCE() %>" type="text" value="<%= process.getVAL_EF_DATE_NAISSANCE() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
							<IMG class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/calendrier.gif" onclick="return showCalendar('<%=process.getNOM_EF_DATE_NAISSANCE()%>', 'dd/mm/y');" hspace="5">
						</span>
					</td>
					<td>
						<span class="sigp2Mandatory">Lieu de naissance :</span>
					</td>
					<td>			
						<img  style="cursor:pointer;" src="images/loupe.gif" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" height="20px" width="20px" onclick='executeBouton("<%=process.getNOM_PB_LIEU_NAISSANCE() %>")'>				
						<span class="sigp2-saisie"><%=process.getVAL_ST_LIEU_NAISSANCE()%></span>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
						<span class="sigp2Mandatory">Date d'arrivée sur le territoire : </span>
					</td>
					<td>
						<span>
							<input id="<%=process.getNOM_EF_DATE_ARRIVEE_TERRITOIRE()%>" class="sigp2-saisie" maxlength="10" style="width: 90px;"	name="<%= process.getNOM_EF_DATE_ARRIVEE_TERRITOIRE() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> type="text" value="<%= process.getVAL_EF_DATE_ARRIVEE_TERRITOIRE()%>">
							<img src="images/calendrier.gif" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" onclick="return showCalendar('<%=process.getNOM_EF_DATE_ARRIVEE_TERRITOIRE()%>', 'dd/mm/y');" hspace="5">
						</span>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
						<span class="sigp2Mandatory">Date de première embauche : </span>
					</td>
					<td>
						<span>
							<input id="<%=process.getNOM_EF_DATE_PREM_EMB()%>" class="sigp2-saisie" maxlength="10" style="width: 90px;"	name="<%= process.getNOM_EF_DATE_PREM_EMB() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> type="text" value="<%= process.getVAL_EF_DATE_PREM_EMB() %>" onblur='executeBouton("<%=process.getNOM_PB_INIT_DATE_DERNIERE_EMBAUCHE() %>")'>
							<img src="images/calendrier.gif" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" onclick="return showCalendar('<%=process.getNOM_EF_DATE_PREM_EMB()%>', 'dd/mm/y');" hspace="5">
						</span>
					</td>
					<td>
						<span class="sigp2">Date de dernière embauche : </span>
					</td>
					<td>
						<span>
							<INPUT id="<%=process.getNOM_EF_DATE_DERN_EMB()%>" class="sigp2-saisie" maxlength="10" style="width: 90px;"	<%= MairieUtils.getDisabled(request, process.getNomEcran()) %>	name="<%= process.getNOM_EF_DATE_DERN_EMB() %>" type="text" value="<%= process.getVAL_EF_DATE_DERN_EMB() %>">
							<IMG src="images/calendrier.gif" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DERN_EMB()%>', 'dd/mm/y');" hspace="5">
						</span>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
						<span class="sigp2">Numéro de carte de séjour : </span>
					</td>
					<td>
						<span>
							<INPUT class="sigp2-saisie" maxlength="20" style="width: 90px;"	name="<%= process.getNOM_EF_NUM_CARTE_SEJOUR() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>	type="text" value="<%= process.getVAL_EF_NUM_CARTE_SEJOUR() %>">
						</span>
					</td>
					<td>
						<span class="sigp2">Date de validité : </span>
					</td>
					<td>
						<INPUT id="<%=process.getNOM_EF_DATE_VALIDITE_CARTE_SEJOUR()%>" class="sigp2-saisie" maxlength="10" style="width: 90px;"	<%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_DATE_VALIDITE_CARTE_SEJOUR() %>" type="text" value="<%= process.getVAL_EF_DATE_VALIDITE_CARTE_SEJOUR() %>">
						<IMG src="images/calendrier.gif" onclick="return showCalendar('<%=process.getNOM_EF_DATE_VALIDITE_CARTE_SEJOUR()%>', 'dd/mm/y');" hspace="5">
					</td>
				</tr>
			</table>
		</FIELDSET>
	
		<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
			<legend class="sigp2Legend">Adresse</legend>
			<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> onclick='executeBouton("<%=process.getNOM_PB_ADRESSE() %>")' <%= process.forRadioHTML(process.getNOM_RG_VILLE_DOMICILE(),process.getNOM_RB_VILLE_DOMICILE_NOUMEA())%>>Nouméa
			<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> onclick='executeBouton("<%=process.getNOM_PB_ADRESSE() %>")' <%= process.forRadioHTML(process.getNOM_RG_VILLE_DOMICILE(),process.getNOM_RB_VILLE_DOMICILE_AUTRE())%>>Autre
			<BR/>
			<%if (!"".equals(process.getVAL_RG_VILLE_DOMICILE())) {%>
				<%if (process.getVAL_RG_VILLE_DOMICILE().equals(process.getNOM_RB_VILLE_DOMICILE_NOUMEA()) ) {%>
				<div align="left">
					<hr>
					<span class="sigp2-titre">Domicile</span>
					<br/>
					<span class="sigp2" style="width:80px">Numéro de rue :</span>
					<INPUT class="sigp2-saisie" maxlength="3" style="width:40px;margin-left: 10px;" name="<%= process.getNOM_EF_NUM_RUE() %>" type="text" value="<%= process.getVAL_EF_NUM_RUE() %>"  <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					
					<span class="sigp2" style="width:40px;margin-left: 10px;">Bis/Ter :</span>
					<INPUT class="sigp2-saisie" maxlength="3" style="width:40px;margin-left: 10px;" name="<%= process.getNOM_EF_BIS_TER() %>" type="text" value="<%= process.getVAL_EF_BIS_TER() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					
					<span class="sigp2Mandatory" style="width:90px;margin-left: 10px;">Nom voie / rue :</span>
					<img style="cursor:pointer;" src="images/loupe.gif" height="20px" width="20px" onclick='executeBouton("<%=process.getNOM_PB_VOIE()%>")' class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" >
					<span class="sigp2-saisie"><%=process.getVAL_ST_VOIE()%></span>
					<br/><br/>
					
					<span class="sigp2">Adresse complémentaire :</span>
					<INPUT class="sigp2-saisie" maxlength="100" style="width:600px;margin-left: 10px;" name="<%= process.getNOM_EF_ADRESSE_COMPLEMENTAIRE() %>" type="text" value="<%= process.getVAL_EF_ADRESSE_COMPLEMENTAIRE() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					<br/><br/>
					
					<span class="sigp2Mandatory" style="width:80px">Code postal :</span>
					<INPUT class="sigp2-saisie" maxlength="5" style="width:40px;margin-left: 10px;" name="<%= process.getNOM_EF_CODE_POSTAL_DOM() %>" type="text" value="<%= process.getVAL_EF_CODE_POSTAL_DOM() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					
					<span class="sigp2Mandatory" style="width:40px;margin-left: 10px;">Ville :</span>
						<SELECT onchange='executeBouton("<%=process.getNOM_PB_COMMUNE_DOM() %>")' class="sigp2-saisie" name="<%= process.getNOM_LB_COMMUNE_DOM() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
							<%=process.forComboHTML(process.getVAL_LB_COMMUNE_DOM(), process.getVAL_LB_COMMUNE_DOM_SELECT())%>
						</SELECT>
				</div>
				<%} else {%>
				<div align="left">
					<hr>
					<span class="sigp2-titre">Domicile</span>
					<br/>
					<span class="sigp2" style="width:80px">Numéro de rue :</span>
					<INPUT class="sigp2-saisie" maxlength="3" style="width:40px;margin-left: 10px;" name="<%= process.getNOM_EF_NUM_RUE() %>" type="text" value="<%= process.getVAL_EF_NUM_RUE() %>"  <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					
					<span class="sigp2" style="width:40px;margin-left: 10px;">Bis/Ter :</span>
					<INPUT class="sigp2-saisie" maxlength="3" style="width:40px;margin-left: 10px;" name="<%= process.getNOM_EF_BIS_TER() %>" type="text" value="<%= process.getVAL_EF_BIS_TER() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> >
					<br/><br/>
					
					<span class="sigp2">Rue :</span>
					<INPUT class="sigp2-saisie" maxlength="120" style="width:723px;margin-left: 10px;" name="<%= process.getNOM_EF_RUE_NON_NOUMEA() %>" type="text" value="<%= process.getVAL_EF_RUE_NON_NOUMEA() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					<br/><br/>
					
					<span class="sigp2Mandatory">Adresse complémentaire :</span>
					<INPUT class="sigp2-saisie" maxlength="100" style="width:600px;margin-left: 10px;" name="<%= process.getNOM_EF_ADRESSE_COMPLEMENTAIRE() %>" type="text" value="<%= process.getVAL_EF_ADRESSE_COMPLEMENTAIRE() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					<br/><br/>
					
					<span class="sigp2Mandatory" style="width:80px">Code postal :</span>
					<INPUT class="sigp2-saisie" maxlength="5" style="width:40px;margin-left: 10px;" name="<%= process.getNOM_EF_CODE_POSTAL_DOM() %>" type="text" value="<%= process.getVAL_EF_CODE_POSTAL_DOM() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
					
					<span class="sigp2Mandatory" style="width:40px;margin-left: 10px;">Ville :</span>
						<SELECT onchange='executeBouton("<%=process.getNOM_PB_COMMUNE_DOM() %>")' class="sigp2-saisie" name="<%= process.getNOM_LB_COMMUNE_DOM() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
							<%=process.forComboHTML(process.getVAL_LB_COMMUNE_DOM(), process.getVAL_LB_COMMUNE_DOM_SELECT())%>
						</SELECT>
				</div>
				<%} %>
				<br/>
				<hr>
				<div>
					<span class="sigp2-titre">Boîte postale</span>
					<br/><br/>
					<span class="sigp2" style="width:30px;">BP :</span>
					<INPUT class="sigp2-saisie" maxlength="5" style="width: 40px;" name="<%= process.getNOM_EF_BP() %>" type="text" value="<%= process.getVAL_EF_BP() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
						
					<span class="sigp2" style="width:80px;margin-left: 10px;">Code postal :</span>
					<INPUT class="sigp2-saisie" maxlength="5" style="width:40px;" name="<%= process.getNOM_EF_CODE_POSTAL_BP() %>" type="text" value="<%= process.getVAL_EF_CODE_POSTAL_BP() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
						
					<span class="sigp2" style="width:40px;margin-left: 10px;">Ville :</span>
						<SELECT onchange='executeBouton("<%=process.getNOM_PB_COMMUNE_BP() %>")' <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> class="sigp2-saisie" name="<%= process.getNOM_LB_COMMUNE_BP() %>">
							<%=process.forComboHTML(process.getVAL_LB_COMMUNE_BP(), process.getVAL_LB_COMMUNE_BP_SELECT())%>
						</SELECT>
				</div>
			<%} %>
		</FIELDSET>
		
			
		<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
			<legend class="sigp2Legend">Coordonnées bancaires</legend>
			<TABLE border="0" cellpadding="0" cellspacing="0">
				<TBODY>
					<TR>
						<TD colspan="2" class="sigp2" style="text-align : center;" width="514">Code banque / Guichet</TD>
						<TD class="sigp2" style="text-align : center;" width="116">N° de compte</TD>
						<TD class="sigp2" style="text-align : center;" width="100">clé RIB</TD>
						<TD class="sigp2" style="text-align : center;">Intitulé de compte</TD>
					</TR>
					<TR>
						<TD class="sigp2-saisie" style="text-align : center;" width="134">
							<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_BANQUE_GUICHET() %>" onchange='executeBouton("<%=process.getNOM_PB_BANQUE()%>")' <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
								<%=process.forComboHTML(process.getVAL_LB_BANQUE_GUICHET(), process.getVAL_LB_BANQUE_GUICHET_SELECT())%>
							</SELECT>
						</TD>
						<TD class="sigp2-saisie" style="text-align : center;" width="380"><%=process.getVAL_ST_BANQUE_GUICHET()%></TD>
						<TD class="sigp2-saisie" style="text-align : center;" width="116">
							<INPUT class="sigp2-saisie" maxlength="11" name="<%= process.getNOM_EF_NUM_COMPTE() %>" size="11" type="text"value="<%= process.getVAL_EF_NUM_COMPTE() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
						</TD>
						<TD class="sigp2-saisie" style="text-align : center;" width="100">
							<INPUT class="sigp2-saisie" maxlength="2"  name="<%= process.getNOM_EF_RIB() %>" size="2" type="text" value="<%= process.getVAL_EF_RIB() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
						</TD>
						<TD class="sigp2-saisie" style="text-align : center;">
							<INPUT class="sigp2-saisie" maxlength="30"  name="<%= process.getNOM_EF_INTITULE_COMPTE() %>" size="30" type="text" value="<%= process.getVAL_EF_INTITULE_COMPTE() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
		</FIELDSET>	
		
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
			<legend class="sigp2Legend">Service national</legend>
			<span class="sigp2">Etat de service : </span>
				<SELECT class="sigp2-liste" style="margin-right:30px" name="<%= process.getNOM_LB_TYPE_SERVICE() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> >
					<%=process.forComboHTML(process.getVAL_LB_TYPE_SERVICE(), process.getVAL_LB_TYPE_SERVICE_SELECT())%> 
				</SELECT>
				<span class="sigp2">VCAT : </span>
				<span class="sigp2-RadioBouton">
					<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_VCAT(),process.getNOM_RB_VCAT_OUI())%>>Oui
					<span style="width:5px"></span>
					<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_VCAT(),process.getNOM_RB_VCAT_NON())%>>Non
				</span>
				<span style="width:30px"></span>
				<span class="sigp2">Debut : </span>
				<INPUT id="<%=process.getNOM_EF_SERVICE_DEBUT()%>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_SERVICE_DEBUT() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> size="10" type="text" value="<%= process.getVAL_EF_SERVICE_DEBUT() %>">
				<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_SERVICE_DEBUT()%>', 'dd/mm/y');">
				<span style="width:10px"></span>
				<span class="sigp2">Fin : </span>
				<INPUT id="<%=process.getNOM_EF_SERVICE_FIN()%>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_SERVICE_FIN() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> size="10" type="text" value="<%= process.getVAL_EF_SERVICE_FIN() %>">
				<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_SERVICE_FIN()%>', 'dd/mm/y');">
		</FIELDSET>	
		
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
			<legend class="sigp2Legend">Couverture sociale</legend>
			<TABLE border="0" cellpadding="0" cellspacing="0">
				<TBODY>
					<TR>
						<TD class="sigp2" style="text-align : center;" width="150">N° CAFAT</TD>
						<TD class="sigp2" style="text-align : center;" width="150">N° RUAMM</TD>
						<TD class="sigp2" style="text-align : center;" width="150">N° Mutuelle</TD>
						<TD class="sigp2" style="text-align : center;" width="150">N° CRE</TD>
						<TD class="sigp2" style="text-align : center;" width="150">N° IRCAFEX</TD>
						<TD class="sigp2" style="text-align : center;" width="150">N° CLR</TD>
					</TR>
					<TR>
						<TD class="sigp2-saisie" style="text-align : center;" width="150">
							<INPUT class="sigp2-saisie" maxlength="15" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_NUM_CAFAT() %>" size="15" type="text" value="<%= process.getVAL_EF_NUM_CAFAT() %>">
						</TD>
						<TD class="sigp2-saisie" style="text-align : center;" width="150">
							<INPUT class="sigp2-saisie" maxlength="15" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_NUM_RUAMM() %>" size="15" type="text" value="<%= process.getVAL_EF_NUM_RUAMM() %>">
						</TD>
						<TD class="sigp2-saisie" style="text-align : center;"width="150">
							<INPUT class="sigp2-saisie" maxlength="15" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_NUM_MUTUELLE() %>" size="15" type="text" value="<%= process.getVAL_EF_NUM_MUTUELLE() %>">
						</TD>
						<TD class="sigp2-saisie" style="text-align : center;" width="150">
							<INPUT class="sigp2-saisie" maxlength="15" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_NUM_CRE() %>" size="15" type="text" value="<%= process.getVAL_EF_NUM_CRE() %>">
						</TD>
						<TD class="sigp2-saisie" style="text-align : center;" width="150">
							<INPUT class="sigp2-saisie" maxlength="15" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_NUM_IRCAFEX() %>" size="15" type="text" value="<%= process.getVAL_EF_NUM_IRCAFEX() %>">
						</TD>
						<TD class="sigp2-saisie" style="text-align : center;" width="150">
							<INPUT class="sigp2-saisie" maxlength="15" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_NUM_CLR() %>" size="15" type="text" value="<%= process.getVAL_EF_NUM_CLR() %>">
						</TD>
					</TR>
				</TBODY>
			</TABLE>
		</FIELDSET>	
		
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;margin-right:10px;width:1030px;">
		    <legend class="sigp2Legend">Liste des contacts</legend>
		    <span style="position:relative;width:9px;"></span>
			<span style="position:relative;width:65px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_CONTACT()%>"></span>
				    
		    <div style="overflow: auto;height: 70px;width:970px;margin-right: 0px;margin-left: 0px;">
				<table class="sigp2NewTab" style="text-align:left;width:950px;">
					<%
					int indiceContact = 0;
					if (process.getListeContact()!=null){
						for (int i = 0;i<process.getListeContact().size();i++){
					%>
							<tr id="<%=indiceContact%>" onmouseover="SelectLigne(<%=indiceContact%>,<%=process.getListeContact().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:40px;" align="center">
									<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_CONTACT(indiceContact)%>">
									<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_CONTACT(indiceContact)%>">
								</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:150px;text-align: left;"><%=process.getVAL_ST_TYPE_CONTACT(indiceContact)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:100px;text-align: center;"><%=process.getVAL_ST_DIFFUSABLE_CONTACT(indiceContact)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:130px;text-align: center;">&nbsp;<%=process.getVAL_ST_PRIORITAIRE_CONTACT(indiceContact)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_DESCRIPTION_CONTACT(indiceContact)%></td>
							</tr>
							<%
							indiceContact++;
						}
					}%>
				</table>	
			</div>
							<%
				if (!"".equals(process.getVAL_ST_ACTION_CONTACT())) {
					%><BR/>
				    <FIELDSET class="sigp2Fieldset" style="text-align:left;margin-right:10px;width:985px;">
					    <legend class="sigp2Legend"><%=process.getVAL_ST_ACTION_CONTACT()%></legend>
					    <br/>
						<%
						if (!process.getVAL_ST_ACTION_CONTACT().equals(process.ACTION_SUPPRESSION)) {
						%>
							<span class="sigp2" style="width:90px">Type contact :</span>
							<span class="sigp2" style="width:500px">
								<SELECT class="sigp2-liste" style="width:200px"<%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%=process.getNOM_LB_TCONTACT()%>" onchange='executeBouton("<%=process.getNOM_PB_SELECT_TCONTACT()%>")'>
									<%=process.forComboHTML(process.getVAL_LB_TCONTACT(), process.getVAL_LB_TCONTACT_SELECT())%>
								</SELECT>
							</span>
							<BR/><BR/>
							<span class="sigp2" style="width:90px">Contact :</span>
							<span class="sigp2" style="width:400px">
								<INPUT class="sigp2-saisie" maxlength="50" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_LIBELLE_CONTACT() %>" size="55" type="text" value="<%= process.getVAL_EF_LIBELLE_CONTACT() %>">
							</span>
							<BR/><BR/>
							<span class="sigp2" style="width:90px">Diffusable :</span>
							<%if(!process.diffusableModifiable){ %>
								<span class="sigp2-RadioBouton" style="width:400px">
									<INPUT type="radio" disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_CONTACT_DIFF(),process.getNOM_RB_CONTACT_DIFF_OUI())%>>Oui
									<INPUT type="radio" disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_CONTACT_DIFF(),process.getNOM_RB_CONTACT_DIFF_NON())%>>Non
								</span>
							<%}else{ %>
								<span class="sigp2-RadioBouton" style="width:400px">
									<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_CONTACT_DIFF(),process.getNOM_RB_CONTACT_DIFF_OUI())%>>Oui
									<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_CONTACT_DIFF(),process.getNOM_RB_CONTACT_DIFF_NON())%>>Non
								</span>
							<%} %>
							<BR/><BR/>
							<span class="sigp2" style="width:90px">Prioritaire :</span>
							<%if(!process.prioritaireModifiable){ %>
							<span class="sigp2-RadioBouton" style="width:400px">
								<INPUT type="radio"  disabled="disabled" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  <%= process.forRadioHTML(process.getNOM_RG_CONTACT_PRIORITAIRE(),process.getNOM_RB_CONTACT_PRIORITAIRE_OUI())%>>Oui
								<INPUT type="radio" disabled="disabled" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  <%= process.forRadioHTML(process.getNOM_RG_CONTACT_PRIORITAIRE(),process.getNOM_RB_CONTACT_PRIORITAIRE_NON())%>>Non
							</span>
							<%}else{ %>
							<span class="sigp2-RadioBouton" style="width:400px">
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  <%= process.forRadioHTML(process.getNOM_RG_CONTACT_PRIORITAIRE(),process.getNOM_RB_CONTACT_PRIORITAIRE_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  <%= process.forRadioHTML(process.getNOM_RG_CONTACT_PRIORITAIRE(),process.getNOM_RB_CONTACT_PRIORITAIRE_NON())%>>Non
							</span>
							<%} %>
						<%
						} else {
						%>
							<BR/>
							<span class="sigp2" >Type contact :</span>
							<span class="sigp2-saisie" ><%=process.getVAL_ST_TCONTACT()%></span>
							<BR/>
							<span class="sigp2">Contact :</span>
							<span class="sigp2-saisie"><%=process.getVAL_ST_LIBELLE_CONTACT()%></span>
							<BR/>
							<span class="sigp2" style="width:90px">Diffusable :</span>
							<span class="sigp2-RadioBouton" style="width:400px">
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_CONTACT_DIFF(),process.getNOM_RB_CONTACT_DIFF_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_CONTACT_DIFF(),process.getNOM_RB_CONTACT_DIFF_NON())%>>Non
							</span>
							<BR/>
							<span class="sigp2" style="width:90px">Prioritaire :</span>
							<span class="sigp2-RadioBouton" style="width:400px">
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_CONTACT_PRIORITAIRE(),process.getNOM_RB_CONTACT_PRIORITAIRE_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_CONTACT_PRIORITAIRE(),process.getNOM_RB_CONTACT_PRIORITAIRE_NON())%>>Non
							</span>
						<%}	%>
						<BR/><BR/>
						<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_CONTACT()%>">
						<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CONTACT()%>">
						<BR>
					</FIELDSET>
					<%
					}
					%>
		</FIELDSET>
		

					
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
			<legend class="sigp2Legend">Titre repas</legend>
			<TABLE border="0" cellpadding="0" cellspacing="0">
				<TBODY>
					<TR>
						<TD class="sigp2" style="text-align : center;" width="150">ID Titre repas</TD>
					</TR>
					<TR>
						<TD class="sigp2-saisie" style="text-align : center;" width="150">
							<INPUT class="sigp2-saisie" maxlength="15" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_ID_TITRE_REPAS() %>" size="15" type="text" value="<%= process.getVAL_EF_ID_TITRE_REPAS() %>">
						</TD>
					</TR>
				</TBODY>
			</TABLE>
		</FIELDSET>	
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
			<legend class="sigp2Legend">Tiarhé</legend>
			<TABLE border="0" cellpadding="0" cellspacing="0">
				<TBODY>
					<TR>
						<TD class="sigp2" style="text-align : center;" width="150">Matricule Tiarhé</TD>
					</TR>
					<TR>
						<TD class="sigp2-saisie" style="text-align : center;" width="150">
							<INPUT class="sigp2-saisie" maxlength="15" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_ID_TIARHE() %>" size="15" type="text" value="<%= process.getVAL_EF_ID_TIARHE() %>">
						</TD>
					</TR>
				</TBODY>
			</TABLE>
		</FIELDSET>	
		
		<br/><br/>
		<FIELDSET style="text-align:center;margin:10px;width:1030px;"  class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>">
			<span class="sigp2"><INPUT type="submit" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>" class="sigp2-Bouton-100"></span>
		</FIELDSET>
		<br>
	
		<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_CIVILITE()%>" value="OK">
		<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_INIT_DATE_DERNIERE_EMBAUCHE()%>" value="OK">
		<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_ADRESSE()%>" value="OK">
		<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_COMMUNE_DOM()%>" value="OK">
		<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_COMMUNE_BP()%>" value="OK">
		<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_LIEU_NAISSANCE()%>">
		<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_VOIE()%>">
		<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_BANQUE()%>" >
		<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_SELECT_TCONTACT()%>" value="x">
	</FORM>
</BODY>
</HTML>
