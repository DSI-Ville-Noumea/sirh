package nc.mairie.metier.agent;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Creancier
 */
public class Creancier extends BasicMetier {
	public String cdCrea;
	public String design;
	public String cdBanq;
	public String cdGuic;
	public String noCpte;
	public String cleRIB;
	public String idetbs;

	/**
	 * Constructeur Creancier.
	 */
	public Creancier() {
		super();
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
	 * Getter de l'attribut design.
	 */
	public String getDesign() {
		return design == null ? Const.CHAINE_VIDE : design.trim();
	}

	/**
	 * Setter de l'attribut design.
	 */
	public void setDesign(String newDesign) {
		design = newDesign;
	}

	/**
	 * Getter de l'attribut cdBanq.
	 */
	public String getCdBanq() {
		return cdBanq;
	}

	/**
	 * Setter de l'attribut cdBanq.
	 */
	public void setCdBanq(String newCdBanq) {
		cdBanq = newCdBanq;
	}

	/**
	 * Getter de l'attribut cdGuic.
	 */
	public String getCdGuic() {
		return cdGuic;
	}

	/**
	 * Setter de l'attribut cdGuic.
	 */
	public void setCdGuic(String newCdGuic) {
		cdGuic = newCdGuic;
	}

	/**
	 * Getter de l'attribut noCpte.
	 */
	public String getNoCpte() {
		return noCpte == null ? Const.CHAINE_VIDE : noCpte.trim();
	}

	/**
	 * Setter de l'attribut noCpte.
	 */
	public void setNoCpte(String newNoCpte) {
		noCpte = newNoCpte;
	}

	/**
	 * Getter de l'attribut cleRIB.
	 */
	public String getCleRIB() {
		return cleRIB;
	}

	/**
	 * Setter de l'attribut cleRIB.
	 */
	public void setCleRIB(String newCleRIB) {
		cleRIB = newCleRIB;
	}

	/**
	 * Getter de l'attribut idetbs.
	 */
	public String getIdetbs() {
		return idetbs;
	}

	/**
	 * Setter de l'attribut idetbs.
	 */
	public void setIdetbs(String newIdetbs) {
		idetbs = newIdetbs;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new CreancierBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected CreancierBroker getMyCreancierBroker() {
		return (CreancierBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : Creancier.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Creancier> listerCreancier(Transaction aTransaction) throws Exception {
		Creancier unCreancier = new Creancier();
		return unCreancier.getMyCreancierBroker().listerCreancier(aTransaction);
	}
}
