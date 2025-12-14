# Mini MapleStory



Java 기반 2D 멀티플레이어 온라인 롤플레잉 게임 (MMORPG)입니다. 메이플스토리에서 영감을 받아 제작되었으며, 외부 게임 엔진이나 라이브러리 없이 순수 Java (Swing, Sockets)로 구현되었습니다.



---



## 실행 방법



### 1. 컴파일 (코드 변경 후 필수)



**Mac/Linux (Bash)**

```bash

rm -rf out/* && find src -name "*.java" -print0 | xargs -0 javac -d out -cp src

```



**Windows (Command Prompt)**

```cmd

rmdir /s /q out

mkdir out

dir /s /b src\*.java > sources.txt

javac -d out -cp src @sources.txt

del sources.txt

```



### 2. 서버 실행

```bash

cd out

java server.GameServer

```

서버가 포트 12345에서 클라이언트 연결을 대기합니다.



### 3. 클라이언트 실행

각 클라이언트마다 새 터미널을 열어 실행합니다.

```bash

cd out

java client.GameClient

```



---



## 조작키



| 키 | 동작 |

|---|---|

| `← →` | 좌우 이동 |

| `Space` | 점프 |

| `Q`, `W`, `E`, `R` | 스킬 사용 |

| `I` | 인벤토리 열기/닫기 |

| `O` | 장비창 열기/닫기 |

| `S` | 스탯창 열기/닫기 |

| `Z` | 아이템 줍기 |

| `↑` | 포탈/NPC 상호작용 |

| `Enter` | 채팅 |



---



## 프로젝트 구조



```

MapleStory/

├── README.md                    # 프로젝트 문서

├── MapleStory.iml              # IntelliJ IDEA 프로젝트 파일

├── .gitignore                  # Git 무시 파일

├── src/                        # 소스코드 디렉토리

│   ├── client/                 # 클라이언트 모듈

│   ├── server/                 # 서버 모듈

│   └── common/                 # 공유 리소스

└── img/                        # 게임 이미지 리소스

    ├── character/              # 캐릭터 스프라이트

    ├── clothes/                # 의상 아이템

    └── npc/                    # NPC 이미지

```



### src 디렉토리 상세 구조



```

src/

├── client/                          # 클라이언트

│   ├── GameClient.java              # 클라이언트 진입점

│   ├── controller/                  # 컨트롤러

│   │   ├── NetworkHandler.java      # 서버 통신 처리

│   │   ├── PlayerInputHandler.java  # 플레이어 입력 처리

│   │   └── NpcDialogHandler.java    # NPC 대화 처리

│   ├── util/                        # 유틸리티

│   │   ├── GameStateParser.java     # 서버 JSON 파싱

│   │   ├── CharacterAnimator.java   # 캐릭터 애니메이션

│   │   └── SpriteManager.java       # 스프라이트 관리

│   └── view/                        # UI 뷰

│       ├── GameFrame.java           # 메인 게임 창

│       ├── GamePanel.java           # 게임 렌더링 패널

│       ├── GameRenderer.java        # 게임 객체 렌더링

│       ├── LoginFrame.java          # 로그인 창

│       ├── LoginPanel.java          # 닉네임 입력 패널

│       ├── InventoryPanel.java      # 인벤토리 UI

│       ├── EquipPanel.java          # 장비 UI

│       ├── StatPanel.java           # 스탯 UI

│       ├── NpcDialogPanel.java      # NPC 대화 UI

│       └── MesosGainMessage.java    # 메소 획득 메시지

├── server/                          # 서버

│   ├── GameServer.java              # 서버 메인 (포트 12345)

│   ├── core/                        # 핵심 로직

│   │   ├── GameLoop.java            # 게임 루프 (60 FPS)

│   │   └── GameState.java           # 게임 상태 관리

│   ├── handler/                     # 핸들러

│   │   ├── ClientHandler.java       # 클라이언트 연결 처리

│   │   └── SkillCreator.java        # 스킬 객체 생성

│   ├── map/                         # 맵

│   │   ├── GameMap.java             # 맵 정의

│   │   └── MapCreator.java          # 맵 팩토리

│   └── util/                        # 유틸리티

│       ├── GameStateSerializer.java # 게임상태 JSON 직렬화

│       └── MessageParser.java       # 클라이언트 메시지 파싱

└── common/                          # 공유 리소스

    ├── ImagePath.java               # 이미지 경로 상수

    ├── player/

    │   └── Player.java              # 플레이어 데이터

    ├── monster/

    │   ├── Monster.java             # 몬스터 추상클래스

    │   ├── GreenSlime.java          # 그린 슬라임

    │   └── Dragon.java              # 드래곤 (보스)

    ├── skills/

    │   ├── Skill.java               # 스킬 추상클래스

    │   ├── Skill1.java              # Q 스킬

    │   ├── Skill2.java              # W 스킬

    │   ├── Skill3.java              # E 스킬

    │   └── Skill4.java              # R 스킬

    ├── item/

    │   └── Item.java                # 아이템

    ├── inventory/

    │   └── Inventory.java           # 인벤토리 관리

    ├── npc/

    │   └── NPC.java                 # NPC 정의

    ├── map/

    │   └── Portal.java              # 포탈 (맵 이동)

    ├── enums/

    │   └── Direction.java           # 방향 enum

    ├── dto/

    │   └── PlayerUpdateDTO.java     # 플레이어 상태 전송 DTO

    └── util/

        ├── ItemSlotMapper.java      # 인벤토리 슬롯 매핑

        └── StatCalculator.java      # 스탯 계산

```



