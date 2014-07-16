package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.FamilleEmploi;

public interface FamilleEmploiDaoInterface {

	public void creerFamilleEmploi(String libelleFamilleEmploi, String codeFamilleEmploi) throws Exception;

	public void supprimerFamilleEmploi(Integer idFamilleEmploi) throws Exception;

	public ArrayList<FamilleEmploi> listerFamilleEmploi() throws Exception;

	public FamilleEmploi chercherFamilleEmploi(Integer idFamilleEmploi) throws Exception;

}
