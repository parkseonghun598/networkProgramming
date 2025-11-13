# MapleStory 게임 프로젝트

Java 기반의 2D 멀티플레이어 횡스크롤 액션 게임입니다. 클라이언트-서버 아키텍처를 사용하여 여러 플레이어가 동시에 접속하여 플레이할 수 있습니다.

---
## 실행 방법

### 1. 재컴파일
```bash
rm -rf out/* && find src -name "*.java" -print0 | xargs -0 javac -d out -cp src
```

### 2. 서버 실행
```bash
cd out
java server.GameServer
```

### 3. 클라이언트 실행 (별도 터미널)
```bash
cd out
java client.GameClient
```

### 4. 다중 클라이언트 접속
여러 터미널에서 클라이언트를 실행하여 멀티플레이어 테스트 가능

---

## 게임 조작법

| 키 | 기능 |
|---|---|
| `←` | 왼쪽 이동 |
| `→` | 오른쪽 이동 |
| `SPACE` | 점프 |
| `Q` | 스킬 1 사용 |

## 아키텍처 설명

### 클라이언트-서버 통신 구조

```
[클라이언트]                    [서버]
    │                            │
    ├─ NetworkHandler ─────────→ ClientHandler
    │    (메시지 전송)            (메시지 수신 및 처리)
    │                            │
    │                            ├─ GameState
    │                            │   (게임 상태 관리)
    │                            │
    │                            └─ GameLoop
    ├─ GameStateParser ←─────────    (상태 업데이트 & 브로드캐스트)
    │    (상태 업데이트)
    │
    └─ GameRenderer
         (화면 렌더링)
```

### 메시지 프로토콜

#### WELCOME (서버 → 클라이언트)
```json
{"type":"WELCOME","payload":{"id":"player_abc123"}}
```

#### PLAYER_UPDATE (클라이언트 → 서버)
```json
{"type":"PLAYER_UPDATE","payload":{"x":100,"y":500,"state":"move","direction":"left"}}
```

#### SKILL_USE (클라이언트 → 서버)
```json
{"type":"SKILL_USE","payload":{"skillType":"skill1","direction":"right"}}
```

#### GAME_STATE (서버 → 클라이언트, 브로드캐스트)
```json
{
  "type":"GAME_STATE",
  "payload":{
    "players":[...],
    "monsters":[...],
    "skills":[...]
  }
}
```

### 게임 루프

**서버 사이드 (60 FPS)**
1. 몬스터 AI 업데이트
2. 스킬 이동 및 범위 체크
3. 게임 상태 직렬화
4. 모든 클라이언트에게 브로드캐스트

**클라이언트 사이드 (60 FPS)**
1. 로컬 점프 물리 계산
2. 키 입력 처리
3. 서버로부터 받은 상태로 동기화
4. 화면 렌더링


## 기술 스택

- **언어**: Java
- **GUI**: Java Swing
- **네트워크**: Java Socket (TCP)
- **데이터 포맷**: JSON (수동 파싱)
- **동시성**: Thread, CopyOnWriteArrayList
