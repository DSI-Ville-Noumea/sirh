<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
	<META http-equiv="Content-Style-Type" content="text/css">
	<title>Référence de l'agent</title>
	<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
	<SCRIPT language="javascript" src="js/GestionMenuHaut.js"></SCRIPT>
	<SCRIPT language="JavaScript" src="js/GestionCartoucheAgentNew.js"></SCRIPT>
	<LINK rel="stylesheet" href="theme/menu.css" type="text/css">
</head>
<BODY bgcolor="#ffffff">
	<FORM name="formu" method="POST" target="Main" action="GestionAgentServlet">
		<div id="actionAgent" align="center" class="actionAgent">
            <IMG name="AgentCreation" title="Création d'un agent"  src="images/ajout.gif" height="15" width="15" onclick='executeBouton(this);'>  
            <IMG name="AgentDeselection" title="Déselectionner un agent" src="images/suppression.gif" height="15" width="15" onclick='executeBouton(this);'> 
			<INPUT type="hidden" name="ACTIVITE" value="AgentCreation">
			<br/>
	        <INPUT type="submit" style="visibility : hidden;" name="ACTION" value="OK" height="0">
		</div>
	</FORM>
</BODY>
</html>