package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.hsct.SPABSEN;

public interface SPABSENDaoInterface {

	public ArrayList<Integer> listerMatriculeAbsencePourSM(String type, Integer moisChoisi, Integer anneeChoisi) throws Exception;

	public ArrayList<SPABSEN> listerAbsencePourAgentTypeEtMoisAnnee(Integer nomatr, String type, Integer moisChoisi, Integer anneeChoisi)
			throws Exception;

	public ArrayList<Integer> listerMatriculeAbsencePourSMDoubleType(String typeMA, String typeLM, Integer moisChoisi, Integer anneeChoisi)
			throws Exception;

	public ArrayList<SPABSEN> listerAbsencePourAgentTypeEtMoisAnneeDoubleType(Integer nomatr, String typeMA, String typeLM, Integer moisChoisi,
			Integer anneeChoisi) throws Exception;
}
