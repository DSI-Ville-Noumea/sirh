package nc.mairie.metier.carriere;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier GradeGenerique
 */
public class GradeGenerique extends BasicMetier {
	public String cdgeng;
	public String libGradeGenerique;
	public String codCadre;
	public String nbPointsAvct;
	public String idCadreEmploi;
	public String cdfili;
	public String texteCapCadreEmploi;
	public String codeInactif;
	public String idDeliberationTerritoriale;
	public String idDeliberationCommunale;

	/**
	 * Retourne un ArrayList d'objet metier : GradeGenerique.
	 * 
	 * @return ArrayList
	 */
	public static GradeGenerique chercherGradeGenerique(Transaction aTransaction, String code) throws Exception {
		GradeGenerique unGradeGenerique = new GradeGenerique();
		return unGradeGenerique.getMyGradeGeneriqueBroker().chercherGradeGenerique(aTransaction, code);
	}

	/**
	 * Retourne un ArrayList d'objet metier : GradeGenerique.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<GradeGenerique> listerGradeGenerique(Transaction aTransaction) throws Exception {
		GradeGenerique unGradeGenerique = new GradeGenerique();
		return unGradeGenerique.getMyGradeGeneriqueBroker().listerGradeGenerique(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : GradeGenerique.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<GradeGenerique> listerGradeGeneriqueOrderLib(Transaction aTransaction) throws Exception {
		GradeGenerique unGradeGenerique = new GradeGenerique();
		return unGradeGenerique.getMyGradeGeneriqueBroker().listerGradeGeneriqueOrderLib(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : GradeGenerique.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<GradeGenerique> listerGradeGeneriqueActif(Transaction aTransaction) throws Exception {
		GradeGenerique unGradeGenerique = new GradeGenerique();
		return unGradeGenerique.getMyGradeGeneriqueBroker().listerGradeGeneriqueActif(aTransaction);
	}

	/**
	 * cree le grade generique
	 * 
	 * @return ArrayList
	 */
	public boolean creerGradeGenerique(Transaction aTransaction) throws Exception {
		setCdgeng(getCdgeng().toUpperCase());
		setLibGradeGenerique(getLibGradeGenerique().toUpperCase());
		setCdfili(getCdfili() != null ? getCdfili().toUpperCase() : null);
		return getMyGradeGeneriqueBroker().creerGradeGenerique(aTransaction);
	}

	/**
	 * modifie le grade generique en base
	 * 
	 * @return ArrayList
	 */
	public boolean modifierGradeGenerique(Transaction aTransaction) throws Exception {
		setCdgeng(getCdgeng().toUpperCase());
		setLibGradeGenerique(getLibGradeGenerique().toUpperCase());
		setCdfili(getCdfili() != null ? getCdfili().toUpperCase() : null);
		return getMyGradeGeneriqueBroker().modifierGradeGenerique(aTransaction);
	}

	/**
	 * Constructeur GradeGenerique.
	 */
	public GradeGenerique() {
		super();
	}

	/**
	 * Getter de l'attribut libGradeGenerique.
	 */
	public String getLibGradeGenerique() {
		return libGradeGenerique == null ? Const.CHAINE_VIDE : libGradeGenerique.trim();
	}

	/**
	 * Setter de l'attribut libGradeGenerique.
	 */
	public void setLibGradeGenerique(String newLibGradeGenerique) {
		libGradeGenerique = newLibGradeGenerique;
	}

	/**
	 * Getter de l'attribut codCadre.
	 */
	public String getCodCadre() {
		return codCadre == null ? Const.CHAINE_VIDE : codCadre.trim();
	}

	/**
	 * Setter de l'attribut codCadre.
	 */
	public void setCodCadre(String newCodCadre) {
		codCadre = newCodCadre;
	}

	/**
	 * Getter de l'attribut codeInactif.
	 */
	public String getCodeInactif() {
		return codeInactif == null ? Const.CHAINE_VIDE : codeInactif.trim();
	}

	/**
	 * Setter de l'attribut codeInactif.
	 */
	public void setCodeInactif(String newCodeInactif) {
		codeInactif = newCodeInactif;
	}

	public String getNbPointsAvct() {
		return nbPointsAvct;
	}

	public void setNbPointsAvct(String nbPointsAvct) {
		this.nbPointsAvct = nbPointsAvct;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new GradeGeneriqueBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected GradeGeneriqueBroker getMyGradeGeneriqueBroker() {
		return (GradeGeneriqueBroker) getMyBasicBroker();
	}

	/**
	 * Renvoie une chaine correspondant a la valeur de cet objet.
	 * 
	 * @return une representation sous forme de chaine du destinataire
	 */
	public String toString() {
		// Inserez ici le code pour finaliser le destinataire
		// Cette implementation transmet le message au super. Vous pouvez
		// remplacer ou completer le message.
		return super.toString();
	}

	public String getIdCadreEmploi() {
		return idCadreEmploi;
	}

	public void setIdCadreEmploi(String idCadreEmploi) {
		this.idCadreEmploi = idCadreEmploi;
	}

	public String getCdfili() {
		return cdfili;
	}

	public void setCdfili(String cdfili) {
		this.cdfili = cdfili;
	}

	public static ArrayList<GradeGenerique> listerGradeGeneriqueAvecCadreEmploi(Transaction aTransaction,
			CadreEmploi cadreEmploiCourant) throws Exception {
		GradeGenerique unGradeGenerique = new GradeGenerique();
		return unGradeGenerique.getMyGradeGeneriqueBroker().listerGradeGeneriqueAvecCadreEmploi(aTransaction,
				cadreEmploiCourant.getIdCadreEmploi().toString());
	}

	public String getTexteCapCadreEmploi() {
		return texteCapCadreEmploi == null ? Const.CHAINE_VIDE : texteCapCadreEmploi.trim();
	}

	public void setTexteCapCadreEmploi(String texteCapCadreEmploi) {
		this.texteCapCadreEmploi = texteCapCadreEmploi;
	}

	public String getCdgeng() {
		return cdgeng == null ? Const.CHAINE_VIDE : cdgeng.trim();
	}

	public void setCdgeng(String cdgeng) {
		this.cdgeng = cdgeng;
	}

	public String getIdDeliberationTerritoriale() {
		return idDeliberationTerritoriale;
	}

	public void setIdDeliberationTerritoriale(String idDeliberationTerritoriale) {
		this.idDeliberationTerritoriale = idDeliberationTerritoriale;
	}

	public String getIdDeliberationCommunale() {
		return idDeliberationCommunale;
	}

	public void setIdDeliberationCommunale(String idDeliberationCommunale) {
		this.idDeliberationCommunale = idDeliberationCommunale;
	}

	public static ArrayList<GradeGenerique> listerGradeGeneriqueAvecDeliberation(Transaction aTransaction,
			Integer idDeliberation) throws Exception {
		GradeGenerique unGradeGenerique = new GradeGenerique();
		return unGradeGenerique.getMyGradeGeneriqueBroker().listerGradeGeneriqueAvecDeliberation(aTransaction,
				idDeliberation);
	}
}
