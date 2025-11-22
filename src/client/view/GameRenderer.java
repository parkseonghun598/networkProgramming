package client.view;

import client.util.SpriteManager;
import common.map.Portal;
import common.monster.Monster;
import common.player.Player;
import common.skills.Skill;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

public class GameRenderer {

    public static void render(Graphics g, BufferedImage background, String errorMessage,
            List<Monster> monsters, List<Skill> skills, List<Player> players,
            List<Portal> portals, String myPlayerId, int width, int height) {
        try {
            if (background != null) {
                g.drawImage(background, 0, 0, width, height, null);
            }

            if (errorMessage != null) {
                g.setColor(Color.RED);
                g.drawString(errorMessage, 50, 50);
                return;
            }

            renderPortals(g, portals);
            renderMonsters(g, monsters);
            renderSkills(g, skills);
            renderPlayers(g, players, myPlayerId);
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

    private static void renderPlayers(Graphics g, List<Player> players, String myPlayerId) {
        Image playerSprite = SpriteManager.getSprite("player");
        for (Player player : players) {
            if (playerSprite != null) {
                g.drawImage(playerSprite, player.getX(), player.getY(), 100, 100, null);
            } else {
                g.setColor(Color.BLUE);
                g.fillRect(player.getX(), player.getY(), 100, 100);
            }

            if (player.getId() != null && player.getId().equals(myPlayerId)) {
                g.setColor(Color.GREEN);
                g.drawRect(player.getX() - 2, player.getY() - 2, 104, 104);
            }
            g.drawString(player.getId(), player.getX(), player.getY() - 5);
        }
    }
}