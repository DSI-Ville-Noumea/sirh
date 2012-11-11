package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.EAE.CampagneActeur;

public interface CampagneActeurDaoInterface {

	public void creerCampagneActeur(Integer idCampagneAction, Integer idAgent) throws Exception;

	public CampagneActeur chercherCampagneActeur(Integer idCampagneAction, Integer idAgent) throws Exception;

	public void supprimerCampagneActeur(Integer idCampagneActeur) throws Exception;

	public ArrayList<CampagneActeur> listerCampagneActeur(Integer idCampagneAction) throws Exception;

	public void supprimerTousCampagneActeurCampagne(Integer idCampagneAction) throws Exception;
}
