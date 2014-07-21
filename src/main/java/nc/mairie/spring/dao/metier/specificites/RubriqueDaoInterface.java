package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.metier.specificites.Rubrique;

public interface RubriqueDaoInterface {

	public ArrayList<Rubrique> listerRubriqueAvecTypeRubrAvecInactives(String typeRubrique) throws Exception;

	public Rubrique chercherRubrique(Integer norubr) throws Exception;

	public ArrayList<Rubrique> listerRubrique7000() throws Exception;

	public ArrayList<Rubrique> listerRubriqueAvecTypeRubr(String type) throws Exception;
}
