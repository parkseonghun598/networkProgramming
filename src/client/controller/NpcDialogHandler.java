package client.controller;

import client.view.NpcDialogPanel;
import common.npc.NPC;
import common.player.Player;

import javax.swing.*;
import java.util.List;

public class NpcDialogHandler {
    private final JPanel gamePanel;
    private final NetworkHandler networkHandler;
    private NpcDialogPanel npcDialogPanel;
    private PlayerProvider playerProvider;

    public interface PlayerProvider {
        Player getMyPlayer();
    }

    public NpcDialogHandler(JPanel gamePanel, NetworkHandler networkHandler, PlayerProvider playerProvider) {
        this.gamePanel = gamePanel;
        this.networkHandler = networkHandler;
        this.playerProvider = playerProvider;
    }

    public void handleNpcClick(int clickX, int clickY, List<NPC> npcs) {
        // Check if NPC was clicked
        for (NPC npc : npcs) {
            int npcWidth = 80;
            int npcHeight = 80;
            if (clickX >= npc.getX() && clickX <= npc.getX() + npcWidth &&
                clickY >= npc.getY() && clickY <= npc.getY() + npcHeight) {
                openNpcDialog(npc);
                break;
            }
        }
    }

    private void openNpcDialog(NPC npc) {
        // 전사 NPC 대화
        if ("npc_warrior".equals(npc.getId())) {
            String[] messages = {
                "안녕하세요! 당신의 이름을 바꿔드릴 수 있는 미스터 뉴네임입니다.\n게임에 접속해줘서 고맙습니다!",
                "당신은 전사로 전직할 수 있는 자격을 갖추었습니다.\n전사는 강력한 힘과 방어력을 가진 직업입니다.",
                "전사로서의 앞날을 기대하겠습니다!\n이 무기를 받아주세요."
            };

            if (npcDialogPanel == null) {
                npcDialogPanel = new NpcDialogPanel(
                    npc.getSpritePath(),
                    messages,
                    new NpcDialogPanel.DialogCallback() {
                        @Override
                        public void onDialogComplete() {
                            closeNpcDialog();
                            // 5개 아이템 드롭 요청 (1행 4개 + 2행 1개)
                            requestItemDrop("defaultWeapon");
                            try { Thread.sleep(100); } catch (InterruptedException e) {}
                            requestItemDrop("bigWeapon");
                            try { Thread.sleep(100); } catch (InterruptedException e) {}
                            requestItemDrop("blackHat");
                            try { Thread.sleep(100); } catch (InterruptedException e) {}
                            requestItemDrop("glove");
                            try { Thread.sleep(100); } catch (InterruptedException e) {}
                            requestItemDrop("shoes");
                        }

                        @Override
                        public void onDialogCancel() {
                            closeNpcDialog();
                        }
                    }
                );
                npcDialogPanel.setBounds(50, 150, 700, 300);
                gamePanel.add(npcDialogPanel);
                npcDialogPanel.setVisible(true);
            } else {
                npcDialogPanel.reset();
                npcDialogPanel.setVisible(true);
            }
            
            gamePanel.revalidate();
            gamePanel.repaint();
        }
    }

    private void closeNpcDialog() {
        if (npcDialogPanel != null) {
            npcDialogPanel.setVisible(false);
            gamePanel.remove(npcDialogPanel);
            npcDialogPanel = null;
            gamePanel.revalidate();
            gamePanel.repaint();
            gamePanel.requestFocusInWindow(); // 게임 패널로 포커스 복귀
        }
    }

    private void requestItemDrop(String itemType) {
        Player myPlayer = playerProvider.getMyPlayer();
        if (myPlayer == null) return;

        String msg = String.format(
            "{\"type\":\"REQUEST_ITEM_DROP\",\"payload\":{\"itemType\":\"%s\",\"x\":%d,\"y\":%d}}",
            itemType, myPlayer.getX() + 30, myPlayer.getY()
        );
        networkHandler.sendMessage(msg);
        System.out.println("Requested item drop: " + itemType);
    }
}

