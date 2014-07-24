package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.agent.Enfant;

public interface EnfantDaoInterface {

	public Integer creerEnfant(Integer idDocument, String nom, String prenom, String sexe, Date dateNaissance,
			Integer codePaysNaissEt, Integer codeCommuneNaissEt, Integer codeCommuneNaissFr, Date dateDeces,
			String nationalite, String commentaire) throws Exception;

	public void modifierEnfant(Integer idEnfant, Integer idDocument, String nom, String prenom, String sexe,
			Date dateNaissance, Integer codePaysNaissEt, Integer codeCommuneNaissEt, Integer codeCommuneNaissFr,
			Date dateDeces, String nationalite, String commentaire) throws Exception;

	public void supprimerEnfant(Integer idEnfant, LienEnfantAgentDao lienEnfantDao) throws Exception;

	public ArrayList<Enfant> listerEnfantAgent(Integer idAgent, LienEnfantAgentDao lienEnfantDao) throws Exception;

	public ArrayList<Enfant> listerEnfantHomonyme(String nom, String prenom, Date dateNaiss) throws Exception;

	public Enfant chercherEnfant(Integer idEnfant) throws Exception;

}
