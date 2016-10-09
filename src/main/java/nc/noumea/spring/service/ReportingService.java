package nc.noumea.spring.service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import nc.mairie.enums.EnumCivilite;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.Recommandation;
import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.hsct.MedecinDao;
import nc.mairie.spring.dao.metier.hsct.RecommandationDao;
import nc.mairie.spring.dao.metier.hsct.VisiteMedicaleDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.poste.TitrePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.noumea.mairie.ads.dto.EntiteDto;

@Service
public class ReportingService implements IReportingService {

	private Logger				logger		= LoggerFactory.getLogger(ReportingService.class);

	protected Font				fontNormal8	= FontFactory.getFont("Arial", 8, Font.NORMAL);
	protected Font				fontNormal9	= FontFactory.getFont("Arial", 9, Font.NORMAL);
	protected Font				fontBold8	= FontFactory.getFont("Arial", 8, Font.BOLD);
	protected Font				fontBold9	= FontFactory.getFont("Arial", 9, Font.BOLD);
	protected SimpleDateFormat	sdf			= new SimpleDateFormat("dd/MM/yyyy");

	protected void addMetaData(Document document, String titre, String author) {

		document.addTitle(titre);
		if (author != null)
			document.addAuthor(author);

		document.addSubject(titre);
	}

	protected void writeSpacing(Document document, int nbLines) throws DocumentException {
		Paragraph newLine = new Paragraph(Chunk.NEWLINE);
		newLine.setFont(fontBold9);
		for (int i = 0; i < nbLines; i++) {
			document.add(newLine);
		}
	}

	protected void writeParagraph(Document document, List<String> listParagraph, Font police, int alignement) throws DocumentException {
		for (String title : listParagraph) {
			Paragraph paragraph = null;
			paragraph = new Paragraph(title, police);
			paragraph.setAlignment(alignement);
			document.add(paragraph);
		}
	}

	protected PdfPTable writeTableau(Document document, float[] relativeWidth) throws DocumentException {
		PdfPTable table = new PdfPTable(relativeWidth);
		table.setWidthPercentage(100f);
		return table;
	}

	protected void writeLine(PdfPTable table, Integer padding, List<CellVo> values) {

		for (CellVo value : values) {
			table.addCell(writeCell(padding, null, value));
		}
	}

	protected PdfPCell writeCell(Integer padding, Integer horizontalAlign, CellVo value) {

		PdfPCell pdfWordCell = new PdfPCell();
		pdfWordCell.setPadding(padding);
		pdfWordCell.setUseAscender(true);
		pdfWordCell.setUseDescender(true);
		pdfWordCell.setBackgroundColor(value.getBackgroundColor());
		pdfWordCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		// /!\ COLSPAN
		if (value.getColspan() != null)
			pdfWordCell.setColspan(value.getColspan());

		// border
		if (!value.isBorder())
			pdfWordCell.setBorder(Rectangle.NO_BORDER);

		// on cree la phrase pour le FONT
		Phrase phrase = null;
		if (null != value.getFont()) {
			phrase = new Phrase(value.getText(), value.getFont());
		} else if (value.isBold()) {
			phrase = new Phrase(value.getText(), fontBold9);
		} else {
			phrase = new Phrase(value.getText(), fontNormal9);
		}
		// on cree le Paragraph pour l alignement
		Paragraph paragraph = new Paragraph(phrase);
		if (null != value.getHorizontalAlign()) {
			paragraph.setAlignment(value.getHorizontalAlign());
		} else if (null != horizontalAlign) {
			paragraph.setAlignment(horizontalAlign);
		}

		pdfWordCell.addElement(paragraph);

		return pdfWordCell;
	}

	@Override
	public byte[] getCertificatAptitudePDF(String idVm) throws NumberFormatException, Exception {
		logger.debug("entered getCertificatAptitudePDF with parameter idVm = {} ", idVm);

		Document document = new Document(PageSize.A4);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, baos);

		// on genere les metadata
		addMetaData(document, "Certificat d'aptitude", "SIRH");

		// on ouvre le document
		document.open();

