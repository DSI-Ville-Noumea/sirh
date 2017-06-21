package nc.mairie.metier.commun;

import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Voie
 */
public class VoieBroker extends BasicBroker {
	/**
	 * Constructeur VoieBroker.
	 */
	public VoieBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * Retourne un Voie.
	 * @return Voie
	 */
	public Voie chercherVoie(Transaction aTransaction, String codVoie) throws Exception {
		return (Voie) executeSelect(aTransaction, "select * from " + getTable() + " where CDVOIE = " + codVoie + " WITH UR");
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("COMPETEN", new BasicRecord("COMPETEN", "CHAR", getMyVoie().getClass().getField("competen"), "STRING"));
		mappage.put("CDVOIE", new BasicRecord("CDVOIE", "NUMERIC", getMyVoie().getClass().getField("codVoie"), "STRING"));
		mappage.put("DEBVOI", new BasicRecord("DEBVOI", "NUMERIC", getMyVoie().getClass().getField("debvoi"), "DATE"));
		mappage.put("LIVOIE", new BasicRecord("LIVOIE", "CHAR", getMyVoie().getClass().getField("livoie"), "STRING"));
		mappage.put("FINPROPR", new BasicRecord("FINPROPR", "NUMERIC", getMyVoie().getClass().getField("finpropr"), "DATE"));
		mappage.put("CODCOM", new BasicRecord("CODCOM", "NUMERIC", getMyVoie().getClass().getField("codCommune"), "STRING"));
		mappage.put("REPLAN", new BasicRecord("REPLAN", "CHAR", getMyVoie().getClass().getField("replan"), "STRING"));
		mappage.put("PREVOI", new BasicRecord("PREVOI", "CHAR", getMyVoie().getClass().getField("prevoi"), "STRING"));
		mappage.put("NOPLAN", new BasicRecord("NOPLAN", "NUMERIC", getMyVoie().getClass().getField("noplan"), "STRING"));
		mappage.put("DTECLASS", new BasicRecord("DTECLASS", "NUMERIC", getMyVoie().getClass().getField("dteclass"), "DATE"));
		mappage.put("ORIVOI", new BasicRecord("ORIVOI", "NUMERIC", getMyVoie().getClass().getField("orivoi"), "STRING"));
		mappage.put("LETALPHA", new BasicRecord("LETALPHA", "CHAR", getMyVoie().getClass().getField("letalpha"), "STRING"));
		mappage.put("DLIDENOM", new BasicRecord("DLIDENOM", "CHAR", getMyVoie().getClass().getField("dlidenom"), "STRING"));
		mappage.put("CDVTIT", new BasicRecord("CDVTIT", "NUMERIC", getMyVoie().getClass().getField("cdvtit"), "STRING"));
		mappage.put("LIVTIT", new BasicRecord("LIVTIT", "CHAR", getMyVoie().getClass().getField("livtit"), "STRING"));
		mappage.put("CDVART", new BasicRecord("CDVART", "NUMERIC", getMyVoie().getClass().getField("cdvart"), "STRING"));
		mappage.put("LIVART", new BasicRecord("LIVART", "CHAR", getMyVoie().getClass().getField("livart"), "STRING"));
		mappage.put("DIMPOR", new BasicRecord("DIMPOR", "NUMERIC", getMyVoie().getClass().getField("dimpor"), "STRING"));
		mappage.put("DIMPOO", new BasicRecord("DIMPOO", "NUMERIC", getMyVoie().getClass().getField("dimpoo"), "STRING"));
		mappage.put("RIVOLI", new BasicRecord("RIVOLI", "NUMERIC", getMyVoie().getClass().getField("rivoli"), "STRING"));
		mappage.put("CDDEBV", new BasicRecord("CDDEBV", "NUMERIC", getMyVoie().getClass().getField("cddebv"), "STRING"));
		mappage.put("DLIDCLAS", new BasicRecord("DLIDCLAS", "CHAR", getMyVoie().getClass().getField("dlidclas"), "STRING"));
		mappage.put("FIMPOR", new BasicRecord("FIMPOR", "NUMERIC", getMyVoie().getClass().getField("fimpor"), "STRING"));
		mappage.put("DPAPOR", new BasicRecord("DPAPOR", "NUMERIC", getMyVoie().getClass().getField("dpapor"), "STRING"));
		mappage.put("CLAVOI", new BasicRecord("CLAVOI", "CHAR", getMyVoie().getClass().getField("clavoi"), "STRING"));
		mappage.put("FIMPOO", new BasicRecord("FIMPOO", "NUMERIC", getMyVoie().getClass().getField("fimpoo"), "STRING"));
		mappage.put("DPAPOO", new BasicRecord("DPAPOO", "NUMERIC", getMyVoie().getClass().getField("dpapoo"), "STRING"));
		mappage.put("CDVCAR", new BasicRecord("CDVCAR", "NUMERIC", getMyVoie().getClass().getField("cdvcar"), "STRING"));
		mappage.put("LIVCAR", new BasicRecord("LIVCAR", "CHAR", getMyVoie().getClass().getField("livcar"), "STRING"));
		mappage.put("FINVOI", new BasicRecord("FINVOI", "NUMERIC", getMyVoie().getClass().getField("finvoi"), "DATE"));
		mappage.put("DELIBFIN", new BasicRecord("DELIBFIN", "CHAR", getMyVoie().getClass().getField("delibfin"), "STRING"));
		mappage.put("PROVOI", new BasicRecord("PROVOI", "CHAR", getMyVoie().getClass().getField("provoi"), "STRING"));
		mappage.put("FPAPOR", new BasicRecord("FPAPOR", "NUMERIC", getMyVoie().getClass().getField("fpapor"), "STRING"));
		mappage.put("DBUPROPR", new BasicRecord("DBUPROPR", "NUMERIC", getMyVoie().getClass().getField("dbupropr"), "DATE"));
		mappage.put("FPAPOO", new BasicRecord("FPAPOO", "NUMERIC", getMyVoie().getClass().getField("fpapoo"), "STRING"));
		mappage.put("DCESSION", new BasicRecord("DCESSION", "CHAR", getMyVoie().getClass().getField("dcession"), "STRING"));
		mappage.put("NOMVOI", new BasicRecord("NOMVOI", "CHAR", getMyVoie().getClass().getField("nomvoi"), "STRING"));
		mappage.put("COMVOI", new BasicRecord("COMVOI", "CHAR", getMyVoie().getClass().getField("comvoi"), "STRING"));
		mappage.put("DLICLASS", new BasicRecord("DLICLASS", "CHAR", getMyVoie().getClass().getField("dliclass"), "STRING"));
		mappage.put("CDFINV", new BasicRecord("CDFINV", "NUMERIC", getMyVoie().getClass().getField("cdfinv"), "STRING"));
		return mappage;
	}

	/**
	 * @return VoieMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Voie();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SIVOIE";
	}

	/**
	 * @return VoieMetier
	 */
	protected Voie getMyVoie() {
		return (Voie) getMyBasicMetier();
	}
}
