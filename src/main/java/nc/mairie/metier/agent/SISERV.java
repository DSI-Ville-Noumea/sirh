package nc.mairie.metier.agent;

public class SISERV {
	private String servi;
	private String liserv;
	private String sigle;

	public String toString() {
		return "SISERV : [servi : " + getServi() + ", liserv : " + getLiserv() + "]";
	}

	public String getServi() {
		return servi;
	}

	public void setServi(String servi) {
		this.servi = servi;
	}

	public String getLiserv() {
		return liserv;
	}

	public void setLiserv(String liserv) {
		this.liserv = liserv;
	}

	public String getSigle() {
		return sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}
}
