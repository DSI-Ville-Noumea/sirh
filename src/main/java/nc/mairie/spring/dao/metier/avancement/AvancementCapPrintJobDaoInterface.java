package nc.mairie.spring.dao.metier.avancement;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.avancement.AvancementCapPrintJob;

public interface AvancementCapPrintJobDaoInterface {

	public void creerAvancementCapPrintJob(Integer idAgent, String login, Integer idCap, String codeCap, Integer idCadreEmploi,
			String libCadreEmploi, boolean isEaes) throws Exception;

	public ArrayList<AvancementCapPrintJob> listerAvancementCapPrintJob() throws Exception;

}
