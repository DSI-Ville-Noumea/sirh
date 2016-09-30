package nc.mairie.gestionagent.eae.dto;

public class EaeDocumentDto {

	private Integer	idEaeDocument;
	private Integer	idCampagneAction;
	private Integer	idDocument;
	private String	typeDocument;

	public Integer getIdEaeDocument() {
		return idEaeDocument;
	}

	public void setIdEaeDocument(Integer idEaeDocument) {
		this.idEaeDocument = idEaeDocument;
	}

	public Integer getIdCampagneAction() {
		return idCampagneAction;
	}

	public void setIdCampagneAction(Integer idCampagneAction) {
		this.idCampagneAction = idCampagneAction;
	}

	public Integer getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(Integer idDocument) {
		this.idDocument = idDocument;
	}

	public String getTypeDocument() {
		return typeDocument;
	}

	public void setTypeDocument(String typeDocument) {
		this.typeDocument = typeDocument;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idEaeDocument == null) ? 0 : idEaeDocument.hashCode());
		result = prime * result + ((typeDocument == null) ? 0 : typeDocument.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EaeDocumentDto other = (EaeDocumentDto) obj;
		if (idEaeDocument == null) {
			if (other.idEaeDocument != null)
				return false;
		} else if (!idEaeDocument.equals(other.idEaeDocument))
			return false;
		if (typeDocument == null) {
			if (other.typeDocument != null)
				return false;
		} else if (!typeDocument.equals(other.typeDocument))
			return false;
		return true;
	}
}
