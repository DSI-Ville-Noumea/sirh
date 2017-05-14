package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.NaturePosteAmenage;

public interface NaturePosteAmenageDaoInterface {

	public NaturePosteAmenage chercherNaturePosteAmenagee(Integer idNaturePosteAmenagee) throws Exception;

	public ArrayList<NaturePosteAmenage> listerNaturePosteAmenagee() throws Exception;
}
