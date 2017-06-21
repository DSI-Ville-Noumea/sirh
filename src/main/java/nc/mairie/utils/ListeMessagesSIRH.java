package nc.mairie.utils;

import java.util.Hashtable;

/**
 * Liste des messages utilises dans SIRH Date de creation : (23/03/2011
 * 10:00:00)
 */
public class ListeMessagesSIRH {
	private static Hashtable<String, String> listeMessages;

	/**
	 * Inserez la description de la methode ici. Date de creation : (23/03/2011
	 * 10:00:00)
	 * 
	 * @return Hashtable
	 */
	public static Hashtable<String, String> getListeMessages() {
		if (listeMessages == null) {
			listeMessages = new Hashtable<String, String>();

			// message commun
			listeMessages.put("ERR001", "Le libellé @ est obligatoire");
			listeMessages.put("ERR002", "La zone @ est obligatoire.");
			listeMessages.put("ERR003", "Le paramêtre @ est incorrect.");
			listeMessages.put("ERR004", "Vous devez d'abord rechercher un agent.");
			listeMessages.put("ERR005", "Aucun @ trouvé.");
			listeMessages.put("ERR006", "Il n'y a pas d'action en cours.");
			listeMessages.put("ERR007", "La date @ est incorrecte. Elle doit être au format date.");
			listeMessages.put("ERR008", "Aucun élément n'est sélectionné dans la liste des @.");
			listeMessages.put("ERR009", "Une erreur s'est produite sur la base de données.");
			listeMessages.put("ERR0091", "Impossible de sauvegarder, le @ est trop long.");

			// AGT-Etat Civil
			listeMessages.put("ERR010", "Vous devez saisir le code pays ou le début son libellé");
			listeMessages.put("ERR011", "Vous devez saisir une partie du libellé de la voie");
			listeMessages.put("ERR012", "Vous devez saisir le code commune ou une partie de son libellé");
			listeMessages.put("ERR013", "La date de naissance doit être antérieure à  la date du jour");
			listeMessages.put("ERR014", "La date d'embauche ne doit pas être postérieure à  la date du jour");
			listeMessages.put("ERR015", "La date d'embauche doit être postérieure à  la date de naissance + 16 ans");
			listeMessages.put("ERR016", "Si la désignation est M, le nom marital doit être à  blanc");
			listeMessages.put("ERR017", "La clé RIB est éronnée.");
			listeMessages.put("ERR018", "Les contacts de type 'Telephone', 'Fax', 'Mobile', 'Mobile professionnel' et 'Ligne directe' ne doivent pas dépasser 6 caractères. Seul l'Email peut contenir jusqu'à  50 caractères.");
			listeMessages.put("ERR019", "La BP est obligatoire si le code postal et la ville de la boîte postale sont renseignés.");
			listeMessages.put("ERR0020", "La date d'arrivée sur le territoire ne peut être supprimée. Vous pouvez cependant la modifier.");

			// AGT-Enfants
			listeMessages.put("ERR020", "Données erronées. Un enfant ne peut avoir plus de 2 parents et celui ci est lié aux agents suivants : @.");

			// AGT-Contrats
			listeMessages.put("ERR030", "Une @ ne peut se faire que sur un contrat en cours.");
			listeMessages.put("ERR031", "Il n'existe aucun contrat pour créer l'avenant.");
			listeMessages.put("ERR032", "Un agent ne peut avoir qu'un seul contrat à  une date donnée");
			listeMessages.put("ERR033", "La date de fin est obligatoire dans le cas d'un CDD");
			listeMessages.put("ERR034", "Une impression ne peut se faire que sur un CDD.");
			listeMessages.put("ERR035", "Attention @ pas égal à  0. Merci de corriger le/les compteur(s) pour pouvoir créer le contrat.");

			// AGT-Visites medicales
			listeMessages.put("ERR040", "Une inaptitude ne peut être créée que pour la dernière visite médicale.");
			listeMessages.put("ERR041", "Il ne peut y avoir deux fois la même inaptitude pour une même période.");
			listeMessages.put("ERR042", "Vous ne pouvez pas modifier une demande de contrôle médicale.");

			// AGT-Accidents du travail
			listeMessages.put("ERR050", "L'agent ne peut pas avoir un accident du travail durant une période d'ITT.");

			// AGT-Prime
			listeMessages.put("ERR060", "Attention, il existe déjà  une prime de ce type sur cette période à  Veuillez contrôler.");
			listeMessages.put("ERR061", "Le jour de la date de début doit etre égal au premier jour du mois.");
			listeMessages.put("ERR062", "Le jour de la date de fin doit etre égal au premier jour du mois.");
			listeMessages.put("ERR063", "La prime est une prime pointage et aucune action ne peut être faite sur celle-ci.");

			// AGT-Position adm
			listeMessages.put("ERR070", "Attention, il existe déjà  une position administrative sur cette période à  Veuillez contrôler.");

			// AGT-Emplois-Affectations
			listeMessages.put("ERR080", "Le temps de travail réglementaire des deux fiche de poste dépasse 100%.");
			listeMessages.put("ERR081", "Cette affectation est inactive, elle ne peut être ni supprimée,ni imprimée.");
			listeMessages.put("ERR082", "ATTENTION ERREUR DE DONNEES. Il existe plusieurs affectations actives pour le même agent.");
			listeMessages.put("ERR083", "Cet agent n'a aucune affectation active.");
			listeMessages.put("ERR084", "Cette Fiche de poste est déjà  affectée.");
			listeMessages.put("ERR085", "Cette Fiche de poste est déjà  affectée à  un autre agent aux dates données.");
			listeMessages.put("ERR086", "Il n'y a pas de carrière active pour cette date de début d'affectation.");
			listeMessages.put("ERR087", "Cette fiche de poste n'est pas 'Validée' ou 'Gelée'. Elle ne peut pas être affectée à  un agent.");
			listeMessages.put("ERR088", "Cette prime est déjà  présente pour cet agent.");
			listeMessages.put("ERR089", "La fiche de poste @ est @. Vous ne pouvez pas créer l'affectation");

			// AGT-HSCT
			listeMessages.put("ERR090", "La position administrative de l'agent n'autorise pas les @ à  la date indiquée.");
			listeMessages.put("ERR091", "Une convocation est en attente, vous ne pouvez pas créer de visite médicale avec ce motif.");
			listeMessages.put("ERR092", "Vous ne pouvez ajouter de document pour ce type.");

			// AGT-Charge
			listeMessages.put("ERR100", "Attention, il existe déjà  une charge de ce type sur cette période. Veuillez contrôler.");
			listeMessages.put("ERR101", "Le taux doit être inférieur a 10");
			listeMessages.put("ERR102", "Seule la dernière PA peut être supprimée.");

			// P&E-Fiche de poste
			listeMessages.put("ERR110", "La liste @ ne doit contenir qu'un seul élément.");
			listeMessages.put("ERR111", "Impossible de créer/supprimer la fiche de poste.");
			listeMessages.put("ERR112", "Vous devez saisir un titre de poste existant. '@' n'est pas correct.");
			listeMessages.put("ERR113", "Recherche par service impossible tant qu'il n'est pas saisi sur la fiche de poste.");
			listeMessages.put("ERR114", "Cette fiche de poste ne peut être inactive car elle est affectée à  un agent.");
			listeMessages.put("ERR115", "Cette fiche de poste ne peut être inactive car elle est utilisée comme responsable hiérarchique.");
			listeMessages.put("ERR116", "Le responsable hiérarchique doit être différent de la fiche courante.");
			listeMessages.put("ERR117", "La fiche de poste @ doit être différente de la fiche courante.");
			listeMessages.put("ERR118", "L'année doit être saisie avec 4 chiffres.");
			listeMessages.put("ERR119", "La mission ne doit pas dépasser 2000 caractères.");
			listeMessages.put("ERR1110", "Plusieurs résultats. Merci d'affiner votre recherche dans la recherche avancée.");
			listeMessages.put("ERR1111", "Si la nature des crédits est @, alors budgété doit être @.");
			listeMessages.put("ERR1112", "Si la nature des crédits est @, alors budgété ne doit pas être @.");
			listeMessages.put("ERR1113", "Budget de remplacement : fiche de poste remplacée nécessaire.");
			listeMessages.put("ERR1114", "Fiche de poste remplacée mais budget différent de remplacement.");
			listeMessages.put("ERR1115", "Le poste n'est pas réglementaire, le budget ne peut pas être permanent.");
			listeMessages.put("ERR1116", "Le poste est réglementaire, le budget doit être permanent.");
			listeMessages.put("ERR1117", "Une FDP ne peut être supprimée que si son statut est : 'En création'.");
			listeMessages.put("ERR1118", "Une FDP ne peut pas être supprimée si elle a déjà  été affectée.");
			listeMessages.put("ERR1119", "Plusieurs agents correspondent à  votre recherche. Merci de passer par la recherche avancée des agents.");
			listeMessages.put("ERR1120", "Aucun agent correspondant à  votre recherche. Merci de passer par la recherche avancée des agents.");

			// P&E-
			listeMessages.put("ERR120", "Impossible de supprimer @ actuellement utilisée par @.");
			listeMessages.put("ERR121", "Le champ @ ne peut pas être 'indeterminé' si la fiche de poste est au statut 'validée'.");
			listeMessages.put("ERR122", "Suppression impossible. Aucune fiche emploi n'est sélectionnée.");
			listeMessages.put("ERR123", "Le statut ne peut repasser à  @.");
			listeMessages.put("ERR124", "Le statut ne peut passer à  @ s'il n'est pas @.");
			listeMessages.put("ERR125", "Impossible de trouver @.");
			listeMessages.put("ERR126", "Le statut de la FDP ne peut être @ si l'entité n'est pas @.");
			listeMessages.put("ERR127", "Le statut de la FDP ne peut être @ si l'entité est @.");

			// AGT-Elt Sal
			listeMessages.put("ERR130", "Cette carrière est inactive, elle ne peut être supprimée.");
			listeMessages.put("ERR131", "Une carrière active existe déjà  pour cet agent. Il est impossible d'en créer une nouvelle.");
			listeMessages.put("ERR132", "L'agent n'a pas de PA en cours.");
			listeMessages.put("ERR133", "Attention : vérifier la PA et l'affectation de cet agent, les dates sont incompatibles.");
			listeMessages.put("ERR134", "Attention, il existe déjà  une carrière sur cette période. Veuillez contrôler.");
			listeMessages.put("ERR135", "Il n'y a pas de PA active pour cette date de début de carrière.");
			listeMessages.put("ERR136", "Cet agent n'a aucune carrière active.");

			// PARAM
			listeMessages.put("ERR140", "Les indices doivent respecter leur cohérence d'infériorité et/ou de supériorité avec les indices suivants ou précédents dans les grilles.");
			listeMessages.put("ERR141", "Cette grille existe déjà .");
			listeMessages.put("ERR142", "L'IBAN doit etre supérieur à  l'IBAN du grade précédant.");
			listeMessages.put("ERR143", "L'IBAN doit etre inférieur à  l'IBAN du grade suivant.");
			listeMessages.put("ERR144", "Impossible de modifier/supprimer le jour.");
			listeMessages.put("ERR145", "Impossible d'activer/désactiver les grilles associées au grade @. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR146", "Le code @ du grade suivant ne correspond à  aucun grade.");
			listeMessages.put("ERR147", "Cette famille ne se gère pas par compteur. Il est donc impossible de saisir un motif.");
			listeMessages.put("ERR148", "La date doit être supérieure à  la date du jour.");
			listeMessages.put("ERR149", "Attention, il n'existe pas de rubrique avec ce numéro.");
			listeMessages.put("ERR1491", "Cette rubrique n'est pas paramétrée.Merci de renseigner son paramétrage dans PARAMETRES/Eléments salaire/Rubrique.");

			// AGT-Actes
			listeMessages.put("ERR150", "La photo doit être au format JPEG (.jpg)");

			// AGT-EAE
			listeMessages.put("ERR160", "La note doit être comprise entre 0 et 20.");
			listeMessages.put("ERR161", "La note ne doit pas avoir plus de 2 décimales.");
			listeMessages.put("ERR162", "Le contenu du rapport circonstancié ne doit pas être vide pour une durée d'avancement minimale ou maximale.");
			listeMessages.put("ERR163", "Le contenu du rapport circonstancié ne doit pas être rempli pour une durée d'avancement moyenne.");
			listeMessages.put("ERR164", "Une erreur est survenue dans la sauvegarde de l'EAE. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR165", "Le fichier doit être au format PDF.");
			listeMessages.put("ERR166", "Vous ne pouvez uploader un document pour une année supérieure à  2012.");
			listeMessages.put("ERR167", "Un fichier existe déjà  pour cette année. Veuillez choisir une autre année.");

			// CAMPAGNE
			listeMessages.put("ERR170", "La date du champ 'A transmettre le' doit être supérieure à  la date du jour.");

			// AVCT
			listeMessages.put("ERR180", "Cet agent n'est pas fontionnaire, contractuel ou détaché. Il ne peut pas être soumis à  l'avancement.");
			listeMessages.put("ERR181", "Cet agent n'est pas de type @. Il ne peut pas être soumis à  l'avancement @.");
			listeMessages.put("ERR182", "Une erreur est survenue dans la génération du tableau. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR183", "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR184", "Une erreur est survenue dans la sauvegarde du tableau. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR185", "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR186", "Aucun barême suivant n'a été trouvé pour se grade. Le calcul de l'avancement ne peut donc se faire.");
			listeMessages.put("ERR187", "Si le motif d'avancement est @. Alors @ doit être égal à  0.");
			listeMessages.put("ERR188", "Ce motif d'avancement n'a pas de règle de gestion. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR189", "Cet avancement ne peut être calculé @.");

			// DROITS
			listeMessages.put("ERR190", "Opération impossible. Vous ne disposez pas des droits d'accés à cette option.");

			// Dates
			listeMessages.put("ERR200", "La date @ doit être supérieure ou égale à la date @.");
			listeMessages.put("ERR201", "Opération impossible. La période saisie ne doit pas chevaucher les périodes précédentes.");
			listeMessages.put("ERR202", "La date @ doit être comprise entre la date @ et la date @.");
			listeMessages.put("ERR203", "Opération impossible. Veuillez vérifier le format des dates.");
			listeMessages.put("ERR204", "La date @ doit être inférieure à la date @.");
			listeMessages.put("ERR205", "La date @ doit être supérieure à la date @.");

			// Campagnes EAE
			listeMessages.put("ERR210", "Toutes les campagnes ne sont pas cloturées. Vous ne pouvez pas en créer une nouvelle.");
			listeMessages.put("ERR211", "Vous ne pouvez pas sélectionner plus de 2 évaluateurs.");
			listeMessages.put("ERR212", "Aucune campagne n'est ouverte. Le calcul ne s'effectue que sur une campagne ouverte.");
			listeMessages.put("ERR216", "Le calcul des EAE pour cette campagne en cours de traitement.");

			// SM-Convocation
			listeMessages.put("ERR300", "La date du prochain RDV pour l'agent @ doit être supérieure ou égale au mois selectionné.");
			listeMessages.put("ERR301", "La date du prochain RDV est incorrecte pour l'agent @. Elle doit être au format date.");
			listeMessages.put("ERR302", "La date du prochain RDV pour l'agent @ doit être supérieure ou égale à  la date du jour");

			// PTG-DROITS
			listeMessages.put("ERR400", "L'agent @ n'est affecté à  aucun poste. Il ne peut être ajouté en tant qu'approbateur.");

			// PTG-VISUALISATION
			listeMessages.put("ERR500", "Le champ date de début est obligatoire.");
			listeMessages.put("ERR501", "La sélection des filtres engendre plus de 1000 agents. Merci de réduire la sélection.");
			listeMessages.put("ERR502", "Le sigle service saisie ne permet pas de trouver le service associé.");
			listeMessages.put("ERR503", "L'agent @ n'existe pas. Merci de saisir un matricule existant.");
			listeMessages.put("ERR504", "L'agent @ n'est affecté à  aucun poste le @. Aucun pointage ne peut être saisi pour cette date.");

			// PTG-VENTILATION
			listeMessages.put("ERR600", "La date de ventilation choisie est un @. Impossible de ventiler les pointages à  une date autre qu'un dimanche.");
			listeMessages.put("ERR601", "Il n'y a pas de ventilation en cours.");
			listeMessages.put("ERR602", "La ventilation des @ n'a pu être lancée. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR603", "La déversement dans la paie des @ n'a pu être lancée. Merci de contacter le responsable du projet.");

			// PTG-PAYEUR
			listeMessages.put("ERR700", "Impossible de récupérer l'historique des éditions pour les @. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR701", "Impossible de consulter l'édition @. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR702", "Une erreur est survenue lors du lancement des éditions pour les @. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR703", "Impossible de récupérer l'historique des éditions des titres repas. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR704", "Le fichier doit être au format CSV.");

			// ABSENCE
			listeMessages.put("ERR800", "Seul un des deux champs durée doit être renseigné.");
			listeMessages.put("ERR801", "Le nouveau solde du compteur ne peut être négatif.");
			listeMessages.put("ERR802", "Cet agent n'est ni contractuel ni convention collective, il ne peut avoir de repos compensateur.");
			listeMessages.put("ERR803", "Pour @ cette demande, merci de renseigner un motif.");
			listeMessages.put("ERR804", "Une erreur est survenue dans la sauvegarde d'un type d'absence. Merci de contacter le responsable du projet.");
			listeMessages.put("ERR805", "L'agent @ n'a pas de base horaire d'absence. Merci de la renseigner dans l'affectation de l'agent.");
			listeMessages.put("ERR806", "Cet agent est adjoint, conseiller municipal ou maire, il ne peut avoir de congé annuel.");
			listeMessages.put("ERR807", "Cet agent n'a pas pour base congé A ou D. Il ne peut avoir de restitution de congés annuel.");
			listeMessages.put("ERR808", "ATTENTION : Le compteur @ a bien été mis à  jour mais il est NEGATIF.");

			// AUtres messages
			listeMessages.put("ERR967", "Aucun élément dans la liste des @.");
			listeMessages.put("ERR968", "La zone @ ne peut être supérieure à  la zone @.");
			listeMessages.put("ERR969", "Il ne peut y avoir qu'un seul contact de type 'Ligne directe'.");
			listeMessages.put("ERR970",
					"Une erreur est survenue lors de la mise à  jour de l'arbre des fiches de poste. Merci de contacter le responsable du projet car cela engendre un soucis sur le Kiosque RH.");
			listeMessages.put("ERR974", "Attention, il existe déjà  @ avec @. Veuillez contrôler.");
			listeMessages.put("ERR975", "Problème rencontré à  la suppression de : @.");
			listeMessages.put("ERR976", "Problème rencontré à  la création de : @.");
			listeMessages.put("ERR978", "Problème rencontré à  la modification de : @.");
			listeMessages.put("ERR979", "Au moins une des 2 zones suivantes doit être renseignée : @ ou @.");
			listeMessages.put("ERR980", "La zone @ ne peut excéder @ caractères.");
			listeMessages.put("ERR981", "La zone @ doit être comprise entre 0 et 100.");
			listeMessages.put("ERR982", "Opération impossible. Vous devez saisir un critère de recherche.");
			listeMessages.put("ERR989", "Suppression impossible. Il existe au moins @ rattaché à  @.");
			listeMessages.put("ERR992", "La zone @ doit être numérique.");
			listeMessages.put("ERR995", "En suppression, aucune zone n'est modifiable.");
			listeMessages.put("ERR996", "Merci de contacter votre administrateur fonctionnel, une erreur est survenue dans SPCOPA.");
			listeMessages.put("ERR997", "Ce numéro de @ est déjà  utilisé, il doit être unique.");

			// Messages d'information
			// AGT
			listeMessages.put("INF001", "Agent @ créé.");
			listeMessages.put("INF002", "Agent @ modifié.");
			listeMessages.put("INF003", "L'agent @ n'est plus affecté à  aucun poste.");
			listeMessages.put("INF004", "Veiller à  supprimer les spécificités liées à  ce poste.");
			listeMessages.put("INF005", "La date de fin de la carrière précédente a été mise à  jour.");
			listeMessages.put("INF006", "Attention : veillez à  vérifier l'affectation de cet agent.");
			listeMessages.put("INF007", "Attention : ce contrat a déjà  3 avenants. Veuillez vérifier son motif.");
			listeMessages.put("INF008", "Attention, changement de statut dans les carrières : N'oubliez pas de modifier les charges en conséquence.");
			listeMessages.put("INF009", "Attention, la position administrative de l'agent n'autorise pas les @ à  la date indiquée.");
			listeMessages.put("INF010", "Le compteur @ a bien été mis à  jour.");
			listeMessages.put("INF011", "Attention : la fiche de poste @ est @.");
			listeMessages.put("INF012", "Attention : il faudra penser à  passer la FDP en @ à  partir du @.");
			listeMessages.put("INF013", "Attention : la FDP @ a été inactivée.");

			// P&E
			listeMessages.put("INF100", "Fiche emploi créée : @");
			listeMessages.put("INF101", "Fiche emploi supprimée : @");
			listeMessages.put("INF102", "La fiche emploi @ va être supprimée. Veuillez valider votre choix.");
			listeMessages.put("INF103", "Fiche de poste créée : @");
			listeMessages.put("INF104", "Fiche de poste dupliquée : @");
			listeMessages.put("INF105", "La fiche emploi @ est liée à  une ou plusieurs fiches de poste. Si vous validez, vous supprimerez également ce(s) lien(s). Merci de valider votre choix.");
			listeMessages.put("INF106", "Fiche de poste modifiée : @");
			listeMessages.put("INF107", "Duplication impossible. Aucune fiche emploi n'est sélectionnée.");
			listeMessages.put("INF108", "Fiche emploi modifiée : @");
			listeMessages.put("INF109", "Compétence @ : @");
			listeMessages.put("INF110", "Activité @ : @");
			listeMessages.put("INF111", "Fiche de poste imprimée : @");
			listeMessages.put("INF112", "Attention : la FDP @ est transitoire, budgeté et réglementaire devrait être à  non.");

			// AVCT
			listeMessages.put("INF200", "Simulation effectuée.");
			listeMessages.put("INF201", "@ agents ont été affectés.");
			listeMessages.put("INF202", "Calcul effectué.");
			listeMessages.put("INF203", "@ EAE(s) ont été mis à  jour.");

			// CARRIERES
			listeMessages.put("INF300", "Attention, la date de debut de la carrière semble incorrecte. Veuillez contrôler.");
			listeMessages.put("INF300", "Attention, vous n'avez pas saisi de motif pour cette carrière.");

			// SUIVI MEDICAL
			listeMessages.put("INF400", "Calcul effectué.");

			// EAE
			listeMessages.put("INF500", "Aucun avancement n'a été trouvé pour cet EAE. Le motif et l'avis SHD n'ont pu être mis à  jour.");
			listeMessages.put("INF501", "L'EAE a été correctement sauvegardé.");
			listeMessages.put("INF502", "Le lancement du calcul des EAE est bien pris en compte. Celui-ci est en cours de traitement.");

			
			// ELECTION
			listeMessages.put("INF700", "Les représentants ont bien été mis à  jour.");

		}
		return listeMessages;
	}
}
