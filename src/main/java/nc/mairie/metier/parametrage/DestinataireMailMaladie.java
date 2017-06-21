package nc.mairie.metier.parametrage;

public class DestinataireMailMaladie {

	public Integer	idDestinataireMailMaladie;
	public Integer	idGroupe;

	public DestinataireMailMaladie() {
		super();
	}

	public String toString() {
		return "DestinataireMailMaladie : [id : " + getIdDestinataireMailMaladie() + ", groupeId : " + getIdGroupe() + "]";
	}

	public Integer getIdDestinataireMailMaladie() {
		return idDestinataireMailMaladie;
	}

	public void setIdDestinataireMailMaladie(Integer idDestinataireMailMaladie) {
		this.idDestinataireMailMaladie = idDestinataireMailMaladie;
	}

	public Integer getIdGroupe() {
		return idGroupe;
	}

	public void setIdGroupe(Integer idGroupe) {
		this.idGroupe = idGroupe;
	}
}
