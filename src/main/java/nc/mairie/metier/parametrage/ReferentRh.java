package nc.mairie.metier.parametrage;

public class ReferentRh {

	public Integer idReferentRh;
	public String servi;
	public Integer idAgentReferent;
	public Integer numeroTelephone;
	public Integer idServiceAds;

	public ReferentRh() {
		super();
	}

	public String toString() {
		return "ReferentRh : [idReferentRh : " + getIdReferentRh() + ", idAgentReferent : " + getIdAgentReferent()
				+ ", numeroTelephone : " + getNumeroTelephone() + "]";
	}

	@Override
	public boolean equals(Object object) {
		return idReferentRh.toString().equals(((ReferentRh) object).getIdReferentRh().toString());
	}

	public Integer getIdAgentReferent() {
		return idAgentReferent;
	}

	public void setIdAgentReferent(Integer idAgentReferent) {
		this.idAgentReferent = idAgentReferent;
	}

	public Integer getNumeroTelephone() {
		return numeroTelephone;
	}

	public void setNumeroTelephone(Integer numeroTelephone) {
		this.numeroTelephone = numeroTelephone;
	}

	public Integer getIdReferentRh() {
		return idReferentRh;
	}

	public void setIdReferentRh(Integer idReferentRh) {
		this.idReferentRh = idReferentRh;
	}

	public Integer getIdServiceAds() {
		return idServiceAds;
	}

	public void setIdServiceAds(Integer idServiceADS) {
		this.idServiceAds = idServiceADS;
	}

	public String getServi() {
		return servi;
	}

	public void setServi(String servi) {
		this.servi = servi;
	}
}
