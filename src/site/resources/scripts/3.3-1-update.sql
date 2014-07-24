----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
update sirh2.accident_travail set date_at_initial = null where date_at_initial='01/01/0001';
update sirh2.handicap set date_fin_handicap = null where date_fin_handicap='01/01/0001';
update sirh2.p_medecin set titre_medecin = null where prenom_medecin='A' and NOM_MEDECIN='RENSEIGNER';
ALTER TABLE SIRH2.CONTRAT ALTER COLUMN DATE_FIN_PERIODE_ESS  DROP NOT NULL ;
update sirh2.contrat set date_fin_periode_ess = null where date_fin_periode_ess='01/01/0001';
update sirh2.contrat set date_fin = null where date_fin='01/01/0001';
update sirh2.autre_admin_agent set date_sortie = null where date_sortie='01/01/0001';
update sirh2.casier_judiciaire set date_extrait = null where date_extrait='01/01/0001';
update sirh2.enfant set date_deces = null where date_deces='01/01/0001';
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
update sirh.accident_travail set date_at_initial = null where date_at_initial='01/01/0001';
update sirh.handicap set date_fin_handicap = null where date_fin_handicap='01/01/0001';
update sirh.p_medecin set titre_medecin = null where prenom_medecin='A' and NOM_MEDECIN='RENSEIGNER';
ALTER TABLE SIRH.CONTRAT ALTER COLUMN DATE_FIN_PERIODE_ESS  DROP NOT NULL ;
update sirh.contrat set date_fin_periode_ess = null where date_fin_periode_ess='01/01/0001';
update sirh.contrat set date_fin = null where date_fin='01/01/0001';
update sirh.autre_admin_agent set date_sortie = null where date_sortie='01/01/0001';
update sirh.casier_judiciaire set date_extrait = null where date_extrait='01/01/0001';
update sirh.enfant set date_deces = null where date_deces='01/01/0001';
