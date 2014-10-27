package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.BaseHorairePointage;

public interface BaseHorairePointageDaoInterface {

	public List<BaseHorairePointage> listerBaseHorairePointage() throws Exception;

	public void creerBaseHorairePointage(BaseHorairePointage base) throws Exception;

	public void modifierBaseHorairePointage(BaseHorairePointage base) throws Exception;

}
