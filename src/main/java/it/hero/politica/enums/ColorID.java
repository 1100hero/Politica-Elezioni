package it.hero.politica.enums;

public enum ColorID {
    BLU("§1"),
    CIANO("§9"),
    VIOLA("§5"),
    VERDE("§a"),
    ROSSO("§c"),
    NERO("§0"),
    GRIGIO("§8"),
    GIALLO("§e");

    private final String color;
    ColorID(String color) {
        this.color = color;
    }
    public String getColorID() {
        return color;
    }
}
