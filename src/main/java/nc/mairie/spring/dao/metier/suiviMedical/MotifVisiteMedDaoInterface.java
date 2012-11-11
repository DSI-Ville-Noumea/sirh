package nc.mairie.spring.dao.metier.suiviMedical;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.suiviMedical.MotifVisiteMed;

public interface MotifVisiteMedDaoInterface {

	public MotifVisiteMed chercherMotifByLib(String lib) throws Exception;

	public MotifVisiteMed chercherMotif(Integer idMotif) throws Exception;

	public ArrayList<MotifVisiteMed> listerMotifVisiteMed() throws Exception;
}
