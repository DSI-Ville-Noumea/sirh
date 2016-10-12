package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.Inaptitude;

public interface InaptitudeDaoInterface {

	public ArrayList<Inaptitude> listerInaptitudeAvecTypeInaptitude(Integer idTypeInaptitude) throws Exception;

}
