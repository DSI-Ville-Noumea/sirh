----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
update sirh2.fiche_poste set date_fin_validite_fp = null where date_fin_validite_fp='01/01/0001';
update sirh2.fiche_poste set date_debut_validite_fp = null where date_debut_validite_fp='01/01/0001';
update sirh2.fiche_poste set date_deb_appli_serv = null where date_deb_appli_serv='01/01/0001';
update sirh2.histo_fiche_poste set date_deb_appli_serv = null where date_deb_appli_serv='01/01/0001';
update sirh2.histo_fiche_poste set date_fin_appli_serv = null where date_fin_appli_serv='01/01/0001';
update sirh2.histo_fiche_poste set date_fin_validite_fp = null where date_fin_validite_fp='01/01/0001';
update sirh2.histo_fiche_poste set date_debut_validite_fp = null where date_debut_validite_fp='01/01/0001';
update sirh2.affectation set date_arrete = null where date_arrete='01/01/0001';
update sirh2.affectation set date_fin_aff = null where date_fin_aff='01/01/0001';
update sirh2.agent set date_naissance = null where date_naissance='01/01/0001';
update sirh2.agent set date_deces = null where date_deces='01/01/0001';
update sirh2.agent set date_premiere_embauche = null where date_premiere_embauche='01/01/0001';
update sirh2.agent set date_derniere_embauche = null where date_derniere_embauche='01/01/0001';
update sirh2.agent set date_validite_carte_sejour = null where date_validite_carte_sejour='01/01/0001';
update sirh2.agent set debut_service = null where debut_service='01/01/0001';
update sirh2.agent set fin_service = null where fin_service='01/01/0001';
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
update sirh.fiche_poste set date_fin_validite_fp = null where date_fin_validite_fp='01/01/0001';
update sirh.fiche_poste set date_debut_validite_fp = null where date_debut_validite_fp='01/01/0001';
update sirh.fiche_poste set date_deb_appli_serv = null where date_deb_appli_serv='01/01/0001';
update sirh.histo_fiche_poste set date_deb_appli_serv = null where date_deb_appli_serv='01/01/0001';
update sirh.histo_fiche_poste set date_fin_appli_serv = null where date_fin_appli_serv='01/01/0001';
update sirh.histo_fiche_poste set date_fin_validite_fp = null where date_fin_validite_fp='01/01/0001';
update sirh.histo_fiche_poste set date_debut_validite_fp = null where date_debut_validite_fp='01/01/0001';
update sirh.affectation set date_arrete = null where date_arrete='01/01/0001';
update sirh.affectation set date_fin_aff = null where date_fin_aff='01/01/0001';
update sirh.agent set date_naissance = null where date_naissance='01/01/0001';
update sirh.agent set date_deces = null where date_deces='01/01/0001';
update sirh.agent set date_premiere_embauche = null where date_premiere_embauche='01/01/0001';
update sirh.agent set date_derniere_embauche = null where date_derniere_embauche='01/01/0001';
update sirh.agent set date_validite_carte_sejour = null where date_validite_carte_sejour='01/01/0001';
update sirh.agent set debut_service = null where debut_service='01/01/0001';
update sirh.agent set fin_service = null where fin_service='01/01/0001';
