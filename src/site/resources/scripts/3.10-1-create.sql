----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
create table SIRH2.P_BASE_HORAIRE_POINTAGE
(
ID_BASE_HORAIRE_POINTAGE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
CODE_BASE_HORAIRE_POINTAGE varchar(100),
LIBELLE_BASE_HORAIRE_POINTAGE varchar(255),
DESCRIPTION_BASE_HORAIRE_POINTAGE varchar(255),
HEURE_LUNDI DECIMAL(4,2),
HEURE_MARDI DECIMAL(4,2),
HEURE_MERCREDI DECIMAL(4,2),
HEURE_JEUDI DECIMAL(4,2),
HEURE_VENDREDI DECIMAL(4,2),
HEURE_SAMEDI DECIMAL(4,2),
HEURE_DIMANCHE DECIMAL(4,2),
BASE_LEGALE DECIMAL(4,2),
BASE_CALCULEE DECIMAL(4,2),
constraint SIRH2.PK_P_BASE_HORAIRE_POINTAGE
primary key (ID_BASE_HORAIRE_POINTAGE)
);
+ ajouter cette table dans les autorisations de opensirh2
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
create table SIRH.P_BASE_HORAIRE_POINTAGE
(
ID_BASE_HORAIRE_POINTAGE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
CODE_BASE_HORAIRE_POINTAGE varchar(100),
LIBELLE_BASE_HORAIRE_POINTAGE varchar(255),
DESCRIPTION_BASE_HORAIRE_POINTAGE varchar(255),
HEURE_LUNDI DECIMAL(4,2),
HEURE_MARDI DECIMAL(4,2),
HEURE_MERCREDI DECIMAL(4,2),
HEURE_JEUDI DECIMAL(4,2),
HEURE_VENDREDI DECIMAL(4,2),
HEURE_SAMEDI DECIMAL(4,2),
HEURE_DIMANCHE DECIMAL(4,2),
BASE_LEGALE DECIMAL(4,2),
BASE_CALCULEE DECIMAL(4,2),
constraint SIRH.PK_P_BASE_HORAIRE_POINTAGE
primary key (ID_BASE_HORAIRE_POINTAGE)
);
+ ajouter cette table dans les autorisations de opensirh