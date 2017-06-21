package nc.mairie.metier.agent;

import java.lang.reflect.Field;
import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.MairieMessages;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;
import nc.mairie.technique.UserAppli;

/**
 * Objet metier Charge
 */
public class Charge extends BasicMetier {
	public String noMatr;
	public String noRubr;
	public String oldNoRubr;
	public String cdCrea;
	public String noMate;
	public String cdChar;
	public String txSal;
	public String mttreg;
	public String datDeb;
	public String oldDatDeb;
	public String datFin;

	/**
	 * Constructeur Charge.
	 */
	public Charge() {
		super();
	}

	/**
	 * Constructeur Charge.
	 */
	public Charge(String noMatr, String numRubr, String dateDeb) {
		super();
		setNoMatr(noMatr);
		setNoRubr(numRubr);
		setDatDeb(dateDeb);
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
	 * Getter de l'attribut cdCrea.
	 */
	public String getCdCrea() {
		return cdCrea;
	}

	/**
	 * Setter de l'attribut cdCrea.
	 */
	public void setCdCrea(String newCdCrea) {
		cdCrea = newCdCrea;
	}

	/**
	 * Getter de l'attribut noMate.
	 */
	public String getNoMate() {
		return noMate == null ? Const.CHAINE_VIDE : noMate.trim();
	}

	/**
	 * Setter de l'attribut noMate.
	 */
	public void setNoMate(String newNoMate) {
		noMate = newNoMate;
	}

	/**
	 * Getter de l'attribut cdChar.
	 */
	public String getCdChar() {
		return cdChar;
	}

	/**
	 * Setter de l'attribut cdChar.
	 */
	public void setCdChar(String newCdChar) {
		cdChar = newCdChar;
	}

	/**
	 * Getter de l'attribut txSal.
	 */
	public String getTxSal() {
		return txSal;
	}

	/**
	 * Setter de l'attribut txSal.
	 */
	public void setTxSal(String newTxSal) {
		txSal = newTxSal;
		if (Const.CHAINE_VIDE.equals(newTxSal))
			txSal = Const.ZERO;
	}

	/**
	 * Getter de l'attribut mttreg.
	 */
	public String getMttreg() {
		return mttreg;
	}

	/**
	 * Setter de l'attribut mttreg.
	 */
	public void setMttreg(String newMttreg) {
		mttreg = newMttreg;
		if (Const.CHAINE_VIDE.equals(newMttreg))
			mttreg = Const.ZERO;
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
		if (oldDatDeb == null)
			oldDatDeb = datDeb;
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
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new ChargeBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected ChargeBroker getMyChargeBroker() {
		return (ChargeBroker) getMyBasicBroker();
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

	/**
	 * Retourne un ArrayList d'objet metier : Charge.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Charge> listerCharge(Transaction aTransaction) throws Exception {
		Charge unCharge = new Charge();
		return unCharge.getMyChargeBroker().listerCharge(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Charge.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Charge> listerChargeAvecAgent(Transaction aTransaction, Agent agent) throws Exception {
		// Test du parametre Agent
		if (agent == null || agent.getIdAgent() == null) {
			aTransaction.declarerErreur(MairieMessages.getMessage("ERR003", "Agent"));
			return new ArrayList<Charge>();
		}

		Charge unCharge = new Charge();
		return unCharge.getMyChargeBroker().listerChargeAvecAgent(aTransaction, agent.getNomatr());
	}

	/**
	 * Retourne un Charge.
	 * 
	 * @return Charge
	 */
	public static Charge chercherCharge(Transaction aTransaction, String nomatr, String norubr, String datdeb)
			throws Exception {
		Charge unCharge = new Charge();
		return unCharge.getMyChargeBroker().chercherCharge(aTransaction, nomatr, norubr,
				Services.convertitDate(Services.formateDate(datdeb), "dd/MM/yy", "yyyyMMdd"));
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false RG_AG_CG_A03
	 */
	public boolean creerCharge(Transaction aTransaction, UserAppli user) throws Exception {
		// Creation du Charge
		return getMyChargeBroker().creerCharge(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false RG_AG_CG_A03
	 */
	public boolean modifierCharge(Transaction aTransaction, Agent agent, UserAppli user) throws Exception {
		// Modification du Charge
		if (getDatDeb().equals(getOldDatDeb()) && getNoRubr().equals(getOldNoRubr()))
			return getMyChargeBroker().modifierCharge(aTransaction);

		setOldDatDeb(null);
		setOldNoRubr(null);

		return getMyChargeBroker().modifierCharge(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetier qui retourne true ou false RG_AG_CG_A03
	 */
	public boolean supprimerCharge(Transaction aTransaction, UserAppli user) throws Exception {
		// Suppression de l'Charge
		return getMyChargeBroker().supprimerCharge(aTransaction);
	}

	private String getOldDatDeb() {
		return oldDatDeb;
	}

	private void setOldDatDeb(String oldDatDeb) {
		this.oldDatDeb = oldDatDeb;
	}

	private String getOldNoRubr() {
		return oldNoRubr;
	}

	private void setOldNoRubr(String oldNoRubr) {
		this.oldNoRubr = oldNoRubr;
	}

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

			// si attribut this null et l'autre aussi vrai
			if (attributThis == null && attributObj == null)
				return true;

			// Si differents alurs faux
			if (!attributThis.equals(attributObj))
				return false;
		}

		// Tout OK alors true
		return true;
	}
}
