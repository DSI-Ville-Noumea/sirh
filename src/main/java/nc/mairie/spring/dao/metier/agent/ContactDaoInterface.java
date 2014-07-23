package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.List;

import nc.mairie.metier.agent.Contact;

public interface ContactDaoInterface {

	public List<Contact> listerContact() throws Exception;

	public ArrayList<Contact> listerContactAgent(Integer idAgent) throws Exception;

	public ArrayList<Contact> listerContactAgentAvecTypeContact(Integer idAgent, Integer idTypeContact)
			throws Exception;

	public void creerContact(Integer idAgent, Integer idTypeContact, String description, boolean diffusable,
			boolean prioritaire) throws Exception;

	public void modifierContact(Integer idContact, Integer idAgent, Integer idTypeContact, String description,
			boolean diffusable, boolean prioritaire) throws Exception;

	public void supprimerContact(Integer idContact) throws Exception;

}
