package nc.mairie.spring.dao.metier.suiviMedical;

import java.util.List;

import nc.mairie.metier.suiviMedical.MotifVisiteMed;

public interface MotifVisiteMedDaoInterface {

	public MotifVisiteMed chercherMotifByLib(String lib) throws Exception;

	public MotifVisiteMed chercherMotif(Integer idMotif) throws Exception;

	public List<MotifVisiteMed> listerMotifVisiteMed() throws Exception;
}
