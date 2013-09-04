package nc.mairie.gestionagent.dto;

public class PrimeDto extends PointageDto {

	private String titre;
	private String typeSaisie;
	private Integer quantite;
	private Integer numRubrique;
	private Integer idRefPrime;

	public PrimeDto() {
	}

	public PrimeDto(PrimeDto primeDto) {
		super((PointageDto) primeDto);

		this.titre = primeDto.titre;
		this.typeSaisie = primeDto.typeSaisie;
		this.quantite = primeDto.quantite;
		this.numRubrique = primeDto.numRubrique;
		this.idRefPrime = primeDto.idRefPrime;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getTypeSaisie() {
		return typeSaisie;
	}

	public void setTypeSaisie(String typeSaisie) {
		this.typeSaisie = typeSaisie;
	}

	public Integer getQuantite() {
		return quantite;
	}

	public void setQuantite(Integer quantite) {
		this.quantite = quantite;
	}

	public Integer getNumRubrique() {
		return numRubrique;
	}

	public void setNumRubrique(Integer numRubrique) {
		this.numRubrique = numRubrique;
	}

	public Integer getIdRefPrime() {
		return idRefPrime;
	}

	public void setIdRefPrime(Integer idRefPrime) {
		this.idRefPrime = idRefPrime;
	}
}
