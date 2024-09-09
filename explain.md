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

## Test 1.1

이건 따로 기록은 안해둬서 그냥 말로만 적는건데, DB connection thread pool을 넉넉하게 가져가면 mvc든 webflux든 모든 모듈에서 100% 성공률을 보여줌.
즉, DB에 병목이 없는 상태에서는 전부 효율적이라고 볼 수 있을까?

## Test 2

- mvc에는 `Thread.sleep()`, coRouter에는 `delay()` 를 500ms를 줘서 DB단 I/O blocking 뿐만 아니라 연산 시간을 추가로 가정
  - (의문) 
    - 이렇게 처리하면 코루틴은 suspend 사용하니까 더 좋게 처리되는거 아닌가? 그렇다고 CPU에 부하를 주자니 그것도 좀 이상함. 실제 서비스는 CPU bound가 적을 것 같은데
  - 주요 비교 대상인 mvc-api와 corouter-api 모듈만 수행했는데, corouter-api가 더 높은 처리량을 보여주긴 함. 그래도 둘 다 70% 이상의 실패율을 보여줌(=처리량이 떨어짐)

## Test 3

DB 병목을 줄이고, 서버의 연산이 오래 걸린다고 가정함. 쿼리 딜레이를 1초 -> 0.1초로 줄이고, 각 요청에서 1초씩 쓰레드나 코루틴을 대기함.

- 이번에도 mvc-api와 corouter-api 모듈만 테스트했는데, corouter-api는 모든 요청을 처리했다.
  - 근데 이게 올바른 비교인지는 모르겠는게, CPU 연산을 수행하는게 아니라, 그냥 해당 코루틴을 delay 시켰기 때문이다.
  - 근데 또 메모리나 CPU 병목이 생기지 않는 한 큰 문제는 아닌거 같기도 하고... 어차피 요청을 반환하는 시간은 delay로 같으니까.
    - 근데 서버의 연산 수행 대신 계속 요청을 받거나 코루틴을 관리하면서 더 속도가 빠른 건 맞을것 같음.

## Test 4

루프를 사용해서 의미없는 연산을 계속 수행하게 함. (추가로 내부에서 계속 수를 더하고, 반환하게 해서 컴파일 최적화 여지를 줄임.)

- 이번에도 mvc-api와 corouter-api 모듈만 테스트했고, 오히려 corouter-api 성능이 낮았다.
  - 예상과 다른 이상한 결과가 나왔는데, mvc-api는 모든 요청을 처리했지만,corouter-api는 25% 정도의 요청을 처리하지 못했다.
  - 가정
    - 예시가 잘못되었다.
    - blocking 방식이므로 netty가 의도한 기본 설정과 다르게 튜닝이 필요하다.
    - 사실은 corouter-api 처럼 사용하는 방식이 비효율적이다.
