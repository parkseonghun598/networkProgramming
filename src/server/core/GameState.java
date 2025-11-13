package server.core;

import common.monster.Monster;
import common.player.Player;
import common.skills.Skill;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//todo : 맵별로 분리 -> 일단으 하나로
public abstract class GameState {
    private final String backgroundImagePath;

    private final Map<String, Player> players;
    private final List<Monster> monsters;
    private int maxMonsterCnt;
    private final List<Skill> skills;

    public GameState(String backgroundImagePath) {
        this.backgroundImagePath = backgroundImagePath;
        this.players = new ConcurrentHashMap<>();
        this.monsters = new CopyOnWriteArrayList<>();
        this.skills = new CopyOnWriteArrayList<>();
        this.maxMonsterCnt = 20; // 기본값 20으로 설정
    }

    /**
     * 각 맵에서 생성할 몬스터를 정의하는 추상 메서드
     * @return 생성된 몬스터 객체
     */
    protected abstract Monster createMonster();

    public void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
    }

    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    public void updatePlayer(String playerId, common.dto.PlayerUpdateDTO dto) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setX(dto.getX());
            player.setY(dto.getY());
            player.setState(dto.getState());
            player.setDirection(dto.getDirection());
        }
    }

    public List<Player> getAllPlayers() {
        return new CopyOnWriteArrayList<>(players.values());
    }

    public void setMaxMonsterCnt(int maxMonsterCnt) {
        this.maxMonsterCnt = maxMonsterCnt;
    }
    public int getMaxMonsterCnt(){
        return this.maxMonsterCnt;
    }
    public void addMonster(Monster monster) {
        monsters.add(monster);
    }

    public void removeMonster(String monsterId) {
        monsters.removeIf(m -> m.getName().equals(monsterId));
    }

    public List<Monster> getAllMonsters() {
        return this.monsters;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public void removeSkill(String skillId) {
        skills.removeIf(s -> s.getId().equals(skillId));
    }

    public List<Skill> getAllSkills() {
        return this.skills;
    }

    public void updateSkills() throws InterruptedException {
        // 모든 스킬 업데이트
        for (Skill skill : skills) {
            skill.update();
        }
        // 비활성화된 스킬 제거
        skills.removeIf(s -> !s.isActive());
    }

    /**
     * 몬스터를 자동으로 관리하는 메서드
     * 현재 몬스터 수가 최대 수보다 적으면 자동으로 생성
     */
    public void manageMonsters() {
        int currentMonsterCount = monsters.size();
        // 몬스터가 부족하면 최대 수까지 생성
        while (currentMonsterCount < maxMonsterCnt) {
            Monster newMonster = createMonster();
            addMonster(newMonster);
            currentMonsterCount++;
            System.out.println("몬스터가 생성되었스빈다/");
        }
    }
}

