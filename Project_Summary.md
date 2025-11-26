# Mini MapleStory 프로젝트 분석

## 1. 프로젝트 개요

`Mini MapleStory`는 Java로 개발된 2D 멀티플레이어 온라인 롤플레잉 게임입니다. 이 프로젝트는 클래식한 클라이언트-서버 아키텍처를 따르며, Java Swing을 사용하여 그래픽 사용자 인터페이스(GUI)를 구현했습니다. 플레이어는 캐릭터를 선택하여 게임 세계에 접속하고, 다른 플레이어와 상호작용하며 몬스터를 사냥하고 스킬을 사용할 수 있습니다.

## 2. 핵심 기능

- **로그인 및 회원가입**: 사용자는 계정을 생성하고 로그인할 수 있습니다.
- **캐릭터 선택**: 사용자는 두 가지 유형의 캐릭터 중 하나를 선택할 수 있습니다.
- **실시간 멀티플레이**: 여러 플레이어가 동일한 맵에서 동시에 플레이할 수 있습니다.
- **채팅**: 게임 내에서 다른 플레이어와 실시간으로 채팅할 수 있습니다.
- **전투 시스템**: 플레이어는 몬스터를 공격하고 스킬을 사용할 수 있습니다.
- **맵 이동**: 포탈을 통해 다른 맵으로 이동할 수 있습니다.

## 3. 패키지 및 클래스별 기능 분석

### `client` 패키지

클라이언트 측 로직을 담당하며, 사용자 인터페이스(UI), 사용자 입력 처리, 서버와의 네트워크 통신을 관리합니다.

- **`GameClient.java`**: 클라이언트 애플리케이션의 진입점(entry point)입니다. `LoginFrame`을 생성하여 로그인 창을 띄웁니다.
- **`controller` 패키지**:
    - **`NetworkHandler.java`**: 서버와의 소켓 통신을 담당하는 `Runnable` 클래스입니다. 서버로부터 게임 상태 업데이트, 채팅 메시지 등을 수신하고, 사용자 입력을 서버로 전송합니다.
    - **`PlayerInputHandler.java`**: 키보드 입력을 처리하여 플레이어의 움직임, 점프, 스킬 사용 등을 제어합니다.
- **`util` 패키지**:
    - **`GameStateParser.java`**: 서버로부터 받은 JSON 형식의 게임 상태 메시지를 파싱하여 클라이언트의 게임 객체(플레이어, 몬스터 등)를 업데이트합니다.
    - **`SpriteManager.java`**: 게임에 사용되는 이미지(스프라이트)를 로드하고 관리합니다.
    - **`UserManager.java`**: `users.txt` 파일에서 사용자 정보를 로드하여 로그인 및 회원가입 기능을 처리합니다.
- **`view` 패키지**:
    - **`LoginFrame.java` & `LoginPanel.java`**: 로그인 및 회원가입 UI를 제공합니다.
    - **`CharacterSelectPanel.java`**: 캐릭터 선택 화면 UI를 제공합니다.
    - **`GameFrame.java` & `GamePanel.java`**: 실제 게임이 진행되는 메인 화면을 구성합니다. `GamePanel`은 게임 렌더링, 키 이벤트 처리, 클라이언트 측 게임 루프를 담당합니다.
    - **`GameRenderer.java`**: `GamePanel`에서 호출되어 실제 게임 화면을 그리는 역할을 합니다. 플레이어, 몬스터, 스킬, 맵 배경 등을 화면에 렌더링합니다.

### `server` 패키지

서버 측 로직을 담당하며, 게임의 핵심 상태를 관리하고 모든 클라이언트의 요청을 처리합니다.

- **`GameServer.java`**: 서버 애플리케이션의 진입점입니다. 클라이언트의 연결을 수락하고, 각 클라이언트에 대한 `ClientHandler` 스레드를 생성합니다.
- **`core` 패키지**:
    - **`GameState.java`**: 게임의 모든 상태(플레이어, 몬스터, 스킬, 맵 정보 등)를 총괄하는 클래스입니다. 게임의 핵심 데이터를 저장하고 관리합니다.
    - **`GameLoop.java`**: 서버의 메인 게임 루프를 실행하는 `Runnable` 클래스입니다. 주기적으로 게임 상태를 업데이트하고, 모든 클라이언트에게 변경된 상태를 브로드캐스팅합니다.
