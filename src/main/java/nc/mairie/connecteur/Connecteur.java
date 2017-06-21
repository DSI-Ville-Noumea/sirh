package nc.mairie.connecteur;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import nc.mairie.connecteur.metier.Spcopa;
import nc.mairie.connecteur.metier.Spmtsr;
import nc.mairie.connecteur.metier.Sppers;
import nc.mairie.connecteur.metier.Sppost;
import nc.mairie.connecteur.metier.Sprens;
import nc.mairie.enums.EnumCivilite;
import nc.mairie.enums.EnumStatutFichePoste;
import nc.mairie.enums.EnumTypeContact;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Contact;
import nc.mairie.metier.commun.Commune;
import nc.mairie.metier.commun.CommuneEtrangere;
import nc.mairie.metier.commun.CommunePostal;
import nc.mairie.metier.commun.Voie;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.referentiel.SituationFamiliale;
import nc.mairie.technique.Transaction;
import nc.mairie.utils.MessageUtils;

public class Connecteur {

	/**
	 * Connecteur modifiant l'instance de Mairie.SPMTSR correspondant a
	 * l'instance modifiee de SIRH.Affectation.
	 * 
	 * @param aTransaction
	 * @param affectation
	 * @param agent
	 * @param fichePosteBase
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean modifierSPMTSR(Transaction aTransaction, Affectation affectation, Integer nomatr, FichePoste fichePosteBase, Date ancienneDateDebut, boolean miseAjourSPPOST)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(aTransaction, fichePosteBase.getIdEntiteGeo().toString());
		// Recherche du spmtsr
		Spmtsr spmtsr = Spmtsr.chercherSpmtsrAvecNoMatrDateDeb(aTransaction, nomatr, sdf.format(ancienneDateDebut));
		spmtsr.setServi(fichePosteBase.getIdServi());
		spmtsr.setRefarr(affectation.getRefArreteAff() == null || affectation.getRefArreteAff().equals(Const.CHAINE_VIDE) ? Const.ZERO : affectation.getRefArreteAff());
		spmtsr.setDatdeb(sdf.format(affectation.getDateDebutAff()));
		spmtsr.setDatfin(affectation.getDateFinAff() == null ? Const.ZERO : sdf.format(affectation.getDateFinAff()));
		spmtsr.setCdecol(eg.getCdEcol());

		if (miseAjourSPPOST) {
			// on regarde si il y a une ligne dans SPPOST
			// si oui alors on met dans POMAT le matricule de l'agent
			Sppost sppost = Sppost.chercherSppost(aTransaction, fichePosteBase.getNumFp().substring(0, 4), fichePosteBase.getNumFp().substring(5, fichePosteBase.getNumFp().length()));
			if (sppost != null) {
				sppost.setPomatr(nomatr.toString());
				if (!sppost.modifierSppost(aTransaction)) {
					return false;
				}
			}
		}

		return spmtsr.modifierSpmtsr(aTransaction);
	}

	/**
	 * Connecteur creant l'instance de Mairie.SPMTSR correspondant a la nouvelle
	 * instance de SIRH.Affectation.
	 * 
	 * @param aTransaction
	 * @param affectation
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean creerSPMTSR(Transaction aTransaction, Affectation affectation, Agent agent, FichePoste fichePoste) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// on regarde si il y aune ligne avec datfin=0 dans spmtsr
		Spmtsr spmtsrSansDatFin = Spmtsr.chercherSpmtsrSansDatFin(aTransaction, agent.getNomatr());
		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();
		} else {
			// si oui on met dans datfin la date de debut du spmatsr suivant
			spmtsrSansDatFin.setDatfin(sdf.format(affectation.getDateDebutAff()));
			spmtsrSansDatFin.modifierSpmtsr(aTransaction);
			if (aTransaction.isErreur()) {
				return false;
			}
		}
		Spmtsr spmtsr = new Spmtsr();
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(aTransaction, fichePoste.getIdEntiteGeo().toString());
		spmtsr.setNomatr(agent.getNomatr().toString());
		spmtsr.setServi(fichePoste.getIdServi());
		spmtsr.setRefarr(affectation.getRefArreteAff() == null ? Const.ZERO : affectation.getRefArreteAff());
		spmtsr.setDatdeb(sdf.format(affectation.getDateDebutAff()));
		spmtsr.setDatfin(affectation.getDateFinAff() == null ? Const.ZERO : sdf.format(affectation.getDateFinAff()));
		spmtsr.setCdecol(eg.getCdEcol());
		// on regarde si il y a une ligne dans SPPOST
		// si oui alors on met dans POMAT le matricule de l'agent
		Sppost sppost = Sppost.chercherSppost(aTransaction, fichePoste.getNumFp().substring(0, 4), fichePoste.getNumFp().substring(5, fichePoste.getNumFp().length()));
		if (sppost != null) {
			sppost.setPomatr(agent.getNomatr().toString());
			if (!sppost.modifierSppost(aTransaction)) {
				return false;
			}
		}

		return spmtsr.creerSpmtsr(aTransaction);
	}

	/**
	 * Connecteur supprimant l'instance de Mairie.SPMTSR correspondant a
	 * l'instance supprimee de SIRH.Affectation.
	 * 
	 * @param aTransaction
	 * @param affectation
	 * @param agent
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean supprimerSPMTSR(Transaction aTransaction, Affectation affectation, Agent agent, FichePoste fichePoste) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// Recherche du spmtsr
		Spmtsr spmtsr = Spmtsr.chercherSpmtsrAvecNoMatrDateDeb(aTransaction, agent.getNomatr(), sdf.format(affectation.getDateDebutAff()));

		return spmtsr.supprimerSpmtsr(aTransaction);
	}

	/**
	 * Connecteur modifiant l'instance de Mairie.SPPOST correspondant a
	 * l'instance modifiee de SIRH.FichePoste.
	 * 
	 * @param aTransaction
	 * @param fichePoste
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean modifierSPPOST(Transaction aTransaction, FichePoste fichePoste, FichePoste ficheResponsable, ArrayList<Affectation> listeAffFP) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// Recherche du sppost
		Sppost sppost = Sppost.chercherSppost(aTransaction, fichePoste.getNumFp().substring(0, 4),
				fichePoste.getNumFp().substring(5, fichePoste.getNumFp().length()));
		//si on ne trouve pas dans SPPOST, alors on crée
		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();

			return creerSPPOST(aTransaction, fichePoste, ficheResponsable);
		}
		sppost.setPoanne(fichePoste.getNumFp().substring(0, 4));
		sppost.setPonuor(fichePoste.getNumFp().substring(5, fichePoste.getNumFp().length()));
		sppost.setPoserv(fichePoste.getIdServi() == null ? Const.CHAINE_VIDE : fichePoste.getIdServi());
		sppost.setPodval(fichePoste.getDateDebutValiditeFp() == null ? Const.ZERO : sdf.format(fichePoste.getDateDebutValiditeFp()).replace("-", Const.CHAINE_VIDE));
		sppost.setPodsup(fichePoste.getDateFinValiditeFp() == null ? Const.ZERO : sdf.format(fichePoste.getDateFinValiditeFp()).replace("-", Const.CHAINE_VIDE));
		sppost.setCodfon(fichePoste.getNfa());
		sppost.setNoacti(fichePoste.getOpi() == null ? Const.CHAINE_VIDE : fichePoste.getOpi());
		sppost.setReglem(fichePoste.getIdCdthorReg().toString());
		sppost.setBudget(fichePoste.getIdCdthorBud().toString());
		if (ficheResponsable != null) {
			sppost.setSoanne(ficheResponsable.getNumFp().substring(0, 4));
			sppost.setSonuor(ficheResponsable.getNumFp().substring(5, ficheResponsable.getNumFp().length()));
		}

		// si la fiche est inactive alors on l'inactive aussi dans SPPOST cf
		// redmine #11476
		if (fichePoste.getIdStatutFp().toString().equals(EnumStatutFichePoste.INACTIVE.getId())) {
			sppost.setCodact("I");
		} else {
			sppost.setCodact("A");
		}
		// si la fiche de poste est attribue a un agent alors on modifie des
		// donnees dans SPMTSR
		// utile seulement si le champ SERVICE de la fiche de poste devient
		// modifiable
		for (Affectation aff : listeAffFP) {
			modifierSPMTSR(aTransaction, aff, getNomatrWithIdAgent(aff.getIdAgent()), fichePoste, aff.getDateDebutAff(), false);
			sppost.setPomatr(getNomatrWithIdAgent(aff.getIdAgent()).toString());
		}

		return sppost.modifierSppost(aTransaction);
	}

	/**
	 * Connecteur creant l'instance de Mairie.SPPOST correspondant a la nouvelle
	 * instance de SIRH.FichePoste.
	 * 
	 * @param aTransaction
	 * @param fichePoste
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean creerSPPOST(Transaction aTransaction, FichePoste fichePoste, FichePoste fichePosteResponsable) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Sppost sppost = new Sppost();
		// sppost.set
		sppost.setPoanne(fichePoste.getNumFp().substring(0, 4));
		sppost.setPonuor(fichePoste.getNumFp().substring(5, fichePoste.getNumFp().length()));
		sppost.setPoserv(fichePoste.getIdServi() == null ? Const.CHAINE_VIDE : fichePoste.getIdServi());
		sppost.setPodval(fichePoste.getDateDebutValiditeFp() == null ? Const.ZERO : sdf.format(fichePoste.getDateDebutValiditeFp()).replace("-", Const.CHAINE_VIDE));
		sppost.setPodsup(fichePoste.getDateFinValiditeFp() == null ? Const.ZERO : sdf.format(fichePoste.getDateFinValiditeFp()).replace("-", Const.CHAINE_VIDE));
		sppost.setCodfon(fichePoste.getNfa());
		sppost.setNoacti(fichePoste.getOpi() == null ? Const.CHAINE_VIDE : fichePoste.getOpi());
		sppost.setReglem(fichePoste.getIdCdthorReg().toString());
		sppost.setBudget(fichePoste.getIdCdthorBud().toString());
		if (fichePosteResponsable != null) {
			sppost.setSoanne(fichePosteResponsable.getNumFp().substring(0, 4));
			sppost.setSonuor(fichePosteResponsable.getNumFp().substring(5, fichePosteResponsable.getNumFp().length()));
		}

		// si la fiche est inactive alors on l'inactive aussi dans SPPOST cf
		// redmine #11476
		if (fichePoste.getIdStatutFp().toString().equals(EnumStatutFichePoste.INACTIVE.getId())) {
			sppost.setCodact("I");
		} else {
			sppost.setCodact("A");
		}

		// champ non utilise mais mis a zero car valeur indefinies non admises
		sppost.setCdlieu(Const.ZERO);
		sppost.setCtitre(Const.ZERO);
		sppost.setPoetud(Const.ZERO);
		sppost.setCrespo(Const.ZERO);
		sppost.setPograd(Const.CHAINE_VIDE);
		sppost.setPomis1(Const.CHAINE_VIDE);
		sppost.setPomis2(Const.CHAINE_VIDE);
		sppost.setPomis3(Const.CHAINE_VIDE);
		sppost.setPomis4(Const.CHAINE_VIDE);
		sppost.setPomatr(Const.ZERO);
		sppost.setPocond(Const.CHAINE_VIDE);
		sppost.setPodval(Const.ZERO);
		sppost.setPoserp(Const.CHAINE_VIDE);
		sppost.setCommen(Const.CHAINE_VIDE);
		sppost.setPrimaire(Const.ZERO);

		return sppost.creerSppost(aTransaction);
	}

	public static boolean modifierSPPERS(Transaction aTransaction, Agent agent, ArrayList<Contact> lContact, SituationFamiliale situFam) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		// Recherche du sppers
		Sppers sppers = Sppers.chercherSppers(aTransaction, agent.getNomatr());
		CommunePostal commPostal = CommunePostal.chercherCommunePostal(aTransaction, agent.getCposVilleDom(), agent.getCcomVilleDom());
		if (agent.getIdVoie() != null) {
			Voie voie = Voie.chercherVoie(aTransaction, agent.getIdVoie().toString());
			sppers.setLidopu(voie.getLivcar().toUpperCase().length() > 15 ? voie.getLivcar().toUpperCase().substring(0, 15) : voie.getLivcar().toUpperCase());
			sppers.setLirue((voie.getPrevoi().toUpperCase() + " " + voie.getNomvoi().toUpperCase()).length() > 20 ? (voie.getPrevoi().toUpperCase() + " " + voie.getNomvoi().toUpperCase()).substring(
					0, 20) : voie.getPrevoi().toUpperCase() + " " + voie.getNomvoi().toUpperCase());
		}

		sppers.setBister(!agent.getNumRueBisTer().equals(Const.CHAINE_VIDE) && agent.getNumRueBisTer().length() > 1 ? agent.getNumRueBisTer().substring(0, 1) : agent.getNumRueBisTer());
		sppers.setBp(agent.getBp());
		sppers.setCdbanq(agent.getCdBanque() == null ? Const.ZERO : agent.getCdBanque().toString());
		sppers.setCddesi(agent.getCivilite().equals(EnumCivilite.M.getCode()) ? "MR" : agent.getCivilite().equals(EnumCivilite.MME.getCode()) ? "MME" : agent.getCivilite().equals(
				EnumCivilite.MLLE.getCode()) ? "MLE" : Const.CHAINE_VIDE);
		sppers.setCdelec(agent.getCodeElection() == null ? Const.CHAINE_VIDE : agent.getCodeElection());
		sppers.setCdfami(situFam.getCodeSituation());
		sppers.setCdguic(agent.getCdGuichet() == null ? Const.ZERO : agent.getCdGuichet().toString());
		sppers.setCdvill(agent.getCposVilleDom() == null ? Const.ZERO : agent.getCposVilleDom().toString());
		sppers.setClerib(agent.getRib() == null ? Const.ZERO : agent.getRib().toString());
		sppers.setDatdec(agent.getDateDeces() != null ? sdf.format(agent.getDateDeces()) : Const.ZERO);
		sppers.setDatemb(agent.getDatePremiereEmbauche() != null ? sdf.format(agent.getDatePremiereEmbauche()) : Const.ZERO);
		sppers.setDatnai(sdf.format(agent.getDateNaissance()));
		sppers.setIdindi(agent.getIdAgent().toString().substring(0, 1) + agent.getIdAgent().toString().substring(2, agent.getIdAgent().toString().length()));
		sppers.setLicare(agent.getAdresseComplementaire().length() > 60 ? agent.getAdresseComplementaire().substring(0, 60) : agent.getAdresseComplementaire());
		String livill = Normalizer.normalize(commPostal.getLibCodePostal().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
		sppers.setLivill(livill.length() > 30 ? livill.substring(0, 30) : livill);
		sppers.setNation(agent.getNationalite());
		sppers.setNocpte(agent.getNumCompte() == null ? Const.ZERO : agent.getNumCompte());
		sppers.setNom(Normalizer.normalize(agent.getNomUsage().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""));
		sppers.setNomatr(agent.getNomatr().toString());

		sppers.setNomjfi(Normalizer.normalize(agent.getNomPatronymique().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""));
		sppers.setNoport(agent.getNumRue().equals(Const.CHAINE_VIDE) ? Const.ZERO : agent.getNumRue().length() > 3 ? agent.getNumRue().substring(0, 3) : agent.getNumRue());
		sppers.setPrenom(Normalizer.normalize(agent.getPrenom().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""));
		sppers.setPrenus(Normalizer.normalize(agent.getPrenomUsage().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""));
		sppers.setSexe(agent.getSexe());

		if (agent.getCodePaysNaissEt() == null) {
			Commune communeFr = Commune.chercherCommune(aTransaction, agent.getCodeCommuneNaissFr());
			sppers.setLieuna(communeFr.getLibCommune().length() > 30 ? communeFr.getLibCommune().substring(0, 30) : communeFr.getLibCommune());
		} else {
			CommuneEtrangere communeEt = CommuneEtrangere.chercherCommuneEtrangere(aTransaction, agent.getCodePaysNaissEt().toString(), agent.getCodeCommuneNaissEt());
			sppers.setLieuna(communeEt.getLibCommuneEtrangere().length() > 30 ? communeEt.getLibCommuneEtrangere().substring(0, 30) : communeEt.getLibCommuneEtrangere());
		}

		sppers.setTeldom(Const.ZERO);
		sppers.setDiffu1(Const.CHAINE_VIDE);
		sppers.setMobpriv(Const.ZERO);
		sppers.setDiffu2(Const.CHAINE_VIDE);
		sppers.setMobprof(Const.ZERO);

		for (Contact c : lContact) {
			if (c.getIdTypeContact().toString().equals(EnumTypeContact.TEL.getCode().toString())) {
				sppers.setTeldom(c.getDescription());
				sppers.setDiffu1(c.isDiffusable() ? "O" : "N");
			}
			if (c.getIdTypeContact().toString().equals(EnumTypeContact.MOBILE.getCode().toString())) {
				sppers.setMobpriv(c.getDescription());
				sppers.setDiffu2(c.isDiffusable() ? "O" : "N");
			}
			if (c.getIdTypeContact().toString().equals(EnumTypeContact.MOBILE_PRO.getCode().toString())) {
				sppers.setMobprof(c.getDescription());
			}
		}
		if (!sppers.modifierSppers(aTransaction))
			return false;
		if (!modifierSPRENS(aTransaction, agent))
			return false;
		return true;
	}

	public static boolean creerSPPERS(Transaction aTransaction, Agent agent, ArrayList<Contact> lContact, SituationFamiliale situFam) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		CommunePostal commPostal = CommunePostal.chercherCommunePostal(aTransaction, agent.getCposVilleDom(), agent.getCcomVilleDom());

		Sppers sppers = new Sppers();
		if (agent.getIdVoie() != null) {
			Voie voie = Voie.chercherVoie(aTransaction, agent.getIdVoie().toString());
			sppers.setLidopu(voie.getLivcar().toUpperCase().length() > 15 ? voie.getLivcar().toUpperCase().substring(0, 15) : voie.getLivcar().toUpperCase());
			sppers.setLirue((voie.getPrevoi().toUpperCase() + " " + voie.getNomvoi().toUpperCase()).length() > 20 ? (voie.getPrevoi().toUpperCase() + " " + voie.getNomvoi().toUpperCase()).substring(
					0, 20) : voie.getPrevoi().toUpperCase() + " " + voie.getNomvoi().toUpperCase());
		} else {
			sppers.setLidopu(Const.CHAINE_VIDE);
			sppers.setLirue(Const.CHAINE_VIDE);
		}
		sppers.setBister(!agent.getNumRueBisTer().equals(Const.CHAINE_VIDE) && agent.getNumRueBisTer().length() > 1 ? agent.getNumRueBisTer().substring(0, 1) : agent.getNumRueBisTer());
		sppers.setBp(agent.getBp());
		sppers.setCdbanq(agent.getCdBanque() == null ? Const.ZERO : agent.getCdBanque().toString());
		sppers.setCddesi(agent.getCivilite().equals(EnumCivilite.M.getCode()) ? "MR" : agent.getCivilite().equals(EnumCivilite.MME.getCode()) ? "MME" : agent.getCivilite().equals(
				EnumCivilite.MLLE.getCode()) ? "MLE" : Const.CHAINE_VIDE);
		sppers.setCdelec(agent.getCodeElection() == null ? Const.CHAINE_VIDE : agent.getCodeElection());
		sppers.setCdetud(Const.ZERO);
		sppers.setCdfami(situFam.getCodeSituation());
		sppers.setCdguic(agent.getCdGuichet() == null ? Const.ZERO : agent.getCdGuichet().toString());
		sppers.setCdregl(Const.ZERO);
		sppers.setCdvill(agent.getCposVilleDom() == null ? Const.ZERO : agent.getCposVilleDom().toString());
		sppers.setClerib(agent.getRib() == null ? Const.ZERO : agent.getRib().toString());
		sppers.setDatdec(agent.getDateDeces() != null ? sdf.format(agent.getDateDeces()) : Const.ZERO);
		sppers.setDatemb(agent.getDatePremiereEmbauche() != null ? sdf.format(agent.getDatePremiereEmbauche()) : Const.ZERO);
		sppers.setDatnai(sdf.format(agent.getDateNaissance()));
		sppers.setDattit(Const.ZERO);
		sppers.setIdadrs(Const.ZERO);
		sppers.setIdcpte(Const.ZERO);
		sppers.setIdindi(agent.getIdAgent().toString().substring(0, 1) + agent.getIdAgent().toString().substring(2, agent.getIdAgent().toString().length()));
		sppers.setLicare(agent.getAdresseComplementaire().length() > 60 ? agent.getAdresseComplementaire().substring(0, 60) : agent.getAdresseComplementaire());
		String livill = Normalizer.normalize(commPostal.getLibCodePostal().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
		sppers.setLivill(livill.length() > 30 ? livill.substring(0, 30) : livill);
		sppers.setNation(agent.getNationalite());
		sppers.setNinsee(Const.ZERO);
		sppers.setNocpte(agent.getNumCompte() == null ? Const.ZERO : agent.getNumCompte());
		sppers.setNom(Normalizer.normalize(agent.getNomUsage().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""));
		sppers.setNomatr(agent.getNomatr().toString());
		sppers.setNomjfi(Normalizer.normalize(agent.getNomPatronymique().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""));
		sppers.setNoport(agent.getNumRue().equals(Const.CHAINE_VIDE) ? Const.ZERO : agent.getNumRue());
		sppers.setPrenom(Normalizer.normalize(agent.getPrenom().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""));
		sppers.setPrenus(Normalizer.normalize(agent.getPrenomUsage().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""));
		sppers.setSexe(agent.getSexe());

		if (agent.getCodePaysNaissEt() == null) {
			Commune communeFr = Commune.chercherCommune(aTransaction, agent.getCodeCommuneNaissFr());
			sppers.setLieuna(communeFr.getLibCommune().length() > 30 ? communeFr.getLibCommune().substring(0, 30) : communeFr.getLibCommune());
		} else {
			CommuneEtrangere communeEt = CommuneEtrangere.chercherCommuneEtrangere(aTransaction, agent.getCodePaysNaissEt().toString(), agent.getCodeCommuneNaissEt());
			sppers.setLieuna(communeEt.getLibCommuneEtrangere().length() > 30 ? communeEt.getLibCommuneEtrangere().substring(0, 30) : communeEt.getLibCommuneEtrangere());
		}
		sppers.setTeldom(Const.ZERO);
		sppers.setDiffu1(Const.CHAINE_VIDE);
		sppers.setMobpriv(Const.ZERO);
		sppers.setDiffu2(Const.CHAINE_VIDE);
		sppers.setMobprof(Const.ZERO);

		for (Contact c : lContact) {
			if (c.getIdTypeContact().toString().equals(EnumTypeContact.TEL.getCode().toString())) {
				sppers.setTeldom(c.getDescription());
				sppers.setDiffu1(c.isDiffusable() ? "O" : "N");
			}
			if (c.getIdTypeContact().toString().equals(EnumTypeContact.MOBILE.getCode().toString())) {
				sppers.setMobpriv(c.getDescription());
				sppers.setDiffu2(c.isDiffusable() ? "O" : "N");
			}
			if (c.getIdTypeContact().toString().equals(EnumTypeContact.MOBILE_PRO.getCode().toString())) {
				sppers.setMobprof(c.getDescription());
			}
		}
		if (!sppers.creerSppers(aTransaction))
			return false;
		if (!creerSPRENS(aTransaction, agent))
			return false;
		return true;
	}

	/**
	 * Connecteur modifiant l'instance de Mairie.SPRENS correspondant a
	 * l'instance modifiee de SIRH.Agent.
	 * 
	 * @param aTransaction
	 * @param agent
	 * @return boolean
	 * @throws Exception
	 */
	private static boolean modifierSPRENS(Transaction aTransaction, Agent agent) throws Exception {
		// Recherche du sprens
		Sprens sprens = Sprens.chercherSprens(aTransaction, agent.getNomatr());
		if (aTransaction.isErreur()) {
			return creerSPRENS(aTransaction, agent);
		} else {
			CommunePostal commune = CommunePostal.chercherCommunePostal(aTransaction, agent.getCposVilleDom(), agent.getCcomVilleDom());
			Spcopa spcopa = Spcopa.chercherSpcopa(aTransaction, Normalizer.normalize(commune.getLibCommune().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""));

			if (spcopa == null || spcopa.getCdcopa() == null || spcopa.getCdcopa().equals(Const.CHAINE_VIDE)) {
				aTransaction.traiterErreur();
				aTransaction.declarerErreur(MessageUtils.getMessage("ERR996"));
				return false;
			}
			sprens.setCdchab(spcopa.getCdcopa());
			return sprens.modifierSprens(aTransaction);
		}
	}

