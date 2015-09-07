package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;

import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.technique.Transaction;
import nc.mairie.technique.UserAppli;

public interface FichePosteDaoInterface {

	public boolean estAffectee(Integer idFichePoste, ArrayList<Affectation> listeAff) throws Exception;

	public FichePoste chercherDerniereFichePoste(Integer annee) throws Exception;

	public FichePoste chercherFichePosteAvecNumeroFP(String numeroFP) throws Exception;

	public FichePoste chercherFichePoste(Integer idFichePoste) throws Exception;

	public ArrayList<FichePoste> listerFichePosteAvecCriteresAvances(List<Integer> listIdServADS, Integer idStatutFP,
			Integer idTitre, String numero, Integer idAgent, boolean avecObservation) throws Exception;

	public ArrayList<FichePoste> listerFichePosteAvecService(Integer idServiceADS) throws Exception;

	public ArrayList<FichePoste> listerFichePosteAvecAgent(ArrayList<Affectation> liens) throws Exception;

	public ArrayList<FichePoste> listerFichePosteAvecTitrePoste(Integer idTitrePoste) throws Exception;

	public ArrayList<FichePoste> listerFichePosteAvecEntiteGeo(Integer idEntiteGeo) throws Exception;

	public ArrayList<FichePoste> listerFichePosteValideesOuGeleesNonAffecteesAvecNumPartiel(String numPartiel)
			throws Exception;

	public ArrayList<FichePoste> listerFichePosteAvecNumPartiel(String numPartiel) throws Exception;

	public ArrayList<FichePoste> listerFichePosteValideesouGeleeNonAffectees() throws Exception;

	public List<FichePoste> listerFichePoste() throws Exception;

	public String createFichePosteNumber(Integer annee) throws Exception;

	public void modifierFichePoste(FichePoste fp, HistoFichePosteDao histoDao, UserAppli user,
			Transaction aTransaction, AffectationDao affDao) throws Exception;

	public Integer creerFichePoste(FichePoste fp, UserAppli user, HistoFichePosteDao histoDao, Transaction aTransaction)
			throws Exception;

	public void supprimerFichePoste(FichePoste fp, Transaction aTransaction) throws Exception;

}
