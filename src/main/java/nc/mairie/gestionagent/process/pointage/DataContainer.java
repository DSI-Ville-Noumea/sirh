package nc.mairie.gestionagent.process.pointage;

import java.util.Date;

public class DataContainer {

	private String chk = "";
	private String chkRappelService = "";
	private String motif = "";
	private String comment = "";
	private Date timeD = new Date();
	private Date timeF = new Date();
	private String nbr = "";
	private String mins = "";
	private int idPtg = 0;
	private int idRefEtat = 0;
	private int idTypeAbsence = 0;

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

}
