package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.poste.Affectation;

public interface AffectationDaoInterface {

	public Affectation chercherAffectationAgentPourDate(Integer idAgent, Date dateCreation) throws Exception;

	public Affectation chercherAffectationAgentAvecDateDebut(Integer idAgent, Date dateDebut) throws Exception;

	public ArrayList<Affectation> listerAffectationAvecFPOrderDatDeb(Integer idFichePoste) throws Exception;

	public Affectation chercherAffectationActiveAvecFP(Integer idFichePoste) throws Exception;

	public Affectation chercherAffectationAvecFP(Integer idFPResponsable) throws Exception;

	public Affectation chercherAffectationActiveAvecAgent(Integer idAgent) throws Exception;

	public ArrayList<Affectation> listerAffectationActiveAvecAgent(Integer idAgent) throws Exception;

	public ArrayList<Affectation> listerAffectationAvecFPPrimaireOuSecondaire(Integer idFichePoste) throws Exception;

	public ArrayList<Affectation> listerAffectationAvecFP(Integer idFichePoste) throws Exception;

	public ArrayList<Affectation> listerAffectationActiveAvecFP(Integer idFichePoste) throws Exception;

	public ArrayList<Affectation> listerAffectationAvecAgent(Integer idAgent) throws Exception;

	public void supprimerAffectation(Integer idAffectation) throws Exception;

	public void modifierAffectation(Affectation aff) throws Exception;

	public Integer creerAffectation(Affectation aff) throws Exception;

	public ArrayList<Affectation> listerAffectationActiveOuFuturAvecFP(Integer idFichePoste) throws Exception;

}