		if (null != idVm) {
			// initialisation DAO
			ApplicationContext context = ApplicationContextProvider.getContext();
			VisiteMedicaleDao visiteMedicaleDao = new VisiteMedicaleDao((SirhDao) context.getBean("sirhDao"));
			AgentDao agentDao = new AgentDao((SirhDao) context.getBean("sirhDao"));
			MedecinDao medecinDao = new MedecinDao((SirhDao) context.getBean("sirhDao"));
			RecommandationDao recommandationDao = new RecommandationDao((SirhDao) context.getBean("sirhDao"));
			AffectationDao affectationDao = new AffectationDao((SirhDao) context.getBean("sirhDao"));
			FichePosteDao fichePosteDao = new FichePosteDao((SirhDao) context.getBean("sirhDao"));
			TitrePosteDao titrePosteDao = new TitrePosteDao((SirhDao) context.getBean("sirhDao"));
			IAdsService adsService = (AdsService) context.getBean("adsService");

			// on recupere les donnnées
			VisiteMedicale vm = visiteMedicaleDao.chercherVisiteMedicale(new Integer(idVm));
			Agent agent = agentDao.chercherAgent(vm.getIdAgent());
			String poste = Const.CHAINE_VIDE;
			// poste agent
			Affectation aff = affectationDao.chercherAffectationAgentPourDate(agent.getIdAgent(), vm.getDateDerniereVisite());
			if (aff != null && aff.getIdFichePoste() != null) {
				FichePoste fp = fichePosteDao.chercherFichePoste(aff.getIdFichePoste());

				String titreFichePoste = fp.getIdTitrePoste() == null ? Const.CHAINE_VIDE
						: titrePosteDao.chercherTitrePoste(fp.getIdTitrePoste()).getLibTitrePoste();
				// Service

				EntiteDto direction = adsService.getAffichageDirection(fp.getIdServiceAds());
				EntiteDto service = adsService.getAffichageSection(fp.getIdServiceAds());
				if (service == null)
					service = adsService.getAffichageService(fp.getIdServiceAds());
				if (service == null)
					service = adsService.getEntiteByIdEntite(fp.getIdServiceAds());
				// on concatene les infos
				poste = (direction == null ? "" : direction.getSigle() + " / ") + (service == null ? "" : service.getSigle() + " / ")
						+ (titreFichePoste);
			}

			// medecin
			Medecin medecin = null;
			String nomMedecin = Const.CHAINE_VIDE;
			if (vm.getIdMedecin() != null) {
				medecin = medecinDao.chercherMedecin(vm.getIdMedecin());
				nomMedecin = medecin.getPrenomMedecin() + " " + medecin.getNomMedecin();
			}
			// recommandation
			Recommandation recom = null;
			String recommandation = Const.CHAINE_VIDE;
			if (vm.getIdRecommandation() != null) {
				recom = recommandationDao.chercherRecommandation(vm.getIdRecommandation());
				recommandation = recom.getDescRecommandation();
			}
			// date prochaine visite
			String dateARevoir = Const.CHAINE_VIDE;
			if (vm.getDureeValidite() != null) {
				dateARevoir = vm.getDureeValidite() + " mois";
			}

			// on commence le document
			genereEnteteDocument(document, agent);

			genereTableau(document, vm.getDateDerniereVisite(), nomMedecin.toUpperCase(), poste.toUpperCase(), recommandation, vm.getCommentaire(),
					dateARevoir);

		}

		// on ferme le document
		document.close();

		return baos.toByteArray();
	}

	private void genereTableau(Document document, Date dateVisite, String nomMedecin, String poste, String avis, String restriction, String aRevoir)
			throws DocumentException {
		PdfPTable table = writeTableau(document, new float[] { 8, 20 });
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		// 1ere ligne : medecin
		List<CellVo> listValuesLigne1 = new ArrayList<CellVo>();
		listValuesLigne1.add(new CellVo("Medecin : ", true, 1, null, Element.ALIGN_LEFT, true, fontBold8));
		listValuesLigne1.add(new CellVo(nomMedecin, true, 1, null, Element.ALIGN_LEFT, true, fontNormal8));
		writeLine(table, 3, listValuesLigne1);

		// 2er ligne : date visite
		List<CellVo> listValuesLigne2 = new ArrayList<CellVo>();
		listValuesLigne2.add(new CellVo("Date de la visite : ", true, 1, null, Element.ALIGN_LEFT, true, fontBold8));
		listValuesLigne2.add(new CellVo(sdf.format(dateVisite), true, 1, null, Element.ALIGN_LEFT, true, fontNormal8));
		writeLine(table, 3, listValuesLigne2);

		// 3eme ligne : poste
		List<CellVo> listValuesLigne3 = new ArrayList<CellVo>();
		listValuesLigne3.add(new CellVo("Poste : ", true, 1, null, Element.ALIGN_LEFT, true, fontBold8));
		listValuesLigne3.add(new CellVo(poste, true, 1, null, Element.ALIGN_LEFT, true, fontNormal8));
		writeLine(table, 3, listValuesLigne3);

		// 4eme ligne : avis
		List<CellVo> listValuesLigne4 = new ArrayList<CellVo>();
		listValuesLigne4.add(new CellVo("Avis médical : ", true, 1, null, Element.ALIGN_LEFT, true, fontBold8));
		listValuesLigne4.add(new CellVo(avis, true, 1, null, Element.ALIGN_LEFT, true, fontNormal8));
		writeLine(table, 3, listValuesLigne4);

		// 5eme ligne : restrictions
		List<CellVo> listValuesLigne5 = new ArrayList<CellVo>();
		listValuesLigne5.add(new CellVo("Restrictions éventuelles : ", true, 1, null, Element.ALIGN_LEFT, true, fontBold8));
		listValuesLigne5.add(new CellVo(restriction, true, 1, null, Element.ALIGN_LEFT, true, fontNormal8));
		writeLine(table, 3, listValuesLigne5);

		// 6eme ligne : restrictions
		List<CellVo> listValuesLigne6 = new ArrayList<CellVo>();
		listValuesLigne6.add(new CellVo("A revoir dans : ", true, 1, null, Element.ALIGN_LEFT, true, fontBold8));
		listValuesLigne6.add(new CellVo(aRevoir, true, 1, null, Element.ALIGN_LEFT, true, fontNormal8));
		writeLine(table, 3, listValuesLigne6);

		document.add(table);
	}

	private void genereEnteteDocument(Document document, Agent agent) throws DocumentException {
		// Espace avant l'entête (pour le papier à entête)
		writeSpacing(document, 1);
		String civilite = agent.getCivilite().equals(EnumCivilite.M.getCode()) ? "MONSIEUR " : "MADAME ";
		String nomPrenom = agent.getNomAgent() + " " + agent.getPrenomAgent();

		List<String> lignesEntete = Arrays.asList("CERTIFICAT D'APTITUDE DE ", civilite + nomPrenom.toUpperCase());

		// Insère les lignes d'entête dans le document
		writeParagraph(document, lignesEntete, fontBold9, Element.ALIGN_CENTER);

		// Espace après l'entête (pour le papier à entête)
		writeSpacing(document, 3);
	}

}
