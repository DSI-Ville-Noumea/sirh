package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.MotifAvancement;

public interface MotifAvancementDaoInterface {

	public ArrayList<MotifAvancement> listerMotifAvancement() throws Exception;

	public MotifAvancement chercherMotifAvancement(Integer idMotifAvancement) throws Exception;

	public MotifAvancement chercherMotifAvancementByLib(String libMotifAvancement) throws Exception;

	public ArrayList<MotifAvancement> listerMotifAvancementSansRevalo() throws Exception;

}
