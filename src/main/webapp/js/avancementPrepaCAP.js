// active/Désactive la saisie des champs suivant la validation SEF
function validSEF(indice) {
	var box = document.formu.elements['NOM_CK_VALID_SEF_' + indice];
	if (box != null && box.checked) {
		/*if (document.formu.elements['NOM_LB_AVIS_CAP_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_' + indice].disabled = true;

		}*/
	} else {
		if (document.formu.elements['NOM_LB_AVIS_CAP_AD_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_AD_' + indice].disabled = false;
		}
		if (document.formu.elements['NOM_EF_ORDRE_MERITE_' + indice] != null) {
			document.formu.elements['NOM_EF_ORDRE_MERITE_' + indice].disabled = false;
		}
	}
}

//active/Désactive la case ordre du mérite
function activeOrdreMerite(indice) {
	if(document.formu.elements['NOM_LB_AVIS_CAP_AD_' + indice].value==1 || document.formu.elements['NOM_LB_AVIS_CAP_AD_' + indice].value==2){
    	document.formu.elements['NOM_EF_ORDRE_MERITE_'+indice].style.visibility = "hidden";
	}else{
    	document.formu.elements['NOM_EF_ORDRE_MERITE_'+indice].style.visibility = "visible";
	}	
}

//active/Désactive toutes les cases à cocher pour l'impression du tableau
function activeTab(tailleListe) {
	for (i = 0; i < tailleListe; i++) {
		var box = document.formu.elements['NOM_CK_TAB_' + i];
		if (document.formu.elements['CHECK_ALL_TAB'].checked) {
			if (box != null && !box.disabled) {
				box.checked = true;
			}
		} else {
			if (box != null && !box.disabled) {
				box.checked = false;
			}
		}
	}
}

//active/Désactive toutes les cases à cocher pour l'impression du tableau + EAEs
function activeEae(tailleListe) {
	for (i = 0; i < tailleListe; i++) {
		var box = document.formu.elements['NOM_CK_EAE_' + i];
		if (document.formu.elements['CHECK_ALL_EAE'].checked) {
			if (box != null && !box.disabled) {
				box.checked = true;
			}
		} else {
			if (box != null && !box.disabled) {
				box.checked = false;
			}
		}
	}
}