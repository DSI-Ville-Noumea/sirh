<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Sélection d'une fiche emploi</TITLE>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT language="javaScript">

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
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTEMetierSelection" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" style="tab-interval:35.4pt" >

    <%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px">
		    <legend class="sigp2Legend">Sélection d'une fiche métier</legend>
		    <br/>
			<span class="sigp2">Recherche alphabétique :</span>
			<span>
				<INPUT class="sigp2-saisie" maxlength="60" name="<%= process.getNOM_EF_RECHERCHE() %>" size="20" type="text" value="<%= process.getVAL_EF_RECHERCHE() %>">
			</span>
               <span>
				<INPUT size="1" type="text" class="sigp2-saisie" maxlength="1" name="ZoneTampon" style="display:none;">
               	<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER()%>" accesskey="R">
               </span>
               <BR><BR>
            <%if(process.getListeFicheMetier()!= null && process.getListeFicheMetier().size()>0){ %>
			<TABLE border="0" class="sigp2" cellpadding="0" cellspacing="0" width="100%">
               	<TBODY>
					<TR class="sigp2">
						<TD style="position:relative;width:90px;text-align: left;">&nbsp;code</TD>
						<TD>libellé</TD>
					</TR>
				</TBODY>
			</TABLE>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
					<%
					int indiceFe = 0;
					if (process.getListeFicheMetier()!=null){
						for (int i = 0;i<process.getListeFicheMetier().size();i++){
					%>
							<tr id="<%=indiceFe%>" onmouseover="SelectLigne(<%=indiceFe%>,<%=process.getListeFicheMetier().size()%>)" ondblclick='executeBouton("<%=process.getNOM_PB_VALIDER(indiceFe)%>")'>
								<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: left;"><%=process.getVAL_ST_CODE(indiceFe)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB(indiceFe)%></td>
								<td><INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_VALIDER(indiceFe)%>" value="x"></td>
							</tr>
							<%
							indiceFe++;
						}
					}%>
					</table>	
				</div>
			<%} %>
			<br/>
           <span>
           	<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_ANNULER()%>" accesskey="A">
           </span>
	    </FIELDSET>
    </FORM>
</BODY>
</HTML>