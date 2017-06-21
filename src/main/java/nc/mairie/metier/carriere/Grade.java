package nc.mairie.metier.carriere;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Grade
 */
public class Grade extends BasicMetier {

	public String codeGrade;
	public String codeActif;
	public String codeGrille;
	public String codeClasse;
	public String codeEchelon;
	public String codeGradeGenerique;
	public String acc;
	public String bm;
	public String dureeMin;
	public String dureeMoy;
	public String dureeMax;
	public String codeTava;
	public String codeGradeSuivant;
	public String codeCadre;
	public String iban;
	public String libGrade;
	public String montantForfait;
	public String montantPrime;
	public String grade;

	/**
	 * Retourne un Grade.
	 * 
	 * @return Grade
	 */
	public static Grade chercherGrade(Transaction aTransaction, String cdgrad) throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().chercherGrade(aTransaction, cdgrad);
	}

	/**
	 * Retourne un Grade.
	 * 
	 * @return Grade
	 */
	public static Grade chercherGradeByCodeGradeGenerique(Transaction aTransaction, String codeGradeGenerique)
			throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().chercherGradeByCodeGradeGenerique(aTransaction, codeGradeGenerique);
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false
	 */
	public boolean creerGrade(Transaction aTransaction) throws Exception {
		// Creation du Grade
		setCodeActif("A");
		setCodeGrade(getCodeGrade().toUpperCase());
		setLibGrade(getLibGrade());
		setCodeGrille(getCodeGrille().toUpperCase());
		setCodeClasse(getCodeClasse().toUpperCase());
		setCodeEchelon(getCodeEchelon().toUpperCase());
		setCodeGradeGenerique(getCodeGradeGenerique().toUpperCase());
		setCodeGradeSuivant(getCodeGradeSuivant() == null ? Const.CHAINE_VIDE : getCodeGradeSuivant().toUpperCase());
		setGrade(getGrade());
		return getMyGradeBroker().creerGrade(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Grade> listerGradeActif(Transaction aTransaction) throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerGradeActif(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Grade> listerGradeAvecTypeGrade(Transaction aTransaction, String typeGrade)
			throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerGradeAvecTypeGrade(aTransaction, typeGrade);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Grade> listerTypeGrade(Transaction aTransaction) throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerTypeGrade(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objets metiers Grade lies a un grade generique.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Grade> listerGradeAvecGradeGenerique(Transaction aTransaction, String codeGradeGenerique)
			throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerGradeAvecGradeGenerique(aTransaction, codeGradeGenerique);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return java.util.Vector
	 */
	public static ArrayList<Grade> listerGradeAvecCodeContenant(Transaction aTransaction, String texte)
			throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerGradeAvecCodeContenant(aTransaction, texte);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return java.util.Vector
	 */
	public static ArrayList<Grade> listerGradeAvecLibelleContenant(Transaction aTransaction, String texte)
			throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerGradeAvecLibelleContenant(aTransaction, texte);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false
	 */
	public boolean modifierGrade(Transaction aTransaction) throws Exception {
		// Modification du Grade
		setCodeGrade(getCodeGrade().toUpperCase());
		setLibGrade(getLibGrade());
		setCodeGrille(getCodeGrille().toUpperCase());
		setCodeClasse(getCodeClasse().toUpperCase());
		setCodeEchelon(getCodeEchelon().toUpperCase());
		setCodeGradeGenerique(getCodeGradeGenerique().toUpperCase());
		setCodeGradeSuivant(getCodeGradeSuivant().toUpperCase());
		setGrade(getGrade());
		return getMyGradeBroker().modifierGrade(aTransaction);
	}

	/**
	 * Constructeur Grade.
	 */
	public Grade() {
		super();
	}

	/**
	 * Getter de l'attribut codeGrade.
	 */
	public String getCodeGrade() {
		return codeGrade == null ? Const.CHAINE_VIDE : codeGrade.trim();
	}

	/**
	 * Setter de l'attribut codeGrade.
	 */
	public void setCodeGrade(String newCodeGrade) {
		codeGrade = newCodeGrade;
	}

	/**
	 * Getter de l'attribut libGrade.
	 */
	public String getLibGrade() {
		return libGrade == null ? Const.CHAINE_VIDE : libGrade.trim();
	}

	/**
	 * Setter de l'attribut libGrade.
	 */
	public void setLibGrade(String newLibGrade) {
		libGrade = newLibGrade;
	}

	/**
	 * Getter de l'attribut montantForfait.
	 */
	public String getMontantForfait() {
		return montantForfait;
	}

	/**
	 * Setter de l'attribut montantForfait.
	 */
	public void setMontantForfait(String newMontantForfait) {
		montantForfait = newMontantForfait;
	}

	/**
	 * Getter de l'attribut montantPrime.
	 */
	public String getMontantPrime() {
		return montantPrime;
	}

	/**
	 * Setter de l'attribut montantPrime.
	 */
	public void setMontantPrime(String newMontantPrime) {
		montantPrime = newMontantPrime;
	}

	/**
	 * Getter de l'attribut codeActif.
	 */
	public String getCodeActif() {
		return codeActif;
	}

	/**
	 * Setter de l'attribut codeActif.
	 */
	public void setCodeActif(String newCodeActif) {
		codeActif = newCodeActif;
	}

	/**
	 * Getter de l'attribut iban.
	 */
	public String getIban() {
		return iban == null ? Const.CHAINE_VIDE : iban.trim();
	}

	/**
	 * Setter de l'attribut iban.
	 */
	public void setIban(String newIban) {
		iban = newIban;
	}

	/**
	 * Getter de l'attribut codeGrille.
	 */
	public String getCodeGrille() {
		return codeGrille;
	}

	/**
	 * Setter de l'attribut codeGrille.
	 */
	public void setCodeGrille(String newCodeGrille) {
		codeGrille = newCodeGrille;
	}

	/**
	 * Getter de l'attribut codeClasse.
	 */
	public String getCodeClasse() {
		return codeClasse == null ? Const.CHAINE_VIDE : codeClasse.trim();
	}

	/**
	 * Setter de l'attribut codeClasse.
	 */
	public void setCodeClasse(String newCodeClasse) {
		codeClasse = newCodeClasse;
	}

	/**
	 * Getter de l'attribut codeEchelon.
	 */
	public String getCodeEchelon() {
		return codeEchelon == null ? Const.CHAINE_VIDE : codeEchelon.trim();
	}

	/**
	 * Setter de l'attribut codeEchelon.
	 */
	public void setCodeEchelon(String newCodeEchelon) {
		codeEchelon = newCodeEchelon;
	}

	/**
	 * Getter de l'attribut codeGradeGenerique.
	 */
	public String getCodeGradeGenerique() {
		return codeGradeGenerique == null ? Const.CHAINE_VIDE : codeGradeGenerique.trim();
	}

	/**
	 * Setter de l'attribut codeGradeGenerique.
	 */
	public void setCodeGradeGenerique(String newCodeGradeGenerique) {
		codeGradeGenerique = newCodeGradeGenerique;
	}

	/**
	 * Getter de l'attribut acc.
	 */
	public String getAcc() {
		return acc;
	}

	/**
	 * Setter de l'attribut acc.
	 */
	public void setAcc(String newAcc) {
		acc = newAcc;
	}

	/**
	 * Getter de l'attribut bm.
	 */
	public String getBm() {
		return bm;
	}

	/**
	 * Setter de l'attribut bm.
	 */
	public void setBm(String newBm) {
		bm = newBm;
	}

	/**
	 * Getter de l'attribut dureeMin.
	 */
	public String getDureeMin() {
		return dureeMin;
	}

	/**
	 * Setter de l'attribut dureeMin.
	 */
	public void setDureeMin(String newDureeMin) {
		dureeMin = newDureeMin;
	}

	/**
	 * Getter de l'attribut dureeMoy.
	 */
	public String getDureeMoy() {
		return dureeMoy == null ? Const.CHAINE_VIDE : dureeMoy.trim();
	}

	/**
	 * Setter de l'attribut dureeMoy.
	 */
	public void setDureeMoy(String newDureeMoy) {
		dureeMoy = newDureeMoy;
	}

	/**
	 * Getter de l'attribut dureeMax.
	 */
	public String getDureeMax() {
		return dureeMax;
	}

	/**
	 * Setter de l'attribut dureeMax.
	 */
	public void setDureeMax(String newDureeMax) {
		dureeMax = newDureeMax;
	}

	/**
	 * Getter de l'attribut codeTava.
	 */
	public String getCodeTava() {
		return codeTava == null ? Const.CHAINE_VIDE : codeTava.trim();
	}

	/**
	 * Setter de l'attribut codeTava.
	 */
	public void setCodeTava(String newCodeTava) {
		codeTava = newCodeTava;
	}

	/**
	 * Getter de l'attribut codeGradeSuivant.
	 */
	public String getCodeGradeSuivant() {
		return codeGradeSuivant == null ? Const.CHAINE_VIDE : codeGradeSuivant.trim();
	}

	/**
	 * Setter de l'attribut codeGradeSuivant.
	 */
	public void setCodeGradeSuivant(String newCodeGradeSuivant) {
		codeGradeSuivant = newCodeGradeSuivant;
	}

	/**
	 * Getter de l'attribut codeCadre.
	 */
	public String getCodeCadre() {
		return codeCadre;
	}

	/**
	 * Setter de l'attribut codeCadre.
	 */
	public void setCodeCadre(String newCodeCadre) {
		codeCadre = newCodeCadre;
	}

	/**
	 * Getter de l'attribut grade.
	 */
	public String getGrade() {
		return grade == null ? Const.CHAINE_VIDE : grade.trim();
	}

	/**
	 * Setter de l'attribut grade.
	 */
	public void setGrade(String newGrade) {
		grade = newGrade;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new GradeBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected GradeBroker getMyGradeBroker() {
		return (GradeBroker) getMyBasicBroker();
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

	public static ArrayList<Grade> listerGradeConvCol(Transaction aTransaction) throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerGradeConvCol(aTransaction);
	}

	public static ArrayList<Grade> listerGradeAvecTypeGradeCodAct(Transaction aTransaction, String typeGrade,
			String codAct) throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerGradeAvecTypeGradeCodAct(aTransaction, typeGrade, codAct);
	}

	public static ArrayList<Grade> listerGradeInitialActif(Transaction aTransaction) throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerGradeInitialActif(aTransaction);
	}

	public static Grade chercherGradeByGradeInitial(Transaction aTransaction, String gradeInitial) throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().chercherGradeByGradeInitial(aTransaction, gradeInitial);
	}

	public static ArrayList<Grade> listerCategorieGrade(Transaction aTransaction) throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerCategorieGrade(aTransaction);
	}

	public static ArrayList<Grade> listerGradeAvecGradeGeneriqueEtGrade(Transaction aTransaction, String cdgeng,
			String libGradeGenerique) throws Exception {
		Grade unGrade = new Grade();
		return unGrade.getMyGradeBroker().listerGradeAvecGradeGeneriqueEtGrade(aTransaction, cdgeng, libGradeGenerique);
	}
}
