package gov.santos.meioambiente.enums;

public enum TipoProblema {
    VAZAMENTO("Vazamento"),
    EXTRAVASAMENTO("Extravasamento"),
    ESGOTO_A_CEU_ABERTO("Esgoto a céu aberto"),
    MAU_CHEIRO("Mau cheiro"),
    OUTRO("Outro");

    private final String rotulo;
    TipoProblema(String rotulo) { this.rotulo = rotulo; }
    public String getRotulo() { return rotulo; }
}
