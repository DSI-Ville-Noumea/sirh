----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
alter table sirh2.AFFECTATION add column ID_BASE_HORAIRE_ABSENCE INTEGER;
alter table sirh2.FICHE_POSTE add column ID_BASE_HORAIRE_ABSENCE INTEGER;
alter table sirh2.HISTO_AFFECTATION add column ID_BASE_HORAIRE_ABSENCE INTEGER;
alter table sirh2.HISTO_FICHE_POSTE add column ID_BASE_HORAIRE_ABSENCE INTEGER;
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
alter table sirh.AFFECTATION add column ID_BASE_HORAIRE_ABSENCE INTEGER;
alter table sirh.FICHE_POSTE add column ID_BASE_HORAIRE_ABSENCE INTEGER;
alter table sirh.HISTO_AFFECTATION add column ID_BASE_HORAIRE_ABSENCE INTEGER;
alter table sirh.HISTO_FICHE_POSTE add column ID_BASE_HORAIRE_ABSENCE INTEGER;
