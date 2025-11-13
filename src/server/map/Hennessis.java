package server.map;

import common.monster.GreenSlime;
import common.monster.Monster;
import server.core.GameState;

public class Hennessis extends GameState {
    private static final int MAX_MONSTER_COUNT = 10;

    public Hennessis() {
        super();
        // 헤네시스 맵의 최대 몬스터 수 설정
        setMaxMonsterCnt(MAX_MONSTER_COUNT);
    }

    @Override
    protected Monster createMonster() {
        // 헤네시스 맵에서는 그린 슬라임 생성
        return new GreenSlime();
    }
}
