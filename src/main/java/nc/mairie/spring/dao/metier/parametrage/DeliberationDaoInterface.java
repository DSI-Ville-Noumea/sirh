package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.parametrage.Deliberation;

public interface DeliberationDaoInterface {

	public ArrayList<Deliberation> listerDeliberation() throws Exception;

	public ArrayList<Deliberation> listerDeliberationCommunale() throws Exception;

	public ArrayList<Deliberation> listerDeliberationTerritoriale() throws Exception;

	public void creerDeliberation(String codeDeliberation, String libelleDeliberation, String typeDeliberation, String texteCAPDeliberation)
			throws Exception;

	public void supprimerDeliberation(Integer idDeliberation) throws Exception;

	public void modifierDeliberation(String codeDeliberation, String libelleDeliberation, String typeDeliberation, String texteCAPDeliberation)
			throws Exception;

}
