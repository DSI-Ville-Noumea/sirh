package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.ActiviteFP;

public interface ActiviteFPDaoInterface {

	public ActiviteFP chercherActiviteFP(Integer idFichePoste, Integer idActivite) throws Exception;

	public void creerActiviteFP(Integer idFichePoste, Integer idActivite,boolean principale) throws Exception;

	public void supprimerActiviteFP(Integer idFichePoste, Integer idActivite,boolean principale) throws Exception;

	public void modifierActiviteFP(Integer idFichePoste, Integer idActivite,boolean principale) throws Exception;

	public ArrayList<ActiviteFP> listerActiviteFPAvecFP(Integer idFichePoste) throws Exception;

	public ArrayList<ActiviteFP> listerActiviteFPAvecActivite(Integer idActivite) throws Exception;

}
