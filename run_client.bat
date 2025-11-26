@echo off
chcp 65001 > nul
echo ====================================
echo  게임 클라이언트 시작
echo ====================================
echo.

cd out
java client.GameClient

pause

