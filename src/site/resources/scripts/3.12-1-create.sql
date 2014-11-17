----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
create table SIRH2.P_ACCUEIL_KIOSQUE
(
ID_ACCUEIL_KIOSQUE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
TEXTE_ACCUEIL_KIOSQUE varchar(255) not null,
constraint SIRH2.PK_ACCUEIL_KIOSQUE
primary key (ID_ACCUEIL_KIOSQUE)
);
+ ajouter cette table dans les autorisations de opensirh

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
create table SIRH.P_ACCUEIL_KIOSQUE
(
ID_ACCUEIL_KIOSQUE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
TEXTE_ACCUEIL_KIOSQUE varchar(255) not null,
constraint SIRH.PK_ACCUEIL_KIOSQUE
primary key (ID_ACCUEIL_KIOSQUE)
);
+ ajouter cette table dans les autorisations de opensirh
