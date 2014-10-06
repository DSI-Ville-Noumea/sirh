----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
create table SIRH2.P_REFERENT_RH
(
SERVI varchar(100) not null ,
ID_AGENT_REFERENT INTEGER not null,
NUMERO_TELEPHONE INTEGER not null,
constraint SIRH.PK_P_REFERENT_RH
primary key (SERVI)
);
+ ajouter cette table dans les autorisations de opensirh

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
create table SIRH.P_REFERENT_RH
(
SERVI varchar(100) not null ,
ID_AGENT_REFERENT INTEGER not null,
NUMERO_TELEPHONE INTEGER not null,
constraint SIRH.PK_P_REFERENT_RH
primary key (SERVI)
);
+ ajouter cette table dans les autorisations de opensirh
