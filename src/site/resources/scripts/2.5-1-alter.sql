----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table sirh2.PERMIS_AGENT alter column DUREE_PERMIS drop not null;
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table sirh.PERMIS_AGENT alter column DUREE_PERMIS drop not null;
