// active/D�sactive la saisie des champs suivant la validation SEF
function validSEF(indice) {
	var box = document.formu.elements['NOM_CK_VALID_SEF_' + indice];
	if (box != null && box.checked) {
		/*if (document.formu.elements['NOM_LB_AVIS_CAP_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_' + indice].disabled = true;

		}*/
	} else {
		if (document.formu.elements['NOM_LB_AVIS_CAP_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_' + indice].disabled = false;
		}
		if (document.formu.elements['NOM_EF_ORDRE_MERITE_' + indice] != null) {
			document.formu.elements['NOM_EF_ORDRE_MERITE_' + indice].disabled = false;
		}
	}
}
// active/D�sactive toutes les cases � cocher pour la validation SFG
function activeSEF(tailleListe) {
	for (i = 0; i < tailleListe; i++) {
		var box = document.formu.elements['NOM_CK_VALID_SEF_' + i];
		if (document.formu.elements['CHECK_ALL_SEF'].checked) {
			if (box != null && !box.disabled) {
				box.checked = true;
				validSEF(i);
			}
		} else {
			if (box != null && !box.disabled) {
				box.checked = false;
				validSEF(i);
			}
		}
	}
}

//active/D�sactive la case ordre du m�rite
function activeOrdreMerite(indice) {
	if(document.formu.elements['NOM_LB_AVIS_CAP_' + indice].value==1){
    	document.formu.elements['NOM_EF_ORDRE_MERITE_'+indice].style.visibility = "hidden";
	}else{
    	document.formu.elements['NOM_EF_ORDRE_MERITE_'+indice].style.visibility = "visible";
	}
	
}