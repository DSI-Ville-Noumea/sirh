package nc.mairie.metier.parametrage;

public class Deliberation {

	public Integer idDeliberation;
	public String codeDeliberation;
	public String libDeliberation;
	public String typeDeliberation;
	public String texteCAP;

	public Deliberation() {
		super();
	}

	public String toString() {
		return "Deliberation : [id : " + getIdDeliberation() + ", lib : " + getLibDeliberation() + "]";
	}

	public Integer getIdDeliberation() {
		return idDeliberation;
	}

	public void setIdDeliberation(Integer idDeliberation) {
		this.idDeliberation = idDeliberation;
	}

	public String getCodeDeliberation() {
		return codeDeliberation;
	}

	public void setCodeDeliberation(String codeDeliberation) {
		this.codeDeliberation = codeDeliberation;
	}

	public String getLibDeliberation() {
		return libDeliberation;
	}

	public void setLibDeliberation(String libDeliberation) {
		this.libDeliberation = libDeliberation;
	}

	public String getTypeDeliberation() {
		return typeDeliberation;
	}

	public void setTypeDeliberation(String typeDeliberation) {
		this.typeDeliberation = typeDeliberation;
	}

	public String getTexteCAP() {
		return texteCAP;
	}

	public void setTexteCAP(String texteCAP) {
		this.texteCAP = texteCAP;
	}

	@Override
	public boolean equals(Object object) {
		return idDeliberation.toString().equals(((Deliberation) object).getIdDeliberation().toString());
	}
}
