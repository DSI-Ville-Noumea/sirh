package nc.noumea.spring.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.agent.Prime;
import nc.mairie.metier.avancement.AvancementContractuels;
import nc.mairie.metier.avancement.AvancementConvCol;
import nc.mairie.metier.carriere.Bareme;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementContractuelsDao;
import nc.mairie.spring.dao.metier.avancement.AvancementConvColDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.ws.ISirhWSConsumer;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;
import nc.mairie.utils.MessageUtils;
import nc.noumea.mairie.ads.dto.EntiteDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "avctService")
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
					avct.setMontantPrime1200("3");
				} else {
					if (prime1200 != null && prime1200.getMtPri() != null) {
						if (Integer.valueOf(prime1200.getMtPri()) > 30) {
							avct.setMontantPrime1200("30");
						} else {
							avct.setMontantPrime1200(prime1200.getMtPri());
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
						avct.getCarriereSimu(), avct.getAnnee(), avct.getDirectionService(), avct.getSectionService(), avct.getCdcadr());

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
		if (carr.getDateDebut().equals("06/10/2010")) {
			System.out.println("ici");
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

		// on recupere le grade du poste
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
		// on cherche a quelle categorie appartient l'agent
		// (A,B,A+..;)
		Grade g = Grade.chercherGrade(aTransaction, fp.getCodeGrade());
		GradeGenerique gg = GradeGenerique.chercherGradeGenerique(aTransaction, g.getCodeGradeGenerique());
		if (aTransaction.isErreur() || gg == null || gg.getNbPointsAvct() == null || gg.getNbPointsAvct().equals("0")) {
			aTransaction.traiterErreur();
			return new AvancementContractuels();
		}
		Bareme bareme = Bareme.chercherBareme(aTransaction, carr.getIban());
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
		avct.setDateProchainGrade(sdf.parse(Services.ajouteAnnee(Services.formateDate(carr.getDateDebut()), 2)));

		avct.setDateArrete(sdf.parse("01/01/" + annee));
		avct.setNumArrete(annee);

		EntiteDto direction = adsService.getAffichageDirection(fp.getIdServiceAds());
		EntiteDto section = adsService.getAffichageSection(fp.getIdServiceAds());

		avct.setDirectionService(direction == null ? Const.CHAINE_VIDE : direction.getSigle());
		avct.setSectionService(section == null ? Const.CHAINE_VIDE : section.getSigle());
		avct.setDateGrade(sdf.parse(carr.getDateDebut()));
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
	public boolean isCarriereContractuelSimu(Transaction aTransaction, Agent agent, AvancementContractuels avct, Carriere carr) throws Exception {
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
		nouvelleCarriere.setCodeGrade(carr.getCodeGrade());
		nouvelleCarriere.setCodeBaseHoraire2(carr.getCodeBaseHoraire2());
		nouvelleCarriere.setIdMotif(Const.ZERO);
		nouvelleCarriere.setModeReglement(carr.getModeReglement());
		nouvelleCarriere.setTypeContrat(carr.getTypeContrat());

		// RG_AG_CA_A03
		nouvelleCarriere.setNoMatricule(agent.getNomatr().toString());
		return nouvelleCarriere;
	}

}
