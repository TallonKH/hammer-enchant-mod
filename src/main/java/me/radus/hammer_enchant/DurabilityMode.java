package me.radus.hammer_enchant;

public enum DurabilityMode {
    NORMAL,
    SQRT;

    static DurabilityMode fromString(String str){
        switch(str.toUpperCase()){
            case "SQRT" -> {
                return SQRT;
            }
            default -> {
                return NORMAL;
            }
        }
    }

    public int calculate(int rawDamage){
        switch(this){
            case SQRT -> {
                return (int) Math.ceil(Math.sqrt(rawDamage));
            }
            default -> {
                return rawDamage;
            }
        }
    }
}
