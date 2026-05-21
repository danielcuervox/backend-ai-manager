package com.cuervo.erp_personal.models;
import java.util.Random;
public enum ReportStyle {

    GUDERIAN("Heinz Guderian with Blitzkrieg Style"),
    ROMMEL("Erwin Rommel with Bold Style"),
    MANSTEIN("Erich von Manstein with Operational Style"),
    CLAUSEWITZ("Carl von Clausewitz with Theoretical Style"),
    PATTON("George Patton with Aggressive Style"),
    CESAR("Gaius Julius Cesar with Charismatic Style"),
    NAPOLEON("Napoleon Bonaparte with Strategic Style"),
    ALEXANDER("Alexander the Great with Visionary Style"),
    LEONIDAS("Leonidas I with Defiant Style"),
    ERIK_THE_RED("Erik the Red with Exploratory Style"),
    SUN_TZU("Sun Tzu with Subtle-Philosophical Style"),
    HANNIBAL("Hannibal Barca with Tactical-Innovative Style"),
    MARCUS_AURELIUS("Marcus Aurelius with Reflective Style"),
    SPORTS_COACH("Sports Coach with Performance-Oriented Style"),
    PSYCHOLOGIST("Psychologist with Cognitive-Analytical Style"),
    MANAGER("Professional Manager with Systemic-Organizational Style");

    private final String description;

    ReportStyle(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    private static final Random RANDOM = new Random();

    public static ReportStyle getRandomStyle() {
        ReportStyle[] styles = values();
        return styles[RANDOM.nextInt(styles.length)];
    }
}
