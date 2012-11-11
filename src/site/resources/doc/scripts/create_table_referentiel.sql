--==============================================================
-- Table: R_TYPE_CONTACT
--==============================================================
create table SIRH.R_TYPE_CONTACT
(
   ID_TYPE_CONTACT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIBELLE VARCHAR(20) not null,
   constraint SIRH.PK_R_TYPE_CONTACT
   primary key (ID_TYPE_CONTACT)
);
--==============================================================
-- Table: R_MOTIF
--==============================================================
create table SIRH.R_MOTIF
(
   ID_MOTIF INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_MOTIF VARCHAR(50) not null,
   constraint SIRH.PK_R_MOTIF
   primary key (ID_MOTIF)
);
--==============================================================
-- Table: R_COLLECTIVITE
--==============================================================
create table SIRH.R_COLLECTIVITE
(
   ID_COLLECTIVITE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   CODE_COLLECTIVITE VARCHAR(2) not null,
   LIB_COURT_COLLECTIVITE VARCHAR(4) not null,
   LIB_LONG_COLLECTIVITE VARCHAR(50) not null,
   constraint SIRH.PK_R_COLLECTIVITE
   primary key (ID_COLLECTIVITE)
);
--==============================================================
-- Table: R_AUTRE_ADMIN
--==============================================================
create table SIRH.R_AUTRE_ADMIN
(
   ID_AUTRE_ADMIN INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_AUTRE_ADMIN VARCHAR(100) not null,
   constraint SIRH.PK_R_AUTRE_ADMIN
   primary key (ID_AUTRE_ADMIN)
);
--==============================================================
-- Table: R_TYPE_CONTRAT
--==============================================================
create table SIRH.R_TYPE_CONTRAT
(
   ID_TYPE_CONTRAT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_TYPE_CONTRAT VARCHAR(3),
   constraint SIRH.PK_R_TYPE_CONTRAT
   primary key (ID_TYPE_CONTRAT)
);
--==============================================================
-- Table: R_TYPE_COMPETENCE
--==============================================================
create table SIRH.R_TYPE_COMPETENCE
(
   ID_TYPE_COMPETENCE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_TYPE_COMPETENCE VARCHAR(30) not null,
   constraint SIRH.PK_R_TYPE_COMPETENCE
   primary key (ID_TYPE_COMPETENCE)
);
--==============================================================
-- Table: R_NIVEAU_ETUDE
--==============================================================
create table SIRH.R_NIVEAU_ETUDE
(
   ID_NIVEAU_ETUDE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   CODE_NIVEAU_ETUDE VARCHAR(10) not null,
   constraint SIRH.PK_R_NIVEAU_ETUDE
   primary key (ID_NIVEAU_ETUDE)
);
--==============================================================
-- Table: R_NOM_HANDICAP
--==============================================================
create table SIRH.R_NOM_HANDICAP
(
   ID_TYPE_HANDICAP INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   NOM_TYPE_HANDICAP VARCHAR(30) not null,
   constraint SIRH.PK_R_NOM_HANDICAP
   primary key (ID_TYPE_HANDICAP)
);
--==============================================================
-- Table: R_BUDGET
--==============================================================
create table SIRH.R_BUDGET
(
   ID_BUDGET INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_BUDGET VARCHAR(25) not null,
   constraint SIRH.PK_R_BUDGET
   primary key (ID_BUDGET)
);
--==============================================================
-- Table: R_STATUT_FP
--==============================================================
create table SIRH.R_STATUT_FP
(
   ID_STATUT_FP INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_STATUT_FP VARCHAR(20) not null,
   constraint SIRH.PK_R_STATUT_FP
   primary key (ID_STATUT_FP)
);
--==============================================================
-- Table: R_SITUATION_FAMILIALE
--==============================================================
create table SIRH.R_SITUATION_FAMILIALE
(
   ID_SITUATION INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   CODE_SITUATION VARCHAR(1) not null,
   LIB_SITUATION VARCHAR(20) not null,
   constraint SIRH.PK_R_SITUATION
   primary key (ID_SITUATION)
);
--==============================================================
-- Table: R_ETAT_SERVICE_MILITAIRE
--==============================================================
create table SIRH.R_ETAT_SERVICE_MILITAIRE
(
   ID_ETAT_SERVICE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   CODE_ETAT_SERVICE VARCHAR(2) not null,
   LIB_ETAT_SERVICE VARCHAR(20) not null,
   constraint SIRH.PK_R_ETAT_SERVICE
   primary key (ID_ETAT_SERVICE)
);
--==============================================================
-- Table: R_TYPE_DROIT
--==============================================================
create table SIRH.R_TYPE_DROIT
(
   ID_TYPE_DROIT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_TYPE_DROIT VARCHAR(20) not null,
   constraint SIRH.PK_R_TYPE_DROIT
   primary key (ID_TYPE_DROIT)
);
--==============================================================
-- Table: R_AVIS_CAP
--==============================================================
create table SIRH.R_AVIS_CAP
(
   ID_AVIS_CAP INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
   LIB_COURT_AVIS_CAP VARCHAR(4) not null,
   LIB_LONG_AVIS_CAP VARCHAR(7) not null,
   constraint SIRH.PK_R_AVIS_CAP
   primary key (ID_AVIS_CAP)
);