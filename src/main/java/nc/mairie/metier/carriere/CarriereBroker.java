package nc.mairie.metier.carriere;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Carriere
 */
public class CarriereBroker extends BasicBroker {
	/**
	 * Constructeur CarriereBroker.
	 */
	public CarriereBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return CarriereMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Carriere();
	}

	/**
	 * @return CarriereMetier
	 */
	protected Carriere getMyCarriere() {
		return (Carriere) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPCARR";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("NOMATR", new BasicRecord("NOMATR", "NUMERIC", getMyCarriere().getClass().getField("noMatricule"),
				"String"));
		mappage.put("CDCATE", new BasicRecord("CDCATE", "NUMERIC",
				getMyCarriere().getClass().getField("codeCategorie"), "String"));
		mappage.put("CDGRAD", new BasicRecord("CDGRAD", "CHAR", getMyCarriere().getClass().getField("codeGrade"),
				"String"));
		mappage.put("REFARR",
				new BasicRecord("REFARR", "NUMERIC", getMyCarriere().getClass().getField("referenceArrete"), "String"));
		mappage.put("DATDEB", new BasicRecord("DATDEB", "NUMERIC", getMyCarriere().getClass().getField("dateDebut"),
				"DATE"));
		mappage.put("DATFIN", new BasicRecord("DATFIN", "NUMERIC", getMyCarriere().getClass().getField("dateFin"),
				"DATE"));
		mappage.put("MODREG", new BasicRecord("MODREG", "CHAR", getMyCarriere().getClass().getField("modeReglement"),
				"String"));
		mappage.put("CDBHOR",
				new BasicRecord("CDBHOR", "NUMERIC", getMyCarriere().getClass().getField("codeBaseHoraire"), "String"));
		mappage.put("IBA", new BasicRecord("IBA", "NUMERIC", getMyCarriere().getClass().getField("iba"), "String"));
		mappage.put("MTFORF", new BasicRecord("MTFORF", "DECIMAL",
				getMyCarriere().getClass().getField("monantForfait"), "String"));
		mappage.put("CDEMPL", new BasicRecord("CDEMPL", "NUMERIC", getMyCarriere().getClass().getField("codeEmploi"),
				"String"));
		mappage.put("CDBASE", new BasicRecord("CDBASE", "CHAR", getMyCarriere().getClass().getField("codeBase"),
				"String"));
		mappage.put("CDTYEM", new BasicRecord("CDTYEM", "NUMERIC", getMyCarriere().getClass()
				.getField("codeTypeEmploi"), "String"));
		mappage.put(
				"CDBHOR2",
				new BasicRecord("CDBHOR2", "DECIMAL", getMyCarriere().getClass().getField("codeBaseHoraire2"), "String"));
		mappage.put("IBAN", new BasicRecord("IBAN", "CHAR", getMyCarriere().getClass().getField("iban"), "String"));
		mappage.put("MOTIFAVCT", new BasicRecord("MOTIFAVCT", "NUMERIC",
				getMyCarriere().getClass().getField("idMotif"), "String"));
		mappage.put("ACCJOUR", new BasicRecord("ACCJOUR", "DECIMAL", getMyCarriere().getClass().getField("ACCJour"),
				"String"));
		mappage.put("ACCMOIS", new BasicRecord("ACCMOIS", "DECIMAL", getMyCarriere().getClass().getField("ACCMois"),
				"String"));
		mappage.put("ACCANNEE", new BasicRecord("ACCANNEE", "DECIMAL", getMyCarriere().getClass().getField("ACCAnnee"),
				"String"));
		mappage.put("BMJOUR", new BasicRecord("BMJOUR", "DECIMAL", getMyCarriere().getClass().getField("BMJour"),
				"String"));
		mappage.put("BMMOIS", new BasicRecord("BMMOIS", "DECIMAL", getMyCarriere().getClass().getField("BMMois"),
				"String"));
		mappage.put("BMANNEE", new BasicRecord("BMANNEE", "DECIMAL", getMyCarriere().getClass().getField("BMAnnee"),
				"String"));
		mappage.put("DATARR", new BasicRecord("DATARR", "NUMERIC", getMyCarriere().getClass().getField("dateArrete"),
				"DATE"));
		mappage.put("CDDCDICA", new BasicRecord("CDDCDICA", "CHAR", getMyCarriere().getClass().getField("typeContrat"),
				"String"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne true ou false
	 */
	public boolean creerCarriere(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne true ou false
	 */
	public boolean modifierCarriere(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetierBroker qui retourne true ou false
	 */
	public boolean supprimerCarriere(Transaction aTransaction) throws Exception {
		return supprimer(aTransaction);
	}

	/**
	 * Retourne la Carriere en cours.
	 * 
	 * @param nomatr
	 * @return Carriere
	 */
	public Carriere chercherCarriereEnCoursAvecAgent(Transaction aTransaction, Integer nomatr) throws Exception {
		String req = "select c.* from " + getTable() + " c where ("
				+ Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE)
				+ " between c.DATDEB and c.DATFIN or c.DATDEB<="
				+ Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE)
				+ " and c.DATFIN=0) and c.nomatr = " + nomatr + " WITH UR ";
		return (Carriere) executeSelect(aTransaction, req);
	}

	/**
	 * Retourne la Carriere en courspour la date donnée
	 * 
	 * @param nomatr
	 * @param date
	 * @return Carriere
	 */
	public Carriere chercherCarriereEnCoursAvecAgentEtDate(Transaction aTransaction, Integer date, Integer nomatr)
			throws Exception {
		String req = "select c.* from " + getTable() + " c where (" + date
				+ " between c.DATDEB and c.DATFIN or c.DATDEB<=" + date + " and c.DATFIN=0) and c.nomatr = " + nomatr
				+ " WITH UR ";
		return (Carriere) executeSelect(aTransaction, req);
	}

	/**
	 * Retourne la derniere Carriere.
	 * 
	 * @param nomatr
	 * @return Carriere
	 */
	public Carriere chercherDerniereCarriereAvecAgent(Transaction aTransaction, Integer nomatr) throws Exception {
		String req = "select c.* from " + getTable() + " c where c.DATFIN=0 and c.nomatr = " + nomatr + " WITH UR ";
		return (Carriere) executeSelect(aTransaction, req);
	}

	/**
	 * Retourne la Carriere en cours.
	 * 
	 * @param nomatr
	 * @param annee
	 * @return Carriere
	 */
	public Carriere chercherDerniereCarriereAvecAgentEtAnnee(Transaction aTransaction, Integer nomatr, String annee)
			throws Exception {
		String req = "select c.* from spcarr c where c.datdeb="
				+ "(select max(carr.datdeb) from spcarr carr where c.nomatr=carr.nomatr and substr(carr.datdeb,0,5) <= '"
				+ annee + "' and (carr.datfin=0 or substr(carr.datfin,0,5) >= '" + annee + "')) " + "and  c.nomatr = "
				+ nomatr + " WITH UR ";
		return (Carriere) executeSelect(aTransaction, req);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Carriere> listerCarriereParCategorieSPCARR(Transaction aTransaction, String idCate)
			throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WHERE CDCATE = " + idCate
				+ " WITH UR");
	}

	public Carriere chercherCarriereAgentPrec(Transaction aTransaction, String nomatr, String datedeb) throws Exception {
		return (Carriere) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = " + nomatr
				+ " and datdeb in (select max(datdeb) from " + getTable() + " where datdeb<" + datedeb + " and nomatr="
				+ nomatr + ")" + " WITH UR ");
	}

	public Carriere chercherCarriereAgentSuiv(Transaction aTransaction, String nomatr, String datedeb) throws Exception {
		return (Carriere) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = " + nomatr
				+ " and datdeb in (select min(datdeb) from " + getTable() + " where datdeb>" + datedeb + " and nomatr="
				+ nomatr + ")" + " WITH UR ");
	}

	/**
	 * @param aTransaction
	 * @param noMatr
	 * @param codeGrade
	 * @return ArrayList
	 * @throws Exception
	 */
	public ArrayList<Carriere> listerCarriereAvecGradeEtStatut(Transaction aTransaction, String noMatr,
			String codeGrade, String codeCategorie) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WHERE CDGRAD = '" + codeGrade
				+ "' and NOMATR=" + noMatr + " and CDCATE=" + codeCategorie + " order by DATDEB WITH UR");
	}

	public Carriere chercherCarriereFonctionnaireAncienne(Transaction aTransaction, String noMatricule)
			throws Exception {
		String sql = "select * from " + getTable() + " c where c.nomatr = " + noMatricule
				+ " and c.cdcate in (1,2,6,16,17,18,19,20) and " + "c.datdeb = (select min(datdeb) from " + getTable()
				+ " carr where carr.nomatr=" + noMatricule + " and carr.cdcate in (1,2,6,16,17,18,19,20)) WITH UR";
		return (Carriere) executeSelect(aTransaction, sql);
	}

	public ArrayList<Carriere> listerCarriereActive(Transaction aTransaction, String annee, String statut)
			throws Exception {
		String reqWhere = Const.CHAINE_VIDE;
		if (statut.equals("Fonctionnaire")) {
			reqWhere = " and (carr.cdcate=1 or carr.cdcate=2 or carr.cdcate=18 or carr.cdcate=20) ";
		} else if (statut.equals("Contractuel")) {
			reqWhere = " and carr.cdcate=4 ";
		} else if (statut.equals("Convention collective")) {
			reqWhere = " and carr.cdcate=7 ";
		} else if (statut.equals("Detache")) {
			reqWhere = " and (carr.cdcate=6 or carr.cdcate=16 or carr.cdcate=17 or carr.cdcate=19) ";
		}
		String sql = "select * from "
				+ getTable()
				+ " carr inner join SPADMN pa on carr.nomatr = pa.nomatr where (pa.datfin = 0 or substr(pa.datfin,0,5) >= '"
				+ annee
				+ "') and LENGTH(TRIM(TRANSLATE(pa.cdpadm,' ', ' +-.0123456789')))=0 "
				+ reqWhere
				+ " and carr.datdeb = (select max(c.datdeb) from spcarr c where c.nomatr=carr.nomatr and substr(c.datdeb,0,5) <= '"
				+ annee + "' and (c.datfin=0 or substr(c.datfin,0,5) >= '" + annee + "')) WITH UR";
		return executeSelectListe(aTransaction, sql);
	}

	public Carriere chercherCarriereSuperieurOuEgaleDate(Transaction aTransaction, Integer noMatricule,
			String dateAvctFinale) throws Exception {
		String req = "select c.* from " + getTable() + " c where c.DATDEB>=" + dateAvctFinale + " and c.nomatr = "
				+ noMatricule + " WITH UR ";
		return (Carriere) executeSelect(aTransaction, req);
	}

	public ArrayList<Carriere> listerCarriereAgentByType(Transaction aTransaction, String noMatricule, String type)
			throws Exception {
		String reqWhere = Const.CHAINE_VIDE;
		if (type.equals("F")) {
			reqWhere = " and (carr.cdcate=1 or carr.cdcate=2 or carr.cdcate=6 or carr.cdcate=16 or carr.cdcate=17 or carr.cdcate=18 or carr.cdcate=19 or carr.cdcate=20) ";
		} else if (type.equals("C")) {
			reqWhere = " and carr.cdcate=4 ";
		} else if (type.equals("CC")) {
			reqWhere = " and carr.cdcate=7 ";
		}
		String req = "select carr.* from " + getTable() + " carr where carr.nomatr=  " + noMatricule + reqWhere
				+ " order by DATDEB desc WITH UR";
		return executeSelectListe(aTransaction, req);

	}

	/**
	 * Retourne un ArrayList d'objet metier : Carriere.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Carriere> listerCarriereAvecMotif(Transaction aTransaction, String idMotif) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where MOTIFAVCT = " + idMotif
				+ " WITH UR");
	}

	public ArrayList<Carriere> listerCarriereAvecAgent(Transaction aTransaction, Integer noMatricule) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " MP  WHERE MP.NOMATR = " + noMatricule
				+ " ORDER BY  MP.DATDEB WITH UR ");
	}
}
