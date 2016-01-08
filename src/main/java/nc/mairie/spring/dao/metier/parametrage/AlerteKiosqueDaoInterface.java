package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.AlerteKiosque;

public interface AlerteKiosqueDaoInterface {

	public List<AlerteKiosque> getAlerteKiosque() throws Exception;

	public void creerAlerteKiosque(AlerteKiosque alerte) throws Exception;

	public void modifierAlerteKiosque(Integer idAlerte, AlerteKiosque alerte) throws Exception;

	public void supprimerAlerteKiosque(Integer idAlerte) throws Exception;

}
