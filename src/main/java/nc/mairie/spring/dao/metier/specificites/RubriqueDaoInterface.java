package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.metier.specificites.Rubrique;

public interface RubriqueDaoInterface {

	public ArrayList<Rubrique> listerRubriqueAvecTypeRubrAvecInactives(String typeRubrique);

	public Rubrique chercherRubrique(Integer norubr);

	public ArrayList<Rubrique> listerRubrique7000();

	public ArrayList<Rubrique> listerRubriqueAvecTypeRubr(String type);
}
