// active/Désactive toutes les cases à cocher pour n'en avoir qu'une
function selectLigne(indice,tailleListe)
{
	var boxLigne = document.formu.elements['NOM_CK_SELECT_LIGNE_'+indice];

	for (i=0; i<tailleListe; i++){
  		var box = document.formu.elements['NOM_CK_SELECT_LIGNE_'+i];  
		if(box!=null){			
			box.checked=false; 
		}	  		
    } 
    boxLigne.checked=true;
}

// active/Désactive toutes les cases à cocher pour les activites de la FDP
function activeACTI(tailleListe)
{
	for (i=0; i<tailleListe; i++){
  		var box = document.formu.elements['NOM_CK_SELECT_LIGNE_ACTI_'+i];  		
  		if(document.formu.elements['CHECK_ALL_ACTI'].checked ){
  			if(box!=null){			
				box.checked=true; 
			}			
  		}else{
	  		if(box!=null){			
				box.checked=false; 
			}
  		}
  		
    } 
}

// active/Désactive toutes les cases à cocher pour les competences de la FDP
function activeCOMP(tailleListe)
{
	for (i=0; i<tailleListe; i++){
  		var box = document.formu.elements['NOM_CK_SELECT_LIGNE_COMP_'+i];  		
  		if(document.formu.elements['CHECK_ALL_COMP'].checked ){
  			if(box!=null){			
				box.checked=true; 
			}			
  		}else{
	  		if(box!=null){			
				box.checked=false; 
			}
  		}
  		
    } 
}