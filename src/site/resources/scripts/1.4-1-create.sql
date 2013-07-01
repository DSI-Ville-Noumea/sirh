----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
create table SIRH2.R_TYPE_JOUR_FERIE
(
ID_TYPE_JOUR_FERIE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
LIB_TYPE_JOUR_FERIE VARCHAR not null,
constraint SIRH2.PK_R_TYPE_JOUR_FERIE
primary key (ID_TYPE_JOUR_FERIE)
);

create table SIRH2.P_JOUR_FERIE
(
ID_JOUR_FERIE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
ID_TYPE_JOUR_FERIE INTEGER not null,
DATE_JOUR DATE not null,
DESCRIPTION varchar(255),
constraint SIRH2.PK_P_JOUR_FERIE
primary key (ID_JOUR_FERIE),
constraint SIRH2.FK_TYPE_JOUR_FERIE
foreign key (ID_TYPE_JOUR_FERIE)
references SIRH2.R_TYPE_JOUR_FERIE (ID_TYPE_JOUR_FERIE)
);

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
create table SIRH.R_TYPE_JOUR_FERIE
(
ID_TYPE_JOUR_FERIE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
LIB_TYPE_JOUR_FERIE VARCHAR not null,
constraint SIRH.PK_R_TYPE_JOUR_FERIE
primary key (ID_TYPE_JOUR_FERIE)
);

create table SIRH.P_JOUR_FERIE
(
ID_JOUR_FERIE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
ID_TYPE_JOUR_FERIE INTEGER not null,
DATE_JOUR DATE not null,
DESCRIPTION varchar(255),
constraint SIRH.PK_P_JOUR_FERIE
primary key (ID_JOUR_FERIE),
constraint SIRH.FK_TYPE_JOUR_FERIE
foreign key (ID_TYPE_JOUR_FERIE)
references SIRH.R_TYPE_JOUR_FERIE (ID_TYPE_JOUR_FERIE)
);



