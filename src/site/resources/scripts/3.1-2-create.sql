----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
create table SIRH2.P_MOTIF_CARRIERE
(
ID_MOTIF_CARRIERE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
LIB_MOTIF_CARRIERE varchar(100),
constraint SIRH2.PK_P_MOTIF_CARRIERE
primary key (ID_MOTIF_CARRIERE)
);
+ ajouter cette table dans les autorisations de opensirh2
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
create table SIRH.P_MOTIF_CARRIERE
(
ID_MOTIF_CARRIERE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
LIB_MOTIF_CARRIERE varchar(100),
constraint SIRH.PK_P_MOTIF_CARRIERE
primary key (ID_MOTIF_CARRIERE)
);
+ ajouter cette table dans les autorisations de opensirh
