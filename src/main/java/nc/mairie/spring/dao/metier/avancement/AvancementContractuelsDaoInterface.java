package nc.mairie.spring.dao.metier.avancement;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.avancement.AvancementContractuels;

public interface AvancementContractuelsDaoInterface {

	public void supprimerAvancementContractuelsTravailAvecAnnee(Integer annee);

	public void modifierAvancementContractuels(Integer idAvct, Integer idAgent, Date dateEmbauche, String numFp, String pa, Date dateGrade, Date dateProchainGrade, String iban, Integer inm,
			Integer ina, String nouvIban, Integer nouvInm, Integer nouvIna, String etat, Date dateArrete, String numArrete, String carriereSimu, Integer annee, String directionService,
			String sectionService, String codeCadre, String grade, String idNouvGrade);

	public void creerAvancementContractuels(Integer idAgent, Date dateEmbauche, String numFp, String pa, Date dateGrade, Date dateProchainGrade, String iban, Integer inm, Integer ina,
			String nouvIban, Integer nouvInm, Integer nouvIna, String etat, Date dateArrete, String numArrete, String carriereSimu, Integer annee, String directionService, String sectionService,
			String codeCadre, String grade, String idNouvGrade) throws Exception;

	public AvancementContractuels chercherAvancementContractuelsAvecAnneeEtAgent(Integer annee, Integer idAgent) throws Exception;

	public ArrayList<AvancementContractuels> listerAvancementContractuelsAnnee(Integer annee) throws Exception;

}
