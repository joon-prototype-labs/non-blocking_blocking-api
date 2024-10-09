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

루프를 사용해서 의미없는 연산을 0.5초동안 계속 수행하게 함. (내부에서 계속 수를 더하고, API 결과로 반환하게 해서 컴파일 최적화로 제거되는 여지를 줄임.)

- 이번에도 mvc-api와 corouter-api 모듈만 테스트했고, 오히려 corouter-api 성능이 낮았다.
  - 예상과 다른 이상한 결과가 나왔는데, mvc-api는 모든 요청을 처리했지만,corouter-api는 25% 정도의 요청을 처리하지 못했다.
  - 가정
    - 예시가 잘못되었다. 구현 방법 등...
    - blocking 방식이므로 netty가 의도한 기본 설정과 다르게 튜닝이 필요하다.
    - 사실은 corouter-api 처럼 사용하는 방식이 비효율적이다.
    - (+ netty같은 이벤트 루프 방식은 기존 방식 대비 io bound에 효율적이고 cpu bound는 비효율적이라는 소리를 들은 것 같은데 이게 원인일까?)

## Test 5

- DB단의 병목이 끼면 성능이 똑같다는건 확인함. 앞 단에서 더 효율적인 처리가 가능하다면, 병목이 없는(=DB 접근이 없는) API가 섞인다면 더 높은 처리율을 보일 것 같음.
  - 이건 가정을 검증하기 위한 거지, 아마 실제로는 이런 DB에 접근하지 않는 API 자체가 없으니까 
  - 이게 만약 성공한다면, corouter-api는 쓰레드 풀의 제한을 넘어서 요청을 받을 수 있는(= 앞단의 병목이 더 적은) 형태라고 보면 좋을듯.
  - db call이 있는 요청 후 0.5 딜레이, 없는 요청 후 0.5초 딜레이를 반복. 그 외 조건은 Test1 과 동일한 상태로 작성.
- 결과
  - ~~corouter-api가 더 많이 실패했는데, netty의 메모리? 과한 요청으로 인해서 바로 실패하고 있었다.~~
    - 이거 착각한거임. 지금 수치는 잘 되는거 맞음. 저번에 200명+1분인가 했을 때 생긴 문제 
  - 따라서 적절한 테스트를 위해서는 DB단의 병목이 발생하면서 과하지 않는 요청이 들어오도록 k6의 스크립트를 가공해야 한다.
  - 왜냐면 지금 상황의 결과는 테스트를 수행하기 위해 적절한 조건이 아니니까... (요청을 못 받고 바로 던지는 상태, 너무 과한 요청이 들어오는 상태라고 볼 수 있음.)
    - 테스트 목적은 앞 단의 병목을 처리하는데 corouter-api이 더 효율적인지 검증하는 것이기 때문. (=mvc는 스레드 풀이 부족하지만, corouter-api는 스레드 풀 제한 없이 받을 수 있음?을 확인하는거)

## 걍 메모

- 어차피 이거 올리기에는 너무 삽질을 많이해서 걍 버려야 할듯
- 근데 timeout 조건 제거하고, 처리 시간 로깅 추가하고 하니까. 처리 시간이 corouter-api가 mvc보다 조금 더 더 효율적임.
- 로그를 보면 mvc는 미리 쓰레드 풀만큼 다 받아놓고 처리한다면, netty는 처리되면 그떄그떄 받아서 처리하는데, 그래서 로그의 duration이 더 높게 나옴.
  - 근데 요청자 입장에서 보면 대기 시간은 마찬가지 아닌가? 오히려 netty가 요청을 더 많이 빨리 받아놔서 timeout으로 실패하는줄 알았는데,
  - 아마 netty가 요청은 먼저 받고, 처리는 반환되어야 하는 듯?
  - 그 근거로 처리 속도를 보면 corouter-api는 요청을 받고 처리하기까지 mvc-api에 비해 높은 변동성을 가짐. 최악은 13초 - 36초로 거의 3배차이 (p90는 1.5, p95는 2배). 따라서 mvc보다 더 많이 받을 수는 있음.
  - 근데? 이게 DB 병목이 있으면 처리 평균 속도는 결국 비슷함. 
