package xyz.jackoneill.litebans.templatestack.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class Template {
    private int index;
    private String template;
    private int punishments;
    private TemplateType type;
    private int expirationDays;

    public Template(String template, int punishments, TemplateType type) {
        this.index = -1;
        this.template = template;
        this.punishments = punishments;
        this.type = type;
    }

    @Override
    public String toString() {
        return "- type=" + this.type + ": " + this.template
                + " punishments=" + this.punishments
                + ",idx=" + this.index;
    }

    public long getCutOffTimestamp() {
        final long expirationCutOff = getExpirationDays() * 24L * 60 * 60 * 1000;
        return System.currentTimeMillis() - expirationCutOff;
    }

    public String getCutOffTimeString() {
        ZonedDateTime dateTime = Instant.ofEpochMilli(getCutOffTimestamp()).atZone(ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
    }
}
