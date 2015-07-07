----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
create table SIRH2.ACTION_FDP_JOB
(
ID_ACTION_FDP_JOB INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
ID_AGENT INTEGER not null,
ID_FICHE_POSTE INTEGER not null,
TYPE_ACTION varchar(255) not null, 
DATE_SUBMISSION TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,  
DATE_STATUT TIMESTAMP,   
STATUT varchar(255),
constraint SIRH2.PK_ACTION_FDP_JOB
primary key (ID_ACTION_FDP_JOB)
);
+ ajouter cette table dans les autorisations de opensirh

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
create table SIRH.ACTION_FDP_JOB
(
ID_ACTION_FDP_JOB INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
ID_AGENT INTEGER not null,
ID_FICHE_POSTE INTEGER not null,
TYPE_ACTION varchar(255) not null, 
DATE_SUBMISSION TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,  
DATE_STATUT TIMESTAMP,   
STATUT varchar(255),
constraint SIRH.PK_ACTION_FDP_JOB
primary key (ID_ACTION_FDP_JOB)
);
+ ajouter cette table dans les autorisations de opensirh
