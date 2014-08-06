package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.hsct.Inaptitude;

public interface InaptitudeDaoInterface {

	public void creerInaptitude(Integer idVisite, Integer idTypeInaptitude, Date dateDebutInaptitude,
			Integer dureeAnnee, Integer dureeMois, Integer dureeJour) throws Exception;

	public void modifierInaptitude(Integer idInaptitude, Integer idVisite, Integer idTypeInaptitude,
			Date dateDebutInaptitude, Integer dureeAnnee, Integer dureeMois, Integer dureeJour) throws Exception;

	public void supprimerInaptitude(Integer idInaptitude) throws Exception;

	public ArrayList<Inaptitude> listerInaptitudeAvecTypeInaptitude(Integer idTypeInaptitude) throws Exception;

	public ArrayList<Inaptitude> listerInaptitudeVisite(Integer idVisite) throws Exception;

}
