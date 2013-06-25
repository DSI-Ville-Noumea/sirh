package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.spring.domain.metier.parametrage.JourFerie;

public interface JourFerieDaoInterface {

	public ArrayList<String> listerAnnee();

	public ArrayList<JourFerie> listerJourByAnnee(String annee);

	public ArrayList<JourFerie> listerJourByAnneeWithType(String annee, Integer idTypeJour);

	public void supprimerJourFerie(Integer idJourFerie);

	public void modifierJourFerie(Integer idJourFerie, Integer idTypeJour, Date dateJour, String description);

	public void creerJourFerie(Integer idTypeJour, Date dateJour, String description);

}
