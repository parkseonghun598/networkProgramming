@echo off
chcp 65001 > nul
echo ====================================
echo  게임 서버 시작
echo  포트: 12345
echo ====================================
echo.

cd out
java server.GameServer

pause

