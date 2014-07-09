package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.TypeJourFerie;

public interface TypeJourFerieDaoInterface {

	public List<TypeJourFerie> listerTypeJour() throws Exception;

	public TypeJourFerie chercherTypeJourByLibelle(String libelle);

}
