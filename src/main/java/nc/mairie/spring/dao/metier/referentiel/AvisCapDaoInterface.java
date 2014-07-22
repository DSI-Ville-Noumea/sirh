package nc.mairie.spring.dao.metier.referentiel;

import java.util.ArrayList;
import java.util.List;

import nc.mairie.metier.referentiel.AvisCap;

public interface AvisCapDaoInterface {

	public ArrayList<AvisCap> listerAvisCapFavDefav() throws Exception;

	public ArrayList<AvisCap> listerAvisCapMinMoyMax() throws Exception;

	public AvisCap chercherAvisCapByLibCourt(String libAvisCapCourt) throws Exception;

	public AvisCap chercherAvisCap(Integer idAvisCap) throws Exception;

	public List<AvisCap> listerAvisCap() throws Exception;

}
