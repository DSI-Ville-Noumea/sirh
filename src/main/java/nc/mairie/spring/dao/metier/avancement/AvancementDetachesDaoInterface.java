package nc.mairie.spring.dao.metier.avancement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.mairie.metier.avancement.AvancementDetaches;

public interface AvancementDetachesDaoInterface {

	public ArrayList<AvancementDetaches> listerAvancementAvecAnneeEtat(Integer annee, String etat, String libFiliere,
			Integer idAgent, List<String> listeSousService, String categorie) throws Exception;

	public AvancementDetaches chercherAvancementAvecAnneeEtAgent(Integer annee, Integer idAgent) throws Exception;

	public void supprimerAvancementTravailAvecCategorie(Integer annee) throws Exception;

	public void modifierAvancement(Integer idAvct, Integer idAgent, Integer idMotifAvct, String directionService,
			String sectionService, String filiere, String grade, String idNouvGrade, Integer annee, String cdcadr,
			Integer bmAnnee, Integer bmMois, Integer bmJour, Integer accAnnee, Integer accMois, Integer accJour,
			Integer nouvBmAnnee, Integer nouvBmMois, Integer nouvBmJour, Integer nouvAccAnnee, Integer nouvAccMois,
			Integer nouvAccJour, String iban, Integer inm, Integer ina, String nouvIban, Integer nouvInm,
			Integer nouvIna, Date dateGrade, Integer periodeStandard, Date dateAvctMoy, String numArrete,
			Date dateArrete, String etat, Integer codeCategorie, String carriereSimu, String userVerifSgc,
			Date dateVerifSgc, String heureVerifSgc, String userVerifSef, Date dateVerifSef, String heureVerifSef,
			String userVerifArr, Date dateVerifArr, String heureVerifArr, String observationArr,
			String userVerifArrImpr, Date dateVerifArrImpr, String heureVerifArrImpr, boolean regularisation,
			boolean agentVdn, String codePa) throws Exception;

	public void creerAvancement(Integer idAgent, Integer idMotifAvct, String directionService, String sectionService,
			String filiere, String grade, String idNouvGrade, Integer annee, String cdcadr, Integer bmAnnee,
			Integer bmMois, Integer bmJour, Integer accAnnee, Integer accMois, Integer accJour, Integer nouvBmAnnee,
			Integer nouvBmMois, Integer nouvBmJour, Integer nouvAccAnnee, Integer nouvAccMois, Integer nouvAccJour,
			String iban, Integer inm, Integer ina, String nouvIban, Integer nouvInm, Integer nouvIna, Date dateGrade,
			Integer periodeStandard, Date dateAvctMoy, String numArrete, Date dateArrete, String etat,
			Integer codeCategorie, String carriereSimu, String userVerifSgc, Date dateVerifSgc, String heureVerifSgc,
			String userVerifSef, Date dateVerifSef, String heureVerifSef, String userVerifArr, Date dateVerifArr,
			String heureVerifArr, String observationArr, String userVerifArrImpr, Date dateVerifArrImpr,
			String heureVerifArrImpr, boolean regularisation, boolean agentVdn, String codePa) throws Exception;

}
