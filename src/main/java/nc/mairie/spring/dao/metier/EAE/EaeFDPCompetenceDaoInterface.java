package nc.mairie.spring.dao.metier.eae;

import java.util.ArrayList;

import nc.mairie.metier.eae.EaeFDPCompetence;

public interface EaeFDPCompetenceDaoInterface {

	public void creerEaeFDPCompetence(Integer idEaeFichePoste, String typeCompetence, String libComp) throws Exception;

	public ArrayList<EaeFDPCompetence> listerEaeFDPCompetence(Integer idEaeFichePoste) throws Exception;

	public void supprimerEaeFDPCompetence(Integer idEaeFDPCompetence) throws Exception;

}
