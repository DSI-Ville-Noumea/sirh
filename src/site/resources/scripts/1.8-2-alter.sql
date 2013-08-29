----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table SIRH2.FICHE_POSTE alter COLUMN OBSERVATION set data type NVARCHAR(255) ;
alter table SIRH2.FICHE_POSTE alter COLUMN MISSIONS set data type NCLOB ;
alter table SIRH2.ACTIVITE alter COLUMN NOM_ACTIVITE set data type NVARCHAR(255) ;
alter table SIRH2.COMPETENCE alter COLUMN NOM_COMPETENCE set data type NVARCHAR(255) ;
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table SIRH.FICHE_POSTE alter COLUMN OBSERVATION set data type NVARCHAR(255) ;
alter table SIRH.FICHE_POSTE alter COLUMN MISSIONS set data type NCLOB ;
alter table SIRH.ACTIVITE alter COLUMN NOM_ACTIVITE set data type NVARCHAR(255) ;
alter table SIRH.COMPETENCE alter COLUMN NOM_COMPETENCE set data type NVARCHAR(255) ;
