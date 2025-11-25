@echo off
chcp 65001 > nul
echo ====================================
echo  프로젝트 컴파일 중...
echo ====================================

REM 기존 out 폴더 삭제
if exist out (
    echo 기존 컴파일 파일 삭제 중...
    rmdir /s /q out
)

REM out 폴더 생성
mkdir out

REM 모든 Java 파일 찾기
echo Java 파일 검색 중...
dir /s /b src\*.java > sources.txt

REM 컴파일
echo 컴파일 시작...
javac -encoding UTF-8 -d out @sources.txt

REM sources.txt 삭제
del sources.txt

if %errorlevel% equ 0 (
    echo.
    echo ====================================
    echo  컴파일 완료! ✓
    echo ====================================
    echo.
    echo 실행 방법:
    echo  1. 서버 실행: run_server.bat
    echo  2. 클라이언트 실행: run_client.bat
    echo.
) else (
    echo.
    echo ====================================
    echo  컴파일 실패! ✗
    echo ====================================
    echo.
)

pause

