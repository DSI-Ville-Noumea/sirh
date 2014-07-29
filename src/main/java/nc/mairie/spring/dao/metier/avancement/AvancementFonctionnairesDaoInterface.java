package nc.mairie.spring.dao.metier.avancement;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.avancement.AvancementFonctionnaires;

public interface AvancementFonctionnairesDaoInterface {

	public AvancementFonctionnaires chercherAvancementFonctionnaireAvecAnneeEtAgent(Integer annee, Integer idAgent)
			throws Exception;

	public ArrayList<AvancementFonctionnaires> listerAvancementAvecAnneeEtat(Integer annee, String etat,
			String filiere, Integer idAgent, ArrayList<String> listeSousService, String categorie, String idCap)
			throws Exception;

	public AvancementFonctionnaires chercherAvancement(Integer idAvct) throws Exception;

	public void supprimerAvancementTravailAvecCategorie(Integer annee) throws Exception;

	public void modifierAvancement(Integer idAvct, Integer idAvisCap, Integer idAgent, Integer idMotifAvct,
			String directionService, String sectionService, String filiere, String grade, String idNouvGrade,
			Integer annee, String cdcadr, Integer bmAnnee, Integer bmMois, Integer bmJour, Integer accAnnee,
			Integer accMois, Integer accJour, Integer nouvBmAnnee, Integer nouvBmMois, Integer nouvBmJour,
			Integer nouvAccAnnee, Integer nouvAccMois, Integer nouvAccJour, String iban, Integer inm, Integer ina,
			String nouvIban, Integer nouvInm, Integer nouvIna, Date dateGrade, Integer periodeStandard,
			Date dateAvctMini,Date dateAvctMoy,Date dateAvctMaxi, String numArrete, Date dateArrete, String etat, Integer codeCategorie,
			String carriereSimu, String userVerifSgc, Date dateVerifSgc, String heureVerifSgc, String userVerifSef,
			Date dateVerifSef, String heureVerifSef,String ordreMerite,String avisSHD, Integer idAvisArr, Integer idAvisEmp, String userVerifArr, Date dateVerifArr, String heureVerifArr,Date dateCap,
			String observationArr, String userVerifArrImpr, Date dateVerifArrImpr, String heureVerifArrImpr,
			boolean regularisation, boolean agentVdn, Integer idCap,String codePa) throws Exception;

	public void creerAvancement(Integer idAvisCap, Integer idAgent, Integer idMotifAvct, String directionService,
			String sectionService, String filiere, String grade, String idNouvGrade, Integer annee, String cdcadr,
			Integer bmAnnee, Integer bmMois, Integer bmJour, Integer accAnnee, Integer accMois, Integer accJour,
			Integer nouvBmAnnee, Integer nouvBmMois, Integer nouvBmJour, Integer nouvAccAnnee, Integer nouvAccMois,
			Integer nouvAccJour, String iban, Integer inm, Integer ina, String nouvIban, Integer nouvInm,
			Integer nouvIna, Date dateGrade, Integer periodeStandard, Date dateAvctMini, Date dateAvctMoy,
			Date dateAvctMaxi, String numArrete, Date dateArrete, String etat, Integer codeCategorie,
			String carriereSimu, String userVerifSgc, Date dateVerifSgc, String heureVerifSgc, String userVerifSef,
			Date dateVerifSef, String heureVerifSef, String ordreMerite, String avisSHD, Integer idAvisArr,
			Integer idAvisEmp, String userVerifArr, Date dateVerifArr, String heureVerifArr, Date dateCap,
			String observationArr, String userVerifArrImpr, Date dateVerifArrImpr, String heureVerifArrImpr,
			boolean regularisation, boolean agentVdn, Integer idCap, String codePa) throws Exception;
}