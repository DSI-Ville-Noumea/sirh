package nc.mairie.metier.commun;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Voie
 */
public class Voie extends BasicMetier {
	public String codCommune;
	public String codVoie;
	public String cdvcar;
	public String cdvart;
	public String cdvtit;
	public String prevoi;
	public String nomvoi;
	public String livoie;
	public String comvoi;
	public String debvoi;
	public String finvoi;
	public String dpapor;
	public String fpapor;
	public String dimpor;
	public String fimpor;
	public String orivoi;
	public String dpapoo;
	public String fpapoo;
	public String dimpoo;
	public String fimpoo;
	public String cddebv;
	public String cdfinv;
	public String noplan;
	public String replan;
	public String provoi;
	public String clavoi;
	public String livcar;
	public String livart;
	public String livtit;
	public String letalpha;
	public String dlidenom;
	public String delibfin;
	public String dliclass;
	public String dteclass;
	public String dbupropr;
	public String dlidclas;
	public String dcession;
	public String finpropr;
	public String competen;
	public String rivoli;

	/**
	 * Constructeur Voie.
	 */
	public Voie() {
		super();
	}

	/**
	 * Retourne un Voie.
	 * 
	 * @return Voie
	 */
	public static Voie chercherVoie(Transaction aTransaction, String codVoie) throws Exception {
		Voie unVoie = new Voie();
		return unVoie.getMyVoieBroker().chercherVoie(aTransaction, codVoie);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new VoieBroker(this);
	}

	/**
	 * Getter de l'attribut cddebv.
	 */
	public String getCddebv() {
		return cddebv;
	}

	/**
	 * Getter de l'attribut cdfinv.
	 */
	public String getCdfinv() {
		return cdfinv;
	}

	/**
	 * Getter de l'attribut cdvart.
	 */
	public String getCdvart() {
		return cdvart;
	}

	/**
	 * Getter de l'attribut cdvcar.
	 */
	public String getCdvcar() {
		return cdvcar;
	}

	/**
	 * Getter de l'attribut cdvtit.
	 */
	public String getCdvtit() {
		return cdvtit;
	}

	/**
	 * Getter de l'attribut clavoi.
	 */
	public String getClavoi() {
		return clavoi;
	}

	/**
	 * Getter de l'attribut codCommune.
	 */
	public String getCodCommune() {
		return codCommune;
	}

	/**
	 * Getter de l'attribut codVoie.
	 */
	public String getCodVoie() {
		return codVoie;
	}

	/**
	 * Getter de l'attribut competen.
	 */
	public String getCompeten() {
		return competen;
	}

	/**
	 * Getter de l'attribut comvoi.
	 */
	public String getComvoi() {
		return comvoi;
	}

	/**
	 * Getter de l'attribut dbupropr.
	 */
	public String getDbupropr() {
		return dbupropr;
	}

	/**
	 * Getter de l'attribut dcession.
	 */
	public String getDcession() {
		return dcession;
	}

	/**
	 * Getter de l'attribut debvoi.
	 */
	public String getDebvoi() {
		return debvoi;
	}

	/**
	 * Getter de l'attribut delibfin.
	 */
	public String getDelibfin() {
		return delibfin;
	}

	/**
	 * Getter de l'attribut dimpoo.
	 */
	public String getDimpoo() {
		return dimpoo;
	}

	/**
	 * Getter de l'attribut dimpor.
	 */
	public String getDimpor() {
		return dimpor;
	}

	/**
	 * Getter de l'attribut dliclass.
	 */
	public String getDliclass() {
		return dliclass;
	}

	/**
	 * Getter de l'attribut dlidclas.
	 */
	public String getDlidclas() {
		return dlidclas;
	}

	/**
	 * Getter de l'attribut dlidenom.
	 */
	public String getDlidenom() {
		return dlidenom;
	}

	/**
	 * Getter de l'attribut dpapoo.
	 */
	public String getDpapoo() {
		return dpapoo;
	}

	/**
	 * Getter de l'attribut dpapor.
	 */
	public String getDpapor() {
		return dpapor;
	}

	/**
	 * Getter de l'attribut dteclass.
	 */
	public String getDteclass() {
		return dteclass;
	}

	/**
	 * Getter de l'attribut fimpoo.
	 */
	public String getFimpoo() {
		return fimpoo;
	}

	/**
	 * Getter de l'attribut fimpor.
	 */
	public String getFimpor() {
		return fimpor;
	}

