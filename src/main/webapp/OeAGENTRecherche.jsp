<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTRecherche" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<lINK rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
		<TITLE>Sélection d'un agent</TITLE>
		
		<SCRIPT type="text/javascript" src="js/jquery-1.6.2.min.js"></SCRIPT>
		<SCRIPT type="text/javascript" src="development-bundle/ui/jquery.ui.core.js"></SCRIPT>
		<SCRIPT type="text/javascript" src="development-bundle/ui/jquery.ui.widget.js"></SCRIPT>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.position.js"></SCRIPT>
		<SCRIPT type="text/javascript" src="development-bundle/ui/jquery.ui.autocomplete.js"></SCRIPT>
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
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
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="return setfocus('<%= process.getFocus() %>')">
		<%@ include file="BanniereErreur.jsp"%>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px" title="Recherche d'un agent">
				<LEGEND class="sigp2Legend">Recherche d'un agent</LEGEND>
				<TABLE border="0" cellpadding="0" cellspacing="0" width="100%">
					<TBODY>
						<TR>
							<TD class="sigp2" width="100">
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_RECHERCHE(),process.getNOM_RB_RECH_SERVICE())%>>
								<span class="sigp2" style="width:60px;">Service 
								<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"
									height="16" style="cursor : pointer;" onclick="agrandirHierarchy();"></span>
								<INPUT type="hidden" id="idServiceADS" size="4" name="<%=process.getNOM_ST_ID_SERVICE_ADS() %>" 
									value="<%=process.getVAL_ST_ID_SERVICE_ADS() %>" class="sigp2-saisie">
									
								
								<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
								<%=process.getCurrentWholeTreeJS(process.getVAL_EF_ZONE().toUpperCase()) %>
								<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
							</TD>
						</TR>
						<TR>
							<TD class="sigp2" valign="top">
								<INPUT type="radio" align="bottom" <%= process.forRadioHTML(process.getNOM_RG_RECHERCHE(),process.getNOM_RB_RECH_NOM())%>>
							Matricule ou début du nom de l'agent</TD>
						</TR>
						<TR>
							<TD class="sigp2">
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_RECHERCHE(),process.getNOM_RB_RECH_PRENOM())%>>
								Début du prénom de l'agent</TD>
						</TR>
						<TR>
							<TD class="sigp2">
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_RECHERCHE(),process.getNOM_RB_RECH_CAFAT())%>>
								Numéro de CAFAT ou RUAMM</TD>
						</TR>
						<tr><td><BR/></td></tr>
						<TR>
							<TD>
								<INPUT size="1" type="text" class="sigp2-saisie" maxlength="1" name="ZoneTampon" style="display:none;">
								<INPUT class="sigp2-saisie" id="service" maxlength="60" name="<%= process.getNOM_EF_ZONE() %>" size="20" type="text" value="<%= process.getVAL_EF_ZONE() %>">
	                  			<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER()%>" accesskey="R">
							</TD>
						</TR>
						
					</TBODY>
	            </TABLE>
	            <BR/><BR/>
			</FIELDSET>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Sélection d'un agent">
				<LEGEND class="sigp2Legend">Sélection d'un agent</LEGEND>
               	<%if(process.getListeAgent()!= null && process.getListeAgent().size()>0){ %>
		    
				    <div class="sigp2-RadioBouton">
				    	<span>
				    	<INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")'<%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_NOMATR())%>>
				    	</span>
				    	<span>Matr</span>
				    	<span style="width:20px;"></span>
				    	<span>
				    		<INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")' <%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_NOM())%>>
				    	</span>
				    	<span>Nom d'usage</span>
				    	<span style="width:105px;"></span>
				    	<span>
				    		<INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")' <%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_PRENOM())%>>
				    	</span>
				    	<span>Prenom</span>
				    	<span style="width:135px;"></span>
				    	<span>
				    		<INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")' <%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_CAFAT())%>>
				    	</span>
				    	<span>Cafat</span>
				    	<span style="width:55px;"></span>
				    	<span>
				    		<INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")' <%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_RUAMM())%>>
				    	</span>
				    	<span>Ruamm</span>
					</div>	            
						<div style="overflow: auto;height: 250px;width:700px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:680px;">
								<%
								int indiceAgent = 0;
								if (process.getListeAgent()!=null){
									for (int i = 0;i<process.getListeAgent().size();i++){
								%>
									<tr id="<%=indiceAgent%>" onmouseover="SelectLigne(<%=indiceAgent%>,<%=process.getListeAgent().size()%>)" ondblclick='executeBouton("<%=process.getNOM_PB_OK(indiceAgent)%>")'>
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;text-align: left;"><%=process.getVAL_ST_MATR(indiceAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_NOM(indiceAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_PRENOM(indiceAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:100px;text-align: left;"><%=process.getVAL_ST_CAFAT(indiceAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_RUAMM(indiceAgent)%></td>
										<td><INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_OK(indiceAgent)%>" value="x"></td>
									</tr>
										<%
										indiceAgent++;
									}
								}%>
							</table>	
						</div>
	            <%} %>
	            <BR>
	            <TABLE border="0" style="text-align : center;" cellpadding="0" cellspacing="0">
					<TBODY>
						<TR>
							<TD>
								<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" accesskey="A" name="<%=process.getNOM_PB_ANNULER()%>">
							</TD>
						</TR>
					</TBODY>
	            </TABLE>
	            <BR>
			</FIELDSET>
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_TRI()%>" value="TRI"><BR>
		</FORM>
	</BODY>
</HTML>