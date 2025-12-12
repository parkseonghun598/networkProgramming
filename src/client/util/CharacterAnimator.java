package client.util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 캐릭터 애니메이션 관리 클래스
 * 각 캐릭터의 상태(idle, walk, jump, attack)에 따른 스프라이트 애니메이션을 관리합니다.
 */
public class CharacterAnimator {
    private String characterFolderPath; // 착용 아이템 조합을 포함한 폴더 경로
    private Map<String, Image[]> animations;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private int frameDelay = 100; // 밀리초 단위 (100ms = 0.1초)
    private String currentState = "idle";

    public CharacterAnimator(String characterFolderPath) {
        this.characterFolderPath = characterFolderPath;
        this.animations = new HashMap<>();
        loadAnimations();
    }

    /**
     * 착용 아이템이 변경되었을 때 애니메이션을 다시 로드합니다.
     */
    public void updateCharacterAppearance(String newCharacterFolderPath) {
        if (!this.characterFolderPath.equals(newCharacterFolderPath)) {
            this.characterFolderPath = newCharacterFolderPath;
            this.animations.clear();
            loadAnimations();
            System.out.println("Updated character appearance to: " + newCharacterFolderPath);
        }
    }

    /**
     * 캐릭터의 모든 애니메이션 프레임을 로드합니다.
     * 폴더가 없으면 기본 캐릭터 폴더를 시도합니다.
     */
    private void loadAnimations() {
        String basePath = "../img/character/" + characterFolderPath + "/";
        File baseDir = new File(basePath);
        
        // 폴더가 없으면 기본 캐릭터 폴더 시도
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            String defaultPath = "../img/character/defaultWarrior/";
            System.out.println("Character folder not found: " + basePath + ", trying default: " + defaultPath);
            basePath = defaultPath;
        }
        
        try {
            // Stand (idle) 애니메이션: stand1_0만 사용 (정적 프레임)
            Image[] standFrames = new Image[1];
            File standFile = new File(basePath + "stand1_0.png");
            if (standFile.exists()) {
                standFrames[0] = ImageIO.read(standFile);
            } else {
                System.err.println("Missing stand frame: " + standFile.getPath());
            }
            animations.put("idle", standFrames);

            // Walk 애니메이션: walk1_0 ~ walk1_4
            Image[] walkFrames = new Image[5];
            boolean hasWalkFrames = false;
            for (int i = 0; i < 5; i++) {
                File file = new File(basePath + "walk1_" + i + ".png");
                if (file.exists()) {
                    walkFrames[i] = ImageIO.read(file);
                    hasWalkFrames = true;
                } else {
                    System.err.println("Missing walk frame: " + file.getPath());
                }
            }
            if (hasWalkFrames) {
                animations.put("move", walkFrames);
            }

            // Jump 애니메이션: jump_0 ~ jump_1
            Image[] jumpFrames = new Image[2];
            boolean hasJumpFrames = false;
            for (int i = 0; i < 2; i++) {
                File file = new File(basePath + "jump_" + i + ".png");
                if (file.exists()) {
                    jumpFrames[i] = ImageIO.read(file);
                    hasJumpFrames = true;
                } else {
                    System.err.println("Missing jump frame: " + file.getPath());
                }
            }
            if (hasJumpFrames) {
                animations.put("jump", jumpFrames);
            }

            // Swing (attack) 애니메이션: swingT1_0 ~ swingT1_3
            Image[] swingFrames = new Image[4];
            boolean hasSwingFrames = false;
            for (int i = 0; i < 4; i++) {
                File file = new File(basePath + "swingT1_" + i + ".png");
                if (file.exists()) {
                    swingFrames[i] = ImageIO.read(file);
                    hasSwingFrames = true;
                } else {
                    System.err.println("Missing swing frame: " + file.getPath());
                }
            }
            if (hasSwingFrames) {
                animations.put("attack", swingFrames);
            }

            System.out.println("Loaded animations for " + characterFolderPath + " from " + basePath);
        } catch (IOException e) {
            System.err.println("Failed to load animations for " + characterFolderPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 현재 상태를 설정합니다.
     * @param state "idle", "move", "jump", "attack"
     */
    public void setState(String state) {
        if (state == null) {
            state = "idle";
        }
        
        // 상태가 변경되면 프레임을 초기화하고 시간을 리셋
        if (!this.currentState.equals(state)) {
            this.currentState = state;
            this.currentFrame = 0; // 상태가 변경되면 프레임을 초기화
            this.lastFrameTime = System.currentTimeMillis();
        }
        
        // idle 상태로 전환될 때마다 프레임을 0으로 고정
        if ("idle".equals(state)) {
            this.currentFrame = 0;
        }
    }

    /**
     * 현재 프레임의 이미지를 반환합니다.
     * 자동으로 다음 프레임으로 전환됩니다.
     */
    public Image getCurrentFrame() {
        Image[] frames = animations.get(currentState);
        if (frames == null || frames.length == 0) {
            // 폴백: idle 애니메이션
            frames = animations.get("idle");
            if (frames == null || frames.length == 0) {
                return null;
            }
        }

        // idle 상태일 때는 애니메이션 없이 첫 번째 프레임만 반환
        if ("idle".equals(currentState)) {
            currentFrame = 0; // idle 상태에서는 항상 0번 프레임으로 고정
            return frames[0];
        }

        // 프레임 업데이트 체크 (idle이 아닐 때만)
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= frameDelay) {
            currentFrame = (currentFrame + 1) % frames.length;
            lastFrameTime = currentTime;
        }
        
        // 현재 프레임이 유효한 범위인지 확인
        if (currentFrame < 0 || currentFrame >= frames.length) {
            currentFrame = 0;
        }
        
        // 프레임이 null이면 첫 번째 프레임 반환
        if (frames[currentFrame] == null && frames.length > 0) {
            return frames[0];
        }

        return frames[currentFrame];
    }

    /**
     * 특정 프레임을 직접 가져옵니다 (디버깅용)
     */
    public Image getFrame(String state, int frameIndex) {
        Image[] frames = animations.get(state);
        if (frames != null && frameIndex >= 0 && frameIndex < frames.length) {
            return frames[frameIndex];
        }
        return null;
    }

    /**
     * 애니메이션 속도를 설정합니다.
     * @param delayMs 프레임 간 지연 시간 (밀리초)
     */
    public void setFrameDelay(int delayMs) {
        this.frameDelay = delayMs;
    }

    /**
     * 현재 상태를 반환합니다.
     */
    public String getCurrentState() {
        return currentState;
    }

    /**
     * 현재 캐릭터 폴더 경로를 반환합니다.
     */
    public String getCharacterFolderPath() {
        return characterFolderPath;
    }
}

