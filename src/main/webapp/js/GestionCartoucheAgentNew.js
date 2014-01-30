//Execute l'action passée en paramètre
function executeBouton(action)
{
	window.parent.frames["MenuHaut"].changerMenuHaut("Empty");	
	document.formu.elements["ACTIVITE"].value = action.name;
	document.formu.elements["ACTION"].click();
}