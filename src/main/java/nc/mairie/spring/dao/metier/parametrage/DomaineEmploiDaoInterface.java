package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.DomaineEmploi;

public interface DomaineEmploiDaoInterface {

	public void creerDomaineEmploi(String libelleDomaineEmploi, String codeDomaineEmploi) throws Exception;

	public void supprimerDomaineEmploi(Integer idDomaineEmploi) throws Exception;

	public ArrayList<DomaineEmploi> listerDomaineEmploi() throws Exception;

	public DomaineEmploi chercherDomaineEmploi(Integer idDomaineEmploi) throws Exception;

}
