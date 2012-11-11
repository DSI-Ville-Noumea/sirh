package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.spring.domain.metier.EAE.EaeParcoursPro;

public interface EaeParcoursProDaoInterface {

	public void creerParcoursPro(Integer idEae, Date dateDebut, Date dateFin, String libParcours) throws Exception;

	public ArrayList<EaeParcoursPro> listerEaeParcoursPro(Integer idEAE) throws Exception;

	public void supprimerEaeParcoursPro(Integer idParcoursPro) throws Exception;
}
