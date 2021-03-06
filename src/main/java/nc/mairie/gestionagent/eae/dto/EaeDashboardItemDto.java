package nc.mairie.gestionagent.eae.dto;


public class EaeDashboardItemDto {

	private String nom;
	private String prenom;
	private int nonAffecte;
	private int nonDebute;
	private int cree;
	private int enCours;
	private int finalise;
	private int fige;
	private int nonDefini;
	private int mini;
	private int maxi;
	private int moy;
	private int changClasse;
	
	private String direction;
	private String service;
	private String section;
	private int nbEaeControle;
	private int nbEaeCAP;
	private int totalEAE;

	public EaeDashboardItemDto() {

	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public int getNonAffecte() {
		return nonAffecte;
	}

	public void setNonAffecte(int nonAffecte) {
		this.nonAffecte = nonAffecte;
	}

	public int getNonDebute() {
		return nonDebute;
	}

	public void setNonDebute(int nonDebute) {
		this.nonDebute = nonDebute;
	}

	public int getCree() {
		return cree;
	}

	public void setCree(int cree) {
		this.cree = cree;
	}

	public int getEnCours() {
		return enCours;
	}

	public void setEnCours(int enCours) {
		this.enCours = enCours;
	}

	public int getFinalise() {
		return finalise;
	}

	public void setFinalise(int finalise) {
		this.finalise = finalise;
	}

	public int getFige() {
		return fige;
	}

	public void setFige(int fige) {
		this.fige = fige;
	}

	public int getNonDefini() {
		return nonDefini;
	}

	public void setNonDefini(int nonDefini) {
		this.nonDefini = nonDefini;
	}

	public int getMini() {
		return mini;
	}

	public void setMini(int mini) {
		this.mini = mini;
	}

	public int getMaxi() {
		return maxi;
	}

	public void setMaxi(int maxi) {
		this.maxi = maxi;
	}

	public int getMoy() {
		return moy;
	}

	public void setMoy(int moy) {
		this.moy = moy;
	}

	public int getChangClasse() {
		return changClasse;
	}

	public void setChangClasse(int changClasse) {
		this.changClasse = changClasse;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public int getNbEaeControle() {
		return nbEaeControle;
	}

	public void setNbEaeControle(int nbEaeControle) {
		this.nbEaeControle = nbEaeControle;
	}

	public int getNbEaeCAP() {
		return nbEaeCAP;
	}

	public void setNbEaeCAP(int nbEaeCAP) {
		this.nbEaeCAP = nbEaeCAP;
	}

	public int getTotalEAE() {
		return totalEAE;
	}

	public void setTotalEAE(int totalEAE) {
		this.totalEAE = totalEAE;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
	
}
