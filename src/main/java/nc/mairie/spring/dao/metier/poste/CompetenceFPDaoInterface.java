package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.CompetenceFP;

public interface CompetenceFPDaoInterface {

	public ArrayList<CompetenceFP> listerCompetenceFPAvecFP(Integer idFichePoste) throws Exception;

	public ArrayList<CompetenceFP> listerCompetenceFPAvecCompetence(Integer idCompetence) throws Exception;

	public void supprimerCompetenceFP(Integer idFichePoste, Integer idCompetence) throws Exception;

	public void creerCompetenceFP(Integer idFichePoste, Integer idCompetence) throws Exception;

	public CompetenceFP chercherCompetenceFP(Integer idFichePoste, Integer idCompetence) throws Exception;

}
