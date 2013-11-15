----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
create table SIRH2.P_NATURE_CREDIT
(
ID_NATURE_CREDIT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
LIB_NATURE_CREDIT varchar(255),
constraint SIRH2.PK_P_NATURE_CREDIT
primary key (ID_NATURE_CREDIT)
);

+ ajouter cette table dans les autorisations de opensirh2
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
create table SIRH.P_NATURE_CREDIT
(
ID_NATURE_CREDIT INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
LIB_NATURE_CREDIT varchar(255),
constraint SIRH.PK_P_NATURE_CREDIT
primary key (ID_NATURE_CREDIT)
);
+ ajouter cette table dans les autorisations de opensirh