- **`handler` 패키지**:
    - **`ClientHandler.java`**: 각 클라이언트와의 통신을 담당하는 `Runnable` 클래스입니다. 클라이언트로부터 메시지를 수신하여 파싱하고, `GameState`를 업데이트합니다.
    - **`SkillCreator.java`**: 클라이언트의 스킬 사용 요청에 따라 서버 측에서 스킬 객체를 생성합니다.
- **`map` 패키지**:
    - **`GameMap.java`**: 개별 맵의 정보를 관리합니다. 맵의 배경 이미지, 몬스터, 포탈 정보 등을 포함합니다.
    - **`MapCreator.java`**: 게임에 사용될 맵(`Hennessis`, `BossMap` 등)을 생성하고 초기화합니다.
- **`util` 패키지**:
    - **`GameStateSerializer.java`**: 현재 `GameState`를 JSON 형식의 문자열로 직렬화하여 클라이언트에게 전송할 수 있도록 합니다.
    - **`MessageParser.java`**: 클라이언트로부터 받은 JSON 메시지를 파싱하여 서버가 이해할 수 있는 데이터 객체로 변환합니다.

### `common` 패키지

클라이언트와 서버 양측에서 공통으로 사용되는 데이터 모델과 유틸리티 클래스를 포함합니다.

- **`ImagePath.java`**: 게임에 사용되는 이미지 파일의 경로를 상수로 정의합니다.
- **`dto` 패키지**:
    - **`PlayerUpdateDTO.java`**: 플레이어 상태 업데이트 시 클라이언트와 서버 간에 데이터를 전송하기 위한 DTO(Data Transfer Object)입니다.
- **`enums` 패키지**:
    - **`Direction.java`**: 플레이어와 몬스터의 방향(LEFT, RIGHT 등)을 나타내는 열거형입니다.
- **`map` 패키지**:
    - **`Portal.java`**: 맵과 맵 사이를 이동할 수 있는 포탈 객체를 정의합니다.
- **`monster` 패키지**:
    - **`Monster.java`**: 몬스터의 기본 속성(HP, 위치 등)과 행동 로직을 정의하는 부모 클래스입니다.
    - **`GreenSlime.java`**: `Monster`를 상속받는 '그린 슬라임' 몬스터 클래스입니다.
- **`player` 패키지**:
    - **`Player.java`**: 플레이어의 상태(ID, 위치, 방향 등)를 저장하는 데이터 클래스입니다.
- **`skills` 패키지**:
    - **`Skill.java`**: 모든 스킬의 기본 속성(데미지, 범위, 속도 등)과 동작을 정의하는 추상 클래스입니다.
    - **`Skill1.java` ~ `Skill4.java`**: `Skill`을 상속받아 각각의 고유한 특성을 가진 스킬들을 구현한 클래스입니다.
- **`user` 패키지**:
    - **`User.java`**: 사용자의 계정 정보(아이디, 비밀번호, 선택한 캐릭터 타입)를 저장하는 데이터 클래스입니다.

## 4. 게임 기능과 관련된 주요 클래스

- **로그인**: `LoginPanel`(View) -> `UserManager`(Client Util) -> `users.txt`
- **캐릭터 이동**: `PlayerInputHandler`(Client Controller) -> `NetworkHandler`(Client Controller) -> `ClientHandler`(Server Handler) -> `GameState`(Server Core)
- **스킬 사용**: `PlayerInputHandler`(Client Controller) -> `NetworkHandler`(Client Controller) -> `ClientHandler`(Server Handler) -> `SkillCreator`(Server Handler) -> `GameState`(Server Core)
- **실시간 렌더링**: `GameLoop`(Server Core) -> `ClientHandler`(Server Handler) -> `NetworkHandler`(Client Controller) -> `GamePanel`(Client View) -> `GameRenderer`(Client View)
- **채팅**: `GamePanel`(Client View) -> `NetworkHandler`(Client Controller) -> `ClientHandler`(Server Handler) -> `GameLoop`(Server Core) -> All `ClientHandler`s
