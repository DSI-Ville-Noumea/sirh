package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.TypeRepresentant;

public interface TypeRepresentantDaoInterface {

	public List<TypeRepresentant> listerTypeRepresentant() throws Exception;

	public TypeRepresentant chercherTypeRepresentant(Integer idTypeRepresentant) throws Exception;

}
