
// active/Désactive toutes les cases à cocher pour l'impression des convocations
function activeImprimerConvoc(tailleListe)
{
	for (i=0; i<tailleListe; i++){
  		var box = document.formu.elements['NOM_CK_A_IMPRIMER_CONVOC_'+i];  		
  		if(document.formu.elements['CHECK_ALL_IMPRIMER_CONVOC'].checked){
  			if(box!=null && !box.disabled){	
				box.checked=true;  
				validConvoque(i);
			}			
	  	}else{
  			if(box!=null && !box.disabled){	
				box.checked=false;	 
				validConvoque(i);
			}		
		}
    }  
}

function validConvoque(indice)
{
  var box = document.formu.elements['NOM_CK_A_IMPRIMER_CONVOC_'+indice];
  if (box != null && box.checked)
  {   
		//document.formu.elements['NOM_ST_ETAT_'+indice].value = "C"; 
  }
  else
  {
		//document.formu.elements['NOM_ST_ETAT_'+indice].value = "P";
  }
}
//active/Désactive toutes les cases à cocher pour l'impression des lettres d'accompagnement
function activeImprimerAccomp(tailleListe)
{
	for (i=0; i<tailleListe; i++){
  		var box = document.formu.elements['NOM_CK_A_IMPRIMER_ACCOMP_'+i];  		
  		if(document.formu.elements['CHECK_ALL_IMPRIMER_ACCOMP'].checked){
  			if(box!=null && !box.disabled){	
				box.checked=true;  
				validAccomp(i);
			}			
	  	}else{
  			if(box!=null && !box.disabled){	
				box.checked=false;	 
				validAccomp(i);
			}		
		}
    }  
}

function validAccomp(indice)
{
  var box = document.formu.elements['NOM_CK_A_IMPRIMER_ACCOMP_'+indice];
  if (box != null && box.checked)
  {   
		//document.formu.elements['NOM_ST_ETAT_'+indice].value = "A"; 
  }
  else
  {
		//document.formu.elements['NOM_ST_ETAT_'+indice].value = "P";
  }
}