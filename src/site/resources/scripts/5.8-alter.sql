----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------

alter table SIRH.ACCIDENT_TRAVAIL
 add column RECHUTE smallint not null default 0 
 add column DATE_FIN TIMESTAMP
 add column AVIS_COMMISSION smallint
 add column ID_AT_REFERENCE integer;

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------

alter table SIRH.ACCIDENT_TRAVAIL
 add column RECHUTE smallint not null default 0 
 add column DATE_FIN TIMESTAMP
 add column AVIS_COMMISSION smallint
 add column ID_AT_REFERENCE integer;
