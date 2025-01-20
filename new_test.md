# 설명

- 지금도 이해가 안가는 부분이기도 하고, 테스트를 다시 해보기로 함.
- Profiler를 사용해서 분석해볼 계획
- 기존 JPA를 사용하던 코드를 보니까 CPU Event를 보면 활동율이 다 적음. 즉 JPA 병목때문에 확인이 어려운 상황.
- JPA 무시하고 외부 IO 없이(하면 당연히 비동기가 높게 나오니까) webflux, coRouter, mvc 테스트해볼 예정 

# 대충 결과 

CPU 사용율은 mvc가 좀 더 높게 나옴.

근데 사실 이거랑 별 차이가 있나 싶기도 하고.

https://www.brendangregg.com/Slides/RxNetty_vs_Tomcat_April2015/#15

아무튼?

병목이 없다는 가정 하에, Netty가 더 빠른 건 맞음.

Call Stack을 보니 Mono 위에 CoRouterFunctionDsl이 올라가서, 효율만 따지면 Webflux + Mono가 나은데?   
코루틴을 쓰면 선형적인 코드를 볼 수 있으므로 편하다는 장점이 있음.

여기까지는 거의 맞는듯?

근데 문제는?  이런 상황에서? Blocking IO인 JDBC를 호출한다면? 그래도  Webflux가 효율적일것인가?

근데 문제는?  이런 상황에서?
Blocking I/O 인 JDBC의 호출을 별도 스레드 풀(HikariCP 사용)에서 처리하면 Netty의 이벤트 루프가 영향을 받지 않는가? (스레드를 계속 점유하지 않고 다음 이벤트 or 코루틴로 넘어가는가? + 성능에 문제가 없는가?)

이건 아직도 잘 모르겠음...

이걸 해결하려면 HikariCP + Netty 조합에 문제가 없는지를 찾아보면 될 듯?

아마 없는거 같은데

일단? 내가 계속 고민하던게 커넥션 풀을 어떻게 사용하는지에 대한 지식이 없어서라는 걸 깨달음.

이걸 공부해봐야 할 듯?


----

mvc

```
     ✗ status is 200
      ↳  98% — ✓ 197956 / ✗ 2044
     ✗ has valid data
      ↳  98% — ✓ 197956 / ✗ 2044

     checks.........................: 98.97% ✓ 395912       ✗ 4088  
     data_received..................: 41 MB  5.3 MB/s
     data_sent......................: 18 MB  2.4 MB/s
     http_req_blocked...............: avg=3.03ms   min=0s       med=1µs     max=6.7s     p(90)=1µs      p(95)=2µs     
     http_req_connecting............: avg=2.96ms   min=0s       med=0s      max=6.7s     p(90)=0s       p(95)=0s      
✓ http_req_duration..............: avg=56.29ms  min=0s       med=46.18ms max=745.15ms p(90)=99.69ms  p(95)=123.38ms
{ expected_response:true }...: avg=56.64ms  min=161µs    med=46.47ms max=745.15ms p(90)=100.03ms p(95)=123.74ms
http_req_failed................: 1.02%  ✓ 2044         ✗ 197956
http_req_receiving.............: avg=80.21µs  min=0s       med=7µs     max=36.69ms  p(90)=22µs     p(95)=69µs    
http_req_sending...............: avg=125.18µs min=0s       med=2µs     max=62.72ms  p(90)=9µs      p(95)=34µs    
http_req_tls_handshaking.......: avg=0s       min=0s       med=0s      max=0s       p(90)=0s       p(95)=0s      
http_req_waiting...............: avg=56.09ms  min=0s       med=46.08ms max=743.23ms p(90)=99.22ms  p(95)=123.21ms
✓ http_reqs......................: 200000 26036.730537/s
iteration_duration.............: avg=75.75ms  min=866.45µs med=48.12ms max=6.89s    p(90)=104.05ms p(95)=133.02ms
iterations.....................: 200000 26036.730537/s
vus............................: 2000   min=2000       max=2000
vus_max........................: 2000   min=2000       max=2000
```

running (0m09.0s), 0000/2000 VUs, 200000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 2000 VUs  0m09.0s/5m0s  200000/200000 shared iters

---

coRouter

