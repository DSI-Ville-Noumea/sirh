package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.parametrage.NatureCredit;

public interface NatureCreditDaoInterface {

	public ArrayList<NatureCredit> listerNatureCredit();

	public void creerNatureCredit(String libelleNatureCredit) throws Exception;

	public void supprimerNatureCredit(Integer idNatureCredit) throws Exception;

}