---



## 클래스 목록 및 기능



### 서버 (server/)



| 클래스 | 설명 |

|------|------|

| `GameServer` | TCP 포트 12345에서 리스닝, 클라이언트 연결 수용, 게임루프 시작 |

| `GameLoop` | 60 FPS로 게임상태 업데이트 및 모든 클라이언트에 상태 브로드캐스트 |

| `GameState` | 전역 게임상태 관리 (모든 플레이어, 몬스터, 스킬, 아이템) |

| `ClientHandler` | 각 클라이언트 연결마다 스레드로 실행, 메시지 수신/처리 |

| `SkillCreator` | 스킬 객체 생성 팩토리 |

| `GameMap` | 맵 정의 (배경, 몬스터, 포탈, NPC) |

| `MapCreator` | 3개 맵 생성 팩토리 (warriorRoom, hennesis, bossMap) |

| `GameStateSerializer` | 게임상태를 JSON으로 직렬화, 충돌 감지 및 몬스터 처치 로직 포함 |

| `MessageParser` | 클라이언트로부터 받은 메시지 파싱 |



### 클라이언트 (client/)



| 클래스 | 설명 |

|------|------|

| `GameClient` | 클라이언트 진입점, LoginFrame 시작 |

| `NetworkHandler` | 서버와의 TCP 소켓 통신 관리, JSON 메시지 수신 |

| `PlayerInputHandler` | 플레이어 입력 처리 (키보드 이벤트) |

| `NpcDialogHandler` | NPC 대화 상호작용 처리 |

| `GameStateParser` | 서버로부터 받은 JSON을 게임상태로 파싱 |

| `CharacterAnimator` | 캐릭터 애니메이션 관리 (idle, move, jump) |

| `SpriteManager` | 스프라이트 이미지 로드 및 관리 |

| `GameFrame` | 메인 게임 윈도우 |

| `GamePanel` | 게임 화면 렌더링 및 입력 처리 (핵심 UI) |

| `GameRenderer` | 게임 객체 (플레이어, 몬스터, 아이템 등) 렌더링 |

| `LoginFrame` | 로그인 윈도우 |

| `LoginPanel` | 닉네임 입력 UI |

| `InventoryPanel` | 인벤토리 UI (I키) |

| `EquipPanel` | 장비 UI (O키) |

| `StatPanel` | 플레이어 스탯 UI (S키) |

| `NpcDialogPanel` | NPC 대화 UI |

| `MesosGainMessage` | 메소 획득 알림 메시지 |



### 공통 (common/)



| 클래스 | 설명 |

|------|------|

| `Player` | 플레이어 데이터 (이름, 위치, 맵, 스탯, 장비, 인벤토리) |

| `Monster` | 몬스터 추상클래스 (HP, 위치, 이동, 충돌) |

| `GreenSlime` | 그린 슬라임 몬스터 (HP: 10) |

| `Dragon` | 드래곤 보스 몬스터 (HP: 20) |

