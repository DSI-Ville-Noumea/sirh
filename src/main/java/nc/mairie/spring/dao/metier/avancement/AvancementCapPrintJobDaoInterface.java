package nc.mairie.spring.dao.metier.avancement;

public interface AvancementCapPrintJobDaoInterface {

	public void creerAvancementCapPrintJob(Integer idAgent, String login, Integer idCap, String codeCap, Integer idCadreEmploi,
			String libCadreEmploi, boolean isEaes) throws Exception;

}
