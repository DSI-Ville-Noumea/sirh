package nc.mairie.spring.dao.metier.avancement;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.avancement.AvancementConvCol;

public interface AvancementConvColDaoInterface {

	public ArrayList<AvancementConvCol> listerAvancementConvColAvecAnnee(Integer annee) throws Exception;

	public void supprimerAvancementConvColTravailAvecAnnee(Integer annee) throws Exception;

	public void modifierAvancementConvCol(Integer idAvct, Integer idAgent, Integer annee, String etat,
			String numArrete, Date dateArrete, Date dateEmbauche, String grade, String libGrade,
			String directionService, String sectionService, String carriereSimu, String montantPrime1200, String codePa);

	public void creerAvancementConvCol(Integer idAgent, Integer annee, String etat, String numArrete, Date dateArrete,
			Date dateEmbauche, String grade, String libGrade, String directionService, String sectionService,
			String carriereSimu, String montantPrime1200, String codePa) throws Exception;

	public AvancementConvCol chercherAvancementConvColAvecAnneeEtAgent(Integer annee, Integer idAgent) throws Exception;

}
