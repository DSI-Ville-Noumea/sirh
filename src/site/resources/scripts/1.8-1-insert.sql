----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
INSERT INTO MAIRIE2.SPWFETAT (CDETAT, LIBETAT)
VALUES
(0, 'Pret'), (1, 'Ecriture pointages en cours'), (2, 'Ecriture pointages termin�e'), (3, 'Calcul salaire en cours'), (4, 'Calcul salaire termin�'),
(5, 'Journal en cours'), (6, 'Journal termin�'), (7, 'Etat payeur en cours'), (8, 'Etat payeur termin�');
INSERT INTO MAIRIE2.SPWFPAIE (CDCHAINE, CDETAT, DATMAJ, PERPAIE) VALUES
('SHC', 0, '2013-08-01 15:12:04', 0),
('SCV', 0, '2013-08-01 15:12:04', 0);
----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
INSERT INTO MAIRIE.SPWFETAT (CDETAT, LIBETAT)
VALUES
(0, 'Pret'), (1, 'Ecriture pointages en cours'), (2, 'Ecriture pointages termin�e'), (3, 'Calcul salaire en cours'), (4, 'Calcul salaire termin�'),
(5, 'Journal en cours'), (6, 'Journal termin�'), (7, 'Etat payeur en cours'), (8, 'Etat payeur termin�');
INSERT INTO MAIRIE.SPWFPAIE (CDCHAINE, CDETAT, DATMAJ, PERPAIE) VALUES
('SHC', 0, '2013-08-01 15:12:04', 0),
('SCV', 0, '2013-08-01 15:12:04', 0);
