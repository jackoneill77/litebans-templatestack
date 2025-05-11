package xyz.jackoneill.litebans.templatestack;

import litebans.api.Database;
import org.bukkit.OfflinePlayer;
import xyz.jackoneill.litebans.templatestack.model.Punishment;
import xyz.jackoneill.litebans.templatestack.model.Template;
import xyz.jackoneill.litebans.templatestack.util.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LiteBansManager {

    private final LiteBansConfig liteBansConfig;

    public LiteBansManager(TemplateStackPlugin plugin) {
        liteBansConfig = new LiteBansConfig(plugin);
    }

    public void loadConfig() {
        this.liteBansConfig.load();
    }

    public LiteBansConfig getConfig() {
        return this.liteBansConfig;
    }

    public List<Punishment> getActiveLadderPunishments(OfflinePlayer player, Template template) {
        final String tableName = template.getType().getTableName();
        long cutOffTimestamp = template.getCutOffTimestamp();
        String uuid = player.getUniqueId().toString();
        int templateIndex = template.getIndex();

        String query = "Select * from " + tableName + " where uuid = ? and template = ? and time > ? order by time desc;";
        List<Punishment> punishments = new ArrayList<>();
        try (PreparedStatement stmt = Database.get().prepareStatement(query)) {
            stmt.setString(1, uuid);
            stmt.setInt(2, templateIndex);
            stmt.setLong(3, cutOffTimestamp);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String reason = rs.getString("reason");
                    long id = rs.getLong("id");
                    long time = rs.getLong("time");
                    String bannedBy = rs.getString("banned_by_name");
                    String playerId = rs.getString("uuid");
                    punishments.add(new Punishment(id, time, reason, templateIndex, bannedBy, playerId));
                }
            }
        } catch (SQLException e) {
            Log.error(e.getMessage());
        }
        return punishments;
    }
}
