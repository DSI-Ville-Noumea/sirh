// active/Désactive la saisie des champs suivant la validation ARRETES
function validArr(indice) {
	var box = document.formu.elements['NOM_CK_VALID_ARR_' + indice];
	if (box != null && box.checked) {
		if (document.formu.elements['NOM_LB_AVIS_CAP_AD_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_AD_' + indice].disabled = true;
		}
		if (document.formu.elements['NOM_LB_AVIS_CAP_AD_EMP_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_AD_EMP_' + indice].disabled = true;
		}
		if (document.formu.elements['NOM_LB_AVIS_CAP_CLASSE_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_CLASSE_' + indice].disabled = true;
		}
		if (document.formu.elements['NOM_LB_AVIS_CAP_CLASSE_EMP_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_CLASSE_EMP_' + indice].disabled = true;
		}
		if (document.formu.elements['NOM_ST_OBSERVATION_' + indice] != null) {
			document.formu.elements['NOM_ST_OBSERVATION_' + indice].setAttribute('readOnly',true);			 
		}
		if (document.formu.elements['NOM_CK_VALID_ARR_IMPR_' + indice] != null) {
			document.formu.elements['NOM_CK_VALID_ARR_IMPR_' + indice].style.visibility = "visible";
		}
	} else {
		if (document.formu.elements['NOM_LB_AVIS_CAP_AD_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_AD_' + indice].disabled = false;
		}
		if (document.formu.elements['NOM_LB_AVIS_CAP_AD_EMP_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_AD_EMP_' + indice].disabled = false;
		}
		if (document.formu.elements['NOM_LB_AVIS_CAP_CLASSE_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_CLASSE_' + indice].disabled = false;
		}
		if (document.formu.elements['NOM_LB_AVIS_CAP_CLASSE_EMP_' + indice] != null) {
			document.formu.elements['NOM_LB_AVIS_CAP_CLASSE_EMP_' + indice].disabled = false;
		}
		if (document.formu.elements['NOM_ST_OBSERVATION_' + indice] != null) {
			document.formu.elements['NOM_ST_OBSERVATION_' + indice].setAttribute('readOnly',false);
		}
		if (document.formu.elements['NOM_CK_VALID_ARR_IMPR_' + indice] != null) {
			document.formu.elements['NOM_CK_VALID_ARR_IMPR_' + indice].style.visibility = "hidden";
		}
	}
}
//active/Désactive la saisie des champs suivant la validation ARRETES_IMPRIME
function validArrImpr(indice) {
	var box = document.formu.elements['NOM_CK_VALID_ARR_IMPR_' + indice];

	if (box != null && box.checked) {
		document.formu.elements['NOM_CK_VALID_ARR_' + indice].style.visibility = "hidden";
	}else{
		document.formu.elements['NOM_CK_VALID_ARR_' + indice].style.visibility = "visible";
		document.formu.elements['NOM_CK_VALID_ARR_' + indice].checked = true;
	}
}