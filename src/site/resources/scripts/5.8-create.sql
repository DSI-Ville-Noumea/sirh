----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
create table SIRH.MALADIE_PRO
(
ID_MALADIE_PRO INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
ID_AGENT INTEGER not null,
RECHUTE smallint not null default 0,
DATE_DECLARATION TIMESTAMP, 
DATE_FIN TIMESTAMP, 
NB_JOURS_ITT INTEGER,
AVIS_COMMISSION smallint,
ID_TYPE_MP INTEGER not null,
DATE_TRANSMISSION_CAFAT TIMESTAMP, 
DATE_DECISION_CAFAT TIMESTAMP, 
TAUX_PRIS_CHARGE_CAFAT INTEGER,
TAUX_PRIS_CHARGE_CAFAT INTEGER,
DATE_TRANSMISSION_APTITUDE TIMESTAMP
constraint SIRH.PK_MALADIE_PRO
primary key (ID_MALADIE_PRO)
);
+ ajouter cette table dans les autorisations de opensirh
+ journaliser la table

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
create table SIRH.MALADIE_PRO
(
ID_MALADIE_PRO INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
ID_AGENT INTEGER not null,
RECHUTE smallint not null default 0,
DATE_DECLARATION TIMESTAMP, 
DATE_FIN TIMESTAMP, 
NB_JOURS_ITT INTEGER,
AVIS_COMMISSION smallint,
ID_TYPE_MP INTEGER not null,
DATE_TRANSMISSION_CAFAT TIMESTAMP, 
DATE_DECISION_CAFAT TIMESTAMP, 
TAUX_PRIS_CHARGE_CAFAT INTEGER,
TAUX_PRIS_CHARGE_CAFAT INTEGER,
DATE_TRANSMISSION_APTITUDE TIMESTAMP
constraint SIRH.PK_MALADIE_PRO
primary key (ID_MALADIE_PRO)
);
+ ajouter cette table dans les autorisations de opensirh
