package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.NiveauEtudeFP;

public interface NiveauEtudeFPDaoInterface {

	public ArrayList<NiveauEtudeFP> listerNiveauEtudeFPAvecFP(Integer idFichePoste) throws Exception;

	public void creerNiveauEtudeFP(Integer idNiveau, Integer idFichePoste) throws Exception;

	public void supprimerNiveauEtudeFP(Integer idNiveau, Integer idFichePoste) throws Exception;

}
