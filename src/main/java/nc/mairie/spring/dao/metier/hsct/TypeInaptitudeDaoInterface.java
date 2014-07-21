package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.TypeInaptitude;

public interface TypeInaptitudeDaoInterface {

	public void creerTypeInaptitude(String description) throws Exception;

	public void supprimerTypeInaptitude(Integer idTypeInaptitude) throws Exception;

	public ArrayList<TypeInaptitude> listerTypeInaptitude() throws Exception;

}
