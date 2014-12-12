package nc.mairie.gestionagent.process.pointage;

import java.util.Date;

import nc.mairie.metier.Const;

public class DataContainer {

	private String chk = Const.CHAINE_VIDE;
	private String chkRappelService = Const.CHAINE_VIDE;
	private String motif = Const.CHAINE_VIDE;
	private String comment = Const.CHAINE_VIDE;
	private Date timeD = new Date();
	private Date timeF = new Date();
	private String nbr = Const.CHAINE_VIDE;
	private String mins = Const.CHAINE_VIDE;
	private int idPtg = 0;
	private int idRefEtat = 0;
	private int idTypeAbsence = 0;
	private int idMotifHsup = 0;

	public DataContainer() {
	}

	public String getChkRappelService() {
		return chkRappelService;
	}

	public void setChkRappelService(String chk) {
		this.chkRappelService = chk;
	}

	public String getChk() {
		return chk;
	}

	public void setChk(String chk) {
		this.chk = chk;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getTimeD() {
		return timeD;
	}

	public void setTimeD(Date timeD) {
		this.timeD = timeD;
	}

	public Date getTimeF() {
		return timeF;
	}

	public void setTimeF(Date timeF) {
		this.timeF = timeF;
	}

	public String getNbr() {
		return nbr;
	}

	public void setNbr(String nbr) {
		this.nbr = nbr;
	}

	public int getIdPtg() {
		return idPtg;
	}

	public void setIdPtg(int idPtg) {
		this.idPtg = idPtg;
	}

	public int getIdRefEtat() {
		return idRefEtat;
	}

	public void setIdRefEtat(int idRefEtat) {
		this.idRefEtat = idRefEtat;
	}

	public String getMins() {
		return mins;
	}

	public void setMins(String mins) {
		this.mins = mins;
	}

	public int getIdTypeAbsence() {
		return idTypeAbsence;
	}

	public void setIdTypeAbsence(int idTypeAbsence) {
		this.idTypeAbsence = idTypeAbsence;
	}

	public int getIdMotifHsup() {
		return idMotifHsup;
	}

	public void setIdMotifHsup(int idMotifHsup) {
		this.idMotifHsup = idMotifHsup;
	}

}
