package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.EAE.EaeFDPActivite;

public interface EaeFDPActiviteDaoInterface {

	public void creerEaeFDPActivite(Integer idEaeFichePoste, String libActi) throws Exception;

	public ArrayList<EaeFDPActivite> listerEaeFDPActivite(Integer idEaeFichePoste) throws Exception;

	public void supprimerEaeFDPActivite(Integer idEaeFDPActivite) throws Exception;

}
