package nc.mairie.metier.paye;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Matricule
 */
public class Matricule extends BasicMetier {
	public String nomatr;
	public String perpre;
	public String perrap;
	public String cdvali;
	public String cdchai;

	/**
	 * Constructeur Matricule.
	 */
	public Matricule() {
		super();
	}

	/**
	 * Getter de l'attribut nomatr.
	 */
	public String getNomatr() {
		return nomatr;
	}

	/**
	 * Setter de l'attribut nomatr.
	 */
	public void setNomatr(String newNomatr) {
		nomatr = newNomatr;
	}

	/**
	 * Getter de l'attribut perpre.
	 */
	public String getPerpre() {
		return perpre;
	}

	/**
	 * Setter de l'attribut perpre.
	 */
	public void setPerpre(String newPerpre) {
		perpre = newPerpre;
	}

	/**
	 * Getter de l'attribut perrap.
	 */
	public String getPerrap() {
		return perrap;
	}

	/**
	 * Setter de l'attribut perrap.
	 */
	public void setPerrap(String newPerrap) {
		perrap = newPerrap;
	}

	/**
	 * Getter de l'attribut cdvali.
	 */
	public String getCdvali() {
		return cdvali;
	}

	/**
	 * Setter de l'attribut cdvali.
	 */
	public void setCdvali(String newCdvali) {
		cdvali = newCdvali;
	}

	/**
	 * Getter de l'attribut cdchai.
	 */
	public String getCdchai() {
		return cdchai;
	}

	/**
	 * Setter de l'attribut cdchai.
	 */
	public void setCdchai(String newCdchai) {
		cdchai = newCdchai;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new MatriculeBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected MatriculeBroker getMyMatriculeBroker() {
		return (MatriculeBroker) getMyBasicBroker();
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
	 * Retourne un Matricule.
	 * 
	 * @return Matricule
	 */
	public static Matricule chercherMatriculeDebut(Transaction aTransaction, String nomatr, String debut)
			throws Exception {
		Matricule unMatricule = new Matricule();
		return unMatricule.getMyMatriculeBroker().chercherMatriculeDebut(aTransaction, nomatr, debut);
	}

	public static Matricule chercherMatricule(Transaction aTransaction, Integer nomatr) throws Exception {
		Matricule unMatricule = new Matricule();
		return unMatricule.getMyMatriculeBroker().chercherMatricule(aTransaction, nomatr);
	}

	/**
	 * Met a jour la ligne matricule en fonction du matricule et de la date de
	 * debut
	 * 
	 * @param aTransaction
	 * @param agent
	 * @param dateDebut
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean updateMatricule(Transaction aTransaction, Agent agent, String dateDebut) throws Exception {
		if (Integer.parseInt(Services.convertitDate(Services.dateDuJour(), "dd/MM/yyyy", "yyyyMM")) < Integer
				.parseInt(Services.convertitDate(Services.formateDate(dateDebut), "dd/MM/yyyy", "yyyyMM"))) {
			return true;
		}
		ArrayList<Carriere> listeCarriere = Carriere.listerCarriereAvecAgent(aTransaction, agent);
		Carriere courante = listeCarriere.size() > 0 ? listeCarriere.get(listeCarriere.size() - 1) : null;

		String codeChaine = courante != null ? "SHC" : Const.CHAINE_VIDE;
		if (courante != null && (courante.getCodeCategorie().equals("7") || courante.getCodeCategorie().equals("8")))
			codeChaine = "SCV";

		Matricule matricule = new Matricule();
		matricule = matricule.getMyMatriculeBroker().chercherMatricule(aTransaction, agent.getNomatr());

		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();
			matricule = new Matricule();
			matricule.setNomatr(agent.getNomatr().toString());
			matricule.setPerrap(Services.convertitDate(Services.formateDate(dateDebut), "dd/MM/yyyy", "yyyyMM"));
			matricule.setCdchai(codeChaine);

			matricule.creerMatricule(aTransaction);
		} else if (Integer.parseInt(matricule.getPerrap()) >= Integer.parseInt(Services.convertitDate(
				Services.formateDate(dateDebut), "dd/MM/yyyy", "yyyyMM"))) {
			matricule.setCdchai(codeChaine);
			matricule.setPerrap(Services.convertitDate(Services.formateDate(dateDebut), "dd/MM/yyyy", "yyyyMM"));
			matricule.modifierMatricule(aTransaction);
		}

		return true;
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false
	 */
	public boolean creerMatricule(Transaction aTransaction) throws Exception {
		setCdvali(Const.CHAINE_VIDE);
		setPerpre(Const.ZERO);

		// Creation du Matricule
		return getMyMatriculeBroker().creerMatricule(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false
	 */
	public boolean modifierMatricule(Transaction aTransaction) throws Exception {
		// Modification du Matricule
		return getMyMatriculeBroker().modifierMatricule(aTransaction);
	}
}
