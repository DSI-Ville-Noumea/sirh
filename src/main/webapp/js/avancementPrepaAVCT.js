// active/Désactive toutes les cases à cocher pour la validation SFG
function activeSGC(tailleListe)
{
	for (i=0; i<tailleListe; i++){
  		var box = document.formu.elements['NOM_CK_VALID_SGC_'+i];  		
  		if(document.formu.elements['CHECK_ALL_SGC'].checked ){
  			if(box!=null && !box.disabled){			
				box.checked=true; 
			}			
  		}else{
  			if(box!=null && !box.disabled){		
				box.checked=false; 
			}
		}
    } 
}