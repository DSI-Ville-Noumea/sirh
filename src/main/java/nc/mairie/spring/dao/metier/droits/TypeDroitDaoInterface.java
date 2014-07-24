package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;

import nc.mairie.metier.droits.TypeDroit;

public interface TypeDroitDaoInterface {

	public ArrayList<TypeDroit> listerTypeDroit() throws Exception;

}
