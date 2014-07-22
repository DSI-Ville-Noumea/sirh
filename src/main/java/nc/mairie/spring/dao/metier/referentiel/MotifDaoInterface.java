package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.Motif;

public interface MotifDaoInterface {

	public List<Motif> listerMotif() throws Exception;

}
