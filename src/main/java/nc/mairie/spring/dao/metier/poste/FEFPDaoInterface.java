package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.FEFP;

public interface FEFPDaoInterface {

	public FEFP chercherFEFPAvecNumFPPrimaire(Integer idFichePoste, boolean emploiPrimaire) throws Exception;

	public void supprimerFEFP(Integer idFicheEmploi, Integer idFichePoste, boolean emploiPrimaire) throws Exception;

	public void creerFEFP(Integer idFicheEmploi, Integer idFichePoste, boolean emploiPrimaire) throws Exception;

	public ArrayList<FEFP> listerFEFPAvecFE(Integer idFicheEmploi) throws Exception;

	public ArrayList<FEFP> listerFEFPAvecFP(Integer idFichePoste) throws Exception;

}
