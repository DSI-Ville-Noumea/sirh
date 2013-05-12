
create table SIRH.PRIME_POINTAGE
(
ID_PRIME_POINTAGE INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),
NUM_RUBRIQUE NUMERIC(4),
constraint SIRH.PK_PRIME_POINTAGE
primary key (ID_PRIME_POINTAGE),
constraint SIRH.FK_PRIME_POINTAGE_RUBRIQUE
foreign key (NUM_RUBRIQUE)
references MAIRIE.SPRUBR (NORUBR)
);


create table SIRH.PRIME_POINTAGE_FP
(
ID_PRIME_POINTAGE INTEGER not null,
ID_FICHE_POSTE INTEGER not null,
constraint SIRH.PK_PRIME_POINTAGE_FP
primary key ( ID_PRIME_POINTAGE,ID_FICHE_POSTE),
constraint SIRH.FK_PRIME_POINTAGE_PP
foreign key (ID_PRIME_POINTAGE)
references SIRH.PRIME_POINTAGE (ID_PRIME_POINTAGE),
constraint SIRH.FK_PRIME_POINTAGE_FP
foreign key (ID_FICHE_POSTE)
references SIRH.FICHE_POSTE (ID_FICHE_POSTE)
);


create table SIRH.PRIME_POINTAGE_AFF
(
ID_PRIME_POINTAGE INTEGER not null,
ID_AFFECTATION INTEGER not null,
constraint SIRH.PK_PRIME_POINTAGE_AFF
primary key (ID_PRIME_POINTAGE, ID_AFFECTATION),
constraint SIRH.FK_PP_PRIME_POINTAGE
foreign key (ID_PRIME_POINTAGE)
references SIRH.PRIME_POINTAGE (ID_PRIME_POINTAGE),
constraint SIRH.FK_PRIME_POINTAGE_AFF
foreign key (ID_AFFECTATION)
references SIRH.AFFECTATION (ID_AFFECTATION)
);