- 그래서 Test 4 처럼 DB Call이 없는걸 추가해서 테스트해봄.
  - 근데 이러니까 corouter-api가 오히려 더 느리고 처리율도 낮은데?
  - 그게 맞나? 요청을 미리 받는거니까? 
    - 아니여야 하지 않나? 오히려 blocking이 없으니까 요청을 더 많이 처리해야 하는거 아닌가?
    - 아무리 생각해봐도 이게 더 효율적인 구조라는걸 이해할수가 없는데, 애초에 non-blocking(recator)에서 blocking이 들어가면 더 느려지는건 당연한거 아니였던가?
    - 이게 어떻게 더 효율적이라는거지?
    - 검증이라도 되면 몰라...... 아니면 내가 잘못하고 있는건가?
    - 이쯤 되면 의심될 정도인게, 효율적이면 다른 곳에서도 어떻게든 찾아서 쓰지 않았을까? 이러면 이게 더 좋은 거라고 어떻게 확신할 수 있지?
  - 혹시 몰라서 DB 병목이 거의 적게 0.1초로 테스트
    - db call 있는거, 없는거 번갈아서 호출
      - http_req_duration(corouter-api): avg=154.5ms  min=161µs med=112.09ms max=1.67s   p(90)=272.19ms p(95)=346.42ms
      - http_req_duration(mvc-api): avg=77.94ms  min=174µs med=96.59ms max=1.51s   p(90)=125.4ms  p(95)=149.67ms
    - db call만 호출하는거
      - http_req_duration(corouter-api): avg=585.12ms min=139.62ms med=580.39ms max=1.98s   p(90)=601.46ms p(95)=620.71ms 
      - http_req_duration(mvc-api): avg=582.22ms min=103.98ms med=575.31ms max=1.27s   p(90)=594.75ms p(95)=603.82ms
      - 어느 부분으로 봐도 더 빠른 경우가 없는데?
      - 뭐가 문제일까?
      - 일단 이걸로 거의 몇 주째 틈틈히 확인하고 있어도, 더 효율적이라는것 모르겠으니까... 콜라한테 물어보던가 하고, 일단은 그만 찾기. 이제는 시간낭비야...

## 마지막 

- 혹시 모르니까 연결되어 있는 커넥션 수를 세어볼꺼임.
  - spring 단에서도 확인해보고, 더 앞단에서도 확인할 수 있으면 하고 싶긴 한데, 가능할지는 모르겠음.

다음은 Test1과 동일한 상황에서 timeout 없이 해본거임.

- 순서대로
  - webmvc + controller + jpa
  - webflux + coRouter + jpa
  - webflux + reactive + r2dbc

결과를 보면 처리율은 webflux 기반의 요청이 더 높게 나옴.    
반면에 처리 시간은 r2dbc -> mvc -> coRouter 순으로 오래 걸렸고,  

이유는 모르겠지만, webflux 기반의 프로젝트가 DB Blocking이 똑같이 걸리는 상태에서 더 많은 요청을 처리한건 사실임.
근데 coRouter는 jpa를 사용했으므로 blocking 처럼 동작하고, 대기시간이 길다는 문제가 있음.

(확인해보니 webflux+jpa 조합의 경우 처리율도 낮으면서 대기시간도 길었다.)

근데 딱히 유의미한 결과는 아닌 것 같은게, 모든(4) 프로젝트를 각자 5회 씩 번갈아 수행하니까 다 비슷한 처리율을 보였다.

유의미하게 차이가 있던건 처리 시간이 r2dbc -> mvc -> coRouter, webflux+jpa 순으로 짦았다는건데,      
r2dbc야 번외로 치고, 앞 단에서 요청을 받는? 시간이 더 빠르다는 말이라고 생각된다. (타임아웃 비율이 높았던 이유) 

그래서 콜라가 그렇게 말한 이유를 알것 같기는 한데, 이게 효과적인건지는 모르겠다. 어치파 처리율이 동일하다면, 먼저 받나 그 나중에 받나 똑같은거 아닌가?  

AI에게 좀 믈어나 보자...

뭐... 대충 머릿속으로도 정리가 된것 같다.     

하지만 어차피 DB 부하가 있는 상태에서, 요청을 먼저 받느냐 나중에 받느냐가 중요한건지 모르겠다.      
대기열도 차서 연결 거부가 생기는 그런 문제가 없다는걸까?

결국 어느정도 이해는 했지만, 얼마나 효과적인지, 사용할 가치가 있는지는 잘 모르겠다.          
성능을 위해서 스프링이 제공하는 여러 편리한 기능(+관련 라이브러리)을 사용하는 것에 비해 더 효율적일까? (coRouter는 아무래도 지원이 좀 떨어진다.)       

