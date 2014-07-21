package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.NomHandicap;

public interface NomHandicapDaoInterface {

	public ArrayList<NomHandicap> listerNomHandicap() throws Exception;

	public NomHandicap chercherNomHandicap(Integer idTypeHandicap) throws Exception;

}
