package com.laudynetwork.networkutils.api.scoreboard;

import lombok.Getter;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;

public abstract class ScoreboardBuilder {

    @Getter
    private final Scoreboard playerBoard;
    private final Objective objective;

    public ScoreboardBuilder(Player player, Component displayName) {

        if (Bukkit.getScoreboardManager().getMainScoreboard().equals(player.getScoreboard()))
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());


        this.playerBoard = player.getScoreboard();

        if (this.playerBoard.getObjective(player.getUniqueId().toString()) != null)
            Objects.requireNonNull(this.playerBoard.getObjective(player.getUniqueId().toString())).unregister();

        this.objective = this.playerBoard.registerNewObjective(player.getUniqueId().toString(), Criteria.DUMMY, displayName);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        createBoard();
    }

    public abstract void createBoard();

    public abstract void update();

    public void setLine(Component content, int line) {
        val team = getByTeam(line);
        team.prefix(content);
        showLine(line);
    }

    public void removeScore(int scoreLine) {
        hideLine(scoreLine);
    }

    private void hideLine(int scoreLine) {
        val line = getLine(scoreLine);

        if (line == null)
            return;

        if (!this.objective.getScore(line.getPlaceholder()).isScoreSet())
            return;

        this.playerBoard.resetScores(line.getPlaceholder());

    }

    private void showLine(int scoreLine) {
        val line = getLine(scoreLine);

        if (line == null)
            return;

        if (this.objective.getScore(line.getPlaceholder()).isScoreSet())
            return;

        this.objective.getScore(line.getPlaceholder()).setScore(scoreLine);
    }

    private Team getByTeam(int scoreLine) {
        val line = getLine(scoreLine);

        assert line != null;

        var team = this.playerBoard.getTeam(line.getPlaceholder());
        if (team == null)
            team = this.playerBoard.registerNewTeam(line.getPlaceholder());

        team.addEntry(line.getPlaceholder());

        return team;
    }

    private ScoreLine getLine(int score) {
        for (ScoreLine value : ScoreLine.values()) {

            if (value.getLine() == score)
                return value;

        }
        return null;
    }
}
