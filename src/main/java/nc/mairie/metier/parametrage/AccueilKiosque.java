package nc.mairie.metier.parametrage;

public class AccueilKiosque {

	public Integer idAccueilKiosque;
	public String texteAccueilKiosque;
	public String titre;

	public AccueilKiosque() {
		super();
	}

	public String toString() {
		return "AccueilKiosque : [texteAccueilKiosque : " + getTexteAccueilKiosque() + "]";
	}

	public Integer getIdAccueilKiosque() {
		return idAccueilKiosque;
	}

	public void setIdAccueilKiosque(Integer idAccueilKiosque) {
		this.idAccueilKiosque = idAccueilKiosque;
	}

	public String getTexteAccueilKiosque() {
		return texteAccueilKiosque;
	}

	public void setTexteAccueilKiosque(String texteAccueilKiosque) {
		this.texteAccueilKiosque = texteAccueilKiosque;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}
}
