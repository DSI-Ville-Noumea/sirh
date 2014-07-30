package nc.mairie.spring.dao.metier.poste;

import java.util.List;

import nc.mairie.metier.poste.NFA;

public interface NFADaoInterface {

	public void supprimerNFA(String codeService, String nfa) throws Exception;

	public void creerNFA(String codeService, String nfa) throws Exception;

	public NFA chercherNFAByCodeService(String codeService) throws Exception;

	public List<NFA> listerNFA() throws Exception;

}
