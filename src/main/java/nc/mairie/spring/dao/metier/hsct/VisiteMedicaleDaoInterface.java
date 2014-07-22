package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.hsct.VisiteMedicale;

public interface VisiteMedicaleDaoInterface {

	public VisiteMedicale chercherVisiteMedicale(Integer idVM) throws Exception;

	public void creerVisiteMedicale(Integer idAgent, Integer idMedecin, Integer idRecommandation,
			Date dateDerniereVisite, Integer dureeValidite, Integer apte, Integer idMotifVm, Integer idSuiviMed)
			throws Exception;

	public void modifierVisiteMedicale(Integer idVM, Integer idAgent, Integer idMedecin, Integer idRecommandation,
			Date dateDerniereVisite, Integer dureeValidite, Integer apte, Integer idMotifVm, Integer idSuiviMed)
			throws Exception;

	public void supprimerVisiteMedicale(Integer idVM) throws Exception;

	public ArrayList<VisiteMedicale> listerVisiteMedicalePourSMCas2(Integer moisChoisi, Integer anneeChoisi)
			throws Exception;

	public VisiteMedicale chercherVisiteMedicaleCriteres(Integer idAgent, Integer idMedecin, Integer idMotif)
			throws Exception;

	public ArrayList<VisiteMedicale> listerVisiteMedicalePourSMCas1(Integer idMotifAgent, Integer idMotifService,
			Integer idMedecin) throws Exception;

	public VisiteMedicale chercherVisiteMedicaleLieeSM(Integer idSuiviMed, Integer idAgent) throws Exception;

	public ArrayList<VisiteMedicale> listerVisiteMedicaleAgent(Integer idAgent) throws Exception;

	public ArrayList<VisiteMedicale> listerVisiteMedicaleAvecMedecin(Integer idMedecin) throws Exception;

	public ArrayList<VisiteMedicale> listerVisiteMedicaleAvecRecommandation(Integer idRecommandation) throws Exception;

}
