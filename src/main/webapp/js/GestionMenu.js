
var courant='';

// Constructeur de la classe Menu
function Menu() { 
//     Déclaration des variables membres (propriétés) 
    this.listeObjet = new Array();
    this.niveau=0;
//     Déclaration d'une fonction membre (méthode) 
    this.ajouterFils= ajouterObjet;
    this.incrementeFils = incrementeNiveau;
    this.afficher = afficherMenu;
} 

// Implantation du code de la fonction membre 
function afficherMenu() { 
	var res = '<div id="menudiv" class="menuGauche">';
	var a;
//	parcours des objets et demande d'affichage
	for (a in this.listeObjet) {
		res += this.listeObjet[a].afficher();
	}
	return res+'</div>';
 }

// Constructeur de la classe Lien
function Lien(aSsModule, aDroit, aTitreLien, aTitre, actif) { 
//     Déclaration des variables membres (propriétés) 
    this.ssModule= aSsModule;
    this.droit= aDroit;
    this.titreLien=aTitreLien;
    this.titre= aTitre; 
    this.isActif = actif;
    this.niveau=0;
    this.type='lien';
    this.parent= '';
//     Déclaration d'une fonction membre (méthode) 
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
		var comment='';
//		Si menu actif
		if (this.isActif) {
			classe = 'LienActif';
			onclic = 'envoieFormulaire(this); changerMenuHaut(\'' + this.parent.nom + '_' + this.ssModule + '\');';
		} else {
			classe = 'LienInactif';
			onclic = 'envoieFormulaire(this); changerMenuHaut(\'' + this.parent.nom + '_' + this.ssModule + '\');';
			comment=' EN CONSTRUCTION ';
		}

		var temp = 	'<span onmouseover="this.style.color=\'#000000\';" onmouseout="this.style.color=\'#0080CC\';" id="'+this.droit+'" class="'+classe+'" TITLE="'+this.titre+'" onClick="'+onclic+'">'+
				this.titreLien+comment+'<br></span>';
		return temp;
	} else {
		return '';
	}
 } 

// Constructeur de la classe Dossier
function Dossier(aNom, aTitre,img) { 
//     Déclaration des variables membres (propriétés) 
    this.nom= aNom; 
    this.titre= aTitre;
    this.listeObjet = new Array();
    this.niveau=1;
    this.type='dossier';
    this.img = img;
//     Déclaration d'une fonction membre (méthode) 
    this.afficher= afficherDossier; 
    this.ajouterFils= ajouterObjet;
    this.incrementeFils = incrementeNiveau;
} 

//Ajoute un objet à la liste
function ajouterObjet (obj) {
   if (obj.type='lien'){
      obj.parent= this;
   }
   var objInc=this.incrementeFils(obj);
   var v = new Array(objInc);
   this.listeObjet = this.listeObjet.concat(v);
}

//incremente niveau
function incrementeNiveau(obj){
   obj.niveau=this.niveau + 1;
   if (obj.type=='dossier') {
//      parcours des éléments et incrémente niveau
	var a;
	for (a in obj.listeObjet) {
		var v = obj.incrementeFils(obj.listeObjet[a]);
		obj.listeObjet[a] = v;
	}

   }
   return obj;
}

// Implantation du code de la fonction membre 
function afficherDossier() { 
	var res;
	
	/* res =  '<span id="'+this.nom+'" onClick="switchMenu('+this.nom+')" class="Dossier" TITLE="'+this.titre+'"> ' +
		'<img width="200" id="menu_'+this.nom+'" onmouseover="menu_'+this.nom+'.src=\'images/navigation/menuGauche/'+this.img+'_Rollover.gif\'" ' +
		'nmouseout="menu_'+this.nom+'.src=\'images/navigation/menuGauche/'+this.img+'.gif\'" src="images/navigation/menuGauche/'+this.img+'.gif"/>' +
	'</span><br>\n'; */
	
	var res = '<div id="'+this.nom+'" onClick="switchMenu('+this.nom+')" class="Dossier" TITLE="'+this.titre+'"><span class="DossierContainer"> ' +
				this.img + '</span></div>';
	
//	parcours des éléments et rajout
	var a;
	var contenu = '<DIV id="'+this.nom+'_SsMenu" class="SsMenu">';
	for (a in this.listeObjet) {
		var temp = this.listeObjet[a].afficher();

		if (temp != '') {
			for (i=1; i< this.listeObjet[a].niveau; i++) {
				contenu += '<IMG src="images/carre_vide.gif">';
			}
			contenu += temp;
		}
	}

//	Si pas de contenu on ne retourne rien
	if (contenu != '') {
		res += contenu + '</DIV>\n';
	} else {
		res = '';
	}
	return res;
 } 

var Open = "";
var Closed = "";

var choix = '';

function preload(){
    if(document.images){
        Open = new Image(16,13);
        Closed = new Image(16,13);
        Open.src = "images/menu_dossier_ouvert.gif";
        Closed.src = "images/menu_dossier_clos.gif";
    }
}

function switchMenu(obj){
	var listeDossiers = document.getElementById("menudiv").getElementsByTagName("span");
	var dossierSelected = document.getElementById(obj.nom);
	// Parcours des dossiers pour surligner le dossier sélectionné
	for (var i=0; i<listeDossiers.length; i++){
		if (listeDossiers[i].className == "Dossier" || listeDossiers[i].className == "DossierSelected"){
			listeDossiers[i].className = "Dossier";
		}
		if (listeDossiers[i].id == dossierSelected.id){
			listeDossiers[i].className = "DossierSelected";
		}
	}
	
	var ssMenu = document.getElementById(obj.nom+'_SsMenu');
	var eltsSsMenu = document.getElementById("menudiv").getElementsByTagName("div");
	// Si le sous-menu sélectionné n'est pas affiché, on l'affiche.
	// S'il est sélectionné, on le ferme
	if(ssMenu.style.display != "block"){
		// Parcours des éléments de sous-menus pour tous les rendre invisibles
		for (var i=0; i<eltsSsMenu.length; i++){
			if (eltsSsMenu[i].className=="SsMenu")
			eltsSsMenu[i].style.display = "none";
		}
		// Affichage du sous-menu sélectionné
		ssMenu.style.display = "block";
	}else{
		ssMenu.style.display = "none";
	}
}

//Lance le formulaire en alimentant le lien
function envoieFormulaire(lien) {
	lien.parentNode.style.display="none";
	if (courant != '') {
		courant.className="LienActif";
	}
	courant = lien;
	courant.className="LienCourant";

	leForm.ACTIVITE.value = lien.id;
	document.leForm.submit();
}

//Change le titre de la barre des titres
function changerTitre(titre) {
	//window.parent.frames("Titre").changerTitre(titre)
}

//Change le menu secondaire (en haut)
function changerMenuHaut(ssModule) {
	window.parent.frames["MenuHaut"].changerMenuHaut(ssModule);
}