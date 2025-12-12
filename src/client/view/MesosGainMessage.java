package client.view;

/**
 * 메소 획득 메시지를 나타내는 클래스
 * 화면에 일정 시간 동안 표시되고 자동으로 사라집니다.
 */
public class MesosGainMessage {
    private int amount; // 획득한 메소 양
    private int x; // 표시할 X 좌표
    private int y; // 표시할 Y 좌표
    private long startTime; // 생성 시간
    private static final long DURATION = 2000; // 2초 동안 표시
    
    public MesosGainMessage(int amount, int x, int y) {
        this.amount = amount;
        this.x = x;
        this.y = y;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * 메시지가 아직 유효한지 확인
     */
    public boolean isAlive() {
        return System.currentTimeMillis() - startTime < DURATION;
    }
    
    /**
     * 현재 Y 좌표를 반환 (위로 올라가는 애니메이션)
     */
    public int getCurrentY() {
        long elapsed = System.currentTimeMillis() - startTime;
        // 2초 동안 50픽셀 위로 이동
        int offset = (int) (elapsed * 50 / DURATION);
        return y - offset;
    }
    
    /**
     * 현재 투명도를 반환 (페이드 아웃 효과)
     */
    public float getAlpha() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > DURATION * 0.7) { // 마지막 30% 동안 페이드 아웃
            float fadeProgress = (elapsed - DURATION * 0.7f) / (DURATION * 0.3f);
            return Math.max(0.0f, 1.0f - fadeProgress);
        }
        return 1.0f;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public int getX() {
        return x;
    }
}

