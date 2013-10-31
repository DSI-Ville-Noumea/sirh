----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table SIRH2.FICHE_POSTE add COLUMN ID_NATURE_CREDIT INTEGER ;
alter table SIRH2.HISTO_FICHE_POSTE add COLUMN ID_NATURE_CREDIT INTEGER ;
alter table SIRH2.FICHE_POSTE add COLUMN NUM_DELIBERATION NVARCHAR(50) ;
alter table SIRH2.HISTO_FICHE_POSTE add COLUMN NUM_DELIBERATION NVARCHAR(50) ;

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table SIRH.FICHE_POSTE add COLUMN ID_NATURE_CREDIT INTEGER ;
alter table SIRH.HISTO_FICHE_POSTE add COLUMN ID_NATURE_CREDIT INTEGER ;
alter table SIRH.FICHE_POSTE add COLUMN NUM_DELIBERATION NVARCHAR(50) ;
alter table SIRH.HISTO_FICHE_POSTE add COLUMN NUM_DELIBERATION NVARCHAR(50) ;



