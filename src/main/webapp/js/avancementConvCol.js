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
// active/Désactive toutes les cases à cocher pour la validation DRH
function activeDRH(tailleListe)
{
	for (i=0; i<tailleListe; i++){
  		var box = document.formu.elements['NOM_CK_VALID_DRH_'+i];  		
  		if(document.formu.elements['CHECK_ALL_DRH'].checked ){
  			if(box!=null && !box.disabled){			
				box.checked=true; 
				validDRH(i);  
			}			
  		}else{
  			if(box!=null && !box.disabled){		
				box.checked=false; 
				validDRH(i);
			}
		}
    } 
}
// active/Désactive toutes les cases à cocher pour le projet arrete
function activeProjet(tailleListe)
{
	for (i=0; i<tailleListe; i++){
  		var box = document.formu.elements['NOM_CK_PROJET_ARRETE_'+i]; 
  		var boxDRH = document.formu.elements['NOM_CK_VALID_DRH_'+i];   		
  		if(document.formu.elements['CHECK_ALL_PROJET'].checked && boxDRH!=null && boxDRH.checked){
  			if(box!=null && !box.disabled){		
				box.checked=true; 
				validProjet(i);
			}
  		}else{
  			if(box!=null && !box.disabled){	
				box.checked=false; 
				validProjet(i);
			}
		}
    }  
}
// active/Désactive toutes les cases à cocher pour le projet arrete
function activeAffecter(tailleListe)
{
	for (i=0; i<tailleListe; i++){
  		var box = document.formu.elements['NOM_CK_AFFECTER_'+i];  
  		var boxProjet = document.formu.elements['NOM_CK_PROJET_ARRETE_'+i];  		
  		if(document.formu.elements['CHECK_ALL_AFFECTER'].checked && boxProjet!=null && boxProjet.checked){
  			if(box!=null && !box.disabled){	
				box.checked=true;  
				validAffecter(i);
			}			
	  	}else{
  			if(box!=null && !box.disabled){	
				box.checked=false;	 
				validAffecter(i);
			}		
		}
    }  
}