```
한국어 요약본

네, 지금까지 논의한 서블릿 기반 서버와 리액티브 서버의 특성을 정리하여 한글로 번역해 드리겠습니다.

## 서버 유형별 특성

### 서블릿 기반 서버 (예: Apache Tomcat)

1. **스레드 풀이 가득 찼을 때:**
   - 새로운 요청을 즉시 처리할 수 없음
   - HTTP 연결이 지연되거나 대기열에 추가됨
   - 대기열이 가득 차면 연결 거부나 TCP 연결 지연 발생 가능

2. **클라이언트 관점:**
   - 연결 설정에 시간이 오래 걸림
   - 연결된 후에는 처리가 상대적으로 빠름
   - 일부 요청은 시간 초과될 수 있음

### 리액티브 서버 (예: Netty)

1. **높은 부하 상황에서:**
   - 비차단, 이벤트 기반 아키텍처 사용
   - 적은 수의 스레드로 많은 동시 연결 처리 가능
   - 새로운 연결을 계속 수락할 수 있음
   - 요청은 비동기적으로 처리됨

2. **클라이언트 관점:**
   - 연결이 빠르게 설정됨
   - 처리 시간이 상대적으로 길게 느껴질 수 있음
   - 서버는 응답성을 유지하지만 개별 요청 처리가 지연될 수 있음

## 클라이언트 경험 비교

1. **연결 시간:**
   - 서블릿: 연결 설정에 시간이 오래 걸림
   - 리액티브: 연결이 빠르게 설정됨

2. **처리 시간:**
   - 서블릿: 연결 후 처리가 빠름
   - 리액티브: 많은 동시 요청으로 인해 처리가 느리게 느껴질 수 있음

3. **전반적인 경험:**
   - 서블릿: "기다렸다가 빨리 처리" - 연결은 느리지만 처리는 빠름
   - 리액티브: "빠르게 응답하지만 천천히 완료" - 연결은 빠르지만 처리 시간이 길어질 수 있음

두 유형의 서버는 각각 장단점이 있으며, 실제 성능과 확장성은 구체적인 구현, 설정, 처리하는 요청의 특성에 따라 달라질 수 있습니다. 일반적으로 리액티브 시스템은 높은 부하 상황에서 더 나은 전반적인 성능과 확장성을 제공하는 경향이 있습니다.

원본

When a servlet-based web server like Apache Tomcat reaches its maximum thread pool capacity, it handles incoming requests differently compared to a reactive server like Netty. Let's examine both scenarios:

## Servlet-based Server (e.g. Apache Tomcat)

When the thread pool is full in a servlet-based server:

1. The server cannot immediately process new incoming requests.

2. The HTTP connection is not immediately rejected. Instead, the server typically queues incoming requests in a connection backlog.

3. If the backlog queue fills up, one of two things may happen:
   - The server may reject new connections, returning an HTTP 503 (Service Unavailable) error.
   - The TCP connection itself may be delayed, as the server's listen backlog queue fills up. This can result in slow connection times for clients.

4. Once threads become available in the pool, queued requests are processed in order.

This behavior can lead to increased latency and potential timeouts for clients if the server remains overloaded for an extended period.

## Reactive Server (e.g. Netty)

Reactive servers like Netty handle high concurrency differently:

1. They use a non-blocking, event-driven architecture that doesn't rely on a thread-per-request model.

2. Instead of a fixed thread pool, they typically use a small number of event loop threads to handle many concurrent connections.

3. When under high load, a reactive server can continue to accept new connections without blocking.

4. Requests are processed asynchronously, allowing the server to handle many more concurrent connections with fewer resources.

5. While individual request processing may be delayed under extreme load, the server remains responsive and can continue accepting new connections.

6. Backpressure mechanisms can be implemented to gracefully handle overload situations without rejecting connections outright.

The key difference is that reactive servers like Netty are designed to handle high concurrency more efficiently, allowing them to scale better under heavy loads compared to traditional servlet-based servers. They can typically maintain responsiveness and continue accepting connections even when processing a large number of concurrent requests.

Citations:
[1] https://stackshare.io/stackups/netty-vs-tomcat
[2] https://loadforge.com/guides/tuning-tomcat-thread-pools-for-enhanced-scalability
[3] https://badrish.net/papers/webserver-TR.pdf
[4] https://docs.oracle.com/cd/E19146-01/821-1834/geeie/index.html
[5] https://www.reddit.com/r/learnjava/comments/tqaak5/im_confused_about_how_does_a_spring_boot_web/
[6] https://www.stefankreidel.io/blog/spring-webmvc-servlet-threading
[7] https://www.alibabacloud.com/blog/java-development-practices-using-thread-pools-and-thread-variables-properly_600180
[8] https://www.baeldung.com/java-web-thread-pool-config/


Based on the information provided and the characteristics of servlet-based and reactive servers, we can compare how they behave under heavy load from a client's perspective:

## Servlet-based Servers (e.g., Apache Tomcat)

When the thread pool is full due to overload:

1. Connection establishment may be delayed. Clients might experience long wait times before the connection is established[1][2].

2. Once connected, processing is typically completed relatively quickly, as a dedicated thread handles the request.

3. Some requests may time out if they wait in the queue longer than the configured connection timeout[2].

## Reactive Servers (e.g., Netty)

Under the same heavy load:

1. Connections are usually established quickly, as reactive servers can accept new connections without blocking[3].

2. Processing time may feel longer to the client, as the server handles many concurrent requests with fewer threads.

3. The server remains responsive, but individual request processing might be delayed due to the high number of concurrent connections being managed.

## Client Perspective Comparison

From a client's point of view:

1. **Connection Time**: 
   - Servlet: Longer wait times to establish a connection
   - Reactive: Quicker connection establishment

2. **Processing Time**:
   - Servlet: Once connected, processing is typically faster
   - Reactive: May feel slower due to concurrent handling of many requests

3. **Overall Experience**:
   - Servlet: "Hurry up and wait" - slow to connect but quick to process
   - Reactive: "Quick to respond but slow to complete" - fast connection but potentially longer processing times

It's important to note that these characteristics can vary depending on the specific implementation, configuration, and nature of the requests being processed. In practice, reactive systems often provide better overall performance and scalability under high load, despite potentially longer individual request processing times.

Citations:
[1] https://stackoverflow.com/questions/46633637/requests-take-too-much-time-in-tomcat-8-on-peak-time
[2] https://www.datadoghq.com/blog/tomcat-architecture-and-performance/
[3] https://www.linkedin.com/advice/1/how-can-you-improve-response-time-web-service-vmrif
[4] https://bcho.tistory.com/720
[5] https://theses.eurasip.org/media/theses/documents/laner-markus-analyzing-packet-delay-in-reactive-networks.pdf
[6] https://loadforge.com/guides/tuning-tomcat-thread-pools-for-enhanced-scalability
[7] https://www.oreilly.com/library/view/java-programming-with/059600088X/ch04s02.html
[8] https://loadforge.com/guides/fine-tuning-tomcat-for-high-traffic-loads
```


