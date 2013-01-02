// active/Désactive toutes les cases à cocher pour la mise à jour des EAEs
function activeMAJ(tailleListe)
{
	for (i=0; i<tailleListe; i++){
  		var box = document.formu.elements['NOM_CK_VALID_MAJ_'+i];  		
  		if(document.formu.elements['CHECK_ALL_MAJ'].checked ){
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