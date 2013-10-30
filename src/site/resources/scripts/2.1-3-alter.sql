----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table SIRH2.FICHE_POSTE add COLUMN ID_NATURE_CREDIT INTEGER ;
alter table SIRH2.HISTO_FICHE_POSTE add COLUMN ID_NATURE_CREDIT INTEGER ;

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table SIRH.FICHE_POSTE add COLUMN ID_NATURE_CREDIT INTEGER ;
alter table SIRH.HISTO_FICHE_POSTE add COLUMN ID_NATURE_CREDIT INTEGER ;