```
➜  webflux-blocking-rocouter git:(main) ✗ k6 run ./stress_test.js

          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

     execution: local
        script: ./stress_test.js
        output: -

     scenarios: (100.00%) 1 scenario, 100 max VUs, 1m0s max duration (incl. graceful stop):
              * default: 100 looping VUs for 30s (gracefulStop: 30s)


     data_received..................: 50 kB 1.3 kB/s
     data_sent......................: 29 kB 752 B/s
     http_req_blocked...............: avg=2.78ms   min=0s    med=8µs     max=18.19ms p(90)=11.62ms p(95)=14.73ms
     http_req_connecting............: avg=251.66µs min=0s    med=0s      max=3.4ms   p(90)=909.8µs p(95)=1.1ms  
     http_req_duration..............: avg=9.36s    min=2.84s med=9.63s   max=13.09s  p(90)=10.87s  p(95)=12.02s 
       { expected_response:true }...: avg=9.36s    min=2.84s med=9.63s   max=13.09s  p(90)=10.87s  p(95)=12.02s 
     http_req_failed................: 0.00% ✓ 0        ✗ 350  
     http_req_receiving.............: avg=3.65ms   min=7µs   med=162.5µs max=31.15ms p(90)=15.36ms p(95)=17.34ms
     http_req_sending...............: avg=954.46µs min=4µs   med=32µs    max=10.93ms p(90)=4.23ms  p(95)=5.56ms 
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s      max=0s      p(90)=0s      p(95)=0s     
     http_req_waiting...............: avg=9.35s    min=2.81s med=9.63s   max=13.09s  p(90)=10.85s  p(95)=12.01s 
     http_reqs......................: 350   8.948771/s
     iteration_duration.............: avg=9.86s    min=3.38s med=10.13s  max=13.61s  p(90)=11.37s  p(95)=12.53s 
     iterations.....................: 350   8.948771/s
     vus............................: 10    min=10     max=100
     vus_max........................: 100   min=100    max=100


running (0m39.1s), 000/100 VUs, 350 complete and 0 interrupted iterations
default ✓ [======================================] 100 VUs  30s
➜  webflux-blocking-rocouter git:(main) ✗ k6 run ./stress_test.js

          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

     execution: local
        script: ./stress_test.js
        output: -

     scenarios: (100.00%) 1 scenario, 100 max VUs, 1m0s max duration (incl. graceful stop):
              * default: 100 looping VUs for 30s (gracefulStop: 30s)


     data_received..................: 34 kB 848 B/s
     data_sent......................: 32 kB 801 B/s
     http_req_blocked...............: avg=2.38ms   min=0s    med=4µs   max=10.52ms p(90)=9.5ms   p(95)=9.79ms  
     http_req_connecting............: avg=816.92µs min=0s    med=0s    max=4.08ms  p(90)=3.32ms  p(95)=3.58ms  
     http_req_duration..............: avg=8.78s    min=1.57s med=7.68s max=36.28s  p(90)=15.78s  p(95)=23.04s  
       { expected_response:true }...: avg=8.78s    min=1.57s med=7.68s max=36.28s  p(90)=15.78s  p(95)=23.04s  
     http_req_failed................: 0.00% ✓ 0        ✗ 380  
     http_req_receiving.............: avg=155.67µs min=6µs   med=31µs  max=10.42ms p(90)=228.3µs p(95)=561.19µs
     http_req_sending...............: avg=53.1µs   min=3µs   med=14µs  max=2.72ms  p(90)=85.5µs  p(95)=183.94µs
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s    max=0s      p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=8.78s    min=1.57s med=7.68s max=36.28s  p(90)=15.78s  p(95)=23.04s  
     http_reqs......................: 380   9.530058/s
     iteration_duration.............: avg=9.28s    min=2.07s med=8.18s max=36.79s  p(90)=16.28s  p(95)=23.55s  
     iterations.....................: 380   9.530058/s
     vus............................: 10    min=10     max=100
     vus_max........................: 100   min=100    max=100


running (0m39.9s), 000/100 VUs, 380 complete and 0 interrupted iterations
default ✓ [======================================] 100 VUs  30s
➜  webflux-blocking-rocouter git:(main) ✗ k6 run ./stress_test.js

          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

     execution: local
        script: ./stress_test.js
        output: -

     scenarios: (100.00%) 1 scenario, 100 max VUs, 1m0s max duration (incl. graceful stop):
              * default: 100 looping VUs for 30s (gracefulStop: 30s)


     data_received..................: 33 kB 835 B/s
     data_sent......................: 32 kB 797 B/s
     http_req_blocked...............: avg=1.15ms   min=1µs   med=7µs    max=5.52ms p(90)=4.51ms  p(95)=4.74ms  
     http_req_connecting............: avg=836.05µs min=0s    med=0s     max=3.75ms p(90)=3.29ms  p(95)=3.47ms  
     http_req_duration..............: avg=8.81s    min=1.95s med=9.57s  max=11.31s p(90)=9.63s   p(95)=10.01s  
       { expected_response:true }...: avg=8.81s    min=1.95s med=9.57s  max=11.31s p(90)=9.63s   p(95)=10.01s  
     http_req_failed................: 0.00% ✓ 0        ✗ 380  
     http_req_receiving.............: avg=167.41µs min=10µs  med=73µs   max=4.34ms p(90)=280.9µs p(95)=549.15µs
     http_req_sending...............: avg=105.03µs min=5µs   med=29µs   max=1.06ms p(90)=400.1µs p(95)=502.29µs
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s     max=0s     p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=8.81s    min=1.95s med=9.57s  max=11.31s p(90)=9.63s   p(95)=10.01s  
     http_reqs......................: 380   9.484307/s
     iteration_duration.............: avg=9.31s    min=2.45s med=10.07s max=11.82s p(90)=10.13s  p(95)=10.52s  
     iterations.....................: 380   9.484307/s
     vus............................: 10    min=10     max=100
     vus_max........................: 100   min=100    max=100


running (0m40.1s), 000/100 VUs, 380 complete and 0 interrupted iterations
default ✓ [======================================] 100 VUs  30s
➜  webflux-blocking-rocouter git:(main) ✗ 
```


