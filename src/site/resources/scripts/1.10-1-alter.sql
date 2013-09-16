----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table SIRH2.AVCT_FONCT add column AGENT_VDN SMALLINT not null default 0;
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table SIRH.AVCT_FONCT add column AGENT_VDN SMALLINT not null default 0;
