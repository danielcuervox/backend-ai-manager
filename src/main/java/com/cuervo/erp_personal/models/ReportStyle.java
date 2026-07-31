package com.cuervo.erp_personal.models;
import java.util.Random;
public enum ReportStyle {

    GUDERIAN("Heinz Guderian with Blitzkrieg Style", "guderian.png"),
    ROMMEL("Erwin Rommel with Bold Style", "rommel.png"),
    MANSTEIN("Erich von Manstein with Operational Style", "manstein.png"),
    CLAUSEWITZ("Carl von Clausewitz with Theoretical Style", "clausewitz.png"),
    PATTON("George Patton with Aggressive Style", "patton.png"),
    CEASAR("Gaius Julius Cesar with Charismatic Style", "ceasar.png"),
    NAPOLEON("Napoleon Bonaparte with Strategic Style", "napoleon.png"),
    ALEXANDER("Alexander the Great with Visionary Style", "alexander.png"),
    LEONIDAS("Leonidas I with Defiant Style", "leonidas.png"),
    ERIK_THE_RED("Erik the Red with Exploratory Style", "erik.png"),
    SUN_TZU("Sun Tzu with Subtle-Philosophical Style", "suntzu.png"),
    HANNIBAL("Hannibal Barca with Tactical-Innovative Style", "hannibal.png"),
    MARCUS_AURELIUS("Marcus Aurelius with Reflective Style", "marcus_aurelius.png"),
    SPORTS_COACH("Sports Coach with Performance-Oriented Style", "coach.png"),
    PSYCHOLOGIST("Psychologist with Cognitive-Analytical Style", "psychologist.png"),
    MANAGER("Professional Manager with Systemic-Organizational Style", "manager.png");

    private final String description;
    private final String photo;

    ReportStyle(String description, String photo) {
        this.description = description;
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }
    public String getPhoto() {
        if (this.photo == null || this.photo.isEmpty()) {
            return "default.png";
        }
        return this.photo;
    }


    private static final Random RANDOM = new Random();

    public static ReportStyle getRandomStyle() {
        ReportStyle[] styles = values();
        return styles[RANDOM.nextInt(styles.length)];
    }
}