coRouter 로그
```
ask: Request started at 2024-09-23T02:30:38.948566Z. Active connections: 10
2024-09-23T11:30:39.954+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-5] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:30:39.954112Z, took 1006 ms. Active connections: 9
2024-09-23T11:30:39.954+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-2] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:30:39.954220Z, took 1006 ms. Active connections: 8
2024-09-23T11:30:39.954+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-8] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:30:39.954828Z, took 1008 ms. Active connections: 7
2024-09-23T11:30:39.955+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-1] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:30:39.955297Z, took 1008 ms. Active connections: 6
2024-09-23T11:30:39.957+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-1] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:30:39.957335Z. Active connections: 7
2024-09-23T11:30:39.957+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-8] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:30:39.957402Z. Active connections: 8
2024-09-23T11:30:39.957+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-3] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:30:39.957517Z, took 1009 ms. Active connections: 7
2024-09-23T11:30:39.957+09:00  INFO 58464 --- [webflux-blocking-corouter] [tor-http-nio-10] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:30:39.957557Z, took 1009 ms. Active connections: 6
2024-09-23T11:30:39.957+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-6] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:30:39.957618Z, took 1009 ms. Active connections: 5
2024-09-23T11:30:39.957+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-4] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:30:39.957633Z, took 1009 ms. Active connections: 3
2024-09-23T11:30:39.957+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-7] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:30:39.957618Z, took 1009 ms. Active connections: 4
2024-09-23T11:30:39.958+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-2] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:30:39.958506Z. Active connections: 5
2024-09-23T11:30:39.958+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-3] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:30:39.958506Z. Active connections: 4
2024-09-23T11:30:39.958+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-4] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:30:39.958583Z. Active connections: 6
2024-09-23T11:30:39.958+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-5] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:30:39.958623Z. Active connections: 7
2024-09-23T11:30:39.958+09:00  INFO 58464 --- [webflux-blocking-corouter] [ctor-http-nio-6] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:30:39.958728Z. Active connections: 8
```

