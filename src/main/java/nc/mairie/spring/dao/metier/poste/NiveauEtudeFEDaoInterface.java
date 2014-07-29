package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.NiveauEtudeFE;

public interface NiveauEtudeFEDaoInterface {

	public ArrayList<NiveauEtudeFE> listerNiveauEtudeFEAvecFE(Integer idFicheEmploi) throws Exception;

	public NiveauEtudeFE chercherNiveauEtudeFE(Integer idNiveau, Integer idFicheEmploi) throws Exception;

	public void creerNiveauEtudeFE(Integer idNiveau, Integer idFicheEmploi) throws Exception;

	public void supprimerNiveauEtudeFE(Integer idNiveau, Integer idFicheEmploi) throws Exception;

	public void supprimerNiveauEtudeFEAvecFE(Integer idFicheEmploi) throws Exception;

}
