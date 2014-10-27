----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table sirh2.AFFECTATION add column ID_BASE_HORAIRE_POINTAGE INTEGER;
alter table sirh2.FICHE_POSTE add column ID_BASE_HORAIRE_POINTAGE INTEGER;
alter table sirh2.AFFECTATION add constraint SIRH2.FK_AFFECTATION_BASE_HORAIRE_POINTAGE foreign key (ID_BASE_HORAIRE_POINTAGE) references SIRH2.P_BASE_HORAIRE_POINTAGE (ID_BASE_HORAIRE_POINTAGE);
alter table sirh2.FICHE_POSTE add constraint SIRH2.FK_FICHE_POSTE_BASE_HORAIRE_POINTAGE foreign key (ID_BASE_HORAIRE_POINTAGE) references SIRH2.P_BASE_HORAIRE_POINTAGE (ID_BASE_HORAIRE_POINTAGE);
alter table sirh2.HISTO_AFFECTATION add column ID_BASE_HORAIRE_POINTAGE INTEGER;
alter table sirh2.HISTO_FICHE_POSTE add column ID_BASE_HORAIRE_POINTAGE INTEGER;
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table sirh.AFFECTATION add column ID_BASE_HORAIRE_POINTAGE INTEGER;
alter table sirh.FICHE_POSTE add column ID_BASE_HORAIRE_POINTAGE INTEGER;
alter table sirh.AFFECTATION add constraint SIRH.FK_AFFECTATION_BASE_HORAIRE_POINTAGE foreign key (ID_BASE_HORAIRE_POINTAGE) references SIRH.P_BASE_HORAIRE_POINTAGE (ID_BASE_HORAIRE_POINTAGE);
alter table sirh.FICHE_POSTE add constraint SIRH.FK_FICHE_POSTE_BASE_HORAIRE_POINTAGE foreign key (ID_BASE_HORAIRE_POINTAGE) references SIRH.P_BASE_HORAIRE_POINTAGE (ID_BASE_HORAIRE_POINTAGE);
alter table sirh.HISTO_AFFECTATION add column ID_BASE_HORAIRE_POINTAGE INTEGER;
alter table sirh.HISTO_FICHE_POSTE add column ID_BASE_HORAIRE_POINTAGE INTEGER;
