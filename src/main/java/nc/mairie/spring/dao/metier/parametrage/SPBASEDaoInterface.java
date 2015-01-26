package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.SPBASE;

public interface SPBASEDaoInterface {

	public ArrayList<SPBASE> listerSPBASE() throws Exception;

	public void creerSPBASE(String cdBase, String liBase, Double nbhLu, Double nbhMa, Double nbhMe, Double nbhJe, Double nbhVe, Double nbhSa,
			Double nbhDi, Double nbasCH, Double nbasHH);

	public void modifierSPBASE(String cdBase, String liBase, Double nbhLu, Double nbhMa, Double nbhMe, Double nbhJe, Double nbhVe, Double nbhSa,
			Double nbhDi, Double nbasCH, Double nbasHH);

	SPBASE chercherBaseHoraire(String codeBase);
}
