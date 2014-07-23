package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.agent.Contrat;

public interface ContratDaoInterface {

	public Contrat chercherContratAgentDateComprise(Integer idAgent, Date date) throws Exception;

	public Contrat chercherDernierContrat(Integer idAgent) throws Exception;

	public String getNumContratChrono() throws Exception;

	public void supprimerContrat(Integer idContrat) throws Exception;

	public void creerContrat(Integer idTypeContrat, Integer idMotif, Integer idAgent, Integer idDocument,
			String numContrat, boolean avenant, Integer idContratRef, Date dateDebut, Date dateFinPeriodeEssai,
			Date dateFin, String justification) throws Exception;

	public void modifierContrat(Integer idContrat, Integer idTypeContrat, Integer idMotif, Integer idAgent,
			Integer idDocument, String numContrat, boolean avenant, Integer idContratRef, Date dateDebut,
			Date dateFinPeriodeEssai, Date dateFin, String justification) throws Exception;

	public Contrat chercherContrat(Integer idContrat) throws Exception;

	public ArrayList<Contrat> listerContratAvenantAvecContratReference(Integer idContratRef) throws Exception;

	public ArrayList<Contrat> listerContratAvecAgent(Integer idAgent) throws Exception;

}
