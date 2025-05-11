package xyz.jackoneill.litebans.templatestack.model;

import lombok.Getter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class Punishment {

    private final long id;
    private final long time;
    private final String reason;
    private final int template;
    private final String bannedBy;
    private final UUID playerId;

    public Punishment(long id, long time, String reason, int template, String bannedBy, String playerId) {
        this.id = id;
        this.time = time;
        this.reason = reason;
        this.template = template;
        this.bannedBy = bannedBy;
        this.playerId = UUID.fromString(playerId);
    }

    public String getTimeString() {
        ZonedDateTime dateTime = Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
    }
}
