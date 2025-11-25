@echo off
chcp 65001 > nul
echo ====================================
echo  컴파일 후 서버/클라이언트 선택
echo ====================================
echo.

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

if %errorlevel% neq 0 (
    echo.
    echo 컴파일 실패!
    pause
    exit /b 1
)

echo.
echo 컴파일 완료!
echo.

:menu
echo ====================================
echo  실행할 프로그램을 선택하세요:
echo ====================================
echo  1. 서버 실행
echo  2. 클라이언트 실행
echo  3. 종료
echo.
set /p choice="선택 (1-3): "

if "%choice%"=="1" goto server
if "%choice%"=="2" goto client
if "%choice%"=="3" goto end
echo 잘못된 선택입니다.
goto menu

:server
echo.
echo 서버 시작 중...
cd out
java server.GameServer
goto end

:client
echo.
echo 클라이언트 시작 중...
cd out
java client.GameClient
goto end

:end