mvc 로그

```
2024-09-23T11:30:04.430+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-39] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:04.430306Z. Active connections: 98
2024-09-23T11:30:04.430+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-84] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:04.430405Z. Active connections: 99
2024-09-23T11:30:04.431+09:00  INFO 58370 --- [webmvc-blocking-controller] [io-18000-exec-1] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:04.431564Z. Active connections: 100
2024-09-23T11:30:04.930+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-24] d.j.webmvcblockingcontroller.Controller  : ask: Request ended at 2024-09-23T02:30:04.930287Z, took 9789 ms. Active connections: 99
2024-09-23T11:30:04.930+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-53] d.j.webmvcblockingcontroller.Controller  : ask: Request ended at 2024-09-23T02:30:04.930839Z, took 9789 ms. Active connections: 98
2024-09-23T11:30:04.932+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-40] d.j.webmvcblockingcontroller.Controller  : ask: Request ended at 2024-09-23T02:30:04.932691Z, took 9792 ms. Active connections: 97
2024-09-23T11:30:04.933+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-45] d.j.webmvcblockingcontroller.Controller  : ask: Request ended at 2024-09-23T02:30:04.933292Z, took 9792 ms. Active connections: 96
2024-09-23T11:30:04.935+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-35] d.j.webmvcblockingcontroller.Controller  : ask: Request ended at 2024-09-23T02:30:04.935414Z, took 9794 ms. Active connections: 95
2024-09-23T11:30:04.935+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-86] d.j.webmvcblockingcontroller.Controller  : ask: Request ended at 2024-09-23T02:30:04.935489Z, took 9794 ms. Active connections: 93
2024-09-23T11:30:04.935+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-78] d.j.webmvcblockingcontroller.Controller  : ask: Request ended at 2024-09-23T02:30:04.935598Z, took 9794 ms. Active connections: 92
2024-09-23T11:30:04.935+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-96] d.j.webmvcblockingcontroller.Controller  : ask: Request ended at 2024-09-23T02:30:04.935862Z, took 9794 ms. Active connections: 91
2024-09-23T11:30:04.935+09:00  INFO 58370 --- [webmvc-blocking-controller] [io-18000-exec-9] d.j.webmvcblockingcontroller.Controller  : ask: Request ended at 2024-09-23T02:30:04.935875Z, took 9795 ms. Active connections: 90
2024-09-23T11:30:04.935+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-42] d.j.webmvcblockingcontroller.Controller  : ask: Request ended at 2024-09-23T02:30:04.935414Z, took 9794 ms. Active connections: 94
2024-09-23T11:30:05.438+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-53] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:05.438451Z. Active connections: 92
2024-09-23T11:30:05.438+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-40] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:05.438825Z. Active connections: 93
2024-09-23T11:30:05.439+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-45] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:05.439163Z. Active connections: 94
2024-09-23T11:30:05.438+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-24] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:05.438406Z. Active connections: 91
2024-09-23T11:30:05.440+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-78] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:05.440565Z. Active connections: 95
2024-09-23T11:30:05.440+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-96] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:05.440838Z. Active connections: 96
2024-09-23T11:30:05.440+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-42] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:05.440843Z. Active connections: 97
2024-09-23T11:30:05.440+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-35] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:05.440865Z. Active connections: 98
2024-09-23T11:30:05.440+09:00  INFO 58370 --- [webmvc-blocking-controller] [io-18000-exec-9] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:05.440865Z. Active connections: 99
2024-09-23T11:30:05.440+09:00  INFO 58370 --- [webmvc-blocking-controller] [o-18000-exec-86] d.j.webmvcblockingcontroller.Controller  : ask: Request started at 2024-09-23T02:30:05.440877Z. Active connections: 100
```

