//***********************************************************
// Activation/Inactivation des champs
//***********************************************************
function avenantObsolete(isAvenant)
{
	if (isAvenant == "NOM_RB_AVENANT_O"){
		var listTypContrat = document.formu.elements["NOM_LB_TYPE_CONTRAT"];
		var selectedContrat = listTypContrat.options[listTypContrat.selectedIndex].text.substring(0,3);
		if (selectedContrat == "CDD"){
			document.formu.elements["NOM_EF_JUSTIFICATION"].disabled=true;
			document.formu.elements["NOM_EF_DATE_FIN_PERIODE_ESSAI"].disabled=true;
			document.formu.elements["NOM_LB_MOTIF"].disabled=true;
		}
	}else{
		document.formu.elements["NOM_EF_JUSTIFICATION"].disabled=false;
		document.formu.elements["NOM_EF_DATE_FIN_PERIODE_ESSAI"].disabled=false;
		document.formu.elements["NOM_LB_MOTIF"].disabled=false;
	}
}

//***********************************************************
// Activation/Inactivation des champs
//***********************************************************
function avenant(isAvenant)
{
	var btnContratRef = document.formu.elements["NOM_PB_CONTRAT_REF"];
	if (isAvenant == "NOM_RB_AVENANT_O"){
		btnContratRef.disabled=false;
		var listTypContrat = document.formu.elements["NOM_LB_TYPE_CONTRAT"];
		var selectedContrat = listTypContrat.options[listTypContrat.selectedIndex].text.substring(0,3);
		if (selectedContrat == "CDD"){
			document.formu.elements["NOM_EF_JUSTIFICATION"].disabled=true;
			document.formu.elements["NOM_EF_DATE_FIN_PERIODE_ESSAI"].disabled=true;
			document.formu.elements["NOM_LB_MOTIF"].disabled=true;
		}
	}else{
		btnContratRef.disabled=true;
		document.formu.elements["NOM_EF_JUSTIFICATION"].disabled=false;
		document.formu.elements["NOM_EF_DATE_FIN_PERIODE_ESSAI"].disabled=false;
		document.formu.elements["NOM_LB_MOTIF"].disabled=false;
	}
}