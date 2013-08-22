package nc.mairie.gestionagent.process.pointage;

import java.util.Date;

public class DataContainer {

    private String chk = "";
    private String motif = "";
    private String comment = "";
    private Date timeD = new Date();
    private Date timeF = new Date();
    private String nbr = "";

    public DataContainer() {
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
}
