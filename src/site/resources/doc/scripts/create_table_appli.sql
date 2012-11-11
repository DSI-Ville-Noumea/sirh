--==============================================================
-- Table: DOCUMENT_ASSOCIE
--==============================================================
create table SIRH.DOCUMENT_ASSOCIE
(
   ID_DOCUMENT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_TYPE_DOCUMENT INTEGER not null,
   CLASSE_DOCUMENT VARCHAR(30),
   NOM_DOCUMENT VARCHAR(30) not null,
   LIEN_DOCUMENT VARCHAR(255) not null,
   DATE_DOCUMENT DATE,
   COMMENTAIRE VARCHAR(100),
   constraint SIRH.PK_DOCUMENT_ASSOCIE
   primary key (ID_DOCUMENT),		 
   constraint SIRH.FK_TYP_DOC
         foreign key (ID_TYPE_DOCUMENT)
         references SIRH.P_TYPE_DOCUMENT(ID_TYPE_DOCUMENT)
);
--==============================================================
-- Table: AGENT
--==============================================================
create table SIRH.AGENT
(
   ID_AGENT INTEGER not null,
   ID_VOIE NUMERIC(4),
   ID_COLLECTIVITE INTEGER not null,
   ID_SITUATION_FAMILIALE INTEGER not null,
   --ID_STATUT INTEGER not null,
   ID_ETAT_SERVICE INTEGER,
   CPOS_VILLE_DOM NUMERIC(5),
   CCOM_VILLE_DOM NUMERIC(5),
   CPOS_VILLE_BP NUMERIC(5),
   CCOM_VILLE_BP NUMERIC(5),
   NOMATR INTEGER not null,
   NOM_MARITAL VARCHAR(60),
   PRENOM VARCHAR(60) not null,
   PRENOM_USAGE VARCHAR(60) not null,
   CIVILITE VARCHAR(1),
   NOM_PATRONYMIQUE VARCHAR(60) not null,
   NOM_USAGE VARCHAR(60),
   DATE_NAISSANCE DATE not null,
   DATE_DECES DATE,
   SEXE VARCHAR(1) not null,
   DATE_PREMIERE_EMBAUCHE DATE not null,
   DATE_DERNIERE_EMBAUCHE DATE,
   NATIONALITE VARCHAR(1) not null,
   CODE_PAYS_NAISS_ET NUMERIC(5),
   CODE_COMMUNE_NAISS_ET NUMERIC(3),
   CODE_COMMUNE_NAISS_FR NUMERIC(5),
   NUM_CARTE_SEJOUR VARCHAR(20),
   DATE_VALIDITE_CARTE_SEJOUR DATE,
   NUM_RUE VARCHAR(4),
   NUM_RUE_BIS_TER VARCHAR(3),
   ADRESSE_COMPLEMENTAIRE VARCHAR(100),
   BP VARCHAR(5),
   CD_BANQUE DECIMAL(5),
   CD_GUICHET DECIMAL(5),
   NUM_COMPTE VARCHAR(11) not null,
   RIB NUMERIC(2) not null,
   INTITULE_COMPTE VARCHAR(50) not null,
   VCAT VARCHAR(1),
   DEBUT_SERVICE DATE,
   FIN_SERVICE DATE,
   NUM_CAFAT VARCHAR(15),
   NUM_RUAMM VARCHAR(15),
   NUM_MUTUELLE VARCHAR(15),
   NUM_CRE VARCHAR(15),
   NUM_IRCAFEX VARCHAR(15),
   NUM_CLR VARCHAR(15),
   CODE_ELECTION VARCHAR(1),
   QUARTIER VARCHAR(20),
   RUE_NON_NOUMEA VARCHAR(120),
   -- champ ajouté car on avait l'info dans MAIRIE
   -- pour le moment pas utilisé dans l'appli mais peut etre un jour
   constraint SIRH.PK_AGENT
   primary key (ID_AGENT),
   constraint SIRH.FK_AGENT_VOIE
         foreign key (ID_VOIE)
         references MAIRIE.SIVOIE (CDVOIE),
   constraint SIRH.FK_AGENT_GUICHET
         foreign key (CD_BANQUE, CD_GUICHET)
         references MAIRIE.SIGUIC (CDBANQ, CDGUIC),
   constraint SIRH.FK_VILLE_DOM
         foreign key (CPOS_VILLE_DOM, CCOM_VILLE_DOM)
         references MAIRIE.SICDPO (CDCPOS, CODCOM),
   constraint SIRH.FK_VILLE_BP
         foreign key (CPOS_VILLE_BP, CCOM_VILLE_BP)
         references MAIRIE.SICDPO (CDCPOS, CODCOM),
   constraint SIRH.FK_COLLECTIVITE
         foreign key (ID_COLLECTIVITE)
         references SIRH.R_COLLECTIVITE (ID_COLLECTIVITE),
   constraint SIRH.FK_LIEU_NAISS_ET
         foreign key (CODE_PAYS_NAISS_ET, CODE_COMMUNE_NAISS_ET)
         references MAIRIE.SIVIET(CODPAY, SCODPA),
   constraint SIRH.FK_LIEU_NAISS_FR
         foreign key (CODE_COMMUNE_NAISS_FR)
         references MAIRIE.SICOMM(CODCOM),		 
   constraint SIRH.FK_SITUATION_FAMILIALE
         foreign key (ID_SITUATION_FAMILIALE)
         references SIRH.R_SITUATION_FAMILIALE (ID_SITUATION),		 
   constraint SIRH.FK_ETAT_SERVICE
         foreign key (ID_ETAT_SERVICE)
         references SIRH.R_ETAT_SERVICE_MILITAIRE (ID_ETAT_SERVICE)
);
--==============================================================
-- Table: ENFANT
--==============================================================
create table SIRH.ENFANT
(
   ID_ENFANT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_DOCUMENT INTEGER,
   NOM VARCHAR(100) not null,
   PRENOM VARCHAR(100) not null,
   SEXE VARCHAR(1) not null,
   DATE_NAISSANCE DATE not null,
   CODE_PAYS_NAISS_ET NUMERIC(5),
   CODE_COMMUNE_NAISS_ET NUMERIC(3),
   CODE_COMMUNE_NAISS_FR NUMERIC(5),
   DATE_DECES DATE,
   NATIONALITE VARCHAR(1) not null,
   COMMENTAIRE VARCHAR(30),
   constraint SIRH.PK_ENFANT
   primary key (ID_ENFANT),
   constraint SIRH.FK_ENFANT_DOC
         foreign key (ID_DOCUMENT)
         references SIRH.DOCUMENT_ASSOCIE (ID_DOCUMENT)
);
--==============================================================
-- Table: SCOLARITE
--==============================================================
create table SIRH.SCOLARITE
(
   ID_SCOLARITE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_ENFANT INTEGER not null,
   DATE_DEBUT_SCOLARITE DATE not null,
   DATE_FIN_SCOLARITE DATE,
   constraint SIRH.PK_SCOLARITE
   primary key (ID_SCOLARITE),
   constraint SIRH.FK_ENFANT
         foreign key (ID_ENFANT)
         references SIRH.ENFANT (ID_ENFANT)
);
--==============================================================
-- Table: DIPLOME
--==============================================================
create table SIRH.DIPLOME_AGENT
(
   ID_DIPLOME INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_TITRE_DIPLOME INTEGER not null,
   ID_SPECIALITE_DIPLOME INTEGER not null,
   ID_AGENT INTEGER not null,
   ID_DOCUMENT INTEGER,
   --SPECIALITE_DIPLOME VARCHAR(100) not null,
   --DATE_OBTENTION DATE not null,
   DATE_OBTENTION DATE,
   --NOM_ECOLE VARCHAR(100) not null,
   NOM_ECOLE VARCHAR(100),
   DATE_VALIDITE DATE,
   constraint SIRH.PK_DIPLOME
   primary key (ID_DIPLOME),
   constraint SIRH.FK_DOC
         foreign key (ID_DOCUMENT)
         references SIRH.DOCUMENT_ASSOCIE (ID_DOCUMENT),
   constraint SIRH.FK_DIPL_AGENT
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_TITRE_DIPL
         foreign key (ID_TITRE_DIPLOME)
         references SIRH.P_TITRE_DIPLOME (ID_TITRE_DIPLOME),
	constraint SIRH.FK_SPEC_DIPL
         foreign key (ID_SPECIALITE_DIPLOME)
         references SIRH.P_SPECIALITE_DIPLOME (ID_SPECIALITE_DIPLOME)
);
--==============================================================
-- Table: AUTRE_ADMIN_AGENT
--==============================================================
create table SIRH.AUTRE_ADMIN_AGENT
(
   ID_AUTRE_ADMIN INTEGER not null,
   ID_AGENT INTEGER not null,
   DATE_ENTREE DATE not null,
   DATE_SORTIE DATE,
   constraint SIRH.PK_AUTRE_ADMIN_AGENT
   primary key (ID_AUTRE_ADMIN, ID_AGENT, DATE_ENTREE),
   constraint SIRH.FK_AUTRE_ADMIN
         foreign key (ID_AUTRE_ADMIN)
         references SIRH.R_AUTRE_ADMIN (ID_AUTRE_ADMIN),
   constraint SIRH.FK_AGENT
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT)
);
--==============================================================
-- Table: CASIER_JUDICIAIRE
--==============================================================
create table SIRH.CASIER_JUDICIAIRE
(
   ID_CASIER_JUD INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_AGENT INTEGER not null,
   ID_DOCUMENT INTEGER,
   NUM_EXTRAIT VARCHAR(5) not null,
   DATE_EXTRAIT DATE not null,
   PRIVATION_DROITS_CIV SMALLINT not null,
   COMM_EXTRAIT VARCHAR(100),
   constraint SIRH.PK_CASIER_JUDICIAIRE
   primary key (ID_CASIER_JUD),
   constraint SIRH.FK_AGT_CASIER
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_DOC_CASIER
         foreign key (ID_DOCUMENT)
         references SIRH.DOCUMENT_ASSOCIE (ID_DOCUMENT)
);
--==============================================================
-- Table: CONTRAT
--==============================================================
create table SIRH.CONTRAT
(
   ID_CONTRAT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_TYPE_CONTRAT INTEGER not null,
   ID_MOTIF INTEGER not null,
   ID_AGENT INTEGER not null,
   ID_DOCUMENT INTEGER,
   NUM_CONTRAT VARCHAR(10) not null,
   AVENANT SMALLINT not null,
   ID_CONTRAT_REF INTEGER,
   DATDEB DATE not null,
   DATE_FIN_PERIODE_ESS DATE not null,
   DATE_FIN DATE,
   JUSTIFICATION VARCHAR(100) not null,
   constraint SIRH.PK_CONTRAT
   primary key (ID_CONTRAT),
   constraint SIRH.FK_AGT_CONTRAT
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_DOC_CONTRAT
         foreign key (ID_DOCUMENT)
         references SIRH.DOCUMENT_ASSOCIE (ID_DOCUMENT),
   constraint SIRH.FK_TYPE_CONTRAT
         foreign key (ID_TYPE_CONTRAT)
         references SIRH.R_TYPE_CONTRAT (ID_TYPE_CONTRAT),
   constraint SIRH.FK_MOTIF
         foreign key (ID_MOTIF)
         references SIRH.R_MOTIF (ID_MOTIF)
);
--==============================================================
-- Table: COMPETENCE
--==============================================================
create table SIRH.COMPETENCE
(
   ID_COMPETENCE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_TYPE_COMPETENCE INTEGER not null,
   NOM_COMPETENCE VARCHAR(255) not null,
   ID_MAIRIE INTEGER,
   --champ ID_MAIRIE a supp apres la reprise
   constraint SIRH.PK_COMPETENCE
   primary key (ID_COMPETENCE),
   constraint SIRH.FK_TYPE_COMPETENCE
         foreign key (ID_TYPE_COMPETENCE)
         references SIRH.R_TYPE_COMPETENCE (ID_TYPE_COMPETENCE)
);
--==============================================================
-- Table: ACTIVITE
--==============================================================
create table SIRH.ACTIVITE
(
   ID_ACTIVITE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   --ID_DOMAINE_ACTIVITE INTEGER not null,
   NOM_ACTIVITE VARCHAR(255) not null,
   --NUM_ORDRE_ACTIVITE VARCHAR(8),
   ID_MAIRIE INTEGER,
   --champ ID_MAIRIE a supp apres la reprise
   constraint SIRH.PK_ACTIVITE
   primary key (ID_ACTIVITE)
   --constraint SIRH.FK_DOMAINE_ACTIVITE
         --foreign key (ID_DOMAINE_ACTIVITE)
         --references SIRH.P_DOMAINE_ACTIVITE (ID_DOMAINE_ACTIVITE)
);
--==============================================================
-- Table: COMPETENCE_ACTIVITE
--==============================================================
--TABLE SUPPRIMEE
/*create table SIRH.COMPETENCE_ACTIVITE
(
   ID_COMPETENCE INTEGER not null,
   ID_ACTIVITE INTEGER not null,
   constraint SIRH.PK_COMPETENCE_ACTIVITE
   primary key (ID_COMPETENCE, ID_ACTIVITE),
   constraint SIRH.FK_COMPETENCE
         foreign key (ID_COMPETENCE)
         references SIRH.COMPETENCE (ID_COMPETENCE),
   constraint SIRH.FK_ACTIVITE
         foreign key (ID_ACTIVITE)
         references SIRH.ACTIVITE (ID_ACTIVITE)
);*/
--==============================================================
-- Table: FICHE_EMPLOI
--==============================================================
create table SIRH.FICHE_EMPLOI
(
   ID_FICHE_EMPLOI INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_DOMAINE_FE INTEGER,
   ID_FAMILLE_EMPLOI INTEGER,
   --ID_FILIERE INTEGER,
   REF_MAIRIE VARCHAR(8) not null,
   CODE_ROME VARCHAR(5),
   NOM_METIER_EMPLOI VARCHAR(100) not null,
   LIEN_HIERARCHIQUE VARCHAR(100),
   DEFINITION_EMPLOI CLOB(2000) not null,
   PRECISIONS_DIPLOMES VARCHAR(100),
   ID_MAIRIE varchar(8),
   ID_CODE_ROME INTEGER,
   --champ ID_MAIRIE a supp apres la reprise
   constraint SIRH.PK_FICHE_EMPLOI
   primary key (ID_FICHE_EMPLOI),
   constraint SIRH.FK_FAMILLE_EMPLOI_FE
         foreign key (ID_FAMILLE_EMPLOI)
         references SIRH.P_FAMILLE_EMPLOI (ID_FAMILLE_EMPLOI),
   constraint SIRH.FK_DOMAINE_FE
         foreign key (ID_DOMAINE_FE)
         references SIRH.P_DOMAINE_FE (ID_DOMAINE_FE),
   --constraint SIRH.FK_FILIERE_FE
         --foreign key (ID_FILIERE)
         --references SIRH.P_FILIERE (ID_FILIERE)
   constraint SIRH.FK_CODE_ROME_FE
         foreign key (ID_CODE_ROME)
         references SIRH.P_CODE_ROME(ID_CODE_ROME)
);
--==============================================================
-- Table: AUTRE_APPELLATION
--==============================================================
create table SIRH.AUTRE_APPELLATION_EMPLOI
(
   ID_AUTRE_APPELLATION_EMPLOI INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_FICHE_EMPLOI INTEGER not null,
   LIB_AUTRE_APPELLATION_EMPLOI VARCHAR(100) not null,
   constraint SIRH.PK_AUTRE_APPEL_EMPLOI
   primary key (ID_AUTRE_APPELLATION_EMPLOI),
   constraint SIRH.FK_AUTRE_APPELLATION_FE
         foreign key (ID_FICHE_EMPLOI)
         references SIRH.FICHE_EMPLOI (ID_FICHE_EMPLOI)
);
--==============================================================
-- Table: CATEGORIE_FE
--==============================================================
create table SIRH.CATEGORIE_FE
(
   ID_CATEGORIE_STATUT INTEGER not null,
   ID_FICHE_EMPLOI INTEGER not null,
   constraint SIRH.PK_CATEGORIE_FE
   primary key (ID_CATEGORIE_STATUT, ID_FICHE_EMPLOI),
   constraint SIRH.FK_CATEGORIE_FE
         foreign key (ID_CATEGORIE_STATUT)
         references SIRH.P_CATEGORIE_STATUT (ID_CATEGORIE_STATUT),
   constraint SIRH.FK_FE_CATEGORIE
         foreign key (ID_FICHE_EMPLOI)
         references SIRH.FICHE_EMPLOI (ID_FICHE_EMPLOI)
);
--==============================================================
-- Table: CADRE_EMPLOI_FE
--==============================================================
create table SIRH.CADRE_EMPLOI_FE
(
   ID_CADRE_EMPLOI INTEGER not null,
   ID_FICHE_EMPLOI INTEGER not null,
   constraint SIRH.PK_CADRE_EMPLOI_FE
   primary key (ID_CADRE_EMPLOI, ID_FICHE_EMPLOI),
   constraint SIRH.FK_CADRE_EMPLOI
         foreign key (ID_CADRE_EMPLOI)
         references SIRH.P_CADRE_EMPLOI (ID_CADRE_EMPLOI),
   constraint SIRH.FK_FE_CADRE_EMPLOI
         foreign key (ID_FICHE_EMPLOI)
         references SIRH.FICHE_EMPLOI (ID_FICHE_EMPLOI)
);
--==============================================================
-- Table: DIPLOME_FE
--==============================================================
create table SIRH.DIPLOME_FE
(
   ID_DIPLOME_GENERIQUE INTEGER not null,
   ID_FICHE_EMPLOI INTEGER not null,
   constraint SIRH.PK_DIPLOME_FE
   primary key (ID_DIPLOME_GENERIQUE, ID_FICHE_EMPLOI),
   constraint SIRH.FK_FE_DIPL_GEN
         foreign key (ID_DIPLOME_GENERIQUE)
         references SIRH.P_DIPLOME_GENERIQUE (ID_DIPLOME_GENERIQUE),
   constraint SIRH.FK_DIPL_GEN_FE
         foreign key (ID_FICHE_EMPLOI)
         references SIRH.FICHE_EMPLOI (ID_FICHE_EMPLOI)
);
--==============================================================
-- Table: NIVEAU_ETUDE_FE
--==============================================================
create table SIRH.NIVEAU_ETUDE_FE
(
   ID_NIVEAU_ETUDE INTEGER not null,
   ID_FICHE_EMPLOI INTEGER not null,
   constraint SIRH.PK_NIVEAU_ETUDE_FE
   primary key (ID_NIVEAU_ETUDE, ID_FICHE_EMPLOI),
   constraint SIRH.FK_FE_NIV_ETUDE
         foreign key (ID_NIVEAU_ETUDE)
         references SIRH.R_NIVEAU_ETUDE (ID_NIVEAU_ETUDE),
   constraint SIRH.FK_NIV_ETUDE_FE
         foreign key (ID_FICHE_EMPLOI)
         references SIRH.FICHE_EMPLOI (ID_FICHE_EMPLOI)
);
--==============================================================
-- Table: VISITE_MEDICALE
--==============================================================
create table SIRH.VISITE_MEDICALE
(
   ID_VISITE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_AGENT INTEGER not null,
   ID_MEDECIN INTEGER not null,
   ID_RECOMMANDATION INTEGER not null,
   DATE_DERNIERE_VISITE DATE not null,
   DUREE_VALIDITE INTEGER not null,
   APTE SMALLINT not null,
   constraint SIRH.PK_VISITE_MEDICALE
   primary key (ID_VISITE),
   constraint SIRH.FK_AGT_VISITE
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_MEDECIN
         foreign key (ID_MEDECIN)
         references SIRH.P_MEDECIN (ID_MEDECIN),
   constraint SIRH.FK_RECOMMANDATION
         foreign key (ID_RECOMMANDATION)
         references SIRH.P_RECOMMANDATION (ID_RECOMMANDATION)
);
--==============================================================
-- Table: INAPTITUDE
--==============================================================
create table SIRH.INAPTITUDE
(
   ID_INAPTITUDE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_VISITE INTEGER not null,
   ID_TYPE_INAPTITUDE INTEGER not null,
   DATE_DEBUT_INAPTITUDE DATE not null,
   DUREE_ANNEE INTEGER,
   DUREE_MOIS INTEGER,
   DUREE_JOUR INTEGER,
   constraint SIRH.PK_INAPTITUDE
   primary key (ID_INAPTITUDE),
   constraint SIRH.INPATITUDE_VISITE
         foreign key (ID_VISITE)
         references SIRH.VISITE_MEDICALE (ID_VISITE),
   constraint SIRH.FK_TYPE_INAPTITUDE
         foreign key (ID_TYPE_INAPTITUDE)
         references SIRH.P_TYPE_INAPTITUDE (ID_TYPE_INAPTITUDE)
);
--==============================================================
-- Table: ACCIDENT_TRAVAIL
--==============================================================
create table SIRH.ACCIDENT_TRAVAIL
(
   ID_AT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_TYPE_AT INTEGER not null,
   ID_SIEGE INTEGER not null,
   ID_AGENT INTEGER not null,
   DATE_AT DATE not null,
   DATE_AT_INITIAL DATE,
   NB_JOURS_ITT INTEGER,
   constraint SIRH.PK_ACCIDENT_TRAVAIL
   primary key (ID_AT),
   constraint SIRH.FK_AGT_AT
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_SIEGE_LESION
         foreign key (ID_SIEGE)
         references SIRH.P_SIEGE_LESION (ID_SIEGE),
   constraint SIRH.FK_TYPE_AT
         foreign key (ID_TYPE_AT)
         references SIRH.P_TYPE_AT (ID_TYPE_AT)
);
--==============================================================
-- Table: HANDICAP
--==============================================================
create table SIRH.HANDICAP
(
   ID_HANDICAP INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_AGENT INTEGER not null,
   ID_TYPE_HANDICAP INTEGER not null,
   ID_MALADIE_PRO INTEGER,
   POURCENT_INCAPACITE INTEGER not null,
   RECONNAISSANCE_MP SMALLINT not null,
   DATE_DEBUT_HANDICAP DATE not null,
   DATE_FIN_HANDICAP DATE,
   HANDICAP_CRDHNC SMALLINT not null,
   NUM_CARTE_CRDHNC VARCHAR(30),
   AMENAGEMENT_POSTE SMALLINT not null,
   COMMENTAIRE_HANDICAP VARCHAR(100),
   RENOUVELLEMENT SMALLINT not null,
   constraint SIRH.PK_HANDICAP
   primary key (ID_HANDICAP),
   constraint SIRH.FK_AGT_HANDICAP
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_MALADIE_PRO
         foreign key (ID_MALADIE_PRO)
         references SIRH.P_MALADIE_PRO (ID_MALADIE_PRO),
   constraint SIRH.FK_NOM_HANDICAP
         foreign key (ID_TYPE_HANDICAP)
         references SIRH.R_NOM_HANDICAP (ID_TYPE_HANDICAP)
);
--==============================================================
-- Table: FICHE_POSTE
--==============================================================
create table SIRH.FICHE_POSTE
(
   ID_FICHE_POSTE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_TITRE_POSTE INTEGER,
   ID_ENTITE_GEO DECIMAL(5),
   ID_BUDGET INTEGER,
   ID_STATUT_FP INTEGER,
   ID_RESPONSABLE INTEGER,
   ID_REMPLACEMENT INTEGER,
   ID_CDTHOR_BUD DECIMAL(2) not null,
   ID_CDTHOR_REG DECIMAL(2) not null,
   ID_SERVI CHAR(4) not null,
   DATE_FIN_VALIDITE_FP DATE,
   OPI VARCHAR(5),
   NFA VARCHAR(5) not null,
   MISSIONS CLOB(2000) not null,
   ANNEE_CREATION NUMERIC(4) not null,
   CODE_GRADE_GENERIQUE CHAR(4),
   NUM_FP VARCHAR(8) not null,
   DATE_DEBUT_VALIDITE_FP DATE,
   DATE_DEB_APPLI_SERV DATE,
   DATE_FIN_APPLI_SERV DATE,
   constraint SIRH.PK_FICHE_POSTE
   primary key (ID_FICHE_POSTE),   
   constraint SIRH.FK_FP_REFSERVI
         foreign key (ID_SERVI)
         references MAIRIE.SISERV (SERVI),
   constraint SIRH.FK_STATUT_FP
         foreign key (ID_STATUT_FP)
         references SIRH.R_STATUT_FP (ID_STATUT_FP),
   constraint SIRH.FK_GRADE_FP
         foreign key (CODE_GRADE_GENERIQUE)
         references MAIRIE.SPGENG (CDGENG),
   constraint SIRH.FK_ENTITE_GEO_FP
         foreign key (ID_ENTITE_GEO)
         references MAIRIE.SILIEU (CDLIEU),
   constraint SIRH.FK_BUDGET_FP
         foreign key (ID_BUDGET)
         references SIRH.R_BUDGET (ID_BUDGET),
   constraint SIRH.FK_TITRE_FP
         foreign key (ID_TITRE_POSTE)
         references SIRH.P_TITRE_POSTE (ID_TITRE_POSTE),
   constraint SIRH.FK_BUDGETE
         foreign key (ID_CDTHOR_BUD)
         references MAIRIE.SPBHOR (CDTHOR),
   constraint SIRH.FK_REGLEMENTAI
         foreign key (ID_CDTHOR_REG)
         references MAIRIE.SPBHOR (CDTHOR),
   constraint SIRH.FK_LIEN_HIERAR
         foreign key (ID_RESPONSABLE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE),
   constraint SIRH.FK_REMPLACEMENT
         foreign key (ID_REMPLACEMENT)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE)   
);
--==============================================================
-- Table: CADRE_EMPLOI_FP
--==============================================================
create table SIRH.CADRE_EMPLOI_FP
(
   ID_CADRE_EMPLOI INTEGER not null,
   ID_FICHE_POSTE INTEGER not null,
   constraint SIRH.PK_CADRE_EMPLOI_FP
   primary key (ID_CADRE_EMPLOI, ID_FICHE_POSTE),
   constraint SIRH.FK_CADRE_EMPLOI_FP
         foreign key (ID_CADRE_EMPLOI)
         references SIRH.P_CADRE_EMPLOI (ID_CADRE_EMPLOI),
   constraint SIRH.FK_FP_CADRE_EMPLOI
         foreign key (ID_FICHE_POSTE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE)
);
--==============================================================
-- Table: ACTIVITE_FE
--==============================================================
create table SIRH.ACTIVITE_FE
(
   ID_ACTIVITE INTEGER not null,
   ID_FICHE_EMPLOI INTEGER not null,
   --ACTIVITE_PRINCIPALE SMALLINT not null,
   constraint SIRH.PK_ACTIVITE_FE
   primary key (ID_ACTIVITE, ID_FICHE_EMPLOI),
   constraint SIRH.FK_ACTIVITE_FE
         foreign key (ID_ACTIVITE)
         references SIRH.ACTIVITE (ID_ACTIVITE),
   constraint SIRH.FK_FE_ACTIVITE
         foreign key (ID_FICHE_EMPLOI)
         references SIRH.FICHE_EMPLOI (ID_FICHE_EMPLOI)
);
--==============================================================
-- Table: COMPETENCE_FE
--==============================================================
create table SIRH.COMPETENCE_FE
(
   ID_COMPETENCE INTEGER not null,
   ID_FICHE_EMPLOI INTEGER not null,
   constraint SIRH.PK_COMPETENCE_FE
   primary key (ID_COMPETENCE, ID_FICHE_EMPLOI),
   constraint SIRH.FK_COMPETENCE_FE
         foreign key (ID_COMPETENCE)
         references SIRH.COMPETENCE (ID_COMPETENCE),
   constraint SIRH.FK_FE_COMPETENCE
         foreign key (ID_FICHE_EMPLOI)
         references SIRH.FICHE_EMPLOI (ID_FICHE_EMPLOI)
);
--==============================================================
-- Table: RECRUTEMENT
--==============================================================
create table SIRH.RECRUTEMENT
(
   ID_RECRUTEMENT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_MOTIF_RECRUT INTEGER,
   ID_MOTIF_NON_RECRUT INTEGER,
   ID_FICHE_POSTE INTEGER not null,
   REFERENCE_SES INTEGER not null,
   REFERENCE_MAIRIE VARCHAR(30) not null,
   REFERENCE_DRHFPNC VARCHAR(30) not null,
   DATE_OUVERTURE DATE not null,
   DATE_VALIDATION DATE not null,
   DATE_CLOTURE DATE,
   DATE_TRANSMISSION DATE,
   DATE_REPONSE DATE,
   NB_CAND_RECUES INTEGER,
   NOM_AGENT_RECRUTE CHAR(100),
   constraint SIRH.PK_RECRUTEMENT
   primary key (ID_RECRUTEMENT),
   constraint SIRH.FK_FP_RECRUT
         foreign key (ID_FICHE_POSTE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE),
   constraint SIRH.FK_MOTIF_RECRUT
         foreign key (ID_MOTIF_RECRUT)
         references SIRH.P_MOTIF_RECRUT (ID_MOTIF_RECRUT),
   constraint SIRH.FK_MOTIF_NON_RECRUT
         foreign key (ID_MOTIF_NON_RECRUT)
         references SIRH.P_MOTIF_NON_RECRUT (ID_MOTIF_NON_RECRUT)
);
/*==============================================================*/
/* Table: DIPLOME_FP                                            */
/*==============================================================*/
create table SIRH.DIPLOME_FP 
(
    ID_DIPLOME_GENERIQUE integer                        not null,
    ID_FICHE_POSTE       integer                        not null,
    constraint SIRH.PK_DIPLOME_FP primary key (ID_DIPLOME_GENERIQUE, ID_FICHE_POSTE)
);
/*==============================================================*/
/* Table: NIVEAU_ETUDE_FP                                       */
/*==============================================================*/
create table SIRH.NIVEAU_ETUDE_FP 
(
    ID_NIVEAU_ETUDE      integer                        not null,
    ID_FICHE_POSTE       integer                        not null,
    constraint SIRH.PK_NIVEAU_ETUDE_FP primary key (ID_NIVEAU_ETUDE, ID_FICHE_POSTE)
);
--==============================================================
-- Table: ACTIVITE_FP
--==============================================================
create table SIRH.ACTIVITE_FP
(
   ID_ACTIVITE INTEGER not null,
   ID_FICHE_POSTE INTEGER not null,
   ACTIVITE_PRINCIPALE SMALLINT not null,
   constraint SIRH.PK_ACTIVITE_FP
   primary key (ID_ACTIVITE, ID_FICHE_POSTE),
   constraint SIRH.FK_ACTIVITE_FP
         foreign key (ID_ACTIVITE)
         references SIRH.ACTIVITE (ID_ACTIVITE),
   constraint SIRH.FK_FP_ACTIVITE
         foreign key (ID_FICHE_POSTE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE)
);
--==============================================================
-- Table: COMPETENCE_FP
--==============================================================
create table SIRH.COMPETENCE_FP
(
   ID_COMPETENCE INTEGER not null,
   ID_FICHE_POSTE INTEGER not null,
   constraint SIRH.PK_COMPETENCE_FP
   primary key (ID_COMPETENCE, ID_FICHE_POSTE),
   constraint SIRH.FK_COMPETENCE_FP
         foreign key (ID_COMPETENCE)
         references SIRH.COMPETENCE (ID_COMPETENCE),
   constraint SIRH.FK_FP_COMPETENCE
         foreign key (ID_FICHE_POSTE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE)
);
--==============================================================
-- Table: DELEGATION
--==============================================================
create table SIRH.DELEGATION
(
   ID_DELEGATION INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_TYPE_DELEGATION INTEGER not null,
   LIB_DELEGATION VARCHAR(100) not null,
   constraint SIRH.PK_DELEGATION
   primary key (ID_DELEGATION),
   constraint SIRH.FK_TYPE_DELEGATION
         foreign key (ID_TYPE_DELEGATION)
         references SIRH.P_TYPE_DELEGATION (ID_TYPE_DELEGATION)
);
--==============================================================
-- Table: AVANTAGE_NATURE
--==============================================================
create table SIRH.AVANTAGE_NATURE
(
   ID_AVANTAGE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   NUM_RUBRIQUE NUMERIC(4),
   ID_TYPE_AVANTAGE INTEGER not null,
   ID_NATURE_AVANTAGE INTEGER,
   MONTANT DECIMAL(8),
   constraint SIRH.PK_AVANTAGE_NATURE
   primary key (ID_AVANTAGE),
   constraint SIRH.FK_AN_TYPE_AVANTAGE
         foreign key (ID_TYPE_AVANTAGE)
         references SIRH.P_TYPE_AVANTAGE (ID_TYPE_AVANTAGE),
   constraint SIRH.FK_AN_NATURE_AVANTAGE
         foreign key (ID_NATURE_AVANTAGE)
         references SIRH.P_NATURE_AVANTAGE (ID_NATURE_AVANTAGE),
   constraint SIRH.FK_AN_RUBRIQUE
         foreign key (NUM_RUBRIQUE)
         references MAIRIE.SPRUBR (NORUBR)
);
--==============================================================
-- Table: REGIME_INDEMNITAIRE
--==============================================================
create table SIRH.REGIME_INDEMNITAIRE
(
   ID_REG_INDEMN INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_TYPE_REG_INDEMN INTEGER not null,
   NUM_RUBRIQUE NUMERIC(4),
   FORFAIT DECIMAL(8),
   NOMBRE_POINTS INTEGER,
   constraint SIRH.PK_REG_INDEMN
   primary key (ID_REG_INDEMN),
   constraint SIRH.FK_RI_TYPE_REG_INDEMN
         foreign key (ID_TYPE_REG_INDEMN)
         references SIRH.P_TYPE_REG_INDEMN (ID_TYPE_REG_INDEMN),
   constraint SIRH.FK_RI_RUBRIQUE
         foreign key (NUM_RUBRIQUE)
         references MAIRIE.SPRUBR (NORUBR)
);
--==============================================================
-- Table: AFFECTATION
--==============================================================
create table SIRH.AFFECTATION
(
   ID_AFFECTATION INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_MOTIF_AFFECTATION INTEGER not null,
   ID_FICHE_POSTE INTEGER not null,
   ID_AGENT INTEGER not null,
   REF_ARRETE_AFF VARCHAR(8),
   DATE_ARRETE DATE not null,
   DATE_DEBUT_AFF DATE not null,
   DATE_FIN_AFF DATE,
   TEMPS_TRAVAIL VARCHAR(3),
   CODE_ECOLE VARCHAR(3),
   ID_FICHE_POSTE_SECONDAIRE INTEGER,
   COMMENTAIRE VARCHAR(100),
   -- champ ajouter car on avait l'info dans MAIRIE
   -- pour le moment pas utilisé dans l'appli mais peut etre un jour
   constraint SIRH.PK_AFFECTATION
   primary key (ID_AFFECTATION),
   constraint SIRH.FK_AFFECT_AGEN
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_AFFECT_FPOS
         foreign key (ID_FICHE_POSTE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE),
   constraint SIRH.FK_AFF_MOTIF_AFF
         foreign key (ID_MOTIF_AFFECTATION)
         references SIRH.P_MOTIF_AFFECTATION (ID_MOTIF_AFFECTATION),
   constraint SIRH.FK_AFFECT_FPOS_SEC
         foreign key (ID_FICHE_POSTE_SECONDAIRE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE)
);
--==============================================================
-- Table: DELEGATION_FP
--==============================================================
create table SIRH.DELEGATION_FP
(
   ID_DELEGATION INTEGER not null,
   ID_FICHE_POSTE INTEGER not null,
   constraint SIRH.PK_DELEGATION_FP
   primary key (ID_DELEGATION, ID_FICHE_POSTE),
   constraint SIRH.FK_DELEGFP_DELEG
         foreign key (ID_DELEGATION)
         references SIRH.DELEGATION (ID_DELEGATION),
   constraint SIRH.FK_DELEGFP_FP
         foreign key (ID_FICHE_POSTE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE)
);
--==============================================================
-- Table: DELEGATION_AFF
--==============================================================
create table SIRH.DELEGATION_AFF
(
   ID_AFFECTATION INTEGER not null,
   ID_DELEGATION INTEGER not null,
   constraint SIRH.PK_DELEGATION_AFF
   primary key (ID_AFFECTATION, ID_DELEGATION),
   constraint SIRH.FK_DELEGAFF_AFF
         foreign key (ID_AFFECTATION)
         references SIRH.AFFECTATION (ID_AFFECTATION),
   constraint SIRH.FK_DELEGAFF_DELEG
         foreign key (ID_DELEGATION)
         references SIRH.DELEGATION (ID_DELEGATION)
);
--==============================================================
-- Table: AVANTAGE_NATURE_FP
--==============================================================
create table SIRH.AVANTAGE_NATURE_FP
(
   ID_FICHE_POSTE INTEGER not null,
   ID_AVANTAGE INTEGER not null,
   constraint SIRH.PK_AN_FP
   primary key (ID_FICHE_POSTE, ID_AVANTAGE),
   constraint SIRH.FK_ANFP_FP
         foreign key (ID_FICHE_POSTE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE),
   constraint SIRH.FK_ANFP_AN
         foreign key (ID_AVANTAGE)
         references SIRH.AVANTAGE_NATURE (ID_AVANTAGE)
);
--==============================================================
-- Table: AVANTAGE_NATURE_AFF
--==============================================================
create table SIRH.AVANTAGE_NATURE_AFF
(
   ID_AVANTAGE INTEGER not null,
   ID_AFFECTATION INTEGER not null,
   constraint SIRH.PK_AVANTAGE_AFF
   primary key (ID_AVANTAGE, ID_AFFECTATION),
   constraint SIRH.FK_ANAFF_AN
         foreign key (ID_AVANTAGE)
         references SIRH.AVANTAGE_NATURE (ID_AVANTAGE),
   constraint SIRH.FK_ANAFF_AFF
         foreign key (ID_AFFECTATION)
         references SIRH.AFFECTATION (ID_AFFECTATION)
);
--==============================================================
-- Table: REG_INDEMN_FP
--==============================================================
create table SIRH.REG_INDEMN_FP
(
   ID_FICHE_POSTE INTEGER not null,
   ID_REGIME INTEGER not null,
   constraint SIRH.PK_RI_FP
   primary key (ID_FICHE_POSTE, ID_REGIME),
   constraint SIRH.FK_RIFP_FP
         foreign key (ID_FICHE_POSTE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE),
   constraint SIRH.FK_RIFP_RI
         foreign key (ID_REGIME)
         references SIRH.REGIME_INDEMNITAIRE (ID_REG_INDEMN)
);
--==============================================================
-- Table: REG_INDEMN_AFF
--==============================================================
create table SIRH.REG_INDEMN_AFF
(
   ID_REGIME INTEGER not null,
   ID_AFFECTATION INTEGER not null,
   constraint SIRH.PK_REG_INDEMN_AFF
   primary key (ID_REGIME, ID_AFFECTATION),
   constraint SIRH.FK_RIAFF_RI
         foreign key (ID_REGIME)
         references SIRH.REGIME_INDEMNITAIRE (ID_REG_INDEMN),
   constraint SIRH.FK_RIAFF_AFF
         foreign key (ID_AFFECTATION)
         references SIRH.AFFECTATION (ID_AFFECTATION)
);
/*==============================================================*/
/* Table: FE_FP                                                 */
/*==============================================================*/
create table SIRH.FE_FP 
(
    ID_FICHE_EMPLOI      integer                        not null,
    ID_FICHE_POSTE       integer                        not null,
    FE_PRIMAIRE          smallint                       not null,
    constraint SIRH.PK_FE_FP primary key (ID_FICHE_EMPLOI, ID_FICHE_POSTE),
	constraint SIRH.FK_FE_FP_FE
         foreign key (ID_FICHE_EMPLOI )
         references SIRH.FICHE_EMPLOI (ID_FICHE_EMPLOI),
	constraint SIRH.FK_FE_FP_FP
         foreign key (ID_FICHE_POSTE )
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE)
);
/*==============================================================*/
/* Table: PA_AGENT                                              */
/*==============================================================*/
create table SIRH.PA_AGENT 
(
    NOMATR               numeric(5)                     not null,
    DATDEB               numeric(8)		        not null,
    ID_AGENT             integer                        not null,
    constraint SIRH.PK_PA_AGENT primary key (NOMATR, ID_AGENT, DATDEB),
	constraint SIRH.FK_PA_AGENT_AGENT
         foreign key (ID_AGENT )
         references SIRH.AGENT (ID_AGENT),
	constraint SIRH.FK_PA_AGENT_PA
         foreign key (NOMATR, DATDEB )
         references MAIRIE.SPADMN (NOMATR, DATDEB)
);
/*==============================================================*/
/* Table: PRIME_AGENT                                              */
/*==============================================================*/
create table SIRH.PRIME_AGENT 
(
    NOMATR               numeric(5)                     not null,
    NORUBR               numeric(4)                     not null,
    DATDEB               numeric(8)		      			not null,
    ID_AGENT             integer                        not null,
    constraint SIRH.PK_PRIME_AGENT primary key (NOMATR, NORUBR, DATDEB, ID_AGENT),
	constraint SIRH.FK_PRIME_AGENT_AGENT
         foreign key (ID_AGENT )
         references SIRH.AGENT (ID_AGENT),
	constraint SIRH.FK_PRIME_AGENT_PRIME
         foreign key (NOMATR, NORUBR, DATDEB )
         references MAIRIE.SPPRIM (NOMATR, NORUBR, DATDEB)
);
/*==============================================================*/
/* Table: CHARGE_AGENT                                          */
/*==============================================================*/
create table SIRH.CHARGE_AGENT 
(
    NOMATR               numeric(5)                     not null,
    NORUBR               numeric(4)                     not null,
    DATDEB               numeric(8)		      			not null,
    ID_AGENT             integer                        not null,
    constraint SIRH.PK_CHARGE_AGENT primary key (NOMATR, NORUBR, DATDEB, ID_AGENT),
	constraint SIRH.FK_CHARGE_AGENT_AGENT
         foreign key (ID_AGENT )
         references SIRH.AGENT (ID_AGENT),
	constraint SIRH.FK_CHARGE_AGENT_CHARGE
         foreign key (NOMATR, NORUBR, DATDEB )
         references MAIRIE.SPCHGE (NOMATR, NORUBR, DATDEB)
);
/*==============================================================*/
/* Table: CARRIERE_AGENT                                        */
/*==============================================================*/
create table SIRH.CARRIERE_AGENT 
(
    NOMATR               numeric(5)                     not null,
    DATDEB               numeric(8)		      			not null,
    ID_AGENT             integer                        not null,
    constraint SIRH.PK_CARRIERE_AGENT primary key (NOMATR, DATDEB, ID_AGENT),
	constraint SIRH.FK_CARRIERE_AGENT_AGENT
         foreign key ( ID_AGENT )
         references SIRH.AGENT (ID_AGENT),
	constraint SIRH.FK_CARRIERE_AGENT_PRIME
         foreign key (NOMATR, DATDEB )
         references MAIRIE.SPCARR (NOMATR, DATDEB)
);
--==============================================================
-- Table: PARENT_ENFANT
--==============================================================
create table SIRH.PARENT_ENFANT
(
   ID_AGENT INTEGER not null,
   ID_ENFANT INTEGER not null,
   ENFANT_A_CHARGE SMALLINT not null default 0,
   constraint SIRH.PK_PARENT_ENFANT
   primary key (ID_AGENT, ID_ENFANT),
   constraint SIRH.FK_PARENT
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_ENFANT_PARENT
         foreign key (ID_ENFANT)
         references SIRH.ENFANT (ID_ENFANT)
);
--==============================================================
-- Table: CONTACT
--==============================================================
create table SIRH.CONTACT
(
   ID_CONTACT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_AGENT INTEGER,
   ID_TYPE_CONTACT INTEGER,
   DESCRIPTION VARCHAR(50) not null,
   DIFFUSABLE VARCHAR(1) not null,
   PRIORITAIRE SMALLINT not null default 0,
   constraint SIRH.PK_CONTACT
   primary key (ID_CONTACT),
   constraint SIRH.FK_AGENT_CONTA
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_TYPE_CONTACT
         foreign key (ID_TYPE_CONTACT)
         references SIRH.R_TYPE_CONTACT (ID_TYPE_CONTACT)
);
--==============================================================
-- Table: HISTO_FICHE_POSTE
--==============================================================
create table SIRH.HISTO_FICHE_POSTE
(
   ID_FICHE_POSTE INTEGER not null ,
   ID_TITRE_POSTE INTEGER,
   ID_ENTITE_GEO DECIMAL(5),
   ID_BUDGET INTEGER,
   ID_STATUT_FP INTEGER,
   ID_RESPONSABLE INTEGER,
   ID_REMPLACEMENT INTEGER,
   ID_CDTHOR_BUD DECIMAL(2) not null,
   ID_CDTHOR_REG DECIMAL(2) not null,
   ID_SERVI CHAR(4) not null,
   DATE_FIN_VALIDITE_FP DATE,
   OPI VARCHAR(5),
   NFA VARCHAR(5) not null,
   MISSIONS CLOB(2000) not null,
   ANNEE_CREATION NUMERIC(4) not null,
   CODE_GRADE_GENERIQUE CHAR(4),
   NUM_FP VARCHAR(8) not null,
   DATE_HISTO 			TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   TYPE_HISTO 			VARCHAR(15),
   USER_HISTO 			VARCHAR(7),
   DATE_DEBUT_VALIDITE_FP DATE,
   DATE_DEB_APPLI_SERV DATE,
   DATE_FIN_APPLI_SERV DATE,
   constraint SIRH.FK_HISTO_FP_REFSERVI
         foreign key (ID_SERVI)
         references MAIRIE.SISERV (SERVI),
   constraint SIRH.FK_HISTO_STATUT_FP
         foreign key (ID_STATUT_FP)
         references SIRH.R_STATUT_FP (ID_STATUT_FP),
   constraint SIRH.FK_HISTO_GRADE_FP
         foreign key (CODE_GRADE_GENERIQUE)
         references MAIRIE.SPGENG (CDGENG),
   constraint SIRH.FK_HISTO_ENTITE_GEO_FP
         foreign key (ID_ENTITE_GEO)
         references MAIRIE.SILIEU (CDLIEU),
   constraint SIRH.FK_HISTO_BUDGET_FP
         foreign key (ID_BUDGET)
         references SIRH.R_BUDGET (ID_BUDGET),
   constraint SIRH.FK_HISTO_TITRE_FP
         foreign key (ID_TITRE_POSTE)
         references SIRH.P_TITRE_POSTE (ID_TITRE_POSTE),
   constraint SIRH.FK_HISTO_BUDGETE
         foreign key (ID_CDTHOR_BUD)
         references MAIRIE.SPBHOR (CDTHOR),
   constraint SIRH.FK_HISTO_REGLEMENTAI
         foreign key (ID_CDTHOR_REG)
         references MAIRIE.SPBHOR (CDTHOR),
   constraint SIRH.FK_HISTO_LIEN_HIERAR
         foreign key (ID_RESPONSABLE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE),
   constraint SIRH.FK_HISTO_REMPLACEMENT
         foreign key (ID_REMPLACEMENT)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE)   
);
--==============================================================
-- Table: HISTO_AFFECTATION
--==============================================================
create table SIRH.HISTO_AFFECTATION
(
   ID_AFFECTATION 		INTEGER not null,
   ID_MOTIF_AFFECTATION INTEGER not null,
   ID_FICHE_POSTE 		INTEGER not null,
   ID_AGENT 			INTEGER,
   REF_ARRETE_AFF 		VARCHAR(8),
   DATE_ARRETE 			DATE not null,
   DATE_DEBUT_AFF 		DATE not null,
   DATE_FIN_AFF 		DATE,
   TEMPS_TRAVAIL 		VARCHAR(3),
   DATE_HISTO 			TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   TYPE_HISTO 			VARCHAR(15),
   USER_HISTO 			VARCHAR(7),   
   CODE_ECOLE 			VARCHAR(3),
   ID_FICHE_POSTE_SECONDAIRE INTEGER,
   COMMENTAIRE VARCHAR(100),
   constraint SIRH.FK_HISTO_AFFECT_AGEN
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_HISTO_AFFECT_FPOS
         foreign key (ID_FICHE_POSTE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE),
   constraint SIRH.FK_HISTO_AFF_MOTIF_AFF
         foreign key (ID_MOTIF_AFFECTATION)
         references SIRH.P_MOTIF_AFFECTATION (ID_MOTIF_AFFECTATION),
   constraint SIRH.FK_HISTO_AFFECT_FPOS_SEC
         foreign key (ID_FICHE_POSTE_SECONDAIRE)
         references SIRH.FICHE_POSTE (ID_FICHE_POSTE)
);
--==============================================================
-- Table: HISTO_PRIME
--==============================================================
create table SIRH.HISTO_PRIME
(
   NO_MATRICULE INTEGER,
   NO_RUBRIQUE INTEGER,
   REF_ARRETE INTEGER,
   DATE_DEBUT DATE,
   DATE_FIN DATE,
   DATE_ARRETE DATE,
   MONTANT FLOAT,
   DATE_HISTO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   TYPE_HISTO VARCHAR(15),
   USER_HISTO VARCHAR(7)
);
--==============================================================
-- Table: HISTO_CHARGE
--==============================================================
create table SIRH.HISTO_CHARGE
(
   NO_MATRICULE INTEGER,
   NO_RUBRIQUE INTEGER,
   CODE_CREANCIER INTEGER,
   NO_MATE VARCHAR(15),
   CODE_CHARGE INTEGER,
   TAUX FLOAT,
   MONTANT FLOAT,
   DATE_FIN DATE,
   DATE_DEBUT DATE,
   DATE_HISTO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   TYPE_HISTO VARCHAR(15),
   USER_HISTO VARCHAR(7)
);
--==============================================================
-- Table: HISTO_POSITION_ADMINISTRATIVE
--==============================================================
create table SIRH.HISTO_POSITION_ADMINISTRATIVE
(
   NO_MATRICULE INTEGER,
   DATE_DEBUT DATE,
   CODE_POSA VARCHAR(2),
   DATE_FIN DATE,
   REF_ARR INTEGER,
   DATE_HISTO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   TYPE_HISTO VARCHAR(15),
   USER_HISTO VARCHAR(7)
);
--==============================================================
-- Table: HISTO_CARRIERE
--==============================================================
create table SIRH.HISTO_CARRIERE
(
   -- copie champ carriere
   NO_MATRICULE 	INTEGER,
   CODE_CATEGORIE 	INTEGER,
   CODE_GRADE 		VARCHAR(4),
   REF_ARRETE 		INTEGER,
   DATE_DEBUT 		DATE,
   DATE_FIN 		DATE,
   MODE_REG 		CHAR(1),
   CODE_BASE_HOR	INTEGER,
   IBA 				INTEGER,
   MONTANT_FORFAIT	FLOAT,
   CODE_EMPLOI		INTEGER,
   CODE_BASE		CHAR(1),
   CODE_TYPE_EMPLOI	INTEGER,
   CODE_BASE_HOR2	FLOAT,
   IBAN				CHAR(7),
   CODE_MOTIF_PROMO CHAR(3),
   ACC_JOUR			FLOAT,
   ACC_MOIS			FLOAT,
   ACC_ANNEE		FLOAT,
   BM_JOUR			FLOAT,
   BM_MOIS			FLOAT,
   BM_ANNEE			FLOAT,
   DATE_ARRETE		DATE,
   CDDCDICA			char(3),   
   --champ historisation
   DATE_HISTO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   TYPE_HISTO VARCHAR(15),
   USER_HISTO VARCHAR(7)
);
--==============================================================
-- Table: DOCUMENT_AGENT
--==============================================================
create table SIRH.DOCUMENT_AGENT
(
   ID_AGENT INTEGER not null,
   ID_DOCUMENT INTEGER not null,
   constraint SIRH.PK_DOCUMENT_AGENT
   primary key (ID_AGENT, ID_DOCUMENT),
   constraint SIRH.FK_DOCUMENT_AGENT
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_AGENT_DOCUMENT
         foreign key (ID_DOCUMENT)
         references SIRH.DOCUMENT_ASSOCIE (ID_DOCUMENT)
);
--==============================================================
-- Table: DROITS_GROUPE
--==============================================================
create table SIRH.DROITS_GROUPE
(
   ID_GROUPE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_GROUPE VARCHAR(50) not null,
   constraint SIRH.PK_GROUPE
   primary key (ID_GROUPE)
);
--==============================================================
-- Table: DROITS_ELEMENT
--==============================================================
create table SIRH.DROITS_ELEMENT
(
   ID_ELEMENT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_ELEMENT VARCHAR(50) not null,
   constraint SIRH.PK_ELEMENT
   primary key (ID_ELEMENT)
);
--==============================================================
-- Table: DROITS
--==============================================================
create table SIRH.DROITS
(
   ID_ELEMENT INTEGER not null,
   ID_GROUPE INTEGER not null,
   ID_TYPE_DROIT INTEGER,
   constraint SIRH.PK_DROIT
   primary key (ID_ELEMENT, ID_GROUPE),
   constraint SIRH.FK_GROUPE
         foreign key (ID_GROUPE)
         references SIRH.DROITS_GROUPE (ID_GROUPE),
   constraint SIRH.FK_ELEMENT
         foreign key (ID_ELEMENT)
         references SIRH.DROITS_ELEMENT (ID_ELEMENT),
   constraint SIRH.FK_TYPE_DROIT
         foreign key (ID_TYPE_DROIT)
         references SIRH.R_TYPE_DROIT (ID_TYPE_DROIT)
);
--==============================================================
-- Table: UTILISATEUR
--==============================================================
create table SIRH.UTILISATEUR
(
	ID_UTILISATEUR INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
	LOGIN_UTILISATEUR VARCHAR(7) not null,
	constraint SIRH.PK_UTILISATEUR
	primary key (ID_UTILISATEUR)
);	
--==============================================================
-- Table: GROUPE_UTILISATEUR
--==============================================================
create table SIRH.GROUPE_UTILISATEUR
(
	ID_UTILISATEUR INTEGER not null,
	ID_GROUPE INTEGER not null,
	constraint SIRH.PK_DROITS_UTILISATEUR
	primary key (ID_UTILISATEUR, ID_GROUPE),
	constraint SIRH.FK_UTILISATEUR
         foreign key (ID_UTILISATEUR)
         references SIRH.UTILISATEUR (ID_UTILISATEUR),
	constraint SIRH.FK_DROITS_GROUPE
         foreign key (ID_GROUPE)
         references SIRH.DROITS_GROUPE (ID_GROUPE)
);	
--==============================================================
-- Table: SERVICE_NFA
--==============================================================
create table SIRH.SERVICE_NFA
(
	CODE_SERVICE CHAR(4) not null,
	NFA VARCHAR(5) not null,
	constraint SIRH.FK_SERVICE_NFA
         foreign key (CODE_SERVICE)
         references MAIRIE.SISERV(SERVI)
);
--==============================================================
-- Table: AVANCEMENT
--==============================================================
create table SIRH.AVANCEMENT
(
   ID_AVCT NUMERIC(8) not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   ID_AVIS_CAP INTEGER,
   ID_AGENT INTEGER not null,
   ID_MOTIF_AVCT INTEGER not null,
   DIRECTION_SERVICE VARCHAR(10),
   SECTION_SERVICE VARCHAR(10),
   AGENT VARCHAR(100),
   FILIERE VARCHAR(50),
   GRADE VARCHAR(10),
   GRADE_GENERIQUE VARCHAR(10),
   CLASSE VARCHAR(60),
   ECHELON VARCHAR(60),   
   ID_NOUV_CLASSE CHAR(2),
   ID_NOUV_GRADE CHAR(4),
   ID_NOUV_GRADE_GENERIQUE CHAR(4),
   ID_NOUV_ECHELON CHAR(3),
   ANNEE NUMERIC(4) not null,
   BM_ANNEE INTEGER,
   BM_MOIS INTEGER,
   BM_JOUR INTEGER,
   ACC_ANNEE INTEGER,
   ACC_MOIS INTEGER,
   ACC_JOUR INTEGER,
   NOUV_BM_ANNEE INTEGER,
   NOUV_BM_MOIS INTEGER,
   NOUV_BM_JOUR INTEGER,
   NOUV_ACC_ANNEE INTEGER,
   NOUV_ACC_MOIS INTEGER,
   NOUV_ACC_JOUR INTEGER,
   IBA INTEGER,
   INM INTEGER,
   INA INTEGER,
   NOUV_IBA INTEGER,
   NOUV_INM INTEGER,
   NOUV_INA INTEGER,
   DATE_GRADE DATE,
   DATE_PROCHAIN_GRADE DATE,
   PERIODE_STANDARD NUMERIC(2),
   DATE_AVCT_MINI DATE,
   DATE_AVCT_MOY DATE,
   DATE_AVCT_MAXI DATE,
   NUM_ARRETE VARCHAR(8),
   DATE_ARRETE DATE,
   ETAT CHAR(1) not null,
   CODE_CATEGORIE INTEGER,
   constraint SIRH.PK_AVANCEMENT
   primary key (ID_AVCT),
   constraint SIRH.FK_AGT_AVCT
         foreign key (ID_AGENT)
         references SIRH.AGENT (ID_AGENT),
   constraint SIRH.FK_NOUV_ECHELON_AVCT
         foreign key (ID_NOUV_ECHELON)
         references MAIRIE.SPECHE (CODECH),
   constraint SIRH.FK_NOUV_CLASSE_AVCT
         foreign key (ID_NOUV_CLASSE)
         references MAIRIE.SPCLAS (CODCLA),
   constraint SIRH.FK_NOUV_GRADE_AVCT
         foreign key (ID_NOUV_GRADE)
         references MAIRIE.SPGRADN (CDGRAD),
   constraint SIRH.FK_NOUV_GRADE_GENERIQUE_AVCT
         foreign key (ID_NOUV_GRADE_GENERIQUE)
         references MAIRIE.SPGENG (CDGENG),
   constraint SIRH.FK_AVIS_CAP_AVCT
         foreign key (ID_AVIS_CAP)
         references SIRH.R_AVIS_CAP (ID_AVIS_CAP),
   constraint SIRH.FK_MOTIF_AVCT
         foreign key (ID_MOTIF_AVCT)
         references SIRH.P_MOTIF_AVCT (ID_MOTIF_AVCT)
);