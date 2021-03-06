package nc.mairie.spring.dao.metier.agent;

import java.util.Date;
import java.util.List;

import nc.mairie.metier.agent.Scolarite;

public interface ScolariteDaoInterface {

	/**
	 * Methode creerObjetMetierBroker qui retourne true ou false
	 */
	public void creerScolarite(Integer idEnfant, Date dateDebut, Date dateFin) throws Exception;

	/**
	 * Methode modifierObjetMetierBroker qui retourne true ou false
	 */
	public void modifierScolarite(Integer idScolarite, Integer idEnfant, Date dateDebut, Date dateFin) throws Exception;

	/**
	 * Retourne un ArrayList d'objet metier Scolarite d'un enfant.
	 * 
	 * @return ArrayList
	 */
	public List<Scolarite> listerScolariteEnfant(Integer idEnfant) throws Exception;

	void supprimerScolarite(Integer id) throws Exception;

	List<Scolarite> getListe() throws Exception;

}
