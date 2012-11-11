//Appel du formulaire par appui sur un bouton ONGLET
function afficheOnglet(onglet) {
	var aBouton = document.createElement('<INPUT type="submit" name = "NOM_PB_ONGLET" value="' +onglet+ '" style="visibility : hidden;">');
	document.formu.appendChild(aBouton);
	aBouton.click();	
}