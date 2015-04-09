package nc.mairie.gestionagent.process.pointage;

public enum EtatPointageEnum {

    SAISI(0), APPROUVE(1), REFUSE(2), REFUSE_DEFINITIVEMENT(3), VENTILE(4), REJETE(5), REJETE_DEFINITIVEMENT(6), VALIDE(7), EN_ATTENTE(8), JOURNALISE(9);
    private int codeEtat;

    EtatPointageEnum(int _value) {
        codeEtat = _value;
    }

    public int getCodeEtat() {
        return codeEtat;
    }

    @Override
    public String toString() {
        return String.valueOf(codeEtat);
    }

    public static EtatPointageEnum getEtatPointageEnum(Integer codeEtat) {

        if (codeEtat == null) {
            return null;
        }

        switch (codeEtat) {
            case 0:
                return SAISI;
            case 1:
                return APPROUVE;
            case 2:
                return REFUSE;
            case 3:
                return REFUSE_DEFINITIVEMENT;
            case 4:
                return VENTILE;
            case 5:
                return REJETE;
            case 6:
                return REJETE_DEFINITIVEMENT;
            case 7:
                return VALIDE;
            case 8:
                return EN_ATTENTE;
            case 9:
                return JOURNALISE;
            default:
                return null;
        }
    }

    public static String getDisplayableEtatPointageEnum(Integer codeEtat) {

        if (codeEtat == null) {
            return "Etat nul";
        }

        switch (codeEtat) {
            case 0:
                return "Saisi";
            case 1:
                return "Approuvé";
            case 2:
                return "Refusé";
            case 3:
                return "Refusé définitivement";
            case 4:
                return "Ventilé";
            case 5:
                return "Rejeté";
            case 6:
                return "Rejeté définitivement";
            case 7:
                return "Validé";
            case 8:
                return "En attente";
            case 9:
                return "Journalisé";
            default:
                return "Etat inconnu";
        }
    }
}
