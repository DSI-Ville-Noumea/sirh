<!-- Sample JSP file -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR"
	content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css"
	type="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Enfants homonymes</TITLE>

<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>

<SCRIPT language="JavaScript">
//afin de sélectionner un élément dans une liste
function executeBouton(nom)
{
document.formu.elements[nom].click();
}
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
	class="nc.mairie.gestionagent.process.OeAGENTEnfantHomonyme"
	id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED"
	background="images/fond.jpg" lang="FR" link="blue" vlink="purple">
<%@ include file="BanniereErreur.jsp"%>
<FORM name="formu" method="POST" class="sigp2-titre"><INPUT
	name="JSP" type="hidden" value="<%= process.getJSP() %>"> <BR />
<FIELDSET class="sigp2Fieldset"
	style="text-align:left;margin:10px;width:1030px;"><legend
	class="sigp2Legend">enfants homonymes</legend> <br />
<span class="sigp2">D'autres enfants nommés <b><%=process.getEnfantCourant().getNom()%>
<%=process.getEnfantCourant().getPrenom()%> </b>ont été trouvés dans la
base.<BR/>
Si l'un d'entre eux correspond à celui que vous souhaitiez créer, merci
de le sélectionner.<BR/>
Dans le cas contraire, vous pouvez créer un nouvel enfant.<BR/>
</span> <BR />
<span style="margin-left:5px;position:relative;width:75px;">Matricule</span>
<span style="position:relative;width:667px;">Parent</span> <span
	style="position:relative;width:200px;">Commentaire</span> <SELECT
	class="sigp2-liste" name="<%= process.getNOM_LB_ENFANT_HOMONYME() %>"
	size="10" style="width:1000px;font-family : monospace;">
	<%=process.forComboHTML(process.getVAL_LB_ENFANT_HOMONYME(), process.getVAL_LB_ENFANT_HOMONYME_SELECT())%>
</SELECT> <BR />
<BR />
</FIELDSET>
<BR />

<FIELDSET class="sigp2Fieldset" style="text-align:center;width:1030px">
<INPUT type="submit" class="sigp2-Bouton-100" value="Sélectionner"
	name="<%=process.getNOM_PB_SELECTIONNER()%>"> <INPUT
	type="submit" class="sigp2-Bouton-100" value="Créer"
	name="<%=process.getNOM_PB_CREER()%>"> <INPUT type="submit"
	class="sigp2-Bouton-100" value="Annuler"
	name="<%=process.getNOM_PB_ANNULER()%>"></FIELDSET>
</FORM>
</BODY>
</HTML>
