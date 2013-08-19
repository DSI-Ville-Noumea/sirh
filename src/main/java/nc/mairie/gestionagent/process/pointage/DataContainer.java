package nc.mairie.gestionagent.process.pointage;

public class DataContainer {

    private String chk = "";
    private String motif = "";
    private String comment = "";
    private String timeD = "";
    private String timeF = "";
    private String nbr = "0";

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

    public String getTimeD() {
        return timeD;
    }

    public void setTimeD(String timeD) {
        this.timeD = timeD;
    }

    public String getTimeF() {
        return timeF;
    }

    public void setTimeF(String timeF) {
        this.timeF = timeF;
    }

    public String getNbr() {
        return nbr;
    }

    public void setNbr(String nbr) {
        this.nbr = nbr;
    }
}