```
     ✗ status is 200
      ↳  98% — ✓ 197972 / ✗ 2028
     ✗ has valid data
      ↳  98% — ✓ 197972 / ✗ 2028

     checks.........................: 98.98% ✓ 395944       ✗ 4056  
     data_received..................: 41 MB  4.8 MB/s
     data_sent......................: 18 MB  2.2 MB/s
     http_req_blocked...............: avg=5.59ms   min=0s     med=1µs     max=6.7s     p(90)=1µs      p(95)=2µs     
     http_req_connecting............: avg=5.56ms   min=0s     med=0s      max=6.7s     p(90)=0s       p(95)=0s      
   ✓ http_req_duration..............: avg=62.99ms  min=0s     med=53.75ms max=655.38ms p(90)=105.62ms p(95)=132.68ms
       { expected_response:true }...: avg=63.18ms  min=1.1ms  med=53.95ms max=655.38ms p(90)=106.05ms p(95)=133.09ms
     http_req_failed................: 1.01%  ✓ 2028         ✗ 197972
     http_req_receiving.............: avg=261.24µs min=0s     med=7µs     max=75.39ms  p(90)=25µs     p(95)=77µs    
     http_req_sending...............: avg=36.16µs  min=0s     med=2µs     max=73.89ms  p(90)=8µs      p(95)=31µs    
     http_req_tls_handshaking.......: avg=0s       min=0s     med=0s      max=0s       p(90)=0s       p(95)=0s      
     http_req_waiting...............: avg=62.7ms   min=0s     med=53.66ms max=654.94ms p(90)=104.55ms p(95)=130.21ms
   ✓ http_reqs......................: 200000 23550.748743/s
     iteration_duration.............: avg=84.34ms  min=1.15ms med=54.84ms max=6.98s    p(90)=109.99ms p(95)=143.14ms
     iterations.....................: 200000 23550.748743/s
     vus............................: 2000   min=2000       max=2000
     vus_max........................: 2000   min=2000       max=2000


running (0m08.5s), 0000/2000 VUs, 200000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 2000 VUs  0m08.5s/5m0s  200000/200000 shared iters
```
---

webflux

```
     ✗ status is 200
      ↳  98% — ✓ 197946 / ✗ 2054
     ✗ has valid data
      ↳  98% — ✓ 197946 / ✗ 2054

     checks.........................: 98.97% ✓ 395892       ✗ 4108  
     data_received..................: 41 MB  5.0 MB/s
     data_sent......................: 18 MB  2.2 MB/s
     http_req_blocked...............: avg=6.99ms  min=0s       med=1µs     max=6.71s    p(90)=1µs     p(95)=2µs     
     http_req_connecting............: avg=6.98ms  min=0s       med=0s      max=6.71s    p(90)=0s      p(95)=0s      
✓ http_req_duration..............: avg=58.9ms  min=0s       med=50.85ms max=699.42ms p(90)=94.98ms p(95)=117.46ms
{ expected_response:true }...: avg=59.2ms  min=916µs    med=50.96ms max=699.42ms p(90)=95.32ms p(95)=117.78ms
http_req_failed................: 1.02%  ✓ 2054         ✗ 197946
http_req_receiving.............: avg=36.04µs min=0s       med=7µs     max=44.06ms  p(90)=21µs    p(95)=57µs    
http_req_sending...............: avg=40.34µs min=0s       med=2µs     max=40.32ms  p(90)=8µs     p(95)=21µs    
http_req_tls_handshaking.......: avg=0s      min=0s       med=0s      max=0s       p(90)=0s      p(95)=0s      
http_req_waiting...............: avg=58.83ms min=0s       med=50.8ms  max=699.11ms p(90)=94.86ms p(95)=117.41ms
✓ http_reqs......................: 200000 24554.842196/s
iteration_duration.............: avg=80.68ms min=944.37µs med=51.83ms max=6.96s    p(90)=99.13ms p(95)=124.78ms
iterations.....................: 200000 24554.842196/s
vus............................: 2000   min=2000       max=2000
vus_max........................: 2000   min=2000       max=2000
```

running (0m08.1s), 0000/2000 VUs, 200000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 2000 VUs  0m08.1s/5m0s  200000/200000 shared iters


---
