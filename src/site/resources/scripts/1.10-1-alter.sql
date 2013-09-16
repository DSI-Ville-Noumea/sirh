----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table SIRH2.AVCT_FONCT add column AGENT_VDN SMALLINT not null default 0;
alter table SIRH2.P_CAP add column CAP_VDN SMALLINT not null default 1;
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table SIRH.AVCT_FONCT add column AGENT_VDN SMALLINT not null default 0;
alter table SIRH.P_CAP add column CAP_VDN SMALLINT not null default 1;
