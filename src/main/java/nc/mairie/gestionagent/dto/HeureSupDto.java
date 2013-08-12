package nc.mairie.gestionagent.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HeureSupDto extends PointageDto  {
	
	private Boolean recuperee;

	public HeureSupDto() {
	
	}
	
	
	public Boolean getRecuperee() {
		return recuperee;
	}

	public void setRecuperee(Boolean recuperee) {
		this.recuperee = recuperee;
	}
}
