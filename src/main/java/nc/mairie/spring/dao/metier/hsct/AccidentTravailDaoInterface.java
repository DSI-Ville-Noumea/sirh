package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.hsct.AccidentTravail;

public interface AccidentTravailDaoInterface {

	public ArrayList<AccidentTravail> listerAccidentTravailAgent(Integer idAgent) throws Exception;

	public ArrayList<AccidentTravail> listerAccidentTravailAvecTypeAT(Integer idTypeAT) throws Exception;

	public ArrayList<AccidentTravail> listerAccidentTravailAvecSiegeLesion(Integer idSiege) throws Exception;

	public AccidentTravail chercherAccidentTravail(Integer idAT) throws Exception;

	public void creerAccidentTravail(Integer idTypeAT, Integer idSiege, Integer idAgent, Date dateAT,
			Date dateInitiale, Integer nbJoursITT, Date dateFin, Integer avisCommission, Integer idAtReference, Boolean rechute) throws Exception;

	public void modifierAccidentTravail(Integer idAT, Integer idTypeAT, Integer idSiege, Integer idAgent, Date dateAT,
			Date dateInitiale, Integer nbJoursITT, Date dateFin, Integer avisCommission, Integer idAtReference, Boolean rechute) throws Exception;

	public void supprimerAccidentTravail(Integer idAT) throws Exception;

}
