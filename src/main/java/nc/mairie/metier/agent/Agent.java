package nc.mairie.metier.agent;

import java.io.Serializable;
import java.util.Date;

import nc.mairie.enums.EnumCivilite;
import nc.mairie.metier.Const;

/**
 * Objet metier Agent
 */
public class Agent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Integer idAgent;
	public Integer idCollectivite;
	public Integer idSituationFamiliale;
	public Integer idVoie;
	public Integer idEtatService;
	public Integer cposVilleDom;
	public Integer ccomVilleDom;
	public Integer cposVilleBp;
	public Integer ccomVilleBp;
	public Integer nomatr;
	public String nomMarital;
	public String prenom;
	public String prenomUsage;
	public String civilite;
	public String nomPatronymique;
	public String nomUsage;
	public Date dateNaissance;
	public Date dateDeces;
	public String sexe;
	public Date datePremiereEmbauche;
	public Date dateDerniereEmbauche;
	public Date dateArriveeTerritoire;
	public String nationalite;
	public Integer codePaysNaissEt;
	public Integer codeCommuneNaissEt;
	public Integer codeCommuneNaissFr;
	public String numCarteSejour;
	public Date dateValiditeCarteSejour;
	public String numRue;
	public String numRueBisTer;
	public String adresseComplementaire;
	public String bp;
	public Integer cdBanque;
	public Integer cdGuichet;
	public String numCompte;
	public Integer rib;
	public String intituleCompte;
	public String vcat;
	public Date debutService;
	public Date finService;
	public String numCafat;
	public String numRuamm;
	public String numMutuelle;
	public String numCre;
	public String numIrcafex;
	public String numClr;
	public String codeElection;
	public String rueNonNoumea;
	public String quartier;
	public Integer idTitreRepas;
	public String idTiarhe;

	/**
	 * Retourne le libelle associe a la civilite de l'agent
	 * 
	 * @return String
	 */
	public String getLibCivilite() {
		for (EnumCivilite e : EnumCivilite.values()) {
			if (e.getCode().equals(getCivilite())) {
				return e.getValue();
			}
		}
		return null;
	}

	/**
	 * Constructeur Agent.
	 */
	public Agent() {
		super();
	}

	/**
	 * Getter de l'attribut idAgent.
	 */
	public Integer getIdAgent() {
		return idAgent;
	}

	/**
	 * Setter de l'attribut idAgent.
	 */
	public void setIdAgent(Integer newIdAgent) {
		idAgent = newIdAgent;
	}

	/**
	 * Getter de l'attribut idVoie.
	 */
	public Integer getIdVoie() {
		return idVoie;
	}

	/**
	 * Setter de l'attribut idVoie.
	 */
	public void setIdVoie(Integer newIdVoie) {
		idVoie = newIdVoie;
	}

	/**
	 * Getter de l'attribut idCollectivite.
	 */
	public Integer getIdCollectivite() {
		return idCollectivite;
	}

	/**
	 * Setter de l'attribut idCollectivite.
	 */
	public void setIdCollectivite(Integer newIdCollectivite) {
		idCollectivite = newIdCollectivite;
	}

	/**
	 * Getter de l'attribut codePostalVilleDom.
	 */
	public Integer getCposVilleDom() {
		return cposVilleDom;
	}

	/**
	 * Setter de l'attribut codePostalVilleDom.
	 */
	public void setCposVilleDom(Integer newCodePostalVilleDom) {
		cposVilleDom = newCodePostalVilleDom;
	}

	/**
	 * Getter de l'attribut codeComVilleDom.
	 */
	public Integer getCcomVilleDom() {
		return ccomVilleDom;
	}

	/**
	 * Setter de l'attribut codeComVilleDom.
	 */
	public void setCcomVilleDom(Integer newCodeComVilleDom) {
		ccomVilleDom = newCodeComVilleDom;
	}

	/**
	 * Getter de l'attribut codePostalVilleBp.
	 */
	public Integer getCposVilleBp() {
		return cposVilleBp;
	}

	/**
	 * Setter de l'attribut codePostalVilleBp.
	 */
	public void setCposVilleBp(Integer newCodePostalVilleBp) {
		cposVilleBp = newCodePostalVilleBp;
	}

	/**
	 * Getter de l'attribut codeComVilleBp.
	 */
	public Integer getCcomVilleBp() {
		return ccomVilleBp;
	}

	/**
	 * Setter de l'attribut codeComVilleBp.
	 */
	public void setCcomVilleBp(Integer newCodeComVilleBp) {
		ccomVilleBp = newCodeComVilleBp;
	}

	/**
	 * Getter de l'attribut noMatricule.
	 */
	public Integer getNomatr() {
		return nomatr;
	}

	/**
	 * Setter de l'attribut noMatricule.
	 */
	public void setNomatr(Integer newNoMatricule) {
		nomatr = newNoMatricule;
	}

	/**
	 * Getter de l'attribut nomMarital.
	 */
	public String getNomMarital() {
		return nomMarital;
	}

	/**
	 * Setter de l'attribut nomMarital.
	 */
	public void setNomMarital(String newNomMarital) {
		nomMarital = newNomMarital;
	}

	/**
	 * Getter de l'attribut prenom.
	 */
	public String getPrenom() {
		return prenom == null ? Const.CHAINE_VIDE : prenom.trim();
	}

	/**
	 * Setter de l'attribut prenom.
	 */
	public void setPrenom(String newPrenom) {
		prenom = newPrenom;
	}

	/**
	 * Getter de l'attribut civilite.
	 */
	public String getCivilite() {
		return civilite;
	}

	/**
	 * Setter de l'attribut civilite.
	 */
	public void setCivilite(String newCivilite) {
		civilite = newCivilite;
	}

	/**
	 * Getter de l'attribut nomPatronymique.
	 */
	public String getNomPatronymique() {
		return nomPatronymique;
	}

	/**
	 * Setter de l'attribut nomPatronymique.
	 */
	public void setNomPatronymique(String newNomPatronymique) {
		nomPatronymique = newNomPatronymique;
	}

	/**
	 * Getter de l'attribut nomUsage.
	 */
	public String getNomUsage() {
		return nomUsage == null ? Const.CHAINE_VIDE : nomUsage.trim();
	}

	/**
	 * Setter de l'attribut nomUsage.
	 */
	public void setNomUsage(String newNomUsage) {
		nomUsage = newNomUsage;
	}

	public String getNomAgent() {
		return getNomUsage();
	}

	public String getPrenomAgent() {
		return getPrenomUsage();
	}

	/**
	 * Getter de l'attribut dateNaissance.
	 */
	public Date getDateNaissance() {
		return dateNaissance;
	}

	/**
	 * Setter de l'attribut dateNaissance.
	 */
	public void setDateNaissance(Date newDateNaissance) {
		dateNaissance = newDateNaissance;
	}

	/**
	 * Getter de l'attribut dateDeces.
	 */
	public Date getDateDeces() {
		return dateDeces;
	}

	/**
	 * Setter de l'attribut dateDeces.
	 */
	public void setDateDeces(Date newDateDeces) {
		dateDeces = newDateDeces;
	}

	/**
	 * Getter de l'attribut sexe.
	 */
	public String getSexe() {
		return sexe;
	}

	/**
	 * Setter de l'attribut sexe.
	 */
	public void setSexe(String newSexe) {
		sexe = newSexe;
	}

	/**
	 * Getter de l'attribut IdSituationFamiliale.
	 */
	public Integer getIdSituationFamiliale() {
		return idSituationFamiliale;
	}

	/**
	 * Setter de l'attribut idSituationFamiliale.
	 */
	public void setIdSituationFamiliale(Integer newIdSituationFamiliale) {
		idSituationFamiliale = newIdSituationFamiliale;
	}

	/**
	 * Getter de l'attribut datePremiereEmbauche.
	 */
	public Date getDatePremiereEmbauche() {
		return datePremiereEmbauche;
	}

	/**
	 * Setter de l'attribut datePremiereEmbauche.
	 */
	public void setDatePremiereEmbauche(Date newDatePremiereEmbauche) {
		datePremiereEmbauche = newDatePremiereEmbauche;
	}

	/**
	 * Getter de l'attribut dateDerniereEmbauche.
	 */
	public Date getDateDerniereEmbauche() {
		return dateDerniereEmbauche;
	}

	/**
	 * Setter de l'attribut dateDerniereEmbauche.
	 */
	public void setDateDerniereEmbauche(Date newDateDerniereEmbauche) {
		dateDerniereEmbauche = newDateDerniereEmbauche;
	}
	

	public Date getDateArriveeTerritoire() {
		return dateArriveeTerritoire;
	}

	public void setDateArriveeTerritoire(Date dateArriveeTerritoire) {
		this.dateArriveeTerritoire = dateArriveeTerritoire;
	}

	/**
	 * Getter de l'attribut nationalite.
	 */
	public String getNationalite() {
		return nationalite;
	}

	/**
	 * Setter de l'attribut nationalite.
	 */
	public void setNationalite(String newNationalite) {
		nationalite = newNationalite;
	}

	/**
	 * Getter de l'attribut codePaysNaissanceEt.
	 */
	public Integer getCodePaysNaissEt() {
		return codePaysNaissEt;
	}

	/**
	 * Setter de l'attribut codePaysNaissanceEt.
	 */
	public void setCodePaysNaissEt(Integer newCodePaysNaissanceEt) {
		codePaysNaissEt = newCodePaysNaissanceEt;
	}

	/**
	 * Getter de l'attribut codeCommuneNaissanceEt.
	 */
	public Integer getCodeCommuneNaissEt() {
		return codeCommuneNaissEt;
	}

	/**
	 * Setter de l'attribut codeCommuneNaissanceEt.
	 */
	public void setCodeCommuneNaissEt(Integer newCodeCommNaissanceEt) {
		codeCommuneNaissEt = newCodeCommNaissanceEt;
	}

	/**
	 * Getter de l'attribut codeCommuneNaissanceFr.
	 */
	public Integer getCodeCommuneNaissFr() {
		return codeCommuneNaissFr;
	}

	/**
	 * Setter de l'attribut codeCommuneNaissanceFr.
	 */
	public void setCodeCommuneNaissFr(Integer newCodeCommNaissanceFr) {
		codeCommuneNaissFr = newCodeCommNaissanceFr;
	}

	/**
	 * Getter de l'attribut numCarteSejour.
	 */
	public String getNumCarteSejour() {
		return numCarteSejour;
	}

	/**
	 * Setter de l'attribut numCarteSejour.
	 */
	public void setNumCarteSejour(String newNumCarteSejour) {
		numCarteSejour = newNumCarteSejour;
	}

	/**
	 * Getter de l'attribut dateValiditeCarteSejour.
	 */
	public Date getDateValiditeCarteSejour() {
		return dateValiditeCarteSejour;
	}

	/**
	 * Setter de l'attribut dateValiditeCarteSejour.
	 */
	public void setDateValiditeCarteSejour(Date newDateValiditeCarteSejour) {
		dateValiditeCarteSejour = newDateValiditeCarteSejour;
	}

	/**
	 * Getter de l'attribut numRue.
	 */
	public String getNumRue() {
		return numRue == null ? Const.CHAINE_VIDE : numRue.trim();
	}

	/**
	 * Setter de l'attribut numRue.
	 */
	public void setNumRue(String newNumRue) {
		numRue = newNumRue;
	}

	/**
	 * Getter de l'attribut numRueBisTer.
	 */
	public String getNumRueBisTer() {
		return numRueBisTer;
	}

	/**
	 * Setter de l'attribut numRueBisTer.
	 */
	public void setNumRueBisTer(String newNumRueBisTer) {
		numRueBisTer = newNumRueBisTer;
	}

	/**
	 * Getter de l'attribut adresseComplementaire.
	 */
	public String getAdresseComplementaire() {
		return adresseComplementaire;
	}

	/**
	 * Setter de l'attribut adresseComplementaire.
	 */
	public void setAdresseComplementaire(String newAdresseComplementaire) {
		adresseComplementaire = newAdresseComplementaire;
	}

	/**
	 * Getter de l'attribut bp.
	 */
	public String getBp() {
		return bp;
	}

	/**
	 * Setter de l'attribut bp.
	 */
	public void setBp(String newBp) {
		bp = newBp;
	}

	/**
	 * Getter de l'attribut numCompte.
	 */
	public String getNumCompte() {
		return numCompte;
	}

	/**
	 * Setter de l'attribut numCompte.
	 */
	public void setNumCompte(String newNumCompte) {
		numCompte = newNumCompte;
	}

	/**
	 * Getter de l'attribut rib.
	 */
	public Integer getRib() {
		return rib;
	}

	/**
	 * Setter de l'attribut rib.
	 */
	public void setRib(Integer newRib) {
		rib = newRib;
	}

	/**
	 * Getter de l'attribut intituleCompte.
	 */
	public String getIntituleCompte() {
		return intituleCompte;
	}

	/**
	 * Setter de l'attribut intituleCompte.
	 */
	public void setIntituleCompte(String newIntituleCompte) {
		intituleCompte = newIntituleCompte;
	}

	/**
	 * Getter de l'attribut idEtatService.
	 */
	public Integer getIdEtatService() {
		return idEtatService;
	}

	/**
	 * Setter de l'attribut idEtatService.
	 */
	public void setIdEtatService(Integer newIdEtatService) {
		idEtatService = newIdEtatService;
	}

	/**
	 * Getter de l'attribut vcat.
	 */
	public String getVcat() {
		return vcat;
	}

	/**
	 * Setter de l'attribut vcat.
	 */
	public void setVcat(String newVcat) {
		vcat = newVcat;
	}

	/**
	 * Getter de l'attribut debutService.
	 */
	public Date getDebutService() {
		return debutService;
	}

	/**
	 * Setter de l'attribut debutService.
	 */
	public void setDebutService(Date newDebutService) {
		debutService = newDebutService;
	}

	/**
	 * Getter de l'attribut finService.
	 */
	public Date getFinService() {
		return finService;
	}

	/**
	 * Setter de l'attribut finService.
	 */
	public void setFinService(Date newFinService) {
		finService = newFinService;
	}

	/**
	 * Getter de l'attribut numCafat.
	 */
	public String getNumCafat() {
		return numCafat != null ? numCafat.trim() : Const.CHAINE_VIDE;
	}

	/**
	 * Setter de l'attribut numCafat.
	 */
	public void setNumCafat(String newNumCafat) {
		numCafat = newNumCafat;
	}

	/**
	 * Getter de l'attribut numRuamm.
	 */
	public String getNumRuamm() {
		return numRuamm != null ? numRuamm.trim() : Const.CHAINE_VIDE;
	}

	/**
	 * Setter de l'attribut numRuamm.
	 */
	public void setNumRuamm(String newNumRuamm) {
		numRuamm = newNumRuamm;
	}

	/**
	 * Getter de l'attribut numClr.
	 */
	public String getNumClr() {
		return numClr;
	}

	/**
	 * Setter de l'attribut numClr.
	 */
	public void setNumClr(String newNumClr) {
		numClr = newNumClr;
	}

	/**
	 * Getter de l'attribut numMutuelle.
	 */
	public String getNumMutuelle() {
		return numMutuelle;
	}

	/**
	 * Setter de l'attribut numMutuelle.
	 */
	public void setNumMutuelle(String newNumMutuelle) {
		numMutuelle = newNumMutuelle;
	}

	/**
	 * Getter de l'attribut numCre.
	 */
	public String getNumCre() {
		return numCre;
	}

	/**
	 * Setter de l'attribut numCre.
	 */
	public void setNumCre(String newNumCre) {
		numCre = newNumCre;
	}

	/**
	 * Getter de l'attribut numIrcafex.
	 */
	public String getNumIrcafex() {
		return numIrcafex;
	}

	/**
	 * Setter de l'attribut numIrcafex.
	 */
	public void setNumIrcafex(String newNumIrcafex) {
		numIrcafex = newNumIrcafex;
	}

	/**
	 * Getter de l'attribut codeElection.
	 */
	public String getCodeElection() {
		return codeElection;
	}

	/**
	 * Setter de l'attribut codeElection.
	 */
	public void setCodeElection(String newCodeElection) {
		codeElection = newCodeElection;
	}

	/**
	 * Getter de l'attribut codeBanque.
	 */
	public Integer getCdBanque() {
		return cdBanque;
	}

	/**
	 * Setter de l'attribut codeBanque.
	 */
	public void setCdBanque(Integer newCodeBanque) {
		cdBanque = newCodeBanque;
	}

	/**
	 * Getter de l'attribut codeGuichet.
	 */
	public Integer getCdGuichet() {
		return cdGuichet;
	}

	/**
	 * Setter de l'attribut codeGuichet.
	 */
	public void setCdGuichet(Integer newCodeGuichet) {
		cdGuichet = newCodeGuichet;
	}

	/**
	 * Getter de l'attribut rueNonNoumea.
	 */
	public String getRueNonNoumea() {
		return rueNonNoumea;
	}

	/**
	 * Setter de l'attribut rueNonNoumea.
	 */
	public void setRueNonNoumea(String newRueNonNoumea) {
		rueNonNoumea = newRueNonNoumea;
	}

	/**
	 * Retourne le prenom d'usage de l'agent
	 * 
	 * @return prenomUsage
	 */
	public String getPrenomUsage() {
		return prenomUsage;
	}

	/**
	 * Met a jour le prenom d'usage de l'agent
	 * 
	 * @param prenomUsage
	 *            String
	 */
	public void setPrenomUsage(String prenomUsage) {
		this.prenomUsage = prenomUsage;
	}

	/**
	 * Surcharge de la methode equals.
	 * 
	 * @param object
	 *            Agent
	 * @return boolean
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (idAgent == null) {
			return false;
		}
		return idAgent.toString().equals(((Agent) object).getIdAgent().toString());
	}

	public String getQuartier() {
		return quartier;
	}

	public void setQuartier(String quartier) {
		this.quartier = quartier;
	}

	public Integer getIdTitreRepas() {
		return idTitreRepas;
	}

	public void setIdTitreRepas(Integer idTitreRepas) {
		this.idTitreRepas = idTitreRepas;
	}

	public String getIdTiarhe() {
		return idTiarhe;
	}

	public void setIdTiarhe(String idTiarhe) {
		this.idTiarhe = idTiarhe;
	}
}
