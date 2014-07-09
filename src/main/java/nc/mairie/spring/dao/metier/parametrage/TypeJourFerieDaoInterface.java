package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.TypeJourFerie;

public interface TypeJourFerieDaoInterface {

	public ArrayList<TypeJourFerie> listerTypeJour();
	
	public TypeJourFerie chercherTypeJourByLibelle(String libelle);

}
