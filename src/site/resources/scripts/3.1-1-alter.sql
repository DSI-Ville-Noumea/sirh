----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table sirh2.AVCT_FONCT add column CODE_PA varchar(10);
alter table sirh2.AVCT_DETACHE add column CODE_PA varchar(10);
alter table sirh2.AVCT_CONV_COL add column CODE_PA varchar(10);
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table sirh.AVCT_FONCT add column CODE_PA varchar(10);
alter table sirh.AVCT_DETACHE add column CODE_PA varchar(10);
alter table sirh.AVCT_CONV_COL add column CODE_PA varchar(10);
