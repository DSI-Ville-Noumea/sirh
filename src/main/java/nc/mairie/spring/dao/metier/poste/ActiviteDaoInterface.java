package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.ActiviteFE;
import nc.mairie.metier.poste.ActiviteFP;

public interface ActiviteDaoInterface {

	public void supprimerActivite(Integer idActivite) throws Exception;

	public void modifierActivite(Integer idActivite, String nomActi) throws Exception;

	public void creerActivite(String nomActi) throws Exception;

	public Activite chercherActivite(Integer idActivite) throws Exception;

	public ArrayList<Activite> listerToutesActiviteAvecFP(ArrayList<ActiviteFP> liens) throws Exception;

	public ArrayList<Activite> listerActiviteAvecFP(ArrayList<ActiviteFP> liens) throws Exception;

	public ArrayList<Activite> listerActiviteAvecFE(ArrayList<ActiviteFE> liens) throws Exception;

	public ArrayList<Activite> listerActivite(boolean ordreAlpha) throws Exception;

}
