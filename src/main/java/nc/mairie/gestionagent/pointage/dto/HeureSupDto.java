package nc.mairie.gestionagent.pointage.dto;

public class HeureSupDto extends PointageDto {

	private boolean recuperee;
	private boolean rappelService;
	private boolean isDPM;

	public HeureSupDto() {

	}

	public boolean isRecuperee() {
		return recuperee;
	}

	public void setRecuperee(boolean recuperee) {
		this.recuperee = recuperee;
	}

	public boolean isRappelService() {
		return rappelService;
	}

	public void setRappelService(boolean rappelService) {
		this.rappelService = rappelService;
	}

	public boolean isDPM() {
		return isDPM;
	}

	public void setDPM(boolean isDPM) {
		this.isDPM = isDPM;
	}
}
