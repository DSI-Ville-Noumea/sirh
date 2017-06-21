package nc.mairie.metier.parametrage;

public class CorpsCap {

	public Integer idCap;
	public String cdgeng;

	public CorpsCap() {
		super();
	}

	public String toString() {
		return "Corps-CAP : [cdgeng : " + getCdgeng() + ", idCap : " + getIdCap() + "]";
	}

	public Integer getIdCap() {
		return idCap;
	}

	public void setIdCap(Integer idCap) {
		this.idCap = idCap;
	}

	public String getCdgeng() {
		return cdgeng;
	}

	public void setCdgeng(String cdgeng) {
		this.cdgeng = cdgeng;
	}

	@Override
	public boolean equals(Object object) {
		return idCap.toString().equals(((CorpsCap) object).getIdCap().toString())
				&& cdgeng.equals(((CorpsCap) object).getCdgeng());
	}
}
