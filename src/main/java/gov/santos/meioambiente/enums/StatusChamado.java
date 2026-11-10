package gov.santos.meioambiente.enums;

public enum StatusChamado {
    ABERTO("Aberto"),
    EM_ANALISE("Em análise"),
    EM_ATENDIMENTO("Em atendimento"),
    RESOLVIDO("Resolvido");

    private final String rotulo;
    StatusChamado(String rotulo) { this.rotulo = rotulo; }
    public String getRotulo() { return rotulo; }
}
