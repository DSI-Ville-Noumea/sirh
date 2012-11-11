--==============================================================
-- Table: P_TITRE_DIPLOME
--==============================================================
create table SIRH.P_TITRE_DIPLOME
(
   ID_TITRE_DIPLOME INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_TITRE_DIPLOME VARCHAR(100) not null,
   NIVEAU_ETUDE VARCHAR(10) not null,
   constraint SIRH.PK_P_TITRE_DIPLOME
   primary key (ID_TITRE_DIPLOME)
);
--==============================================================
-- Table: P_SPECIALITE_DIPLOME
--==============================================================
create table SIRH.P_SPECIALITE_DIPLOME
(
   ID_SPECIALITE_DIPLOME INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_SPECIALITE_DIPLOME VARCHAR(100) not null,
   constraint SIRH.PK_P_SPE_DIPLOME
   primary key (ID_SPECIALITE_DIPLOME)
);
--==============================================================
-- Table: P_DOMAINE_ACTIVITE
--==============================================================
--TABLE SUPPRIMEE
/*create table SIRH.P_DOMAINE_ACTIVITE
(
   ID_DOMAINE_ACTIVITE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_DOMAINE_ACTIVITE VARCHAR(20) not null,
   constraint SIRH.PK_P_DOMAINE_ACTIVITE
   primary key (ID_DOMAINE_ACTIVITE)
);*/
--==============================================================
-- Table: P_DOMAINE_FE
--==============================================================
create table SIRH.P_DOMAINE_FE
(
   ID_DOMAINE_FE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   CODE_DOMAINE_FE VARCHAR(2) not null,
   LIB_DOMAINE_FE VARCHAR(100) not null,
   constraint SIRH.PK_P_DOMAINE_FE
   primary key (ID_DOMAINE_FE)
);
--==============================================================
-- Table: P_FAMILLE_EMPLOI
--==============================================================
create table SIRH.P_FAMILLE_EMPLOI
(
   ID_FAMILLE_EMPLOI INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   --CODE_FAMILLE_EMPLOI VARCHAR(2) not null,
   CODE_FAMILLE_EMPLOI VARCHAR(3) not null,
   LIB_FAMILLE_EMPLOI VARCHAR(100) not null,
   constraint SIRH.PK_P_FAMILLE_EMPLOI
   primary key (ID_FAMILLE_EMPLOI)
);
--==============================================================
-- Table: P_FILIERE
--==============================================================
/*create table SIRH.P_FILIERE
(
   ID_FILIERE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_FILIERE VARCHAR(100) not null,
   constraint SIRH.PK_P_FILIERE
   primary key (ID_FILIERE)
);*/
--==============================================================
-- Table: P_CADRE_EMPLOI
--==============================================================
create table SIRH.P_CADRE_EMPLOI
(
   ID_CADRE_EMPLOI INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_CADRE_EMPLOI VARCHAR(100) not null,
   constraint SIRH.PK_P_CADRE_EMPLOI
   primary key (ID_CADRE_EMPLOI)
);
--==============================================================
-- Table: P_DIPLOME_GENERIQUE
--==============================================================
create table SIRH.P_DIPLOME_GENERIQUE
(
   ID_DIPLOME_GENERIQUE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_DIPLOME_GENERIQUE VARCHAR(100) not null,
   constraint SIRH.PK_P_DIPLOME_GENERIQUE
   primary key (ID_DIPLOME_GENERIQUE)
);
--==============================================================
-- Table: P_MEDECIN
--==============================================================
create table SIRH.P_MEDECIN
(
   ID_MEDECIN INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   NOM_MEDECIN VARCHAR(50) not null,
   constraint SIRH.PK_P_MEDECIN
   primary key (ID_MEDECIN)
);
--==============================================================
-- Table: P_RECOMMANDATION
--==============================================================
create table SIRH.P_RECOMMANDATION
(
   ID_RECOMMANDATION INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   DESC_RECOMMANDATION VARCHAR(255) not null,
   constraint SIRH.PK_P_RECOMMANDATION
   primary key (ID_RECOMMANDATION)
);
--==============================================================
-- Table: P_TYPE_INAPTITUDE
--==============================================================
create table SIRH.P_TYPE_INAPTITUDE
(
   ID_TYPE_INAPTITUDE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   DESC_TYPE_INAPTITUDE VARCHAR(255) not null,
   constraint SIRH.PK_P_TYPE_INAPTITUDE
   primary key (ID_TYPE_INAPTITUDE)
);
--==============================================================
-- Table: P_TYPE_AT
--==============================================================
create table SIRH.P_TYPE_AT
(
   ID_TYPE_AT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   DESC_TYPE_AT VARCHAR(255) not null,
   constraint SIRH.PK_P_TYPE_AT
   primary key (ID_TYPE_AT)
);
--==============================================================
-- Table: P_SIEGE_LESION
--==============================================================
create table SIRH.P_SIEGE_LESION
(
   ID_SIEGE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   DESC_SIEGE VARCHAR(255) not null,
   constraint SIRH.PK_P_SIEGE_LESION
   primary key (ID_SIEGE)
);
--==============================================================
-- Table: P_MALADIE_PRO
--==============================================================
create table SIRH.P_MALADIE_PRO
(
   ID_MALADIE_PRO INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   CODE_MALADIE_PRO VARCHAR(30) not null,
   LIB_MALADIE_PRO VARCHAR(255) not null,
   constraint SIRH.PK_P_MALADIE_PRO
   primary key (ID_MALADIE_PRO)
);
--==============================================================
-- Table: P_TITRE_POSTE
--==============================================================
create table SIRH.P_TITRE_POSTE
(
   ID_TITRE_POSTE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_TITRE_POSTE VARCHAR(100) not null,
   constraint SIRH.PK_P_TITRE_POSTE
   primary key (ID_TITRE_POSTE)
);
--==============================================================
-- Table: P_MOTIF_NON_RECRUT
--==============================================================
create table SIRH.P_MOTIF_NON_RECRUT
(
   ID_MOTIF_NON_RECRUT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_MOTIF_NON_RECRUT VARCHAR(50) not null,
   constraint SIRH.PK_P_MOTIF_NON_RECRUT
   primary key (ID_MOTIF_NON_RECRUT)
);
--==============================================================
-- Table: P_MOTIF_RECRUT
--==============================================================
create table SIRH.P_MOTIF_RECRUT
(
   ID_MOTIF_RECRUT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_MOTIF_RECRUT VARCHAR(50) not null,
   constraint SIRH.PK_P_MOTIF_RECRUT
   primary key (ID_MOTIF_RECRUT)
);
--==============================================================
-- Table: P_TYPE_DELEGATION
--==============================================================
create table SIRH.P_TYPE_DELEGATION
(
   ID_TYPE_DELEGATION INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_TYPE_DELEGATION VARCHAR(30) not null,
   constraint SIRH.PK_P_TYPE_DELEGATION
   primary key (ID_TYPE_DELEGATION)
);
--==============================================================
-- Table: P_TYPE_AVANTAGE
--==============================================================
create table SIRH.P_TYPE_AVANTAGE
(
   ID_TYPE_AVANTAGE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_TYPE_AVANTAGE VARCHAR(50) not null,
   constraint SIRH.PK_P_TYPE_AVANTAGE
   primary key (ID_TYPE_AVANTAGE)
);
--==============================================================
-- Table: P_NATURE_AVANTAGE
--==============================================================
create table SIRH.P_NATURE_AVANTAGE
(
   ID_NATURE_AVANTAGE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_NATURE_AVANTAGE VARCHAR(50) not null,
   constraint SIRH.PK_P_NATURE_AVANTAGE
   primary key (ID_NATURE_AVANTAGE)
);
--==============================================================
-- Table: P_TYPE_REG_INDEMN
--==============================================================
create table SIRH.P_TYPE_REG_INDEMN
(
   ID_TYPE_REG_INDEMN INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_TYPE_REG_INDEMN VARCHAR(20) not null,
   constraint SIRH.PK_P_TYPE_REG_INDEMN
   primary key (ID_TYPE_REG_INDEMN)
);
--==============================================================
-- Table: P_MOTIF_AFFECTATION
--==============================================================
create table SIRH.P_MOTIF_AFFECTATION
(
   ID_MOTIF_AFFECTATION INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_MOTIF_AFFECTATION VARCHAR(30) not null,
   constraint SIRH.PK_P_MOTIF_AFF
   primary key (ID_MOTIF_AFFECTATION)
);
--==============================================================
-- Table: P_TYPE_DOCUMENT
--==============================================================
create table SIRH.P_TYPE_DOCUMENT
(
   ID_TYPE_DOCUMENT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_TYPE_DOCUMENT VARCHAR(30) not null,
   COD_TYPE_DOCUMENT VARCHAR(5) not null,
   constraint SIRH.PK_P_TYPE_DOCUMENT
   primary key (ID_TYPE_DOCUMENT)
);
--==============================================================
-- Table: P_MOTIF_AVCT
--==============================================================
create table SIRH.P_MOTIF_AVCT
(
   ID_MOTIF_AVCT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_MOTIF_AVCT VARCHAR(50) not null,
   constraint SIRH.PK_P_MOTIF_AVCT
   primary key (ID_MOTIF_AVCT)
);
--==============================================================
-- Table: P_CATEGORIE_STATUT
--==============================================================
create table SIRH.P_CATEGORIE_STATUT
(
   ID_CATEGORIE_STATUT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_CATEGORIE_STATUT VARCHAR(2) not null,
   --NB_PTS_AVCT INTEGER not null,
   constraint SIRH.PK_P_CATEGORIE_STATUT
   primary key (ID_CATEGORIE_STATUT)
);
--==============================================================
-- Table: P_CODE_ROME
--==============================================================
create table SIRH.P_CODE_ROME
(
   ID_CODE_ROME INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_CODE_ROME VARCHAR(5) not null,
   DESC_CODE_ROME VARCHAR(100) not null,
   constraint SIRH.PK_P_CODE_ROME
   primary key (ID_CODE_ROME)
);