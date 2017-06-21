package nc.mairie.metier.agent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;
import nc.mairie.technique.UserAppli;

/**
 * Objet metier Prime
 */
public class Prime extends BasicMetier {
	public String noMatr;
	public String noRubr;
	public String oldNoRubr;
	public String refArr;
	public String datDeb;
	public String oldDateDeb;
	public String datFin;
	public String mtPri;
	public String dateArrete;

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

	/**
	 * Retourne un ArrayList d'objet metier : Prime.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Prime> listerPrime(Transaction aTransaction) throws Exception {
		Prime unPrime = new Prime();
		return unPrime.getMyPrimeBroker().listerPrime(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Prime.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Prime> listerPrimeAvecAgent(Transaction aTransaction, Agent agent) throws Exception {

		Prime unePrime = new Prime();
		return unePrime.getMyPrimeBroker().listerPrimeAvecAgent(aTransaction, agent.getNomatr().toString());
	}

	public static Prime chercherDernierePrimeOuverteAvecRubrique(Transaction aTransaction, Integer noMatricule, String noRubr) throws Exception {
		Prime unPrime = new Prime();
		return unPrime.getMyPrimeBroker().chercherDernierePrimeOuverteAvecRubrique(aTransaction, noMatricule.toString(), noRubr);
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false RG_AG_PR_A04
	 */
	public boolean creerPrime(Transaction aTransaction, UserAppli user) throws Exception {
		// Creation du Prime
		return getMyPrimeBroker().creerPrime(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false RG_AG_PR_A04
	 */
	public void modifierPrime(Transaction aTransaction, Agent agent, UserAppli user) throws Exception {
		if (getDatDeb().equals(getOldDateDeb()) && getNoRubr().equals(getOldNoRubr())) {
			getMyPrimeBroker().modifierPrime(aTransaction);
			return;
		}

		setOldDateDeb(null);
		setOldNoRubr(null);

		if (!getMyPrimeBroker().modifierPrime(aTransaction))
			return;
	}

	/**
	 * Methode supprimerObjetMetier qui retourne true ou false RG_AG_PR_A04
	 */
	public boolean supprimerPrime(Transaction aTransaction, UserAppli user) throws Exception {
		// Suppression de l'Prime
		return getMyPrimeBroker().supprimerPrime(aTransaction);
	}

	/**
	 * Constructeur Prime.
	 */
	public Prime(String noMatr, String numRubr, String dateDeb) {
		super();
		setNoMatr(noMatr);
		setNoRubr(numRubr);
		setDatDeb(dateDeb);
	}

	/**
	 * Constructeur Prime.
	 */
	public Prime() {
		super();
	}

	/**
	 * Getter de l'attribut noMatr.
	 */
	public String getNoMatr() {
		return noMatr;
	}

	/**
	 * Setter de l'attribut noMatr.
	 */
	public void setNoMatr(String newNoMatr) {
		noMatr = newNoMatr;
	}

	/**
	 * Getter de l'attribut noRubr.
	 */
	public String getNoRubr() {
		return noRubr;
	}

	/**
	 * Setter de l'attribut noRubr.
	 */
	public void setNoRubr(String newNoRubr) {
		if (oldNoRubr == null)
			oldNoRubr = noRubr;
		noRubr = newNoRubr;
	}

	/**
	 * Getter de l'attribut refArr.
	 */
	public String getRefArr() {
		return refArr == null ? Const.CHAINE_VIDE : refArr.trim();
	}

	/**
	 * Setter de l'attribut refArr.
	 */
	public void setRefArr(String newRefArr) {
		refArr = newRefArr;
	}

	/**
	 * Getter de l'attribut datDeb.
	 */
	public String getDatDeb() {
		return datDeb;
	}

	/**
	 * Setter de l'attribut datDeb.
	 */
	public void setDatDeb(String newDatDeb) {
		if (oldDateDeb == null)
			oldDateDeb = datDeb;
		datDeb = newDatDeb;
	}

	/**
	 * Getter de l'attribut datFin.
	 */
	public String getDatFin() {
		return datFin;
	}

	/**
	 * Setter de l'attribut datFin.
	 */
	public void setDatFin(String newDatFin) {
		datFin = newDatFin;
	}

	/**
	 * Getter de l'attribut mtPri.
	 */
	public String getMtPri() {
		return mtPri == null ? Const.CHAINE_VIDE : mtPri.trim();
	}

	/**
	 * Setter de l'attribut mtPri.
	 */
	public void setMtPri(String newMtPri) {
		mtPri = newMtPri;
	}

	/**
	 * Getter de l'attribut dateArrete.
	 */
	public String getDateArrete() {
		return dateArrete;
	}

	/**
	 * Setter de l'attribut dateArrete.
	 */
	public void setDateArrete(String newDateArrete) {
		dateArrete = newDateArrete;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new PrimeBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected PrimeBroker getMyPrimeBroker() {
		return (PrimeBroker) getMyBasicBroker();
	}

	private String getOldDateDeb() {
		return oldDateDeb;
	}

	private void setOldDateDeb(String oldDateDeb) {
		this.oldDateDeb = oldDateDeb;
	}

	private String getOldNoRubr() {
		return oldNoRubr;
	}

	private void setOldNoRubr(String oldNoRubr) {
		this.oldNoRubr = oldNoRubr;
	}

	/**
	 * Verifie si deux objets sont egaux. Retourne un booleen qui indique si cet
	 * objet equivaut a celui indique. Cette methode est utilisee lorsqu'un
	 * objet est stocke dans une table de hachage.
	 * 
	 * @param obj
	 *            l'objet a comparer avec
	 * @return boolean true si ces objets sont egaux ; false dans le cas
	 *         contraire.
	 * @see Hashtable
	 */
	public boolean equals(BasicMetier obj) throws Exception {

		// Si pas la meme classe alors faux
		if (!obj.getClass().equals(getClass()))
			return false;

		Field[] fieldsThis = getClass().getFields();
		// parcours des attributs
		for (int i = 0; i < fieldsThis.length; i++) {
			String name = fieldsThis[i].getName();
			Object attributThis = fieldsThis[i].get(this);
			Object attributObj = obj.getClass().getField(name).get(obj);

			// si attribut this null et pas l'autre alors faux
			if (attributThis == null && attributObj != null)
				return false;

			// si attribut this null et l'autre aussi alors continue
			if (attributThis == null && attributObj == null)
				continue;

			// Si differents alurs faux
			if (!attributThis.equals(attributObj))
				return false;
		}

		// Tout OK alors true
		return true;
	}

	public static Prime chercherPrime1200ByRubrAndDate(Transaction aTransaction, Integer noMatricule, String date) throws Exception {
		Prime unPrime = new Prime();
		return unPrime.getMyPrimeBroker().chercherPrime1200ByRubrAndDate(aTransaction, noMatricule.toString(), date);
	}

	public static List<Prime> listerPrime1200ByAgent(Transaction aTransaction, Integer noMatricule) throws Exception {
		Prime unePrime = new Prime();
		return unePrime.getMyPrimeBroker().listerPrime1200ByAgent(aTransaction, noMatricule.toString());
	}
}
