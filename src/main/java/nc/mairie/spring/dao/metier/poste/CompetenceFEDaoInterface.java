package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.CompetenceFE;

public interface CompetenceFEDaoInterface {

	public ArrayList<CompetenceFE> listerCompetenceFEAvecCompetence(Integer idCompetence) throws Exception;

	public ArrayList<CompetenceFE> listerCompetenceFEAvecFE(Integer idFicheEmploi) throws Exception;

	public void supprimerCompetenceFE(Integer idFicheEmploi, Integer idCompetence) throws Exception;

	public void creerCompetenceFE(Integer idFicheEmploi, Integer idCompetence) throws Exception;

	public CompetenceFE chercherCompetenceFE(Integer idFicheEmploi, Integer idCompetence) throws Exception;

	public ArrayList<CompetenceFE> listerCompetenceFEAvecFEEtTypeComp(Integer idFicheEmploi, Integer idCompetence)
			throws Exception;

}
