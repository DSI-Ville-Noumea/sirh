package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.CodeRome;

public interface CodeRomeDaoInterface {

	public void creerCodeRome(String libelleCodeRome, String decsriptionCodeRome) throws Exception;

	public void supprimerCodeRome(Integer idCodeRome) throws Exception;

	public List<CodeRome> listerCodeRome() throws Exception;

}
