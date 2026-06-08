package io.github.mrspock182.pokemon.repository.orm;

public class BattleSessionOrm {
    private String battleId;
    private String playerAId;
    private String playerBId;
    private String connectionIdA;
    private String connectionIdB;
    private String status;
    private String teamA;
    private String teamB;
    private Integer winsA;
    private Integer winsB;
    private Integer currentRound;
    private String winnerId;
    private Long ttl;

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public String getPlayerAId() {
        return playerAId;
    }

    public void setPlayerAId(String playerAId) {
        this.playerAId = playerAId;
    }

    public String getPlayerBId() {
        return playerBId;
    }

    public void setPlayerBId(String playerBId) {
        this.playerBId = playerBId;
    }

    public String getConnectionIdA() {
        return connectionIdA;
    }

    public void setConnectionIdA(String connectionIdA) {
        this.connectionIdA = connectionIdA;
    }

    public String getConnectionIdB() {
        return connectionIdB;
    }

    public void setConnectionIdB(String connectionIdB) {
        this.connectionIdB = connectionIdB;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTeamA() {
        return teamA;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }

    public Integer getWinsA() {
        return winsA;
    }

    public void setWinsA(Integer winsA) {
        this.winsA = winsA;
    }

    public Integer getWinsB() {
        return winsB;
    }

    public void setWinsB(Integer winsB) {
        this.winsB = winsB;
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
}
