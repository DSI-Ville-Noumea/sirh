package nc.mairie.metier.carriere;

/**
 * Objet metier Categorie
 */
public class Categorie {

	public Integer idCategorieStatut;
	public String libCategorieStatut;

	/**
	 * Constructeur Categorie.
	 */
	public Categorie() {
		super();
	}

	public Integer getIdCategorieStatut() {
		return idCategorieStatut;
	}

	public void setIdCategorieStatut(Integer idCategorieStatut) {
		this.idCategorieStatut = idCategorieStatut;
	}

	public String getLibCategorieStatut() {
		return libCategorieStatut;
	}

	public void setLibCategorieStatut(String libCategorieStatut) {
		this.libCategorieStatut = libCategorieStatut;
	}

	@Override
	public boolean equals(Object object) {
		return idCategorieStatut.toString().equals(((Categorie) object).getIdCategorieStatut().toString());
	}
}
