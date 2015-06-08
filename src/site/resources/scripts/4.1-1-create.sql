----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
create table SIRH2.P_ALERTE_KIOSQUE
(
ID_ALERTE_KIOSQUE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
TEXTE_ALERTE_KIOSQUE NCLOB not null,
DATE_DEBUT DATE not null,     
DATE_FIN DATE,     
AGENT smallint not null default 0,
APPRO_ABS smallint not null default 0,
APPRO_PTG smallint not null default 0,
OPE_ABS smallint not null default 0,
OPE_PTG smallint not null default 0,
VISEUR_ABS smallint not null default 0,
constraint SIRH2.PK_ALERTE_KIOSQUE
primary key (ID_ALERTE_KIOSQUE)
);
+ ajouter cette table dans les autorisations de opensirh

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
create table SIRH.P_ALERTE_KIOSQUE
(
ID_ALERTE_KIOSQUE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
TEXTE_ALERTE_KIOSQUE NCLOB not null,
DATE_DEBUT DATE not null,     
DATE_FIN DATE,     
AGENT smallint not null default 0,
APPRO_ABS smallint not null default 0,
APPRO_PTG smallint not null default 0,
OPE_ABS smallint not null default 0,
OPE_PTG smallint not null default 0,
VISEUR_ABS smallint not null default 0,
constraint SIRH.PK_ALERTE_KIOSQUE
primary key (ID_ALERTE_KIOSQUE)
);
+ ajouter cette table dans les autorisations de opensirh
