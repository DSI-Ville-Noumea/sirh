package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.Competence;
import nc.mairie.metier.poste.CompetenceFE;
import nc.mairie.metier.poste.CompetenceFP;

public interface CompetenceDaoInterface {

	public Competence chercherCompetenceAvecType(Integer idCompetence, Integer idTypeCompetence) throws Exception;

	public ArrayList<Competence> listerCompetenceAvecFEEtTypeComp(Integer idTypeCompetence,
			ArrayList<CompetenceFE> liens) throws Exception;

	public ArrayList<Competence> listerCompetenceAvecFP(ArrayList<CompetenceFP> liens) throws Exception;

	public ArrayList<Competence> listerCompetenceAvecFE(ArrayList<CompetenceFE> liens) throws Exception;

	public void supprimerCompetence(Integer idCompetence) throws Exception;

	public void modifierCompetence(Integer idCompetence, Integer idTypeCompetence, String nomComp) throws Exception;

	public void creerCompetence(Integer idTypeCompetence, String nomComp) throws Exception;

	public Competence chercherCompetence(Integer idCompetence) throws Exception;

	public ArrayList<Competence> listerCompetenceAvecType(Integer idTypeCompetence) throws Exception;

}
