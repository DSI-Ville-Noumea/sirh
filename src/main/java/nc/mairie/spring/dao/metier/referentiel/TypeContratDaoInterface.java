package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.TypeContrat;

public interface TypeContratDaoInterface {

	public List<TypeContrat> listerTypeContrat() throws Exception;

	public TypeContrat chercherTypeContrat(Integer idTypeContrat) throws Exception;

}
