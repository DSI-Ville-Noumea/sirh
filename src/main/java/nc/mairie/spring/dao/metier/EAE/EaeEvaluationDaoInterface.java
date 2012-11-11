package nc.mairie.spring.dao.metier.EAE;

import java.sql.Connection;

import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;

public interface EaeEvaluationDaoInterface {

	public EaeEvaluation chercherEaeEvaluation(Integer idEAE) throws Exception;

	public Connection creerEaeEvaluation(Integer idEae, Integer idNiveau, Double noteAnnee, Double noteAnneeN1, Double noteAnneeN2,
			Double noteAnneeN3, String avis, String avctDifferencie, String changementClasse, String avisSHD) throws Exception;

	public int compterAvisSHDNonDefini(Integer idCampagneEAE, String direction, String section) throws Exception;

	public int compterAvisSHDAvct(Integer idCampagneEAE, String direction, String section, String dureeAvct) throws Exception;

	public int compterAvisSHDChangementClasse(Integer idCampagneEAE, String direction, String section) throws Exception;
}
