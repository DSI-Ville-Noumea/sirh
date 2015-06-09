----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------

alter table SIRH2.P_ACCUEIL_KIOSQUE add column TITRE varchar(255);
alter table SIRH2.P_ALERTE_KIOSQUE add column TITRE varchar(255);

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------

alter table SIRH.P_ACCUEIL_KIOSQUE add column TITRE varchar(255);
alter table SIRH.P_ALERTE_KIOSQUE add column TITRE varchar(255);