	/**
	 * Getter de l'attribut finpropr.
	 */
	public String getFinpropr() {
		return finpropr;
	}

	/**
	 * Getter de l'attribut finvoi.
	 */
	public String getFinvoi() {
		return finvoi;
	}

	/**
	 * Getter de l'attribut fpapoo.
	 */
	public String getFpapoo() {
		return fpapoo;
	}

	/**
	 * Getter de l'attribut fpapor.
	 */
	public String getFpapor() {
		return fpapor;
	}

	/**
	 * Getter de l'attribut letalpha.
	 */
	public String getLetalpha() {
		return letalpha;
	}

	/**
	 * Getter de l'attribut livart.
	 */
	public String getLivart() {
		return livart;
	}

	/**
	 * Getter de l'attribut livcar.
	 */
	public String getLivcar() {
		return livcar == null ? Const.CHAINE_VIDE : livcar.trim();
	}

	/**
	 * Getter de l'attribut livoie.
	 */
	public String getLivoie() {
		return livoie;
	}

	/**
	 * Getter de l'attribut livtit.
	 */
	public String getLivtit() {
		return livtit;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected VoieBroker getMyVoieBroker() {
		return (VoieBroker) getMyBasicBroker();
	}

	/**
	 * Getter de l'attribut nomvoi.
	 */
	public String getNomvoi() {
		return nomvoi==null ?Const.CHAINE_VIDE : nomvoi.trim();
	}

	/**
	 * Getter de l'attribut noplan.
	 */
	public String getNoplan() {
		return noplan;
	}

	/**
	 * Getter de l'attribut orivoi.
	 */
	public String getOrivoi() {
		return orivoi;
	}

	/**
	 * Getter de l'attribut prevoi.
	 */
	public String getPrevoi() {
		return prevoi == null ? Const.CHAINE_VIDE : prevoi.trim();
	}

	/**
	 * Getter de l'attribut provoi.
	 */
	public String getProvoi() {
		return provoi;
	}

	/**
	 * Getter de l'attribut replan.
	 */
	public String getReplan() {
		return replan;
	}

	/**
	 * Getter de l'attribut rivoli.
	 */
	public String getRivoli() {
		return rivoli;
	}

	/**
	 * Setter de l'attribut cddebv.
	 */
	public void setCddebv(String newCddebv) {
		cddebv = newCddebv;
	}

	/**
	 * Setter de l'attribut cdfinv.
	 */
	public void setCdfinv(String newCdfinv) {
		cdfinv = newCdfinv;
	}

	/**
	 * Setter de l'attribut cdvart.
	 */
	public void setCdvart(String newCdvart) {
		cdvart = newCdvart;
	}

	/**
	 * Setter de l'attribut cdvcar.
	 */
	public void setCdvcar(String newCdvcar) {
		cdvcar = newCdvcar;
	}

	/**
	 * Setter de l'attribut cdvtit.
	 */
	public void setCdvtit(String newCdvtit) {
		cdvtit = newCdvtit;
	}

	/**
	 * Setter de l'attribut clavoi.
	 */
	public void setClavoi(String newClavoi) {
		clavoi = newClavoi;
	}

	/**
	 * Setter de l'attribut codCommune.
	 */
	public void setCodCommune(String newCodCommune) {
		codCommune = newCodCommune;
	}

	/**
	 * Setter de l'attribut codVoie.
	 */
	public void setCodVoie(String newCodVoie) {
		codVoie = newCodVoie;
	}

	/**
	 * Setter de l'attribut competen.
	 */
	public void setCompeten(String newCompeten) {
		competen = newCompeten;
	}

	/**
	 * Setter de l'attribut comvoi.
	 */
	public void setComvoi(String newComvoi) {
		comvoi = newComvoi;
	}

	/**
	 * Setter de l'attribut dbupropr.
	 */
	public void setDbupropr(String newDbupropr) {
		dbupropr = newDbupropr;
	}

	/**
	 * Setter de l'attribut dcession.
	 */
	public void setDcession(String newDcession) {
		dcession = newDcession;
	}

	/**
	 * Setter de l'attribut debvoi.
	 */
	public void setDebvoi(String newDebvoi) {
		debvoi = newDebvoi;
	}

	/**
	 * Setter de l'attribut delibfin.
	 */
	public void setDelibfin(String newDelibfin) {
		delibfin = newDelibfin;
	}

	/**
	 * Setter de l'attribut dimpoo.
	 */
	public void setDimpoo(String newDimpoo) {
		dimpoo = newDimpoo;
	}

	/**
	 * Setter de l'attribut dimpor.
	 */
	public void setDimpor(String newDimpor) {
		dimpor = newDimpor;
	}

	/**
	 * Setter de l'attribut dliclass.
	 */
	public void setDliclass(String newDliclass) {
		dliclass = newDliclass;
	}

	/**
	 * Setter de l'attribut dlidclas.
	 */
	public void setDlidclas(String newDlidclas) {
		dlidclas = newDlidclas;
	}

	/**
	 * Setter de l'attribut dlidenom.
	 */
	public void setDlidenom(String newDlidenom) {
		dlidenom = newDlidenom;
	}

	/**
	 * Setter de l'attribut dpapoo.
	 */
	public void setDpapoo(String newDpapoo) {
		dpapoo = newDpapoo;
	}

	/**
	 * Setter de l'attribut dpapor.
	 */
	public void setDpapor(String newDpapor) {
		dpapor = newDpapor;
	}

	/**
	 * Setter de l'attribut dteclass.
	 */
	public void setDteclass(String newDteclass) {
		dteclass = newDteclass;
	}

	/**
	 * Setter de l'attribut fimpoo.
	 */
	public void setFimpoo(String newFimpoo) {
		fimpoo = newFimpoo;
	}

	/**
	 * Setter de l'attribut fimpor.
	 */
	public void setFimpor(String newFimpor) {
		fimpor = newFimpor;
	}

	/**
	 * Setter de l'attribut finpropr.
	 */
	public void setFinpropr(String newFinpropr) {
		finpropr = newFinpropr;
	}

	/**
	 * Setter de l'attribut finvoi.
	 */
	public void setFinvoi(String newFinvoi) {
		finvoi = newFinvoi;
	}

	/**
	 * Setter de l'attribut fpapoo.
	 */
	public void setFpapoo(String newFpapoo) {
		fpapoo = newFpapoo;
	}

	/**
	 * Setter de l'attribut fpapor.
	 */
	public void setFpapor(String newFpapor) {
		fpapor = newFpapor;
	}

	/**
	 * Setter de l'attribut letalpha.
	 */
	public void setLetalpha(String newLetalpha) {
		letalpha = newLetalpha;
	}

	/**
	 * Setter de l'attribut livart.
	 */
	public void setLivart(String newLivart) {
		livart = newLivart;
	}

	/**
	 * Setter de l'attribut livcar.
	 */
	public void setLivcar(String newLivcar) {
		livcar = newLivcar;
	}

	/**
	 * Setter de l'attribut livoie.
	 */
	public void setLivoie(String newLivoie) {
		livoie = newLivoie;
	}

	/**
	 * Setter de l'attribut livtit.
	 */
	public void setLivtit(String newLivtit) {
		livtit = newLivtit;
	}

	/**
	 * Setter de l'attribut nomvoi.
	 */
	public void setNomvoi(String newNomvoi) {
		nomvoi = newNomvoi;
	}

	/**
	 * Setter de l'attribut noplan.
	 */
	public void setNoplan(String newNoplan) {
		noplan = newNoplan;
	}

	/**
	 * Setter de l'attribut orivoi.
	 */
	public void setOrivoi(String newOrivoi) {
		orivoi = newOrivoi;
	}

	/**
	 * Setter de l'attribut prevoi.
	 */
	public void setPrevoi(String newPrevoi) {
		prevoi = newPrevoi;
	}

	/**
	 * Setter de l'attribut provoi.
	 */
	public void setProvoi(String newProvoi) {
		provoi = newProvoi;
	}

	/**
	 * Setter de l'attribut replan.
	 */
	public void setReplan(String newReplan) {
		replan = newReplan;
	}

	/**
	 * Setter de l'attribut rivoli.
	 */
	public void setRivoli(String newRivoli) {
		rivoli = newRivoli;
	}

	/**
	 * Renvoie une chaine correspondant a la valeur de cet objet.
	 * 
	 * @return une representation sous forme de chaine du destinataire
	 */
	public String toString() {
		// Inserez ici le code pour finaliser le destinataire
		// Cette implementation transmet le message au super. Vous pouvez
		// remplacer ou completer le message.
		return super.toString();
	}
}
