package nc.mairie.gestionagent.dto;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VentilDto {

    protected int idVentil;
    protected Date date;
    protected int idAgent;
    protected int etat;

    
    public VentilDto() {
    }

    public int getId_agent() {
        return idAgent;
    }

    public void setIdAgent(int idAgent) {
        this.idAgent = idAgent;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }
}
