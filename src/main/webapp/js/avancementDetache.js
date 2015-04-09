// active/Désactive la saisie des champs suivant la validation DRH
function validDRH(indice)
{
  var box = document.formu.elements['NOM_CK_VALID_DRH_'+indice];
  if (box != null && box.checked)
  {
    document.formu.elements['NOM_CK_PROJET_ARRETE_'+indice].style.visibility = "visible";
  }
  else
  {
    document.formu.elements['NOM_CK_PROJET_ARRETE_'+indice].style.visibility = "hidden";
  }
}

// active/Désactive la saisie des champs suivant le projet d'arrete
function validProjet(indice)
{
  var box = document.formu.elements['NOM_CK_PROJET_ARRETE_'+indice];
  if (box != null && box.checked)
  {
	document.formu.elements['NOM_CK_VALID_DRH_'+indice].disabled = true;
    document.formu.elements['NOM_EF_NUM_ARRETE_'+indice].style.visibility = "visible";
    document.formu.elements['NOM_EF_DATE_ARRETE_'+indice].style.visibility = "visible";
    document.formu.elements['NOM_CK_AFFECTER_'+indice].style.visibility = "visible";
  }
  else
  {
	document.formu.elements['NOM_CK_VALID_DRH_'+indice].disabled = false;
    document.formu.elements['NOM_EF_NUM_ARRETE_'+indice].style.visibility = "hidden";
    document.formu.elements['NOM_EF_DATE_ARRETE_'+indice].style.visibility = "hidden";
    document.formu.elements['NOM_CK_AFFECTER_'+indice].style.visibility = "hidden";
  }
}

// active/Désactive la saisie des champs suivant affecter
function validAffecter(indice)
{
  var box = document.formu.elements['NOM_CK_AFFECTER_'+indice];
  if (box != null && box.checked)
  {
	document.formu.elements['NOM_CK_PROJET_ARRETE_'+indice].disabled = true;   
	document.formu.elements['NOM_EF_NUM_ARRETE_'+indice].disabled = true; 
	document.formu.elements['NOM_EF_DATE_ARRETE_'+indice].disabled = true; 
  }
  else
  {
	document.formu.elements['NOM_CK_PROJET_ARRETE_'+indice].disabled = false;   
	document.formu.elements['NOM_EF_NUM_ARRETE_'+indice].disabled = false; 
	document.formu.elements['NOM_EF_DATE_ARRETE_'+indice].disabled = false; 
  }
}