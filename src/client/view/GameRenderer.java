package client.view;

import client.util.SpriteManager;
import client.util.CharacterAnimator;
import common.item.Item;
import common.map.Portal;
import common.monster.Monster;
import common.npc.NPC;
import common.player.Player;
import common.skills.Skill;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class GameRenderer {

    public static void render(Graphics g, BufferedImage background, String errorMessage,
            List<Monster> monsters, List<Skill> skills, List<Player> players,
            List<Portal> portals, List<NPC> npcs, List<Item> items, String myPlayerId, Map<String, CharacterAnimator> playerAnimators,
            int width, int height) {
        try {
            if (background != null) {
                g.drawImage(background, 0, 0, width, height, null);
            }

            if (errorMessage != null) {
                g.setColor(Color.RED);
                g.drawString(errorMessage, 50, 50);
                return;
            }

            renderNpcs(g, npcs);
            renderItems(g, items);
            renderPortals(g, portals);
            renderMonsters(g, monsters);
            renderSkills(g, skills);
            renderPlayers(g, players, myPlayerId, playerAnimators);
        } catch (Exception e) {
            e.printStackTrace();
            g.setColor(Color.RED);
            g.drawString("Rendering Error: " + e.getMessage(), 10, 50);
        }
    }

    private static void renderPortals(Graphics g, List<Portal> portals) {
        Image portalSprite = SpriteManager.getSprite("portal");
        if (portalSprite != null) {
            for (Portal portal : portals) {
                g.drawImage(portalSprite, portal.getBounds().x, portal.getBounds().y,
                        portal.getBounds().width, portal.getBounds().height, null);
            }
        }
    }

    private static void renderMonsters(Graphics g, List<Monster> monsters) {
        for (Monster monster : monsters) {
            String name = monster.getName();
            Image monsterSprite = SpriteManager.getSprite(name);

            if (monsterSprite != null) {
                g.drawImage(monsterSprite, monster.getX(), monster.getY(), 50, 50, null);
            } else {
                System.err.println(name + "의 이미지를 불러오는데 실패했습니다.");
            }
            // Draw Background for Text
            g.setColor(new Color(255, 255, 255, 180));
            g.fillRect(monster.getX() - 10, monster.getY() - 40, 120, 20);

            g.setColor(Color.BLACK);
            g.drawString(monster.getName(), monster.getX(), monster.getY() - 25);

            // Draw HP Bar
            int maxHp = monster.getMaxHp();
            int currentHp = monster.getHp();

            if (maxHp > 0) {
                int barWidth = 50;
                int barHeight = 5;
                int hpBarWidth = (int) ((double) currentHp / maxHp * barWidth);

                g.setColor(Color.RED);
                g.fillRect(monster.getX(), monster.getY() - 15, barWidth, barHeight);
                g.setColor(Color.GREEN);
                g.fillRect(monster.getX(), monster.getY() - 15, hpBarWidth, barHeight);
                g.setColor(Color.BLACK);
                g.drawRect(monster.getX(), monster.getY() - 15, barWidth, barHeight);
            }
        }
    }

    private static void renderSkills(Graphics g, List<Skill> skills) {
        for (Skill skill : skills) {
            Image sprite = skill.getSprite();
            if (sprite != null) {
                if (common.enums.Direction.LEFT.equals(skill.getDirection())) {
                    g.drawImage(sprite, skill.getX() + skill.getWidth(), skill.getY(),
                            -skill.getWidth(), skill.getHeight(), null);
                } else {
                    g.drawImage(sprite, skill.getX(), skill.getY(),
                            skill.getWidth(), skill.getHeight(), null);
                }
            } else {
                g.setColor(Color.YELLOW);
                g.fillRect(skill.getX(), skill.getY(), skill.getWidth(), skill.getHeight());
            }
        }
    }

    private static void renderPlayers(Graphics g, List<Player> players, String myPlayerId, 
                                     Map<String, CharacterAnimator> playerAnimators) {
        for (Player player : players) {
            Image playerSprite = null;
            
            // 애니메이터에서 현재 프레임 가져오기
            CharacterAnimator animator = playerAnimators.get(player.getId());
            if (animator != null) {
                playerSprite = animator.getCurrentFrame();
            }
            
            // 애니메이터가 없거나 프레임이 없으면 폴백
            if (playerSprite == null) {
                String characterType = player.getCharacterType() != null ? player.getCharacterType() : "defaultWarrior";
                playerSprite = SpriteManager.getSprite(characterType);
                
                // 캐릭터 전용 스프라이트가 없으면 기본 player 스프라이트
                if (playerSprite == null) {
                    playerSprite = SpriteManager.getSprite("player");
                }
            }
            
            // 캐릭터 크기 설정
            int characterWidth = 60;
            int characterHeight = 60;
            
            // 방향에 따라 이미지 뒤집기
            if (playerSprite != null) {
                common.enums.Direction direction = player.getDirection();
                if (direction != null && direction == common.enums.Direction.RIGHT) {
                    // 오른쪽을 보고 있으면 이미지를 뒤집어서 그리기
                    g.drawImage(playerSprite, player.getX() + characterWidth, player.getY(), -characterWidth, characterHeight, null);
                } else {
                    // 왼쪽을 보고 있으면 정상적으로 그리기
                    g.drawImage(playerSprite, player.getX(), player.getY(), characterWidth, characterHeight, null);
                }
            } else {
                // 최종 폴백: 파란 사각형
                g.setColor(Color.BLUE);
                g.fillRect(player.getX(), player.getY(), characterWidth, characterHeight);
            }

            // 내 플레이어는 초록색 테두리 표시
            if (player.getId() != null && player.getId().equals(myPlayerId)) {
                g.setColor(Color.GREEN);
                g.drawRect(player.getX() - 2, player.getY() - 2, characterWidth + 4, characterHeight + 4);
            }

            // 플레이어 위에 유저명 표시
            String displayName = player.getUsername() != null ? player.getUsername() : player.getId();
            g.setColor(Color.WHITE);
            g.drawString(displayName, player.getX() + 10, player.getY() - 5);
        }
    }

    private static void renderNpcs(Graphics g, List<NPC> npcs) {
        for (NPC npc : npcs) {
            // NPC 스프라이트 가져오기
            Image npcSprite = SpriteManager.getSprite(npc.getId());
            
            if (npcSprite != null) {
                // NPC 크기
                int npcWidth = 80;
                int npcHeight = 80;
                g.drawImage(npcSprite, npc.getX(), npc.getY(), npcWidth, npcHeight, null);
                
                // NPC 이름 표시
                g.setColor(Color.WHITE);
                g.drawString(npc.getName(), npc.getX() + 10, npc.getY() - 5);
            } else {
                // 폴백: 회색 사각형
                g.setColor(Color.GRAY);
                g.fillRect(npc.getX(), npc.getY(), 80, 80);
                g.setColor(Color.WHITE);
                g.drawString(npc.getName(), npc.getX() + 10, npc.getY() - 5);
            }
        }
    }

    private static void renderItems(Graphics g, List<Item> items) {
        for (Item item : items) {
            // 아이템 스프라이트 가져오기
            Image itemSprite = SpriteManager.getSprite(item.getType());
            
            if (itemSprite != null) {
                // 아이템 크기
                int itemWidth = 40;
                int itemHeight = 40;
                g.drawImage(itemSprite, item.getX(), item.getY(), itemWidth, itemHeight, null);
                
                // 아이템 이름 표시
                g.setColor(Color.YELLOW);
                g.drawString(item.getName(), item.getX() - 5, item.getY() - 5);
            } else {
                // 폴백: 노란색 사각형
                g.setColor(Color.ORANGE);
                g.fillRect(item.getX(), item.getY(), 40, 40);
                g.setColor(Color.YELLOW);
                g.drawString(item.getName(), item.getX() - 5, item.getY() - 5);
            }
        }
    }
}