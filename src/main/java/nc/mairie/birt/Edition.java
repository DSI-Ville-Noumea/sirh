package nc.mairie.birt;

import java.util.Locale;
import java.util.Map;

/**
 * Cette classe permet la g�n�ration dans n'importe quel format de n'importe
 * quelle �dition. Il faut d'abord l'instancier, puis on lance le rapport et on
 * fait le rendu. Si n�cessaire, cette classe peut �tre thread�e facilement.
 * 
 * @author BiM
 * 
 */
public final class Edition {

//	public Edition(String test) {
//		super();
//	}
//
//	private static Edition m_Instance = null;
//	private IReportEngine engine;
//
//	/**
//	 * Constructeur. Il configure le moteur BIRT de sorte � ce qu'il puisse
//	 * retrouver les ressources BIRT mais �galement les librairies le faisant
//	 * tourner Il est alors d�marr� avec cette configuration.
//	 * 
//	 * @throws BirtException
//	 */
//	private Edition() throws BirtException {
//
//		// R�cup�ration du chemin du r�pertoire courant
//		String repertoireCourant = "C:/workspaceRAD7/sirh/Mairie_GestionAgent_EAE";
//
//		// Affectation du chemin vers la plateforme Birt
//		String BIRT_HOME = repertoireCourant + "/runtime/ReportEngine";
//
//		EngineConfig config = new EngineConfig();
//		config.setBIRTHome(BIRT_HOME);
//		Platform.startup(config);
//		IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
//		engine = factory.createReportEngine(config);
//	}
//
//	/**
//	 * Instanciation du service.
//	 * 
//	 * @return l'instance en cours du service
//	 * @throws BirtException
//	 */
//	public static synchronized Edition getInstance() throws BirtException {
//		if (m_Instance == null) {
//			m_Instance = new Edition();
//		}
//		return m_Instance;
//	}
//
//	/**
//	 * G�n�ration de l'�tat.
//	 * 
//	 * @param _input
//	 *            Nom du fichier d'entr�e sans l'extension (rptdesign), le
//	 *            fichier de sortie portera le m�me nom
//	 * @param _langue
//	 *            Locale pour d�finir la langue de g�n�ration du rapport
//	 * @param _parameters
//	 *            Map contenant les param�tres du rapport sous la forme <Nom,
//	 *            Valeur>
//	 * @throws EngineException
//	 */
//	public synchronized void run(String _input, Locale _langue, Map<String, Object> _parameters) throws EngineException {
//		this.run(_input, _input, _langue, _parameters);
//	}
//
//	/**
//	 * G�n�ration de l'�tat.
//	 * 
//	 * @param _input
//	 *            Nom du fichier d'entr�e sans l'extension (rptdesign)
//	 * @param _output
//	 *            Nom du fichier de sortie sans l'extension (rptdocument)
//	 * @param _langue
//	 *            Locale pour d�finir la langue de g�n�ration du rapport
//	 * @param _parameters
//	 *            Map contenant les param�tres du rapport sous la forme <Nom,
//	 *            Valeur>
//	 * @throws EngineException
//	 */
//	public synchronized void run(String _input, String _output, Locale _langue, Map<String, Object> _parameters) throws EngineException {
//
//		// R�cup�ration du chemin du r�pertoire courant
//		String repertoireCourant = "C:/workspaceRAD7/sirh/Mairie_GestionAgent_EAE";
//		// On sauvegarde finalement le rapport
//		String cheminRapport = repertoireCourant + "/reports/";
//
//		IRunTask runTask = null;
//		try {
//			/* R�cup�ration du rptDesign */
//			IReportRunnable design = engine.openReportDesign(cheminRapport + _input + ".rptdesign");
//
//			/* Cr�ation de la t�che d'�x�cution */
//			runTask = engine.createRunTask(design);
//
//			/* Param�tres */
//			runTask.setParameterValues(_parameters);
//
//			/* Langue */
//			runTask.setLocale(_langue);
//
//			/* Ex�cution */
//			runTask.run(cheminRapport + _output + ".rptdocument");
//		} finally {
//			if (runTask != null) {
//				runTask.close();
//			}
//		}
//	}
//
//	/**
//	 * Rendu de l'�tat.
//	 * 
//	 * @param _input
//	 *            Nom du fichier d'entr�e sans l'extension (rptdocument)
//	 * @param _format
//	 *            Format de sortie du fichier
//	 * @throws EngineException
//	 */
//	public synchronized void render(String _input, String _format) throws EngineException {
//		this.render(_input, _input, _format);
//	}
//
//	/**
//	 * Rendu de l'�tat.
//	 * 
//	 * @param _input
//	 *            Nom du fichier d'entr�e sans l'extension (rptdocument), le
//	 *            fichier de sortie portera le m�me nom.
//	 * @param _output
//	 *            Nom du fichier de sortie sans l'extension (format final)
//	 * @param _format
//	 *            Format de sortie du fichier
//	 * @throws EngineException
//	 */
//	public synchronized void render(String _input, String _output, String _format) throws EngineException {
//
//		// R�cup�ration du chemin du r�pertoire courant
//		String repertoireCourant = "C:/workspaceRAD7/sirh/Mairie_GestionAgent_EAE";
//		// On sauvegarde finalement le rapport
//		String cheminRapport = repertoireCourant + "/reports/";
//
//		IReportDocument doc = null;
//		IRenderTask renderTask = null;
//		try {
//			/* Cr�ation des options de rendu */
//			IRenderOption option = new RenderOption();
//
//			/* Fichier et format de sortie */
//			option.setOutputFileName(cheminRapport + _output + "." + _format);
//			option.setOutputFormat(_format);
//
//			/* R�cup�ration du rptDocument */
//			doc = engine.openReportDocument(cheminRapport + _input + ".rptdocument");
//
//			/* Rendu */
//			renderTask = engine.createRenderTask(doc);
//
//			/* Chargement des options de rendu */
//			renderTask.setRenderOption(option);
//
//			/* Cr�ation du fichier de rendu */
//			renderTask.render();
//		} finally {
//			if (renderTask != null) {
//				renderTask.close();
//			}
//			if (doc != null) {
//				doc.close();
//			}
//		}
//	}
//
//	public void destructeur() {
//		engine.destroy();
//		Platform.shutdown();
//		m_Instance = null;
//	}
//
//	public void test(String cheminRptDesign, String destinationSansExtension) {
//		try {
//
//			// create engine config, start engine and platform
//			EngineConfig config = new EngineConfig();
//			Platform.startup();
//			ReportEngine engine = new ReportEngine(config);
//
//			// open the report design and create a new run task
//			IReportRunnable reportDesign = engine.openReportDesign(cheminRptDesign);
//			IRunAndRenderTask runTask = engine.createRunAndRenderTask(reportDesign);
//
//			// the multi-select values to use. This just needs to be Object[].
//			String[] values = { "9005138", "9003041" };
//			runTask.setParameterValue("id_agent", values);
//
//			// set the render outputs
//			PDFRenderOption renderOption = new PDFRenderOption();
//			renderOption.setOutputFileName(destinationSansExtension + ".pdf");
//			renderOption.setOutputFormat("PDF");
//			runTask.setRenderOption(renderOption);
//
//			// run report, close the task, and destroy the engine.
//			runTask.run();
//			runTask.close();
//			engine.destroy();
//		} catch (EngineException e) {
//			e.printStackTrace();
//		} catch (BirtException e) {
//			e.printStackTrace();
//		}
//	}
}