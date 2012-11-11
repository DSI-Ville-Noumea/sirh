package nc.mairie.birt;


public class GenerateurRapportBIRT {

//	public static void main(String[] args) {
//
//		// Récupération du chemin du répertoire courant
//		String repertoireCourant = System.getProperty("user.dir");
//
//		// Affectation du chemin vers la plateforme Birt
//		String BIRT_HOME = repertoireCourant + "/runtime/ReportEngine";
//
//		// Configuration OSGI
//		DesignConfig config = new DesignConfig();
//		config.setBIRTHome(BIRT_HOME);
//
//		try {
//			// Démarrage de la plateforme
//			Platform.startup(config);
//
//			// Récupération de la fabrique de 'concepteurs de rapports'
//			IDesignEngineFactory iDesignEngineFactory = (IDesignEngineFactory) Platform
//					.createFactoryObject(IDesignEngineFactory.EXTENSION_DESIGN_ENGINE_FACTORY);
//
//			// Récupération d'une instance de 'concepteur de rapport'
//			IDesignEngine engine = iDesignEngineFactory.createDesignEngine(config);
//
//			// Création d'une session de travail
//			SessionHandle session = engine.newSessionHandle(ULocale.FRENCH);
//
//			// Création d'un élement rapport
//			ReportDesignHandle design = session.createDesign();
//
//			// Création d'une fabrique de modules à insérer dans le rapport
//			ElementFactory factory = design.getElementFactory();
//
//			/*
//			 * Création d'un élément master page
//			 */
//			// Création d'un élément Master Page
//			DesignElementHandle element = factory.newSimpleMasterPage("Page Master");
//			// Ajout de l'élément au rapport
//			design.getMasterPages().add(element);
//
//			/*
//			 * Création d'un élément source de données
//			 */
//
//			// Spécification des paramètres de connection
//			String nomDriver = "org.postgresql.Driver";
//			String urlBase = "jdbc:postgresql://localhost:5432/tutoriel_birt";
//			String loginBase = "postgres";
//			String passBase = "postgres";
//			String nomSourceDeDonnees = "table_des_produits";
//
//			// création d'un élément source de données de type jdbc
//			OdaDataSourceHandle dsHandle = factory.newOdaDataSource(nomSourceDeDonnees, "org.eclipse.birt.report.data.oda.jdbc");
//
//			// customisation de l'élément
//			dsHandle.setProperty("odaDriverClass", nomDriver);
//			dsHandle.setProperty("odaURL", urlBase);
//			dsHandle.setProperty("odaUser", loginBase);
//			dsHandle.setProperty("odaPassword", passBase);
//
//			// ajout de l'élément au rapport
//			design.getDataSources().add(dsHandle);
//
//			/*
//			 * Création d'un élément magasin de données (dataset) sur lequel se
//			 * base le paramètre de sélection
//			 */
//			String nomDuDataSetPourParametreDeSelection = "MagasinPourParametreDeSelection";
//			String requete = "SELECT type_produit FROM table_des_produits group by type_produit;";
//			OdaDataSetHandle odaDataSetHandle = factory.newOdaDataSet(nomDuDataSetPourParametreDeSelection,
//					"org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet");
//			odaDataSetHandle.setDataSource(nomSourceDeDonnees);
//			odaDataSetHandle.setQueryText(requete);
//
//			// ajout de l'élément au rapport
//			design.getDataSets().add(odaDataSetHandle);
//
//			/*
//			 * Création d'un élément paramètre de sélection
//			 */
//			String nomDuParametreFiltre = "Type Produit";
//
//			ScalarParameterHandle scalarParameterHandle = factory.newScalarParameter(nomDuParametreFiltre);
//
//			// choix du type de choix d'affichage (ici liste-box)
//			scalarParameterHandle.setControlType(DesignChoiceConstants.PARAM_CONTROL_LIST_BOX);
//
//			// choix du type de valeurs (dynamique ici)
//			scalarParameterHandle.setValueType(DesignChoiceConstants.PARAM_VALUE_TYPE_DYNAMIC);
//
//			// choix du volume de sélection possible (ici volume unitaire)
//			scalarParameterHandle.setParamType(DesignChoiceConstants.SCALAR_PARAM_TYPE_SIMPLE);
//
//			// choix du type des données contenues dans le paramètre
//			scalarParameterHandle.setDataType(DesignChoiceConstants.PARAM_TYPE_STRING);
//
//			// texte à afficher dans la fenêtre
//			String textePourAffichage = "Choisir le type";
//			scalarParameterHandle.setPromptText(textePourAffichage);
//
//			// Spécification que la valeur choisie doit être contenue dans la
//			// List-Box
//			scalarParameterHandle.setMustMatch(true);
//
//			// Spécification que l'affichage doit être fait de la même manière
//			// que dans la source de données du paramètre de sélection
//			scalarParameterHandle.setFixedOrder(true);
//
//			// Spécification de la non présentation de doublon dans la liste de
//			// choix
//			scalarParameterHandle.setDistinct(true);
//
//			// affectation du dataset pour obtenir la liste des paramètres
//			// d'affichage
//			scalarParameterHandle.setDataSetName(nomDuDataSetPourParametreDeSelection);
//
//			// Spécification de la valeur d'affichage de la source associée à la
//			// séléction
//			scalarParameterHandle.setValueExpr("dataSetRow[\"type_produit\"]");
//
//			// Spécification du libellé d'affichage de la source associée à la
//			// séléction
//			scalarParameterHandle.setLabelExpr("dataSetRow[\"type_produit\"]");
//
//			// Pas de formatage de la valeur
//			scalarParameterHandle.setCategory("Unformatted");
//
//			// Ajout du paramètre au rapport
//			design.getParameters().add(scalarParameterHandle);
//
//			/*
//			 * Construction du magasin de données principal
//			 */
//
//			String nomDuDataSetPrincipal = "DataSetPrincipal";
//			String requetePrincipal = "SELECT id, type_produit, nom, prix FROM table_des_produits where type_produit=?";
//			OdaDataSetHandle odaDataSetHandlePrincipal = factory.newOdaDataSet(nomDuDataSetPrincipal,
//					"org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet");
//
//			odaDataSetHandlePrincipal.setDataSource(nomSourceDeDonnees);
//			odaDataSetHandlePrincipal.setQueryText(requetePrincipal);
//
//			// affectation du report parameter
//			PropertyHandle propertyHandle = odaDataSetHandlePrincipal.getPropertyHandle(OdaDataSetHandle.PARAMETERS_PROP);
//
//			OdaDataSetParameter odaDataSetParameter = StructureFactory.createOdaDataSetParameter();
//
//			odaDataSetParameter.setName("param1");
//			odaDataSetParameter.setDataType(DesignChoiceConstants.PARAM_TYPE_STRING);
//			odaDataSetParameter.setPosition(1);
//			odaDataSetParameter.setIsInput(true);
//			odaDataSetParameter.setIsOutput(false);
//			odaDataSetParameter.setParamName(nomDuParametreFiltre);
//
//			propertyHandle.addItem(odaDataSetParameter);
//
//			// ajout de l'élément au rapport
//			design.getDataSets().add(odaDataSetHandlePrincipal);
//
//			/*
//			 * Construction de la mise en forme (Layout)
//			 */
//
//			CellHandle cell;
//			HashMap<String, String> mappingNomColonneLibelle = new HashMap<String, String>();
//			mappingNomColonneLibelle.put("id", "Identifiant");
//			mappingNomColonneLibelle.put("type_produit", "Type de Produit");
//			mappingNomColonneLibelle.put("nom", "Nom du produit");
//			mappingNomColonneLibelle.put("prix", "Prix du produit");
//
//			// Création d'un objet Tableau
//			TableHandle tableHandle = factory.newTableItem("Tableau Test", mappingNomColonneLibelle.size());
//
//			// Dimension du tableau
//			tableHandle.setWidth("100%");
//
//			// Affectation d'un magasin de données au tableau
//			tableHandle.setDataSet(design.findDataSet(nomDuDataSetPrincipal));
//
//			// Création du mapping données-valeurs pour chaque colonne
//			PropertyHandle computedSet = tableHandle.getColumnBindings();
//			ComputedColumn cs1 = null;
//
//			Set<String> listeColonne = mappingNomColonneLibelle.keySet();
//			Iterator<String> iterateurListeColonne = listeColonne.iterator();
//			String valeur;
//			String libelle;
//			int k;
//			while (iterateurListeColonne.hasNext()) {
//				valeur = iterateurListeColonne.next();
//				libelle = mappingNomColonneLibelle.get(valeur);
//				cs1 = StructureFactory.createComputedColumn();
//				cs1.setName(valeur);
//				cs1.setExpression("dataSetRow[\"" + valeur + "\"]");
//				computedSet.addItem(cs1);
//
//			}
//
//			StyleHandle styleHandle;
//
//			// Mise en place et formatage des libellés des cellules de la
//			// première ligne
//			RowHandle tableHeader = (RowHandle) tableHandle.getHeader().get(0);
//			int i = -1;
//			iterateurListeColonne = listeColonne.iterator();
//			while (iterateurListeColonne.hasNext()) {
//				i = i + 1;
//				valeur = iterateurListeColonne.next();
//				libelle = mappingNomColonneLibelle.get(valeur);
//				LabelHandle label1 = factory.newLabel(valeur);
//				label1.setText(libelle);
//				cell = (CellHandle) tableHeader.getCells().get(i);
//				cell.getContent().add(label1);
//
//				// formatage du css
//				styleHandle = cell.getPrivateStyle();
//				ColorHandle colorHandle = styleHandle.getBackgroundColor();
//				colorHandle.setValue("gray");
//				styleHandle.setTextAlign(DesignChoiceConstants.TEXT_ALIGN_CENTER);
//				styleHandle.setVerticalAlign(DesignChoiceConstants.VERTICAL_ALIGN_MIDDLE);
//				styleHandle.setBorderBottomStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
//				styleHandle.setBorderTopStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
//				styleHandle.setBorderRightStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
//				styleHandle.setBorderLeftStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
//			}
//
//			// Mise en place du contenu et du formatage des cellules d'une ligne
//			// courante
//			RowHandle tableDetail = (RowHandle) tableHandle.getDetail().get(0);
//
//			i = -1;
//			iterateurListeColonne = listeColonne.iterator();
//			while (iterateurListeColonne.hasNext()) {
//				i = i + 1;
//				valeur = iterateurListeColonne.next();
//				cell = (CellHandle) tableDetail.getCells().get(i);
//				DataItemHandle data = factory.newDataItem("data_" + valeur);
//				data.setResultSetColumn(valeur);
//				cell.getContent().add(data);
//
//				// formatage du css
//				styleHandle = cell.getPrivateStyle();
//				styleHandle.setTextAlign(DesignChoiceConstants.TEXT_ALIGN_CENTER);
//				styleHandle.setBorderBottomStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
//				styleHandle.setBorderTopStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
//				styleHandle.setBorderRightStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
//				styleHandle.setBorderLeftStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
//
//			}
//
//			// On ajoute finalement l'élément mise en forme
//			design.getBody().add(tableHandle);
//
//			// On sauvegarde finalement le rapport
//			String cheminRapport = repertoireCourant + "/reports/testBirt.rptdesign";
//			design.saveAs(cheminRapport);
//			design.close();
//
//			// Arrêt de la plateforme
//			Platform.shutdown();
//
//		} catch (BirtException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
