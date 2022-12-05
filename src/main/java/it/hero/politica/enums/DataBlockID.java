package it.hero.politica.enums;

public enum DataBlockID {
    BLU(34),
    CIANO(35),
    VIOLA(36),
    VERDE(37),
    ROSSO(38),
    NERO(39),
    GRIGIO(40),
    GIALLO(41);

    private final int value;

    public int getValue(){
        return value;
    }

    DataBlockID(int value) {
        this.value = value;
    }
}
