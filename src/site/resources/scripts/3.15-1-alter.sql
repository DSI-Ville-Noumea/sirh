----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table SIRH2.P_ACCUEIL_KIOSQUE alter COLUMN TEXTE_ACCUEIL_KIOSQUE set data type NCLOB ;
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table SIRH.P_ACCUEIL_KIOSQUE alter COLUMN TEXTE_ACCUEIL_KIOSQUE set data type NCLOB ;

