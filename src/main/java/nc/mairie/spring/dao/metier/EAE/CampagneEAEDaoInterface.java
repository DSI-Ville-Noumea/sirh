package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.eae.CampagneEAE;

public interface CampagneEAEDaoInterface {

	public ArrayList<CampagneEAE> listerCampagneEAE() throws Exception;

	public CampagneEAE chercherCampagneEAE(Integer idCampagneEAE) throws Exception;

	public Integer creerCampagneEAE(Integer annee, Date dateDebut, String commentaire) throws Exception;

	public void modifierCampagneEAE(Integer idCampagneEAE, Date dateDebut, String commentaire) throws Exception;

	public void modifierOuvertureKiosqueCampagneEAE(Integer idCampagneEAE, Date dateOuvertureKiosque) throws Exception;

	public void modifierFermetureKiosqueCampagneEAE(Integer idCampagneEAE, Date dateFermetureKiosque) throws Exception;

	public void modifierFinCampagneEAE(Integer idCampagneEAE, Date dateFin) throws Exception;

	public CampagneEAE chercherCampagneEAEAnnee(Integer annee) throws Exception;

	public CampagneEAE chercherCampagneEAEOuverte() throws Exception;
}
