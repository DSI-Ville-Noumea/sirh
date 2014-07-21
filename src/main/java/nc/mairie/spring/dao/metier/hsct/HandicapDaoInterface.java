package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.hsct.Handicap;

public interface HandicapDaoInterface {

	public void creerHandicap(Integer idAgent, Integer idTypeHandicap, Integer idMaladiePro,
			Integer pourcentIncapacite, boolean reconnaissanceMp, Date dateDebutHandicap, Date dateFinHandicap,
			boolean handicapCRDHNC, String numCarteCrdhnc, boolean amenagementPoste, String commentaireHandicap,
			boolean renouvellement) throws Exception;

	public void modifierHandicap(Integer idHandicap, Integer idAgent, Integer idTypeHandicap, Integer idMaladiePro,
			Integer pourcentIncapacite, boolean reconnaissanceMp, Date dateDebutHandicap, Date dateFinHandicap,
			boolean handicapCRDHNC, String numCarteCrdhnc, boolean amenagementPoste, String commentaireHandicap,
			boolean renouvellement) throws Exception;

	public void supprimerHandicap(Integer idHandicap) throws Exception;

	public Handicap chercherHandicap(Integer idHandicap) throws Exception;

	public ArrayList<Handicap> listerHandicapAvecMaladiePro(Integer idMaladiePro) throws Exception;

	public ArrayList<Handicap> listerHandicapAgent(Integer idAgent) throws Exception;

}
