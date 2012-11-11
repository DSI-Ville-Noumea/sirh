var courant='';
var ssModuleCourant='';

// Constructeur de la classe Menu
function MenuHaut(ssModule) { 
//     Déclaration des variables membres (propriétés) 
    this.listeObjet = new Array();
    this.niveau=0;
    this.nom= ssModule;
//     Déclaration d'une fonction membre (méthode) 
    this.ajouterFils= ajouterObjet;
    this.incrementeFils = incrementeNiveau;
    this.afficher = AfficherMenu;
} 

// Affichage du menu haut
function AfficherMenu() {
	var res = '<div id="'+ this.nom + '" class="menuHaut">';
	var a;
	// Parcours des objets et demande d'affichage
	for (a in this.listeObjet) {
		res += this.listeObjet[a].afficher();
	}
	return res+'</div>';
}

// Constructeur de la classe Lien
function Lien(aDroit, aTitreLien, aTitre, actif, selected, img) { 
//     Déclaration des variables membres (propriétés) 
    this.droit= aDroit;
    this.titreLien=aTitreLien;
    this.titre= aTitre; 
    this.isActif = actif;
    this.niveau=0;
    this.type='lien';
    this.isSelected = selected;
    this.img = img;
	//Déclaration d'une fonction membre (méthode)
    this.afficher= afficherLien; 
} 

// Implantation du code de la fonction membre 
function afficherLien() {
	var trouve = false;
//	Vérif du droit de l'utilisateur
	var a;
	for (a in listeDroits) {
		if (listeDroits[a] == this.droit) {
			trouve=true;
		}
	}

//	Si droit trouvé
	if (trouve) {
		// Si menu actif
		if (this.isActif) {
			if (this.isSelected){
				courant = this;
				classe = 'LienHautCourant';
				onclick = '';
			}else{
				onclick = 'envoieFormulaire(this);'
				classe = 'LienHautActif';
			}
		} else {
			classe = 'LienHautInactif';
			onclick = '';
		}

		/*var temp = 	'<span id="'+this.droit+'" class="'+classe+'" TITLE="'+this.titre+'" onClick="'+onclick+'">'+
				this.titreLien + '</span>';*/

		var temp = 	'<span id="'+this.droit+'" class="'+classe+'" TITLE="'+this.titre+'" onClick="'+onclick+'"><img height="47" id="menuhaut_'+this.droit+'" onmouseover="menuhaut_'+this.droit+'.src=\'images/navigation/menuHaut/'+this.img+'_Rollover.gif\'" onmouseout="menuhaut_'+this.droit+'.src=\'images/navigation/menuHaut/'+this.img+'.gif\'" src="images/navigation/menuHaut/'+this.img+'.gif"/></span>';
		
		return temp;
	} else {
		return '';
	}
 } 

//Ajoute un objet à la liste
function ajouterObjet (obj) {
   var objInc=this.incrementeFils(obj);
   var v = new Array(objInc);
   this.listeObjet = this.listeObjet.concat(v);
}

//incremente niveau
function incrementeNiveau(obj){
   obj.niveau=this.niveau + 1;
   if (obj.type=='dossier') {
	//parcours des éléments et incrémente niveau
	var a;
	for (a in obj.listeObjet) {
		var v = obj.incrementeFils(obj.listeObjet[a])
		obj.listeObjet[a] = v;
	}

   }
   return obj
}

//Change le sous-menu secondaire (en haut)
function changerMenuHaut(ssModule) {
	if (ssModuleCourant != ''){
		var menuAEffacer = document.getElementById(ssModuleCourant);
		menuAEffacer.style.display = 'none';
	}
	if (ssModule != ''){
		var menuAAfficher = document.getElementById(ssModule);
		if (menuAAfficher != null){
			menuAAfficher.style.display = 'block';
			ssModuleCourant = ssModule;
			
			var eltsMenuHaut = document.getElementById(ssModule).getElementsByTagName("span");
			eltsMenuHaut[0].className = "LienHautCourant";
			courant = eltsMenuHaut[0];
			for (var i=1; i<eltsMenuHaut.length; i++){
				if (eltsMenuHaut[i].className != "LienHautInactif"){
					eltsMenuHaut[i].className = "LienHautActif";
				}
			}
		}
	}
}

//Lance le formulaire en alimentant le lien
function envoieFormulaire(lien) {
	if (courant != '') {
		courant.className="LienHautActif";
	}else{
		var eltsDuMenuHaut = document
	}
	courant = lien;
	courant.className="LienHautCourant";

	leForm.ACTIVITE.value = lien.id;
	document.leForm.submit();
}