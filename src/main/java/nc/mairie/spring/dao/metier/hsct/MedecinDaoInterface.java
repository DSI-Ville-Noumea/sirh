package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.Medecin;

public interface MedecinDaoInterface {

	public Medecin chercherMedecin(Integer idMedecin) throws Exception;

	public void creerMedecin(String titreMedecin, String prenomMedecin, String nomMedecin) throws Exception;

	public void supprimerMedecin(Integer idMedecin) throws Exception;

	public Medecin chercherMedecinARenseigner(String prenomMedecin, String nomMedecin) throws Exception;

	public ArrayList<Medecin> listerMedecin() throws Exception;

}
