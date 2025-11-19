# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

메이플스토리에서 영감을 받은 Java 기반 2D 멀티플레이어 횡스크롤 액션 게임입니다. 클라이언트-서버 아키텍처와 TCP 소켓을 사용하여 실시간 멀티플레이어 게임플레이를 구현합니다.

**기술 스택:**
- 언어: Java (외부 의존성 없음)
- GUI: Java Swing
- 네트워크: Java Socket (TCP)
- 데이터 포맷: 수동 JSON 파싱 (라이브러리 미사용)
- 동시성: Thread, CopyOnWriteArrayList, ConcurrentHashMap

## 빌드 및 실행 명령어

### 컴파일
```bash
rm -rf out/* && find src -name "*.java" -print0 | xargs -0 javac -d out -cp src
```

### 서버 실행
```bash
cd out
java server.GameServer
```

### 클라이언트 실행
```bash
cd out
java client.GameClient
```

여러 터미널에서 클라이언트 명령어를 실행하여 동시에 여러 클라이언트를 접속시킬 수 있습니다.

## 아키텍처

### 클라이언트-서버 통신 구조

**서버 (포트 12345):**
- `GameServer` - 메인 진입점, 클라이언트 연결 수락
- `ClientHandler` - 클라이언트당 하나씩, 메시지 파싱 및 플레이어 상태 처리
- `GameLoop` - 60 FPS 루프, 게임 상태 업데이트 및 모든 클라이언트에 브로드캐스트
- `GameState` - 플레이어, 몬스터, 스킬, 현재 맵에 대한 중앙 권한

**클라이언트:**
- `GameClient` → `GameFrame` → `GamePanel` - Swing UI 계층 구조
- `NetworkHandler` - TCP 연결 유지, 게임 상태 업데이트 수신
- `PlayerInputHandler` - 키보드 입력 처리, 서버로 업데이트 전송
- `GameRenderer` - 60 FPS로 플레이어, 몬스터, 스킬을 화면에 렌더링

### 메시지 프로토콜 (수동 JSON)

모든 메시지는 줄 구분 JSON 문자열이며 문자열 연산을 통해 수동으로 파싱됩니다.

**WELCOME** (서버 → 클라이언트, 연결 시):
```json
{"type":"WELCOME","payload":{"id":"player_abc123"}}
```

**PLAYER_UPDATE** (클라이언트 → 서버):
```json
{"type":"PLAYER_UPDATE","payload":{"x":100,"y":500,"state":"move","direction":"left"}}
```

**SKILL_USE** (클라이언트 → 서버):
```json
{"type":"SKILL_USE","payload":{"skillType":"skill1","direction":"right"}}
```

**GAME_STATE** (서버 → 모든 클라이언트, 약 16ms마다 브로드캐스트):
```json
{
  "type":"GAME_STATE",
  "payload":{
    "map":{"backgroundImagePath":"img/henessis.png"},
    "players":[{"id":"player_123","x":100,"y":500}],
    "monsters":[{"id":"m1","name":"그린슬라임","type":"green_slime","x":300,"y":500}],
    "skills":[{"id":"s1","playerId":"player_123","type":"skill1","x":150,"y":500,"direction":"right","active":true}]
  }
}
```

### 핵심 아키텍처 패턴

**서버 권한:** 클라이언트측 점프 물리를 제외한 모든 게임 상태는 서버가 권한을 가집니다. 클라이언트는 입력을 전송하고, 서버가 검증 후 상태를 브로드캐스트합니다.

**스레드 안전성:** GameLoop와 여러 ClientHandler 스레드의 동시 접근을 처리하기 위해 스킬/몬스터는 `CopyOnWriteArrayList`, 플레이어는 `ConcurrentHashMap`을 사용합니다.

**맵 시스템:** `GameMap`이 몬스터와 배경을 캡슐화합니다. `MapCreator` 팩토리로 관리되며, 서버의 `GameState`가 현재 맵 참조를 보유합니다.

**스킬 시스템:** `common/skills/`의 추상 `Skill` 베이스 클래스. 서버는 SKILL_USE 메시지 수신 시 `SkillCreator` 팩토리를 통해 스킬을 생성합니다. 스킬은 매 프레임 위치를 업데이트하고 최대 사거리 이동 후 비활성화됩니다.

**수동 JSON:** 모든 직렬화/역직렬화는 `GameStateSerializer`와 `MessageParser`에서 문자열 연산으로 처리 - 외부 JSON 라이브러리 없음.

**패키지 구조:**
- `common/` - 공유 클래스 (Player, Monster, Skill, DTOs)
- `server/` - 서버측 로직 (core, handler, map, util)
- `client/` - 클라이언트측 로직 (view, controller, util)

### 중요 구현 세부사항

**플레이어 ID 생성:** `ClientHandler:27`에서 `Integer.toHexString(hashCode())` 사용 - UUID 아님.

**점프 물리:** 응답성을 위해 클라이언트측에서만 처리. 다른 이동은 서버에서 동기화.

**스킬 방향:** 현재 스킬 방향이 제대로 인식되지 않는 알려진 이슈 존재 (최근 커밋 히스토리 참조).

**몬스터 생성:** `GameMap`이 팩토리 패턴을 사용하여 `maxMonsters`까지 몬스터 개체수 유지 (`MapCreator.Hennessis()`는 GreenSlime 팩토리로 맵 생성).

**게임 루프 타이밍:** 서버와 클라이언트 모두 `Thread.sleep(16)`으로 60 FPS 목표.

**리소스 로딩:** `img/` 디렉토리에서 이미지 로드. 경로는 `ImagePath` 상수 클래스에서 관리.

## 개발 시 주요 패턴

새 기능 추가 시:

**1. 새 몬스터 타입:**
   - `common/monster/`에 `Monster`를 상속하는 클래스 생성
   - `MapCreator`에 팩토리 메서드 추가
   - 새 필드가 필요하면 `GameStateSerializer` 업데이트

**2. 새 스킬:**
   - `common/skills/`에 `Skill`을 상속하는 클래스 생성
   - `loadResources()`와 `getType()` 구현
   - `SkillCreator.createSkill()`에 케이스 추가
   - 클라이언트 `PlayerInputHandler`에 키 바인딩 추가

**3. 새 메시지 타입:**
   - `MessageParser`에 파싱 메서드 추가
   - `ClientHandler.run()` (서버) 또는 `NetworkHandler.run()` (클라이언트)에 핸들러 추가
   - 필요시 직렬화 업데이트

**4. 새 맵:**
   - `MapCreator`에 팩토리 메서드 추가
   - 배경 이미지 경로 제공
   - 몬스터 팩토리와 최대 개체수 정의
