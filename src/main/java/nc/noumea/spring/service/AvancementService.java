package nc.noumea.spring.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.AutreAdministrationAgent;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.agent.Prime;
import nc.mairie.metier.avancement.AvancementContractuels;
import nc.mairie.metier.avancement.AvancementConvCol;
import nc.mairie.metier.avancement.AvancementDetaches;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.Bareme;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.referentiel.AvisCap;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.AutreAdministrationAgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementContractuelsDao;
import nc.mairie.spring.dao.metier.avancement.AvancementConvColDao;
import nc.mairie.spring.dao.metier.avancement.AvancementDetachesDao;
import nc.mairie.spring.dao.metier.avancement.AvancementFonctionnairesDao;
import nc.mairie.spring.dao.metier.parametrage.MotifAvancementDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.referentiel.AvisCapDao;
import nc.mairie.spring.ws.ISirhWSConsumer;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;
import nc.mairie.utils.MessageUtils;
import nc.noumea.mairie.ads.dto.EntiteDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "avancementService")
public class AvancementService implements IAvancementService {

	@Autowired
	private ISirhWSConsumer sirhConsumer;

	@Override
	public AvancementConvCol calculAvancementConventionCollective(Transaction aTransaction, Agent a, String annee, IAdsService adsService, FichePosteDao ficheDao, AffectationDao affDao)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Recuperation de la carriere en cours
		Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(aTransaction, a);
		if (aTransaction.isErreur() || carr == null || carr.getDateDebut() == null) {
			aTransaction.traiterErreur();
			return null;
		}
		PositionAdmAgent paAgent = PositionAdmAgent.chercherPositionAdmAgentDateComprise(aTransaction, a.getNomatr(),
				Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE));
		if (aTransaction.isErreur() || paAgent == null || paAgent.getCdpadm() == null || paAgent.estPAInactive(aTransaction)) {
			aTransaction.traiterErreur();
			return null;
		}
		// L'agent doit avoir 3 ans d'anciennete minimum et 30 maximum pour
		// être eligible.
		// on cherche la carriere consecutive en tant que Convention
		// collective pour savoir si l'agent repond à la regle de
		// l'anciennete
		ArrayList<Carriere> listeCarriereConvCol = Carriere.listerCarriereAgentByType(aTransaction, a.getNomatr(), "CC");
		Carriere plusAnciennCarrConvColl = null;
		for (int i = 0; i < listeCarriereConvCol.size(); i++) {
			Carriere carrCours = listeCarriereConvCol.get(i);
			if (listeCarriereConvCol.size() > i + 1) {
				if (listeCarriereConvCol.get(i + 1) != null) {
					Carriere carrPrecedente = listeCarriereConvCol.get(i + 1);
					if (carrCours.getDateDebut().equals(carrPrecedente.getDateFin())) {
						plusAnciennCarrConvColl = carrPrecedente;
					} else {
						plusAnciennCarrConvColl = carrCours;
					}
				} else {
					plusAnciennCarrConvColl = carrCours;
				}
			} else {
				plusAnciennCarrConvColl = carrCours;
			}
		}

		if (plusAnciennCarrConvColl != null) {
			if (Services.compareDates(Services.ajouteAnnee(Services.formateDate(plusAnciennCarrConvColl.getDateDebut()), 3), "30/06/" + annee) <= 0) {

				aTransaction.traiterErreur();
				// Création de l'avancement
				AvancementConvCol avct = new AvancementConvCol();
				avct.setIdAgent(a.getIdAgent());
				avct.setAnnee(Integer.valueOf(annee));
				avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());

				// PA
				avct.setCodePa(paAgent.getCdpadm());

				avct.setDateArrete(sdf.parse("01/01/" + annee));
				avct.setNumArrete(annee);
				avct.setDateEmbauche(a.getDateDerniereEmbauche());

				Affectation aff = null;
				try {
					aff = affDao.chercherAffectationActiveAvecAgent(a.getIdAgent());
				} catch (Exception e2) {
					return null;
				}
				if (aff == null || aff.getIdFichePoste() == null) {
					return null;
				}
				FichePoste fp = ficheDao.chercherFichePoste(aff.getIdFichePoste());
				EntiteDto direction = adsService.getAffichageDirection(fp.getIdServiceAds());
				EntiteDto section = adsService.getAffichageSection(fp.getIdServiceAds());
				if (carr != null) {
					if (carr.getCodeGrade() != null && carr.getCodeGrade().length() != 0) {
						Grade grd = Grade.chercherGrade(aTransaction, carr.getCodeGrade());
						avct.setGrade(grd.getCodeGrade());
						avct.setLibGrade(grd.getLibGrade());
					}
				}
				avct.setDirectionService(direction == null ? Const.CHAINE_VIDE : direction.getSigle());
				avct.setSectionService(section == null ? Const.CHAINE_VIDE : section.getSigle());

				// On regarde si il y a deja une prime de saisie
				@SuppressWarnings("unused")
				Prime primeExist = Prime.chercherPrime1200ByRubrAndDate(aTransaction, a.getNomatr(), annee + "0101");
				if (aTransaction.isErreur()) {
					aTransaction.traiterErreur();
					avct.setCarriereSimu(null);
				} else {
					avct.setCarriereSimu("S");
				}

				// on cherche la derniere prime 1200
				Prime prime1200 = Prime.chercherDernierePrimeOuverteAvecRubrique(aTransaction, a.getNomatr(), "1200");
				if (aTransaction.isErreur()) {
					aTransaction.traiterErreur();
					// 19539 : on regarde si c'est un nouveau ou non
					List<Prime> listPrime1200 = Prime.listerPrime1200ByAgent(aTransaction, a.getNomatr());
					if (aTransaction.isErreur()) {
						aTransaction.traiterErreur();
					}
					if (listPrime1200.size() == 0) {
						avct.setMontantPrime1200("3");
					} else {
						avct.setMontantPrime1200(null);
						return avct;
					}
				} else {
					if (prime1200 != null && prime1200.getMtPri() != null) {
						if (Integer.valueOf(prime1200.getMtPri()) >= 30) {
							avct.setMontantPrime1200("30");
						} else {
							avct.setMontantPrime1200(String.valueOf(Integer.valueOf(prime1200.getMtPri()) + 1));
						}
					}
				}

				return avct;
			} else {
				return new AvancementConvCol();
			}
		} else {
			return null;
		}
	}

	@Override
	public ReturnMessageDto isAvancementConventionCollective(Transaction aTransaction, Agent agent) throws Exception {
		ReturnMessageDto result = new ReturnMessageDto();
		// il faut regarder si cet agent est de type Convention Collective
		Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(aTransaction, agent);
		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();
		}
		if (carr == null || carr.getCodeCategorie() == null || !carr.getCodeCategorie().equals("7")) {
			// "ERR181",
			// "Cet agent n'est pas de type @. Il ne peut pas être soumis a l'avancement @."
			result.getErrors().add(MessageUtils.getMessage("ERR181", "convention collective", "des conventions collectives"));
			return result;
		}

		return result;
	}

	@Override
	public List<Agent> listAgentAvctConvCol(Transaction aTransaction, String idServiceAds, String annee, IAdsService adsService, AgentDao agentDao) throws Exception {
		List<Agent> la = new ArrayList<Agent>();
		List<Integer> listeSousService = null;
		if (!idServiceAds.equals(Const.CHAINE_VIDE)) {
			listeSousService = adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(idServiceAds));
		}

		// Récupération des agents
		ArrayList<Carriere> listeCarriereActive = Carriere.listerCarriereActive(aTransaction, annee, "Convention collective");
		String listeNomatrAgent = Const.CHAINE_VIDE;
		for (Carriere carr : listeCarriereActive) {
			listeNomatrAgent += carr.getNoMatricule() + ",";
		}
		if (!listeNomatrAgent.equals(Const.CHAINE_VIDE)) {
			listeNomatrAgent = listeNomatrAgent.substring(0, listeNomatrAgent.length() - 1);
		}
		la = agentDao.listerAgentEligibleAvct(listeSousService, listeNomatrAgent);
		return la;
	}

	@Override
	public boolean creerAvancementConventionCollective(AvancementConvCol avct, AvancementConvColDao convColDa) {
		try {
			// avant de crer un avancement, il ne doit pas y en avoir
			@SuppressWarnings("unused")
			AvancementConvCol avctOlde = convColDa.chercherAvancementConvColAvecAnneeEtAgent(Integer.valueOf(avct.getAnnee()), avct.getIdAgent());
			return false;
		} catch (Exception e) {
			try {
				convColDa.creerAvancementConvCol(avct.getIdAgent(), avct.getAnnee(), avct.getEtat(), avct.getNumArrete(), avct.getDateArrete(), avct.getDateEmbauche(), avct.getGrade(),
						avct.getLibGrade(), avct.getDirectionService(), avct.getSectionService(), avct.getCarriereSimu(), avct.getMontantPrime1200(), avct.getCodePa());
				return true;
			} catch (Exception e2) {
				return false;
			}
		}
	}

	@Override
	public boolean isPrimeAvctConvColSimu(Transaction aTransaction, Agent agent, AvancementConvCol avct) throws Exception {
		// on regarde si la prime existe deja ou pas
		@SuppressWarnings("unused")
		Prime primeExist = Prime.chercherPrime1200ByRubrAndDate(aTransaction, agent.getNomatr(), avct.getAnnee() + "0101");
		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Prime getNewPrimeConventionCollective(Transaction aTransaction, Agent agent, AvancementConvCol avct) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Prime newPrime = null;
		// on recherche la derniere ligne de prime pour la rubrique
		// 1200(prime anciennete)
		Prime prime = Prime.chercherDernierePrimeOuverteAvecRubrique(aTransaction, agent.getNomatr(), "1200");
		// si il y en a une alors on la ferme et on en crée une
		// nouvelle
		if (!aTransaction.isErreur()) {
			if (!prime.getDatDeb().equals("01/01/" + avct.getAnnee())) {
				newPrime = new Prime();
				newPrime.setNoMatr(agent.getNomatr().toString());
				if ((Integer.valueOf(prime.getMtPri()) + 1) > 30) {
					newPrime.setMtPri("30");
				} else {
					newPrime.setMtPri(String.valueOf(Integer.valueOf(prime.getMtPri()) + 1));
				}
				newPrime.setDatDeb("01/01/" + avct.getAnnee());
				newPrime.setDatFin(Const.ZERO);
				newPrime.setRefArr(avct.getNumArrete());
				newPrime.setDateArrete(sdf.format(avct.getDateArrete()));
				newPrime.setNoRubr("1200");
			}
		} else {
			aTransaction.traiterErreur();

			newPrime = new Prime();
			newPrime.setNoMatr(agent.getNomatr().toString());
			newPrime.setMtPri("3");
			newPrime.setDatDeb("01/01/" + avct.getAnnee());
			newPrime.setDatFin(Const.ZERO);
			newPrime.setRefArr(avct.getNumArrete());
			newPrime.setDateArrete(sdf.format(avct.getDateArrete()));
			newPrime.setNoRubr("1200");
		}
		return newPrime;
	}

	@Override
	public ReturnMessageDto isAvancementContractuel(Transaction aTransaction, Agent agent) throws Exception {
		ReturnMessageDto result = new ReturnMessageDto();
		// il faut regarder si cet agent est de type contractuel
		Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(aTransaction, agent);
		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();
		}
		if (carr == null || carr.getCodeCategorie() == null || !carr.getCodeCategorie().equals("4")) {
			// "ERR181",
			// "Cet agent n'est pas de type @. Il ne peut pas être soumis a l'avancement @."
			result.getErrors().add(MessageUtils.getMessage("ERR181", "contractuel", "des contractuels"));
			return result;
		}

		return result;
	}

	@Override
	public List<Agent> listAgentAvctContractuel(Transaction aTransaction, String idServiceAds, String annee, IAdsService adsService, AgentDao agentDao) throws Exception {
		List<Agent> la = new ArrayList<Agent>();
		List<Integer> listeSousService = null;
		if (!idServiceAds.equals(Const.CHAINE_VIDE)) {
			listeSousService = adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(idServiceAds));
		}

		// Récupération des agents
		ArrayList<Carriere> listeCarriereActive = Carriere.listerCarriereActive(aTransaction, annee, "Contractuel");
		String listeNomatrAgent = Const.CHAINE_VIDE;
		for (Carriere carr : listeCarriereActive) {
			listeNomatrAgent += carr.getNoMatricule() + ",";
		}
		if (!listeNomatrAgent.equals(Const.CHAINE_VIDE)) {
			listeNomatrAgent = listeNomatrAgent.substring(0, listeNomatrAgent.length() - 1);
		}
		la = agentDao.listerAgentEligibleAvct(listeSousService, listeNomatrAgent);
		return la;
	}

	@Override
	public boolean creerAvancementContractuel(AvancementContractuels avct, AvancementContractuelsDao avancementContractuelsDao) {
		try {
			// avant de crer un avancement, il ne doit pas y en avoir
			@SuppressWarnings("unused")
			AvancementContractuels avctOlde = avancementContractuelsDao.chercherAvancementContractuelsAvecAnneeEtAgent(Integer.valueOf(avct.getAnnee()), avct.getIdAgent());
			return false;
		} catch (Exception e) {
			try {
				avancementContractuelsDao.creerAvancementContractuels(avct.getIdAgent(), avct.getDateEmbauche(), avct.getNumFp(), avct.getPa(), avct.getDateGrade(), avct.getDateProchainGrade(),
						avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getEtat(), avct.getDateArrete(), avct.getNumArrete(),
						avct.getCarriereSimu(), avct.getAnnee(), avct.getDirectionService(), avct.getSectionService(), avct.getCdcadr(), avct.getGrade(), avct.getIdNouvGrade());

				return true;
			} catch (Exception e2) {
				return false;
			}
		}
	}

	@Override
	public AvancementContractuels calculAvancementContractuel(Transaction aTransaction, Agent agent, String annee, IAdsService adsService, FichePosteDao fichePosteDao, AffectationDao affectationDao,
			boolean avctPrev) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
		// Recuperation de la carriere en cours
		Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(aTransaction, agent);
		if (aTransaction.isErreur() || carr == null || carr.getDateDebut() == null) {
			aTransaction.traiterErreur();
			return null;
		}
		PositionAdmAgent paAgent = PositionAdmAgent.chercherPositionAdmAgentDateComprise(aTransaction, agent.getNomatr(),
				Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE));
		if (aTransaction.isErreur() || paAgent == null || paAgent.getCdpadm() == null || paAgent.estPAInactive(aTransaction)) {
			aTransaction.traiterErreur();
			return null;
		}
		if (!avctPrev) {
			if (!(Services.compareDates(Services.ajouteAnnee(Services.formateDate(carr.getDateDebut()), 2), "31/12/" + annee) <= 0)) {
				// L'agent doit avoir la date début de la nouvelle carriere
				// comprise
				// dans l'année d'avancement
				return new AvancementContractuels();
			}
		}

		// Création de l'avancement
		AvancementContractuels avct = new AvancementContractuels();
		avct.setIdAgent(agent.getIdAgent());
		avct.setDateEmbauche(agent.getDateDerniereEmbauche());
		avct.setAnnee(Integer.valueOf(annee));
		avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());

		PositionAdm pa = PositionAdm.chercherPositionAdm(aTransaction, paAgent.getCdpadm());
		avct.setPa(pa.getLiPAdm());

		Affectation aff = null;
		try {
			aff = affectationDao.chercherAffectationActiveAvecAgent(agent.getIdAgent());
		} catch (Exception e2) {
			return null;
		}
		if (aff == null || aff.getIdFichePoste() == null) {
			return null;
		}
		FichePoste fp = fichePosteDao.chercherFichePoste(aff.getIdFichePoste());
		avct.setNumFp(fp.getNumFp());

		// IBA,INM,INA
		Bareme bareme = Bareme.chercherBareme(aTransaction, carr.getIban());
		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();
		}
		// si la carriere à un grade (contractualisation des conventions
		// collectives)
		if (carr.getCodeGrade() != null && !carr.getCodeGrade().equals(Const.CHAINE_VIDE)) {

			Grade gradeActuel = Grade.chercherGrade(aTransaction, carr.getCodeGrade());
			if (aTransaction.isErreur()) {
				aTransaction.traiterErreur();
				return avct;
			}

			Grade gradeSuivant = Grade.chercherGrade(aTransaction, gradeActuel.getCodeGradeSuivant());
			if (aTransaction.isErreur()) {
				aTransaction.traiterErreur();
				return avct;
			}
			avct.setIdNouvGrade(gradeSuivant.getCodeGrade() == null || gradeSuivant.getCodeGrade().length() == 0 ? null : gradeSuivant.getCodeGrade());
			avct.setCdcadr(gradeActuel.getCodeCadre());

			avct.setIban(carr.getIban());
			avct.setInm(Integer.valueOf(bareme.getInm()));
			avct.setIna(Integer.valueOf(bareme.getIna()));

			// on cherche le nouveau bareme
			if (gradeSuivant != null && gradeSuivant.getIban() != null) {
				Bareme nouvBareme = Bareme.chercherBareme(aTransaction, gradeSuivant.getIban());
				// on rempli les champs
				avct.setNouvIban(nouvBareme.getIban());
				avct.setNouvInm(Integer.valueOf(nouvBareme.getInm()));
				avct.setNouvIna(Integer.valueOf(nouvBareme.getIna()));
			}
		} else {
			// on recupere le grade du poste
			// on cherche a quelle categorie appartient l'agent
			// (A,B,A+..;)
			Grade g = Grade.chercherGrade(aTransaction, fp.getCodeGrade());
			GradeGenerique gg = GradeGenerique.chercherGradeGenerique(aTransaction, g.getCodeGradeGenerique());
			if (aTransaction.isErreur() || gg == null || gg.getNbPointsAvct() == null || gg.getNbPointsAvct().equals("0")) {
				aTransaction.traiterErreur();
				return new AvancementContractuels();
			}
			avct.setCdcadr(gg.getCodCadre());
			// on calcul le nouvel INM
			String nouvINM = String.valueOf(Integer.valueOf(bareme.getInm()) + Integer.valueOf(gg.getNbPointsAvct()));
			// avec ce nouvel INM on recupere l'iban et l'ina
			// correspondant
			Bareme nouvBareme = (Bareme) Bareme.listerBaremeByINM(aTransaction, nouvINM).get(0);
			// on rempli les champs
			avct.setNouvIban(nouvBareme.getIban());
			avct.setNouvInm(Integer.valueOf(nouvBareme.getInm()));
			avct.setNouvIna(Integer.valueOf(nouvBareme.getIna()));

		}
		avct.setDateProchainGrade(sdf.parse(Services.ajouteAnnee(Services.formateDate(carr.getDateDebut()), 2)));

		avct.setDateArrete(sdf.parse("01/01/" + annee));
		avct.setNumArrete(annee);

		EntiteDto direction = adsService.getAffichageDirection(fp.getIdServiceAds());
		EntiteDto section = adsService.getAffichageSection(fp.getIdServiceAds());

		avct.setDirectionService(direction == null ? Const.CHAINE_VIDE : direction.getSigle());
		avct.setSectionService(section == null ? Const.CHAINE_VIDE : section.getSigle());
		avct.setDateGrade(sdf.parse(carr.getDateDebut()));
		avct.setGrade(carr.getCodeGrade());
		avct.setIban(carr.getIban());
		avct.setInm(Integer.valueOf(bareme.getInm()));
		avct.setIna(Integer.valueOf(bareme.getIna()));

		// on regarde si l'agent a une carriere de simulation deja
		// saisie
		// autrement dis si la carriere actuelle a pour datfin 0
		if (carr.getDateFin() == null || carr.getDateFin().equals(Const.ZERO)) {
			avct.setCarriereSimu(null);
		} else {
			avct.setCarriereSimu("S");
		}
		return avct;
	}

	@Override
	public boolean isCarriereContractuelSimu(AvancementContractuels avct, Carriere carr) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
		// si la carriere est bien la derniere de la liste
		String dateDebAvct = sdf.format(avct.getDateProchainGrade());
		String dateDebCarr = carr.getDateDebut();
		if ((carr.getDateFin() == null || carr.getDateFin().equals("0")) && !dateDebAvct.equals(dateDebCarr)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Carriere getNewCarriereContractuel(Transaction aTransaction, Agent agent, AvancementContractuels avct, Carriere carr) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
		// on crée un nouvelle carriere
		Carriere nouvelleCarriere = new Carriere();
		nouvelleCarriere.setCodeCategorie(carr.getCodeCategorie());
		nouvelleCarriere.setReferenceArrete(avct.getNumArrete().equals(Const.CHAINE_VIDE) ? Const.ZERO : avct.getNumArrete());
		nouvelleCarriere.setDateArrete(avct.getDateArrete() == null ? Const.ZERO : sdf.format(avct.getDateArrete()));
		nouvelleCarriere.setDateDebut(sdf.format(avct.getDateProchainGrade()));
		nouvelleCarriere.setDateFin(Const.ZERO);
		nouvelleCarriere.setIban(avct.getNouvIban());
		// champ a remplir pour creer une carriere NB : on
		// reprend ceux de la carriere precedente
		nouvelleCarriere.setCodeBase(Const.CHAINE_VIDE);
		nouvelleCarriere.setCodeTypeEmploi(carr.getCodeTypeEmploi());
		nouvelleCarriere.setCodeGrade(avct.getIdNouvGrade());
		nouvelleCarriere.setCodeBaseHoraire2(carr.getCodeBaseHoraire2());
		nouvelleCarriere.setIdMotif(Const.ZERO);
		nouvelleCarriere.setModeReglement(carr.getModeReglement());
		nouvelleCarriere.setTypeContrat(carr.getTypeContrat());

		// RG_AG_CA_A03
		nouvelleCarriere.setNoMatricule(agent.getNomatr().toString());
		return nouvelleCarriere;
	}

	@Override
	public ReturnMessageDto isAvancementDetache(Transaction aTransaction, Agent agent) throws Exception {
		ReturnMessageDto result = new ReturnMessageDto();
		// il faut regarder si cet agent est de type Détaché
		Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(aTransaction, agent);
		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();
		}
		if (carr == null || carr.getCodeCategorie() == null
				|| (!carr.getCodeCategorie().equals("6") && !carr.getCodeCategorie().equals("16") && !carr.getCodeCategorie().equals("17") && !carr.getCodeCategorie().equals("19"))) {
			// "ERR181",
			// "Cet agent n'est pas de type @. Il ne peut pas être soumis a l'avancement @."
			result.getErrors().add(MessageUtils.getMessage("ERR181", "détaché", "des détachés"));
			return result;
		}

		return result;
	}

	@Override
	public List<Agent> listAgentAvctDetache(Transaction aTransaction, String idServiceAds, String annee, IAdsService adsService, AgentDao agentDao) throws Exception {
		List<Agent> la = new ArrayList<Agent>();
		List<Integer> listeSousService = null;
		if (!idServiceAds.equals(Const.CHAINE_VIDE)) {
			listeSousService = adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(idServiceAds));
		}

		// Récupération des agents
		ArrayList<Carriere> listeCarriereActive = Carriere.listerCarriereActive(aTransaction, annee, "Detache");
		String listeNomatrAgent = Const.CHAINE_VIDE;
		for (Carriere carr : listeCarriereActive) {
			listeNomatrAgent += carr.getNoMatricule() + ",";
		}
		if (!listeNomatrAgent.equals(Const.CHAINE_VIDE)) {
			listeNomatrAgent = listeNomatrAgent.substring(0, listeNomatrAgent.length() - 1);
		}
		la = agentDao.listerAgentEligibleAvct(listeSousService, listeNomatrAgent);
		return la;
	}

	@Override
	public AvancementDetaches calculAvancementDetache(Transaction aTransaction, Agent a, String annee, IAdsService adsService, FichePosteDao fichePosteDao, AffectationDao affectationDao,
			AutreAdministrationAgentDao autreAdministrationAgentDao, boolean avctPrev) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Recuperation de la carriere en cours
		Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(aTransaction, a);
		if (aTransaction.isErreur() || carr == null || carr.getDateDebut() == null) {
			aTransaction.traiterErreur();
			return null;
		}
		PositionAdmAgent paAgent = PositionAdmAgent.chercherPositionAdmAgentDateComprise(aTransaction, a.getNomatr(),
				Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE));
		if (aTransaction.isErreur() || paAgent == null || paAgent.getCdpadm() == null || paAgent.estPAInactive(aTransaction)) {
			aTransaction.traiterErreur();
			return null;
		}

		// on regarde si il y a d'autre carrieres avec le meme grade
		// si oui on prend la carriere plus lointaine
		ArrayList<Carriere> listeCarrMemeGrade = Carriere.listerCarriereAvecGradeEtStatut(aTransaction, a.getNomatr(), carr.getCodeGrade(), carr.getCodeCategorie());
		if (listeCarrMemeGrade != null && listeCarrMemeGrade.size() > 0) {
			carr = (Carriere) listeCarrMemeGrade.get(0);
		}
		Grade gradeActuel = Grade.chercherGrade(aTransaction, carr.getCodeGrade());
		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();
			return null;
		}
		// Si pas de grade suivant, agent non eligible
		if (gradeActuel.getCodeGradeSuivant() != null && gradeActuel.getCodeGradeSuivant().length() != 0) {
			// Création de l'avancement
			AvancementDetaches avct = new AvancementDetaches();
			avct.setIdAgent(a.getIdAgent());
			avct.setCodeCategorie(Integer.valueOf(carr.getCodeCategorie()));
			avct.setAnnee(Integer.valueOf(annee));
			avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());

			// PA
			avct.setCodePa(paAgent.getCdpadm());

			// on traite si l'agent est detaché ou non
			if (paAgent.getCdpadm().equals("54") || paAgent.getCdpadm().equals("56") || paAgent.getCdpadm().equals("57") || paAgent.getCdpadm().equals("58")) {
				avct.setAgentVdn(false);
			} else {
				avct.setAgentVdn(true);
			}
			// BM/ACC
			avct.setNouvBmAnnee(Integer.valueOf(carr.getBMAnnee()));
			avct.setNouvBmMois(Integer.valueOf(carr.getBMMois()));
			avct.setNouvBmJour(Integer.valueOf(carr.getBMJour()));
			avct.setNouvAccAnnee(Integer.valueOf(carr.getACCAnnee()));
			avct.setNouvAccMois(Integer.valueOf(carr.getACCMois()));
			avct.setNouvAccJour(Integer.valueOf(carr.getACCJour()));

			// calcul BM/ACC applicables
			int nbJoursBM = AvancementDetaches.calculJourBM(gradeActuel, carr);
			int nbJoursACC = AvancementDetaches.calculJourACC(gradeActuel, carr);

			int nbJoursBonusDepart = nbJoursBM + nbJoursACC;
			int nbJoursBonus = nbJoursBM + nbJoursACC;
			// Calcul date avancement au Grade actuel
			if (gradeActuel.getDureeMoy() != null && gradeActuel.getDureeMoy().length() != 0) {
				avct.setPeriodeStandard(Integer.valueOf(gradeActuel.getDureeMoy()));
				if (nbJoursBonusDepart > Integer.parseInt(gradeActuel.getDureeMoy()) * 30) {
					String dateAvct = carr.getDateDebut().substring(0, 6) + annee;
					avct.setDateAvctMoy(sdf.parse(dateAvct));
					nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
				} else {
					avct.setDateAvctMoy(AvancementDetaches.calculDateAvctMoy(gradeActuel, carr));
					nbJoursBonus = 0;
				}
			}

			if (avct.getDateAvctMoy() == null) {
				return null;
			}
			if (!avctPrev) {
				// si la date avct moy (année ) sup a l'année choisie pour
				// la simu alors on sort l'agent du calcul
				Integer anneeNumerique = avct.getAnnee();
				Integer anneeDateAvctMoyNumerique = Integer.valueOf(sdf.format(avct.getDateAvctMoy()).substring(6, sdf.format(avct.getDateAvctMoy()).length()));
				if (anneeDateAvctMoyNumerique > anneeNumerique) {
					return null;
				}
			}

			// Calcul du grade suivant (BM/ACC)
			Grade gradeSuivant = Grade.chercherGrade(aTransaction, gradeActuel.getCodeGradeSuivant());
			if (gradeSuivant.getDureeMoy() != null && gradeSuivant.getDureeMoy().length() > 0 && Services.estNumerique(gradeSuivant.getDureeMoy())) {
				boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
				while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0 && gradeSuivant.getDureeMoy() != null
						&& gradeSuivant.getDureeMoy().length() > 0) {
					nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMoy()) * 30;
					gradeSuivant = Grade.chercherGrade(aTransaction, gradeSuivant.getCodeGradeSuivant());
					isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
				}
			}

			int nbJoursRestantsBM = nbJoursBonus > nbJoursACC ? nbJoursBonus - nbJoursACC : Integer.parseInt(Const.ZERO);
			int nbJoursRestantsACC = nbJoursBonus - nbJoursRestantsBM;

			avct.setNouvBmAnnee(nbJoursRestantsBM / 365);
			avct.setNouvBmMois((nbJoursRestantsBM % 365) / 30);
			avct.setNouvBmJour((nbJoursRestantsBM % 365) % 30);

			avct.setNouvAccAnnee(nbJoursRestantsACC / 365);
			avct.setNouvAccMois((nbJoursRestantsACC % 365) / 30);
			avct.setNouvAccJour((nbJoursRestantsACC % 365) % 30);

			avct.setIdNouvGrade(gradeSuivant.getCodeGrade() == null || gradeSuivant.getCodeGrade().length() == 0 ? null : gradeSuivant.getCodeGrade());
			avct.setCdcadr(gradeActuel.getCodeCadre());

			// IBA,INM,INA
			Bareme bareme = Bareme.chercherBareme(aTransaction, carr.getIban());
			if (aTransaction.isErreur()) {
				aTransaction.traiterErreur();
			}
			avct.setIban(carr.getIban());
			avct.setInm(Integer.valueOf(bareme.getInm()));
			avct.setIna(Integer.valueOf(bareme.getIna()));

			// on cherche le nouveau bareme
			if (gradeSuivant != null && gradeSuivant.getIban() != null) {
				Bareme nouvBareme = Bareme.chercherBareme(aTransaction, gradeSuivant.getIban());
				// on rempli les champs
				avct.setNouvIban(nouvBareme.getIban());
				avct.setNouvInm(Integer.valueOf(nouvBareme.getInm()));
				avct.setNouvIna(Integer.valueOf(nouvBareme.getIna()));
			}

			// on regarde si l'agent est AFFECTE dans une autre
			// administration
			if (paAgent.getCdpadm().equals("54") || paAgent.getCdpadm().equals("56") || paAgent.getCdpadm().equals("57") || paAgent.getCdpadm().equals("58")) {
				avct.setDirectionService(null);
				avct.setSectionService(null);
				// alors on va chercher l'autre administration de
				// l'agent
				try {
					AutreAdministrationAgent autreAdminAgent = autreAdministrationAgentDao.chercherAutreAdministrationAgentActive(a.getIdAgent());
					if (autreAdminAgent != null && autreAdminAgent.getIdAutreAdmin() != null) {
						avct.setDirectionService(autreAdminAgent.getIdAutreAdmin().toString());
					}
				} catch (Exception e2) {

				}
			} else {

				// on recupere le grade du poste
				Affectation aff = null;
				try {
					aff = affectationDao.chercherAffectationActiveAvecAgent(a.getIdAgent());
				} catch (Exception e2) {
					return null;
				}
				if (aff == null || aff.getIdFichePoste() == null) {
					return null;
				}
				FichePoste fp = fichePosteDao.chercherFichePoste(aff.getIdFichePoste());
				EntiteDto direction = adsService.getAffichageDirection(fp.getIdServiceAds());
				EntiteDto section = adsService.getAffichageSection(fp.getIdServiceAds());
				avct.setDirectionService(direction == null ? Const.CHAINE_VIDE : direction.getSigle());
				avct.setSectionService(section == null ? Const.CHAINE_VIDE : section.getSigle());
			}

			if (carr != null) {
				if (carr.getCodeGrade() != null && carr.getCodeGrade().length() != 0) {
					Grade grd = Grade.chercherGrade(aTransaction, carr.getCodeGrade());
					avct.setGrade(grd.getCodeGrade());

					// on prend l'id motif de la colonne CDTAVA du grade
					// si CDTAVA correspond a AVANCEMENT DIFF alors on
					// calcul les 3 dates sinon on calcul juste la date
					// moyenne
					if (grd.getCodeTava() != null && !grd.getCodeTava().equals(Const.CHAINE_VIDE)) {
						avct.setIdMotifAvct(Integer.valueOf(grd.getCodeTava()));
					} else {
						avct.setIdMotifAvct(null);
					}

					if (grd.getCodeGradeGenerique() != null) {
						// on cherche le grade generique pour trouver la
						// filiere
						GradeGenerique ggCarr = GradeGenerique.chercherGradeGenerique(aTransaction, grd.getCodeGradeGenerique());
						if (aTransaction.isErreur())
							aTransaction.traiterErreur();

						if (ggCarr != null && ggCarr.getCdfili() != null) {
							FiliereGrade fil = FiliereGrade.chercherFiliereGrade(aTransaction, ggCarr.getCdfili());
							if (aTransaction.isErreur())
								aTransaction.traiterErreur();
							avct.setFiliere(fil == null ? null : fil.getLibFiliere());
						}
					}
				}
			}
			avct.setDateGrade(sdf.parse(carr.getDateDebut()));
			avct.setBmAnnee(Integer.valueOf(carr.getBMAnnee()));
			avct.setBmMois(Integer.valueOf(carr.getBMMois()));
			avct.setBmJour(Integer.valueOf(carr.getBMJour()));
			avct.setAccAnnee(Integer.valueOf(carr.getACCAnnee()));
			avct.setAccMois(Integer.valueOf(carr.getACCMois()));
			avct.setAccJour(Integer.valueOf(carr.getACCJour()));

			// on regarde si l'agent a une carriere de simulation deja
			// saisie
			// autrement dis si la carriere actuelle a pour datfin 0
			if (carr.getDateFin() == null || carr.getDateFin().equals(Const.ZERO)) {
				avct.setCarriereSimu(null);
			} else {
				avct.setCarriereSimu("S");
			}

			avct.setDateVerifSef(null);
			avct.setDateVerifSgc(null);
			return avct;
		} else {
			return new AvancementDetaches();
		}

	}

	@Override
	public boolean creerAvancementDetache(AvancementDetaches avct, AvancementDetachesDao avancementDetachesDao) {
		try {
			// avant de crer un avancement, il ne doit pas y en avoir
			@SuppressWarnings("unused")
			AvancementDetaches avctOlde = avancementDetachesDao.chercherAvancementAvecAnneeEtAgent(Integer.valueOf(avct.getAnnee()), avct.getIdAgent());
			return false;
		} catch (Exception e) {
			try {
				avancementDetachesDao.creerAvancement(avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(), avct.getFiliere(), avct.getGrade(),
						avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(),
						avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(), avct.getInm(),
						avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMoy(), avct.getNumArrete(),
						avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(),
						avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getObservationArr(),
						avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getCodePa());

				return true;
			} catch (Exception e2) {
				return false;
			}
		}
	}

	@Override
	public boolean isCarriereDetacheSimu(AvancementDetaches avct, Carriere carr) {
		SimpleDateFormat sdfFormatDate = new SimpleDateFormat("dd/MM/yyyy");
		if ((carr.getDateFin() == null || carr.getDateFin().equals("0")) && !sdfFormatDate.format(avct.getDateAvctMoy()).equals(carr.getDateDebut())) {
			return false;
		} else {
			return true;
		}
	}

	public void calculAccBmDetache(Transaction aTransaction, AvancementDetaches avct, Carriere ancienneCarriere, Carriere nouvelleCarriere, String libCourtAvisCap) throws Exception {
		SimpleDateFormat sdfFormatDate = new SimpleDateFormat("dd/MM/yyyy");
		Grade gradeActuel = Grade.chercherGrade(aTransaction, ancienneCarriere.getCodeGrade());
		// calcul BM/ACC applicables
		int nbJoursBM = AvancementDetaches.calculJourBM(gradeActuel, ancienneCarriere);
		int nbJoursACC = AvancementDetaches.calculJourACC(gradeActuel, ancienneCarriere);

		int nbJoursBonus = nbJoursBM + nbJoursACC;

		// Calcul date avancement au Grade actuel
		if (libCourtAvisCap.equals("Moy")) {
			avct.setPeriodeStandard(Integer.valueOf(gradeActuel.getDureeMoy()));
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMoy()) * 30) {
				String dateAvct = ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee();
				avct.setDateAvctMoy(sdfFormatDate.parse(dateAvct));
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
			} else {
				avct.setDateAvctMoy(AvancementDetaches.calculDateAvctMoy(gradeActuel, ancienneCarriere));
				nbJoursBonus = 0;
			}
		}

		// Calcul du grade suivant (BM/ACC)
		Grade gradeSuivant = Grade.chercherGrade(aTransaction, gradeActuel.getCodeGradeSuivant());
		if (libCourtAvisCap.equals("Moy")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0 && gradeSuivant.getDureeMoy() != null
					&& gradeSuivant.getDureeMoy().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMoy()) * 30;
				gradeSuivant = Grade.chercherGrade(aTransaction, gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
			}
		}

		int nbJoursRestantsBM = nbJoursBonus > nbJoursACC ? nbJoursBonus - nbJoursACC : Integer.parseInt(Const.ZERO);
		int nbJoursRestantsACC = nbJoursBonus - nbJoursRestantsBM;

		// on met a jour les champs de l'avancement pour affichage tableau
		avct.setNouvBmAnnee(nbJoursRestantsBM / 365);
		avct.setNouvBmMois((nbJoursRestantsBM % 365) / 30);
		avct.setNouvBmJour((nbJoursRestantsBM % 365) % 30);

		avct.setNouvAccAnnee(nbJoursRestantsACC / 365);
		avct.setNouvAccMois((nbJoursRestantsACC % 365) / 30);
		avct.setNouvAccJour((nbJoursRestantsACC % 365) % 30);

		avct.setIdNouvGrade(gradeSuivant.getCodeGrade() == null || gradeSuivant.getCodeGrade().length() == 0 ? null : gradeSuivant.getCodeGrade());

		// on met a jour les champs pour la creation de la carriere
		nouvelleCarriere.setCodeGrade(avct.getIdNouvGrade());
		nouvelleCarriere.setACCAnnee(avct.getNouvAccAnnee().toString());
		nouvelleCarriere.setACCMois(avct.getNouvAccMois().toString());
		nouvelleCarriere.setACCJour(avct.getNouvAccJour().toString());
		nouvelleCarriere.setBMAnnee(avct.getNouvBmAnnee().toString());
		nouvelleCarriere.setBMMois(avct.getNouvBmMois().toString());
		nouvelleCarriere.setBMJour(avct.getNouvBmJour().toString());
	}

	@Override
	public Carriere getNewCarriereDetache(Transaction aTransaction, Agent agent, AvancementDetaches avct, Carriere carr, String dateAvct) throws Exception {
		SimpleDateFormat sdfFormatDate = new SimpleDateFormat("dd/MM/yyyy");
		Carriere nouvelleCarriere = new Carriere();
		nouvelleCarriere.setCodeCategorie(carr.getCodeCategorie());
		nouvelleCarriere.setReferenceArrete(avct.getNumArrete().equals(Const.CHAINE_VIDE) ? Const.ZERO : avct.getNumArrete());
		nouvelleCarriere.setDateArrete(avct.getDateArrete() == null ? Const.ZERO : sdfFormatDate.format(avct.getDateArrete()));
		nouvelleCarriere.setDateDebut(dateAvct);
		nouvelleCarriere.setDateFin(Const.ZERO);
		// on calcul Grade - ACC/BM en fonction de l'avis CAP
		// il est different du resultat affiché dans le tableau
		// si AVIS_CAP != MOY
		// car pour la simulation on prenait comme ref de calcul
		// la duree MOY
		calculAccBmDetache(aTransaction, avct, carr, nouvelleCarriere, "Moy");

		// on recupere iban du grade
		Grade gradeSuivant = Grade.chercherGrade(aTransaction, avct.getIdNouvGrade());
		nouvelleCarriere.setIban(Services.lpad(gradeSuivant.getIban(), 7, "0"));

		// champ a remplir pour creer une carriere NB : on
		// reprend ceux de la carriere precedente
		nouvelleCarriere.setCodeBase(Const.CHAINE_VIDE);
		nouvelleCarriere.setCodeTypeEmploi(carr.getCodeTypeEmploi());
		nouvelleCarriere.setCodeBaseHoraire2(carr.getCodeBaseHoraire2());
		nouvelleCarriere.setIdMotif(Const.ZERO);
		nouvelleCarriere.setModeReglement(carr.getModeReglement());
		nouvelleCarriere.setTypeContrat(carr.getTypeContrat());

		// RG_AG_CA_A03
		nouvelleCarriere.setNoMatricule(agent.getNomatr().toString());
		return nouvelleCarriere;
	}

	@Override
	public ReturnMessageDto isAvancementFonctionnaire(Transaction aTransaction, Agent agent) throws Exception {
		ReturnMessageDto result = new ReturnMessageDto();
		// il faut regarder si cet agent est de type Détaché
		Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(aTransaction, agent);
		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();
		}
		if (carr == null || carr.getCodeCategorie() == null
				|| (!carr.getCodeCategorie().equals("1") && !carr.getCodeCategorie().equals("2") && !carr.getCodeCategorie().equals("18") && !carr.getCodeCategorie().equals("20"))) {
			// "ERR181",
			// "Cet agent n'est pas de type @. Il ne peut pas être soumis a l'avancement @."
			result.getErrors().add(MessageUtils.getMessage("ERR181", "fonctionnaire", "des fonctionnaires"));
			return result;
		}

		return result;
	}

	@Override
	public List<Agent> listAgentAvctFonctionnaire(Transaction aTransaction, String idServiceAds, String annee, IAdsService adsService, AgentDao agentDao) throws Exception {
		List<Agent> la = new ArrayList<Agent>();
		// Récupération des agents
		// on recupere les sous-service du service selectionne

		List<Integer> listeSousService = null;
		if (!idServiceAds.equals(Const.CHAINE_VIDE)) {
			listeSousService = adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(idServiceAds));
		}

		// Récupération des agents
		ArrayList<Carriere> listeCarriereActive = Carriere.listerCarriereActive(aTransaction, annee, "Fonctionnaire");
		String listeNomatrAgent = Const.CHAINE_VIDE;
		for (Carriere carr : listeCarriereActive) {
			listeNomatrAgent += carr.getNoMatricule() + ",";
		}
		if (!listeNomatrAgent.equals(Const.CHAINE_VIDE)) {
			listeNomatrAgent = listeNomatrAgent.substring(0, listeNomatrAgent.length() - 1);
		}
		la = agentDao.listerAgentEligibleAvct(listeSousService, listeNomatrAgent);
		return la;
	}

	@Override
	public boolean creerAvancementFonctionnaire(AvancementFonctionnaires avct, AvancementFonctionnairesDao avancementFonctionnairesDao) {
		try {
			// avant de crer un avancement, il ne doit pas y en avoir
			@SuppressWarnings("unused")
			AvancementFonctionnaires avctOlde = avancementFonctionnairesDao.chercherAvancementFonctionnaireAvecAnneeEtAgent(Integer.valueOf(avct.getAnnee()), avct.getIdAgent());
			return false;
		} catch (Exception e) {
			try {
				avancementFonctionnairesDao.creerAvancement(avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(), avct.getFiliere(),
						avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(),
						avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(),
						avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(),
						avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(),
						avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getOrdreMerite(),
						avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(),
						avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(),
						avct.getCodePa(), avct.isAutre());
				return true;
			} catch (Exception e2) {
				return false;
			}
		}
	}

	@Override
	public boolean isCarriereFonctionnaireSimu(Carriere carr) {
		if (carr.getDateFin() == null || carr.getDateFin().equals("0")) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Carriere getNewCarriereFonctionnaire(Transaction aTransaction, Agent agent, AvancementFonctionnaires avct, Carriere carr, AvancementFonctionnairesDao avancementFonctionnairesDao,
			String idAvisEmp, Date dateAvctFinale) throws Exception {
		SimpleDateFormat sdfFormatDate = new SimpleDateFormat("dd/MM/yyyy");
		Carriere nouvelleCarriere = new Carriere();

		// on calcul Grade - ACC/BM en fonction de l'avis CAP
		// il est different du resultat affiché dans le tableau
		// si AVIS_CAP != MOY
		// car pour la simulation on prenait comme ref de calcul
		// la duree MOY
		if ((carr.getCodeCategorie().equals("2") || carr.getCodeCategorie().equals("18")) && avct.getPeriodeStandard() == 12) {
			nouvelleCarriere.setCodeGrade(avct.getIdNouvGrade());
			nouvelleCarriere.setACCAnnee(avct.getNouvAccAnnee().toString());
			nouvelleCarriere.setACCMois(avct.getNouvAccMois().toString());
			nouvelleCarriere.setACCJour(avct.getNouvAccJour().toString());
			nouvelleCarriere.setBMAnnee(avct.getNouvBmAnnee().toString());
			nouvelleCarriere.setBMMois(avct.getNouvBmMois().toString());
			nouvelleCarriere.setBMJour(avct.getNouvBmJour().toString());
			avancementFonctionnairesDao.modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
					avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(),
					avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(),
					avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(),
					avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(),
					avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getOrdreMerite(), avct.getAvisShd(),
					avct.getIdAvisArr(), avct.getIdAvisEmp(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(), avct.getObservationArr(),
					avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(), avct.getCodePa(), avct.isAutre());
		} else {
			calculAccBmFonctionnaire(aTransaction, avct, carr, nouvelleCarriere, idAvisEmp, avancementFonctionnairesDao);
		}
		if (avct.getCodeCategorie() == 2) {
			nouvelleCarriere.setCodeCategorie("1");
		} else if (avct.getCodeCategorie() == 18) {
			nouvelleCarriere.setCodeCategorie("20");
		} else {
			nouvelleCarriere.setCodeCategorie(carr.getCodeCategorie());
		}

		nouvelleCarriere.setReferenceArrete(avct.getNumArrete() == null || avct.getNumArrete().equals(Const.CHAINE_VIDE) ? Const.ZERO : avct.getNumArrete());
		nouvelleCarriere.setDateArrete(avct.getDateArrete() == null ? Const.ZERO : sdfFormatDate.format(avct.getDateArrete()));
		nouvelleCarriere.setDateDebut(sdfFormatDate.format(dateAvctFinale));
		nouvelleCarriere.setDateFin(Const.ZERO);

		// on recupere iban du grade
		Grade gradeSuivant = Grade.chercherGrade(aTransaction, avct.getIdNouvGrade());
		if (Services.estNumerique(gradeSuivant.getIban())) {
			nouvelleCarriere.setIban(Services.lpad(gradeSuivant.getIban(), 7, "0"));
		} else {
			nouvelleCarriere.setIban(gradeSuivant.getIban());
		}

		nouvelleCarriere.setIdMotif(Const.ZERO);

		// champ a remplir pour creer une carriere NB : on
		// reprend ceux de la carriere precedente
		nouvelleCarriere.setCodeBase(Const.CHAINE_VIDE);
		nouvelleCarriere.setCodeTypeEmploi(carr.getCodeTypeEmploi());
		nouvelleCarriere.setCodeBaseHoraire2(carr.getCodeBaseHoraire2());
		nouvelleCarriere.setModeReglement(carr.getModeReglement());
		nouvelleCarriere.setTypeContrat(carr.getTypeContrat());

		// RG_AG_CA_A03
		nouvelleCarriere.setNoMatricule(agent.getNomatr().toString());
		return nouvelleCarriere;
	}

	public void calculAccBmFonctionnaire(Transaction aTransaction, AvancementFonctionnaires avct, Carriere ancienneCarriere, Carriere nouvelleCarriere, String libCourtAvisCap,
			AvancementFonctionnairesDao avancementFonctionnaireDao) throws Exception {
		SimpleDateFormat sdfFormatDate = new SimpleDateFormat("dd/MM/yyyy");

		Grade gradeActuel = Grade.chercherGrade(aTransaction, ancienneCarriere.getCodeGrade());
		// calcul BM/ACC applicables
		int nbJoursBM = AvancementFonctionnaires.calculJourBM(gradeActuel, ancienneCarriere);
		int nbJoursACC = AvancementFonctionnaires.calculJourACC(gradeActuel, ancienneCarriere);

		int nbJoursBonus = nbJoursBM + nbJoursACC;

		// Calcul date avancement au Grade actuel
		if (libCourtAvisCap.equals("MIN")) {
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMin()) * 30) {
				String date = ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee();
				avct.setDateAvctMini(sdfFormatDate.parse(date));
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMin()) * 30;
			} else {
				avct.setDateAvctMini(AvancementFonctionnaires.calculDateAvctMini(gradeActuel, ancienneCarriere));
				nbJoursBonus = 0;
			}
		} else if (libCourtAvisCap.equals("MOY") || libCourtAvisCap.equals("FAV")) {
			avct.setPeriodeStandard(Integer.valueOf(gradeActuel.getDureeMoy()));
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMoy()) * 30) {
				String date = ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee();
				avct.setDateAvctMoy(sdfFormatDate.parse(date));
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
			} else {
				avct.setDateAvctMoy(AvancementFonctionnaires.calculDateAvctMoy(gradeActuel, ancienneCarriere));
				nbJoursBonus = 0;
			}
		} else if (libCourtAvisCap.equals("MAX")) {
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMax()) * 30) {
				String date = ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee();
				avct.setDateAvctMaxi(sdfFormatDate.parse(date));
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMax()) * 30;
			} else {
				avct.setDateAvctMaxi(AvancementFonctionnaires.calculDateAvctMaxi(gradeActuel, ancienneCarriere));
				nbJoursBonus = 0;
			}
		}

		// Calcul du grade suivant (BM/ACC)
		Grade gradeSuivant = Grade.chercherGrade(aTransaction, gradeActuel.getCodeGradeSuivant());
		if (libCourtAvisCap.equals("MIN")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMin()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0 && gradeSuivant.getDureeMin() != null
					&& gradeSuivant.getDureeMin().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMin()) * 30;
				gradeSuivant = Grade.chercherGrade(aTransaction, gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMin()) * 30);
			}
		} else if (libCourtAvisCap.equals("MOY") || libCourtAvisCap.equals("FAV")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0 && gradeSuivant.getDureeMoy() != null
					&& gradeSuivant.getDureeMoy().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMoy()) * 30;
				gradeSuivant = Grade.chercherGrade(aTransaction, gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
			}
		} else if (libCourtAvisCap.equals("MAX")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMax()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0 && gradeSuivant.getDureeMax() != null
					&& gradeSuivant.getDureeMax().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMax()) * 30;
				gradeSuivant = Grade.chercherGrade(aTransaction, gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMax()) * 30);
			}
		}

		int nbJoursRestantsBM = nbJoursBonus > nbJoursACC ? nbJoursBonus - nbJoursACC : Integer.parseInt(Const.ZERO);
		int nbJoursRestantsACC = nbJoursBonus - nbJoursRestantsBM;

		// on met a jour les champs de l'avancement pour affichage tableau
		avct.setNouvBmAnnee(nbJoursRestantsBM / 365);
		avct.setNouvBmMois((nbJoursRestantsBM % 365) / 30);
		avct.setNouvBmJour((nbJoursRestantsBM % 365) % 30);

		avct.setNouvAccAnnee(nbJoursRestantsACC / 365);
		avct.setNouvAccMois((nbJoursRestantsACC % 365) / 30);
		avct.setNouvAccJour((nbJoursRestantsACC % 365) % 30);

		avct.setIdNouvGrade(gradeSuivant.getCodeGrade() == null || gradeSuivant.getCodeGrade().length() == 0 ? null : gradeSuivant.getCodeGrade());

		avancementFonctionnaireDao.modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
				avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(),
				avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(),
				avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(),
				avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(),
				avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getOrdreMerite(), avct.getAvisShd(),
				avct.getIdAvisArr(), avct.getIdAvisEmp(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(), avct.getObservationArr(),
				avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(), avct.getCodePa(), avct.isAutre());

		// on met a jour les champs pour la creation de la carriere
		if (nouvelleCarriere != null) {
			nouvelleCarriere.setCodeGrade(avct.getIdNouvGrade());
			nouvelleCarriere.setACCAnnee(avct.getNouvAccAnnee().toString());
			nouvelleCarriere.setACCMois(avct.getNouvAccMois().toString());
			nouvelleCarriere.setACCJour(avct.getNouvAccJour().toString());
			nouvelleCarriere.setBMAnnee(avct.getNouvBmAnnee().toString());
			nouvelleCarriere.setBMMois(avct.getNouvBmMois().toString());
			nouvelleCarriere.setBMJour(avct.getNouvBmJour().toString());
		}
	}

	@Override
	public AvancementFonctionnaires calculAvancementFonctionnaire(Transaction aTransaction, Agent a, String annee, IAdsService adsService, FichePosteDao fichePosteDao, AffectationDao affectationDao,
			AutreAdministrationAgentDao autreAdministrationAgentDao, MotifAvancementDao motifAvancementDao, AvisCapDao avisCapDao, boolean avctPrev) throws Exception {
		AvancementFonctionnaires avct = getAvancementFonctionnaire(aTransaction, a, annee, adsService, fichePosteDao, affectationDao, autreAdministrationAgentDao, motifAvancementDao, avisCapDao,
				avctPrev);
		if (avct != null) {
			avct.setAutre(false);
			// #19141 : on traite les territoriaux et les avancement auto
			// uniquement
			if (avct.getIdMotifAvct() != null && avct.getIdMotifAvct().toString().equals("3")) {
				avct.setAutre(true);
			}

			if (avct.getCodeCategorie() != null && (avct.getCodeCategorie().toString().equals("18") || avct.getCodeCategorie().toString().equals("20"))) {
				avct.setAutre(true);
			}
		}
		return avct;
	}

	private AvancementFonctionnaires getAvancementFonctionnaire(Transaction aTransaction, Agent a, String annee, IAdsService adsService, FichePosteDao fichePosteDao, AffectationDao affectationDao,
			AutreAdministrationAgentDao autreAdministrationAgentDao, MotifAvancementDao motifAvancementDao, AvisCapDao avisCapDao, boolean avctPrev) throws Exception {
		SimpleDateFormat sdfFormatDate = new SimpleDateFormat("dd/MM/yyyy");
		// Recuperation de la carriere en cours
		Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(aTransaction, a);
		if (aTransaction.isErreur() || carr == null || carr.getDateDebut() == null) {
			aTransaction.traiterErreur();
			return null;
		}
		PositionAdmAgent paAgent = PositionAdmAgent.chercherPositionAdmAgentDateComprise(aTransaction, a.getNomatr(),
				Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE));
		if (aTransaction.isErreur() || paAgent == null || paAgent.getCdpadm() == null || paAgent.estPAInactive(aTransaction)) {
			aTransaction.traiterErreur();
			return null;
		}

		// on regarde si il y a d'autre carrieres avec le meme grade
		// si oui on prend la carriere plus lointaine
		ArrayList<Carriere> listeCarrMemeGrade = Carriere.listerCarriereAvecGradeEtStatut(aTransaction, a.getNomatr(), carr.getCodeGrade(), carr.getCodeCategorie());
		if (listeCarrMemeGrade != null && listeCarrMemeGrade.size() > 0) {
			carr = (Carriere) listeCarrMemeGrade.get(0);
		}
		Grade gradeActuel = Grade.chercherGrade(aTransaction, carr.getCodeGrade());
		if (aTransaction.isErreur()) {
			aTransaction.traiterErreur();
			return null;
		}

		// Si pas de grade suivant, agent non eligible
		if (gradeActuel.getCodeGradeSuivant() != null && gradeActuel.getCodeGradeSuivant().length() != 0) {
			// Création de l'avancement
			AvancementFonctionnaires avct = new AvancementFonctionnaires();
			avct.setIdAgent(a.getIdAgent());
			avct.setCodeCategorie(Integer.valueOf(carr.getCodeCategorie()));
			avct.setAnnee(Integer.valueOf(annee));
			avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());

			// PA
			avct.setCodePa(paAgent.getCdpadm());

			// on traite si l'agent est detaché ou non
			if (paAgent.getCdpadm().equals("54") || paAgent.getCdpadm().equals("56") || paAgent.getCdpadm().equals("57") || paAgent.getCdpadm().equals("58")) {
				avct.setAgentVdn(false);
			} else {
				avct.setAgentVdn(true);
			}
			// SI stagiaire sur un grade a durée moyenne different de 12
			// mois
			if ((carr.getCodeCategorie().equals("2") || carr.getCodeCategorie().equals("18")) && (!gradeActuel.getDureeMoy().equals("12"))) {

				avct.setNouvBmAnnee(Integer.valueOf(carr.getBMAnnee()));
				avct.setNouvBmMois(Integer.valueOf(carr.getBMMois()));
				avct.setNouvBmJour(Integer.valueOf(carr.getBMJour()));
				Integer nouvACCStage = Integer.valueOf(carr.getACCAnnee()) + 1;
				avct.setNouvAccAnnee(nouvACCStage);
				avct.setNouvAccMois(Integer.valueOf(carr.getACCMois()));
				avct.setNouvAccJour(Integer.valueOf(carr.getACCJour()));

				// par defaut avis CAP = "MOYENNE"
				AvisCap avisCap = avisCapDao.chercherAvisCapByLibCourt(Const.AVIS_CAP_MOY);
				avct.setIdAvisCap(avisCap.getIdAvisCap());

				avct.setPeriodeStandard(12);

				avct.setDateAvctMoy(sdfFormatDate.parse(Services.ajouteAnnee(carr.getDateDebut(), 1)));
				avct.setDateAvctMaxi(null);
				avct.setDateAvctMini(null);

				if (!avctPrev) {
					// si la date avct moy (année ) sup a l'année choisie
					// pour
					// la simu alors on sort l'agent du calcul
					Integer anneeNumerique = avct.getAnnee();
					Integer anneeDateAvctMoyNumerique = Integer.valueOf(sdfFormatDate.format(avct.getDateAvctMoy()).substring(6, sdfFormatDate.format(avct.getDateAvctMoy()).length()));
					if (anneeDateAvctMoyNumerique > anneeNumerique) {
						return null;
					}
				}

				// le grade suivant reste le meme
				avct.setIdNouvGrade(gradeActuel.getCodeGrade() == null || gradeActuel.getCodeGrade().length() == 0 ? null : gradeActuel.getCodeGrade());
				avct.setCdcadr(gradeActuel.getCodeCadre());

				// IBA,INM,INA
				Bareme bareme = Bareme.chercherBareme(aTransaction, carr.getIban());
				if (aTransaction.isErreur()) {
					aTransaction.traiterErreur();
				}
				avct.setIban(carr.getIban());
				avct.setInm(Integer.valueOf(bareme.getInm()));
				avct.setIna(Integer.valueOf(bareme.getIna()));
				avct.setNouvIban(carr.getIban());
				avct.setNouvInm(Integer.valueOf(bareme.getInm()));
				avct.setNouvIna(Integer.valueOf(bareme.getIna()));

			} else {
				// BM/ACC
				avct.setNouvBmAnnee(Integer.valueOf(carr.getBMAnnee()));
				avct.setNouvBmMois(Integer.valueOf(carr.getBMMois()));
				avct.setNouvBmJour(Integer.valueOf(carr.getBMJour()));
				avct.setNouvAccAnnee(Integer.valueOf(carr.getACCAnnee()));
				avct.setNouvAccMois(Integer.valueOf(carr.getACCMois()));
				avct.setNouvAccJour(Integer.valueOf(carr.getACCJour()));

				// par defaut avis CAP = "MOYENNE"
				AvisCap avisCap = avisCapDao.chercherAvisCapByLibCourt(Const.AVIS_CAP_MOY);
				avct.setIdAvisCap(avisCap.getIdAvisCap());

				// calcul BM/ACC applicables
				int nbJoursBM = AvancementFonctionnaires.calculJourBM(gradeActuel, carr);
				int nbJoursACC = AvancementFonctionnaires.calculJourACC(gradeActuel, carr);

				int nbJoursBonusDepart = nbJoursBM + nbJoursACC;
				int nbJoursBonus = nbJoursBM + nbJoursACC;
				// Calcul date avancement au Grade actuel
				if (gradeActuel.getDureeMin() != null && gradeActuel.getDureeMin().length() != 0 && !gradeActuel.getDureeMin().equals("0")) {
					if (nbJoursBonusDepart > Integer.parseInt(gradeActuel.getDureeMin()) * 30) {
						String date = carr.getDateDebut().substring(0, 6) + annee;
						avct.setDateAvctMini(sdfFormatDate.parse(date));
						nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
					} else {
						avct.setDateAvctMini(AvancementFonctionnaires.calculDateAvctMini(gradeActuel, carr));
						nbJoursBonus = 0;
					}
				}
				if (gradeActuel.getDureeMoy() != null && gradeActuel.getDureeMoy().length() != 0) {
					avct.setPeriodeStandard(Integer.valueOf(gradeActuel.getDureeMoy()));
					if (nbJoursBonusDepart > Integer.parseInt(gradeActuel.getDureeMoy()) * 30) {
						String date = carr.getDateDebut().substring(0, 6) + annee;
						avct.setDateAvctMoy(sdfFormatDate.parse(date));
						nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
					} else {
						avct.setDateAvctMoy(AvancementFonctionnaires.calculDateAvctMoy(gradeActuel, carr));
						nbJoursBonus = 0;
					}
				}
				if (gradeActuel.getDureeMax() != null && gradeActuel.getDureeMax().length() != 0 && !gradeActuel.getDureeMax().equals("0")) {
					if (nbJoursBonusDepart > Integer.parseInt(gradeActuel.getDureeMax()) * 30) {
						String date = carr.getDateDebut().substring(0, 6) + annee;
						avct.setDateAvctMaxi(sdfFormatDate.parse(date));
						nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
					} else {
						avct.setDateAvctMaxi(AvancementFonctionnaires.calculDateAvctMaxi(gradeActuel, carr));
						nbJoursBonus = 0;
					}
				}

				if (!avctPrev) {
					// si la date avct moy (année ) sup a l'année choisie
					// pour
					// la simu alors on sort l'agent du calcul
					Integer anneeNumerique = avct.getAnnee();
					Integer anneeDateAvctMoyNumerique = Integer.valueOf(sdfFormatDate.format(avct.getDateAvctMoy()).substring(6, sdfFormatDate.format(avct.getDateAvctMoy()).length()));
					if (anneeDateAvctMoyNumerique > anneeNumerique) {
						return null;
					}
				}

				// Calcul du grade suivant (BM/ACC)
				Grade gradeSuivant = Grade.chercherGrade(aTransaction, gradeActuel.getCodeGradeSuivant());
				if (gradeSuivant.getDureeMoy() != null && gradeSuivant.getDureeMoy().length() > 0 && Services.estNumerique(gradeSuivant.getDureeMoy())) {
					boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
					while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0 && gradeSuivant.getDureeMoy() != null
							&& gradeSuivant.getDureeMoy().length() > 0) {
						nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMoy()) * 30;
						gradeSuivant = Grade.chercherGrade(aTransaction, gradeSuivant.getCodeGradeSuivant());
						isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
					}
				}

				int nbJoursRestantsBM = nbJoursBonus > nbJoursACC ? nbJoursBonus - nbJoursACC : Integer.parseInt(Const.ZERO);
				int nbJoursRestantsACC = nbJoursBonus - nbJoursRestantsBM;

				avct.setNouvBmAnnee(nbJoursRestantsBM / 365);
				avct.setNouvBmMois((nbJoursRestantsBM % 365) / 30);
				avct.setNouvBmJour((nbJoursRestantsBM % 365) % 30);

				avct.setNouvAccAnnee(nbJoursRestantsACC / 365);
				avct.setNouvAccMois((nbJoursRestantsACC % 365) / 30);
				avct.setNouvAccJour((nbJoursRestantsACC % 365) % 30);

				avct.setIdNouvGrade(gradeSuivant.getCodeGrade() == null || gradeSuivant.getCodeGrade().length() == 0 ? null : gradeSuivant.getCodeGrade());
				avct.setCdcadr(gradeActuel.getCodeCadre());

				// IBA,INM,INA
				Bareme bareme = Bareme.chercherBareme(aTransaction, carr.getIban());
				if (aTransaction.isErreur()) {
					aTransaction.traiterErreur();
				}
				avct.setIban(carr.getIban());
				avct.setInm(Integer.valueOf(bareme.getInm()));
				avct.setIna(Integer.valueOf(bareme.getIna()));

				// on cherche le nouveau bareme
				if (gradeSuivant != null && gradeSuivant.getIban() != null) {
					Bareme nouvBareme = Bareme.chercherBareme(aTransaction, gradeSuivant.getIban());
					// on rempli les champs
					avct.setNouvIban(nouvBareme.getIban());
					avct.setNouvInm(Integer.valueOf(nouvBareme.getInm()));
					avct.setNouvIna(Integer.valueOf(nouvBareme.getIna()));
				}
			}
			// on regarde si l'agent est AFFECTE dans une autre
			// administration
			if (paAgent.getCdpadm().equals("54") || paAgent.getCdpadm().equals("56") || paAgent.getCdpadm().equals("57") || paAgent.getCdpadm().equals("58")) {
				avct.setDirectionService(null);
				avct.setSectionService(null);
				// alors on va chercher l'autre administration de
				// l'agent
				try {
					AutreAdministrationAgent autreAdminAgent = autreAdministrationAgentDao.chercherAutreAdministrationAgentActive(a.getIdAgent());
					if (autreAdminAgent != null && autreAdminAgent.getIdAutreAdmin() != null) {
						avct.setDirectionService(autreAdminAgent.getIdAutreAdmin().toString());
					}
				} catch (Exception e2) {
					// pas d'autres admi
				}
			} else {
				// on recupere le grade du poste
				Affectation aff = null;
				try {
					aff = affectationDao.chercherAffectationActiveAvecAgent(a.getIdAgent());
				} catch (Exception e2) {
					return null;
				}
				if (aff == null || aff.getIdFichePoste() == null) {
					return null;
				}
				FichePoste fp = fichePosteDao.chercherFichePoste(aff.getIdFichePoste());
				EntiteDto direction = adsService.getAffichageDirection(fp.getIdServiceAds());
				EntiteDto section = adsService.getAffichageSection(fp.getIdServiceAds());
				avct.setDirectionService(direction == null ? Const.CHAINE_VIDE : direction.getSigle());
				avct.setSectionService(section == null ? Const.CHAINE_VIDE : section.getSigle());
			}

			if (carr != null) {
				if (carr.getCodeGrade() != null && carr.getCodeGrade().length() != 0) {
					Grade grd = Grade.chercherGrade(aTransaction, carr.getCodeGrade());
					avct.setGrade(grd.getCodeGrade());

					// on prend l'id motif de la colonne CDTAVA du grade
					// si CDTAVA correspond a AVANCEMENT DIFF alors on
					// calcul les 3 dates sinon on calcul juste la date
					// moyenne
					if (grd.getCodeTava() != null && !grd.getCodeTava().equals(Const.CHAINE_VIDE)) {
						avct.setIdMotifAvct(Integer.valueOf(grd.getCodeTava()));
						MotifAvancement motif = motifAvancementDao.chercherMotifAvancementByLib("AVANCEMENT DIFFERENCIE");
						if (motif.getIdMotifAvct() != avct.getIdMotifAvct()) {
							avct.setDateAvctMaxi(null);
							avct.setDateAvctMini(null);
						}
					} else {
						avct.setIdMotifAvct(null);
					}

					if (grd.getCodeGradeGenerique() != null) {
						// on cherche le grade generique pour trouver la
						// filiere
						GradeGenerique ggCarr = GradeGenerique.chercherGradeGenerique(aTransaction, grd.getCodeGradeGenerique());
						if (aTransaction.isErreur())
							aTransaction.traiterErreur();

						if (ggCarr != null && ggCarr.getCdfili() != null) {
							FiliereGrade fil = FiliereGrade.chercherFiliereGrade(aTransaction, ggCarr.getCdfili());
							avct.setFiliere(fil.getLibFiliere());
						}
					}
				}
			}
			avct.setDateGrade(sdfFormatDate.parse(carr.getDateDebut()));
			avct.setBmAnnee(Integer.valueOf(carr.getBMAnnee()));
			avct.setBmMois(Integer.valueOf(carr.getBMMois()));
			avct.setBmJour(Integer.valueOf(carr.getBMJour()));
			avct.setAccAnnee(Integer.valueOf(carr.getACCAnnee()));
			avct.setAccMois(Integer.valueOf(carr.getACCMois()));
			avct.setAccJour(Integer.valueOf(carr.getACCJour()));

			// on regarde si l'agent a une carriere de simulation deja
			// saisie
			// autrement dis si la carriere actuelle a pour datfin 0
			if (carr.getDateFin() == null || carr.getDateFin().equals(Const.ZERO)) {
				avct.setCarriereSimu(null);
			} else {
				avct.setCarriereSimu("S");
			}

			avct.setDateVerifSef(null);
			avct.setDateVerifSgc(null);

			return avct;
		} else {
			return new AvancementFonctionnaires();
		}
	}

}
