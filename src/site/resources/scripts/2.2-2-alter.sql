----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table sirh2.AVCT_CAP_PRINT_JOB add column AVIS_EAE smallint not null default 0;
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table sirh.AVCT_CAP_PRINT_JOB add column AVIS_EAE smallint not null default 0;