package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.ActiviteFE;

public interface ActiviteFEDaoInterface {

	public ArrayList<ActiviteFE> listerActiviteFEAvecFE(Integer idFicheEmploi) throws Exception;

	public ArrayList<ActiviteFE> listerActiviteFEAvecActivite(Integer idActivite) throws Exception;

	public ActiviteFE chercherActiviteFE(Integer idFicheEmploi, Integer idActivite) throws Exception;

	public void creerActiviteFE(Integer idFicheEmploi, Integer idActivite) throws Exception;

	public void supprimerActiviteFE(Integer idFicheEmploi, Integer idActivite) throws Exception;

	public void supprimerActiviteFEAvecFE(Integer idFicheEmploi) throws Exception;

}
