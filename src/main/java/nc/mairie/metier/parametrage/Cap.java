package nc.mairie.metier.parametrage;

public class Cap {

	public Integer idCap;
	public String codeCap;
	public String refCap;
	public String description;
	public String typeCap;
	public Integer capVdn;

	public Cap() {
		super();
	}

	public String toString() {
		return "Cap : [id : " + getIdCap() + ", lib : " + getCodeCap() + ", isVDN :"
				+ getCapVdn().toString().equals("1") + " ]";
	}

	public Integer getIdCap() {
		return idCap;
	}

	public void setIdCap(Integer idCap) {
		this.idCap = idCap;
	}

	public String getCodeCap() {
		return codeCap != null ? codeCap.toUpperCase() : null;
	}

	public void setCodeCap(String codeCap) {
		this.codeCap = codeCap;
	}

	public String getRefCap() {
		return refCap != null ? refCap.toUpperCase() : null;
	}

	public void setRefCap(String refCap) {
		this.refCap = refCap;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTypeCap() {
		return typeCap;
	}

	public void setTypeCap(String typeCap) {
		this.typeCap = typeCap;
	}

	public Integer getCapVdn() {
		return capVdn;
	}

	public void setCapVdn(Integer capVDN) {
		this.capVdn = capVDN;
	}

	@Override
	public boolean equals(Object object) {
		return idCap.toString().equals(((Cap) object).getIdCap().toString());
	}
}