r2dbc 로그 - (아마 Mono 기반의 처리 방식이라 뭔가 다르게 처리되었을 것으로 보임)
```
2024-09-23T11:31:29.276+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-8] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:31:29.276275Z. Active connections: 1
2024-09-23T11:31:29.276+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-8] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:31:29.276435Z, took 0 ms. Active connections: 0
2024-09-23T11:31:29.291+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-5] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:31:29.291103Z. Active connections: 1
2024-09-23T11:31:29.291+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-5] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:31:29.291297Z, took 0 ms. Active connections: 0
2024-09-23T11:31:29.328+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-3] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:31:29.328770Z. Active connections: 1
2024-09-23T11:31:29.328+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-3] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:31:29.328920Z, took 0 ms. Active connections: 0
2024-09-23T11:31:29.362+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-1] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:31:29.362575Z. Active connections: 1
2024-09-23T11:31:29.362+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-1] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:31:29.362731Z, took 0 ms. Active connections: 0
2024-09-23T11:31:30.093+09:00  INFO 58315 --- [webflux-non-blocking] [tor-http-nio-10] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:31:30.093142Z. Active connections: 1
2024-09-23T11:31:30.093+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-2] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:31:30.093148Z. Active connections: 2
2024-09-23T11:31:30.093+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-2] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:31:30.093293Z, took 0 ms. Active connections: 0
2024-09-23T11:31:30.093+09:00  INFO 58315 --- [webflux-non-blocking] [tor-http-nio-10] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:31:30.093293Z, took 0 ms. Active connections: 1
2024-09-23T11:31:30.124+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-4] java.lang.ModuleLayer$Controller         : ask: Request started at 2024-09-23T02:31:30.124661Z. Active connections: 1
2024-09-23T11:31:30.124+09:00  INFO 58315 --- [webflux-non-blocking] [ctor-http-nio-4] java.lang.ModuleLayer$Controller         : ask: Request ended at 2024-09-23T02:31:30.124832Z, took 0 ms. Active connections: 0
```


## 결론

- DB단 부하가 있다면 성능 차이는 X
  - 오히려 Netty가 Tomcat 대비 요청 처리 시간이 긴 것처럼 나옴. (이거는 Netty는 요청을 바로 받고, Tomcat은 Queue에서 대기하는건 요청 처리 시간으로 안치는 것 같은데, 그래서 그런듯?)
- 그러나 어플리케이션 단 성능은 Netty가 더 좋다고 함. (여기서는 검증 X. 팀원 피셜)
  - 코루틴 사용해서 중간중간에 타 API 호출이 섞였을 때, 더 성능 좋게 처리할 수도 있음.
  - 즉, 코루틴 + Webflux 자체가 좋다기보다는 비동기 처리 가능한 I/O를 비동기로 처리하는 옵션이 생김. + 어플리케이션(WAS) 성능이 Netty가 더 좋음. 요청도로 볼 수 있을 것 같다. 