| `Skill` | 스킬 추상클래스 (이동, 범위, 데미지) |

| `Skill1` ~ `Skill4` | Q, W, E, R 스킬 구현 |

| `Item` | 아이템 (장비, 코인) |

| `Inventory` | 인벤토리 관리 |

| `NPC` | NPC 정의 및 대화 |

| `Portal` | 맵 이동 포탈 |

| `Direction` | 방향 enum (LEFT, RIGHT) |

| `PlayerUpdateDTO` | 플레이어 상태 전송용 DTO |

| `ItemSlotMapper` | 인벤토리 슬롯 매핑 |

| `StatCalculator` | 플레이어 스탯 계산 |

| `ImagePath` | 이미지 경로 상수 |



---



## 구현 기능



### 멀티플레이어

- TCP 소켓 기반 실시간 다중 사용자 접속

- 서버에서 60 FPS로 게임상태 브로드캐스트

- `ConcurrentHashMap`, `CopyOnWriteArrayList`를 사용한 스레드 안전 처리



### 계정 시스템

- 닉네임 입력 후 바로 게임 시작

- 기본 캐릭터 타입: defaultWarrior



### 캐릭터 시스템

- 실시간 캐릭터 애니메이션 (대기, 이동, 점프)

- 레벨 시스템 (최대 레벨 5)

- 경험치 획득 및 레벨업

- 스탯 시스템 (HP, 공격력 등)



### 전투 시스템

- 4가지 스킬 (Q, W, E, R)

- 몬스터와의 충돌 감지 및 데미지 처리

- 몬스터 처치 시 경험치 획득



### 아이템 시스템

- 몬스터 처치 시 코인 드롭 (확정)

- 장비 아이템 드롭 (10% 확률)

- Z키로 아이템 줍기

- 메소(게임 화폐) 시스템



### 인벤토리 & 장비

- 인벤토리 UI (I키)

- 장비창 UI (O키)

- 장비 슬롯: 무기, 모자, 상의, 하의, 장갑, 신발

- 장비 착용 시 스탯 변화



### 맵 시스템

- 3개 맵: WarriorRoom (시작), Hennesis (일반), BossMap (보스)

- 포탈을 통한 맵 이동 (↑키)

- 각 맵별 고유 몬스터 배치



### 몬스터

| 몬스터 | HP | 출현 맵 |

|------|------|------|

| 그린 슬라임 | 10 | Hennesis |

| 드래곤 | 20 | BossMap |



### NPC 시스템

- NPC 대화 기능 (↑키)

- 대화 UI 표시



### 채팅

- 실시간 채팅 (Enter키)

- 다른 플레이어에게 메시지 전송



---



## 기술 스택



- **언어**: Java

- **GUI**: Java Swing

- **네트워크**: TCP Sockets

- **데이터 포맷**: Custom JSON 파싱 (외부 라이브러리 미사용)

- **동시성 처리**: `Thread`, `ExecutorService`, `CopyOnWriteArrayList`, `ConcurrentHashMap`



---



## 아키텍처



### Client-Server 모델



**서버**

- 다중 `ClientHandler` 스레드로 클라이언트 연결 관리

- `GameState`에서 전역 게임상태 관리

- `GameLoop`에서 60 FPS로 상태 업데이트 및 브로드캐스트



**클라이언트**

- `GamePanel`에서 UI 렌더링

- `PlayerInputHandler`에서 사용자 입력 처리

- `GameRenderer`에서 서버로부터 받은 상태 기반 렌더링

- `NetworkHandler`에서 서버와 통신



### 통신 프로토콜 (JSON 기반)



**서버 → 클라이언트**

- `GAME_STATE`: 게임상태 (플레이어, 몬스터, 아이템)

- `WELCOME`: 플레이어 ID 할당

- `CHAT`: 채팅 메시지



**클라이언트 → 서버**

- `USER_INFO`: 사용자명, 캐릭터 타입

- `PLAYER_UPDATE`: 위치, 상태, 방향

- `SKILL_USE`: 스킬 사용

- `CHAT`: 메시지 전송

- `PICKUP_ITEM`: 아이템 습득

- `EQUIP_ITEM`: 장비 착용



---



## 개발 환경



- Java 8 이상

- IntelliJ IDEA (권장)

- 외부 라이브러리 없음 (순수 Java)