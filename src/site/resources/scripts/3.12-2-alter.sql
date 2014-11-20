----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table SIRH2.FICHE_POSTE alter COLUMN OBSERVATION set data type NCLOB ;
drop table SIRH2.P_REFERENT_RH;
create table SIRH2.P_REFERENT_RH
(
ID_REFERENT_RH INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
SERVI varchar(100) ,
ID_AGENT_REFERENT INTEGER not null,
NUMERO_TELEPHONE INTEGER not null,
constraint SIRH2.PK_P_REFERENT_RH
primary key (ID_REFERENT_RH)
);
+ ajouter cette table dans les autorisations de opensirh
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table SIRH.FICHE_POSTE alter COLUMN OBSERVATION set data type NCLOB ;
drop table SIRH.P_REFERENT_RH;
create table SIRH.P_REFERENT_RH
(
ID_REFERENT_RH INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
SERVI varchar(100) ,
ID_AGENT_REFERENT INTEGER not null,
NUMERO_TELEPHONE INTEGER not null,
constraint SIRH.PK_P_REFERENT_RH
primary key (ID_REFERENT_RH)
);
+ ajouter cette table dans les autorisations de opensirh