	/**
	 * Connecteur creant l'instance de Mairie.SPRENS correspondant a la nouvelle
	 * instance de SIRH.Agent.
	 * 
	 * @param aTransaction
	 * @param agent
	 * @return boolean
	 * @throws Exception
	 */
	private static boolean creerSPRENS(Transaction aTransaction, Agent agent) throws Exception {
		Sprens sprens = new Sprens();
		CommunePostal commune = CommunePostal.chercherCommunePostal(aTransaction, agent.getCposVilleDom(), agent.getCcomVilleDom());
		Spcopa spcopa = Spcopa.chercherSpcopa(aTransaction, Normalizer.normalize(commune.getLibCommune().toUpperCase(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""));
		sprens.setCdchab(spcopa.getCdcopa());
		sprens.setNomatr(agent.getNomatr().toString());
		// champ non utilise mais mis a zero car valeur indefinies non admises
		sprens.setNomp(Const.CHAINE_VIDE);
		sprens.setPrep(Const.CHAINE_VIDE);
		sprens.setNomm(Const.CHAINE_VIDE);
		sprens.setPrem(Const.CHAINE_VIDE);
		sprens.setNomc(Const.CHAINE_VIDE);
		sprens.setPrec(Const.CHAINE_VIDE);
		sprens.setMatc(Const.ZERO);
		sprens.setDatmar(Const.ZERO);
		sprens.setPrev(Const.CHAINE_VIDE);
		sprens.setObs1(Const.CHAINE_VIDE);
		sprens.setObs2(Const.CHAINE_VIDE);
		sprens.setNbscol(Const.ZERO);
		sprens.setCdcnai(Const.ZERO);
		sprens.setCodevp(Const.CHAINE_VIDE);
		sprens.setCodnai(Const.ZERO);
		sprens.setScodpa(Const.ZERO);

		return sprens.creerSprens(aTransaction);
	}

	public static boolean modifierSPPOST_Primaire(Transaction aTransaction, String numFP, boolean isPrimaire) throws Exception {
		// Recherche du sppost
		Sppost sppost = Sppost.chercherSppost(aTransaction, numFP.substring(0, 4), numFP.substring(5, numFP.length()));

		sppost.setPrimaire(isPrimaire ? "1" : "0");

		return sppost.modifierSppost(aTransaction);
	}

	public static Integer getNomatrWithIdAgent(Integer idAgent) {
		String id = idAgent.toString();
		String nomatr = id.substring(3, id.length());
		return Integer.valueOf(nomatr);
	}

	public static boolean supprimerSPPOST(Transaction aTransaction, FichePoste fichePoste) throws Exception {
		// Recherche du sppost
		Sppost sppost = Sppost.chercherSppost(aTransaction, fichePoste.getNumFp().substring(0, 4), fichePoste.getNumFp().substring(5, fichePoste.getNumFp().length()));

		return sppost.supprimerSppost(aTransaction);
	}
}
