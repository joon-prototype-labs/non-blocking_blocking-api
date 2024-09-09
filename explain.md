## Test 1
- spec
    - vuser: 100
    - duration: 30s
    - timeout: 8s
    - vuser's api request: every 0.5 seconds
    - query call delay time: 1s
- explain
    - (체크 필요) 아마 점직적으로 요청 빈도를 높인 것 같음. 요청 횟수는 400~500 사이로 발생함.
    - DB 단에서 의도적으로 쿼리를 delay하면서 DB단 병목 발생시킴.
    - 프로젝트 별 성능 비교 
      - TODO 작성 예정: 요청 처리 시간, 성공률, 네트워크 접속 시간(blocking으로 인한 이벤트 루프 delay?, 원인 파악 필요) 
      - mvc-api(webmvc + controller + jpa)
      - corouter-api(webflux + coRouter + jpa)
      - webflux-blocking-api(webflux + reactive + jpa)
      - webflux-non-blocking-api(webflux + reactive + r2dbc)

## Test 2

TODO 작성 예정:    
    ~~Test 2는 DB 처리 시간을 짦게 가져가서 WAS 단 병목을 어느정도로 버틸 수 있는지 확인.~~   
    ~~뭐 근데 이미 Test 1에서 이미 한거 아닌가?`~~
    ~~Test 3에서 delay를 엄청 길게 잡아서 DB 단 부하가 심하면 성능이 비슷하다는걸 검증해봐야 하나?~~
    Test 1으로도 충분한 결과가 나온거 같은데? 분석 해보고 테스트 더 할지말지 결졍

## Temp Memo

아래는 동일한 설정 내용을 `.properties` 확장 파일을 사용하는 방식으로 변환한 것입니다. 각 환경 간 성능 차이를 최소화하면서도 블로킹과 논블로킹의 성능 차이를 공정하게 비교할 수 있는 설정입니다.

### 1. **mvc-api (webmvc + controller + jpa)**

#### **Tomcat 기본 설정**:
```properties
server.tomcat.max-threads=200
server.tomcat.min-spare-threads=10
```

#### **JPA 설정**:
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### 2. **corouter-api (webflux + coRouter + jpa)**

#### **Netty 기본 설정**:
```properties
reactor.netty.max-threads=200
```

#### **JPA 설정**:
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### 3. **webflux-blocking-api (webflux + reactive + jpa)**

#### **Netty 기본 설정**:
```properties
reactor.netty.max-threads=200
```

#### **블로킹 스레드 풀 설정**:
```properties
spring.task.execution.pool.max-size=50
spring.task.execution.pool.queue-capacity=500
```

#### **JPA 설정**:
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### 4. **webflux-non-blocking-api (webflux + reactive + r2dbc)**

#### **Netty 기본 설정**:
```properties
reactor.netty.max-threads=200
```

#### **R2DBC 설정**:
```properties
spring.r2dbc.pool.max-size=10
spring.r2dbc.pool.initial-size=5
```

---

이 설정들은 주로 기본값을 유지하면서 블로킹과 논블로킹 환경의 성능 차이를 공정하게 비교할 수 있도록 `.properties` 파일 형식으로 구성되었습니다. `Tomcat vs Netty`, `JPA vs R2DBC` 간의 성능 차이를 최소화할 수 있도록 설계되었습니다.

---

성능 차이를 최소화하면서 기본 설정에 가깝게 맞추려면, 각 환경에 대한 스레드 풀과 데이터베이스 연결 풀의 설정을 조정하여 비교를 공정하게 만들 수 있습니다. 아래 설정들은 크게 튜닝하지 않고, 환경 간의 성능 차이를 줄이도록 기본 설정에 가깝게 맞춘 것입니다.

### 1. **mvc-api (webmvc + controller + jpa)**

#### **Tomcat 기본 설정**
Tomcat은 스레드 기반 모델이므로 기본적으로 동기적인 작업에 적합합니다.

- **Tomcat 스레드 풀**:
    - `maxThreads`: 200 (기본)
    - `minSpareThreads`: 10
```yaml
server:
  tomcat:
    max-threads: 200
    min-spare-threads: 10
```

#### **JPA 설정**
JPA는 기본적으로 동기적이고 블로킹이므로 `HikariCP`와 같은 연결 풀을 통해 트랜잭션 관리를 최적화합니다.

- **HikariCP 기본 설정**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10  # 기본 풀 사이즈 유지
      minimum-idle: 5
```

### 2. **corouter-api (webflux + coRouter + jpa)**

WebFlux는 논블로킹을 기반으로 하지만, JPA는 블로킹이므로 성능 저하를 유도하기 위한 블로킹 처리가 필요합니다.

#### **Netty 기본 설정**
Netty는 비동기 논블로킹 환경이므로 기본적으로 적은 스레드로 높은 동시성을 처리합니다.

- **Netty 스레드 풀 기본 설정**:
```yaml
reactor:
  netty:
    max-threads: 200  # 논블로킹을 위한 기본 설정
```

#### **JPA 설정**
JPA는 동기 블로킹 호출을 유도하므로 `Schedulers.boundedElastic()`로 작업을 처리할 수 있습니다.

- **JPA 기본 풀 설정**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
```
이 환경에서 JPA를 사용하는 것은 논블로킹 프레임워크에서 블로킹 작업을 처리하는 구조로, 성능 차이를 확인할 수 있습니다.

### 3. **webflux-blocking-api (webflux + reactive + jpa)**

이 환경에서는 WebFlux에서 블로킹 작업을 처리하여 성능 저하를 비교합니다.

- **Netty 기본 설정**: 기본 Netty 스레드 풀 설정을 사용합니다.
```yaml
reactor:
  netty:
    max-threads: 200
```

#### **블로킹 스레드 풀 설정**: WebFlux는 기본적으로 논블로킹이지만, 블로킹 작업을 처리하기 위한 스레드 풀을 사용합니다.
```yaml
spring:
  task:
    execution:
      pool:
        max-size: 50
        queue-capacity: 500
```

#### **JPA 기본 설정**:
JPA는 블로킹 I/O를 유도하므로 성능 저하가 발생할 수 있습니다.

- **HikariCP 기본 설정**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
```

### 4. **webflux-non-blocking-api (webflux + reactive + r2dbc)**

R2DBC는 완전히 논블로킹이므로 WebFlux와 자연스럽게 통합됩니다. 이 환경에서 성능 저하가 발생하지 않도록 기본 설정을 사용합니다.

#### **Netty 기본 설정**:
```yaml
reactor:
  netty:
    max-threads: 200
```

#### **R2DBC 설정**
R2DBC는 논블로킹이므로 최대한 기본 설정에 가깝게 유지하여 성능 차이를 비교합니다.

- **Connection Pool 기본 설정**:
```yaml
spring:
  r2dbc:
    pool:
      max-size: 10
      initial-size: 5
```

---

이 설정들은 주로 기본값을 유지하면서도, 블로킹과 논블로킹의 성능 차이를 공정하게 비교할 수 있도록 환경을 준비합니다. `Tomcat vs Netty`, `JPA vs R2DBC` 간의 성능 차이를 줄이기 위한 최소한의 설정만을 적용했습니다.
