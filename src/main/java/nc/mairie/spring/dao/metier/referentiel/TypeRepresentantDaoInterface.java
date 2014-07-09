package nc.mairie.spring.dao.metier.referentiel;

import java.util.ArrayList;

import nc.mairie.metier.referentiel.TypeRepresentant;

public interface TypeRepresentantDaoInterface {

	public ArrayList<TypeRepresentant> listerTypeRepresentant() throws Exception;

	public TypeRepresentant chercherTypeRepresentant(Integer idTypeRepresentant) throws Exception;

}
