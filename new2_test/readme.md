# Test 1

## 설명

```
export const options = {
    scenarios: {
        stress_test: {
            executor: 'shared-iterations',
            vus: 2000,           // 2000 virtual users
            iterations: 50000,   // 5만 번 반복
            maxDuration: '5m'    // 최대 5분 제한
        }
    }
};
```

0.01초 (10ms)대기 후 응답하는 DB 쿼리를 호출하고 응답하는 API를 실행함.

## 테스트 결과

mvc 

타임아웃은 없고, 트래픽이 값자기 많아져서 처리 가능하기까지 대응하기 전에는 조금 실패함.

```
WARN[0000] Request Failed                                error="Get \"http://localhost:18000/ask\": read tcp 127.0.0.1:64359->127.0.0.1:18000: read: connection reset by peer"

     data_received..................: 6.8 MB 103 kB/s
     data_sent......................: 4.2 MB 63 kB/s
     http_req_blocked...............: avg=2.26ms  min=0s      med=1µs  max=127.15ms p(90)=3µs   p(95)=290µs
     http_req_connecting............: avg=2.25ms  min=0s      med=0s   max=123.83ms p(90)=0s    p(95)=268µs
     http_req_duration..............: avg=2.56s   min=4.25ms  med=2.7s max=5.79s    p(90)=2.99s p(95)=3.09s
       { expected_response:true }...: avg=2.66s   min=13.22ms med=2.7s max=5.79s    p(90)=2.99s p(95)=3.11s
     http_req_failed................: 3.81%  ✓ 1908       ✗ 48092 
     http_req_receiving.............: avg=44.67µs min=0s      med=18µs max=85.87ms  p(90)=51µs  p(95)=82µs 
     http_req_sending...............: avg=60µs    min=1µs     med=4µs  max=6.88ms   p(90)=12µs  p(95)=21µs 
     http_req_tls_handshaking.......: avg=0s      min=0s      med=0s   max=0s       p(90)=0s    p(95)=0s   
     http_req_waiting...............: avg=2.56s   min=4.22ms  med=2.7s max=5.79s    p(90)=2.99s p(95)=3.09s
     http_reqs......................: 50000  754.788074/s
     iteration_duration.............: avg=2.59s   min=13.26ms med=2.7s max=5.79s    p(90)=2.99s p(95)=3.09s
     iterations.....................: 50000  754.788074/s
     vus............................: 251    min=251      max=2000
     vus_max........................: 2000   min=2000     max=2000


running (1m06.2s), 0000/2000 VUs, 50000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 2000 VUs  1m06.2s/5m0s  50000/50000 shared iters

```

---

non-blocking

마찬가지로 초반에 많이 실패

```
WARN[0000] Request Failed                                error="Get \"http://localhost:18003/ask\": read tcp 127.0.0.1:56005->127.0.0.1:18003: read: connection reset by peer"

     data_received..................: 4.2 MB 61 kB/s
     data_sent......................: 4.2 MB 61 kB/s
     http_req_blocked...............: avg=2.94ms   min=0s       med=1µs   max=303.25ms p(90)=4µs   p(95)=332µs
     http_req_connecting............: avg=2.91ms   min=0s       med=0s    max=303.23ms p(90)=0s    p(95)=298µs
     http_req_duration..............: avg=2.66s    min=0s       med=2.83s max=3.14s    p(90)=2.96s p(95)=3.03s
       { expected_response:true }...: avg=2.77s    min=425.74ms med=2.83s max=3.14s    p(90)=2.96s p(95)=3.03s
     http_req_failed................: 3.84%  ✓ 1922       ✗ 48078 
     http_req_receiving.............: avg=54.02µs  min=0s       med=18µs  max=138.3ms  p(90)=46µs  p(95)=81µs 
     http_req_sending...............: avg=130.96µs min=0s       med=5µs   max=59.22ms  p(90)=15µs  p(95)=36µs 
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s    max=0s       p(90)=0s    p(95)=0s   
     http_req_waiting...............: avg=2.66s    min=0s       med=2.83s max=3.13s    p(90)=2.96s p(95)=3.03s
     http_reqs......................: 50000  722.815934/s
     iteration_duration.............: avg=2.7s     min=36.11ms  med=2.83s max=3.14s    p(90)=2.96s p(95)=3.03s
     iterations.....................: 50000  722.815934/s
     vus............................: 177    min=177      max=2000
     vus_max........................: 2000   min=2000     max=2000


running (1m09.2s), 0000/2000 VUs, 50000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 2000 VUs  1m09.2s/5m0s  50000/50000 shared iters

```

---

webflux

초반에 많이 실패하는건 동일한데, 중간에도 자주 타임아웃 발생함.

```
WARN[0079] Request Failed                                error="Get \"http://localhost:18002/ask\": dial: i/o timeout"

     data_received..................: 4.0 MB 51 kB/s
     data_sent......................: 4.0 MB 51 kB/s
     http_req_blocked...............: avg=404.73ms min=0s      med=1µs   max=19.51s  p(90)=5µs   p(95)=67.23ms
     http_req_connecting............: avg=404.7ms  min=0s      med=0s    max=19.51s  p(90)=0s    p(95)=67.12ms
     http_req_duration..............: avg=1.27s    min=0s      med=1.26s max=16.47s  p(90)=1.79s p(95)=1.85s  
       { expected_response:true }...: avg=1.39s    min=18.42ms med=1.32s max=16.47s  p(90)=1.8s  p(95)=1.86s  
     http_req_failed................: 8.21%  ✓ 4109       ✗ 45891 
     http_req_receiving.............: avg=33.46µs  min=0s      med=20µs  max=22.94ms p(90)=53µs  p(95)=77µs   
     http_req_sending...............: avg=72.37µs  min=0s      med=6µs   max=14.16ms p(90)=18µs  p(95)=47µs   
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s    max=0s      p(90)=0s    p(95)=0s     
     http_req_waiting...............: avg=1.27s    min=0s      med=1.26s max=16.47s  p(90)=1.79s p(95)=1.85s  
     http_reqs......................: 50000  631.850052/s
     iteration_duration.............: avg=2.78s    min=5.42ms  med=1.34s max=35.63s  p(90)=1.86s p(95)=19.73s 
     iterations.....................: 50000  631.850052/s
     vus............................: 58     min=58       max=2000
     vus_max........................: 2000   min=2000     max=2000


running (1m19.1s), 0000/2000 VUs, 50000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 2000 VUs  1m19.1s/5m0s  50000/50000 shared iters
```

---

corouter

이것도 타임아웃 많이 발생함

```
WARN[0066] Request Failed                                error="Get \"http://localhost:18001/ask\": dial: i/o timeout"
WARN[0066] Request Failed                                error="Get \"http://localhost:18001/ask\": dial: i/o timeout"
WARN[0066] Request Failed                                error="Get \"http://localhost:18001/ask\": dial tcp 127.0.0.1:18001: connect: connection reset by peer"
WARN[0066] Request Failed                                error="Get \"http://localhost:18001/ask\": read tcp 127.0.0.1:61460->127.0.0.1:18001: read: connection reset by peer"
WARN[0066] Request Failed                                error="Get \"http://localhost:18001/ask\": dial tcp 127.0.0.1:18001: connect: connection reset by peer"
WARN[0068] Request Failed                                error="Get \"http://localhost:18001/ask\": dial tcp 127.0.0.1:18001: connect: connection reset by peer"

     data_received..................: 4.1 MB 57 kB/s
     data_sent......................: 3.9 MB 54 kB/s
     http_req_blocked...............: avg=365.87ms min=0s      med=1µs   max=19.59s   p(90)=2µs   p(95)=14.18ms
     http_req_connecting............: avg=365.58ms min=0s      med=0s    max=19.52s   p(90)=0s    p(95)=13.89ms
     http_req_duration..............: avg=1.24s    min=0s      med=1.23s max=15.77s   p(90)=1.67s p(95)=1.72s  
       { expected_response:true }...: avg=1.34s    min=11.42ms med=1.28s max=15.77s   p(90)=1.68s p(95)=1.73s  
     http_req_failed................: 7.23%  ✓ 3616       ✗ 46384 
     http_req_receiving.............: avg=61.3µs   min=0s      med=15µs  max=105.76ms p(90)=42µs  p(95)=73µs   
     http_req_sending...............: avg=58.09µs  min=0s      med=4µs   max=43.34ms  p(90)=13µs  p(95)=28µs   
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s    max=0s       p(90)=0s    p(95)=0s     
     http_req_waiting...............: avg=1.24s    min=0s      med=1.23s max=15.77s   p(90)=1.67s p(95)=1.72s  
     http_reqs......................: 50000  689.206352/s
     iteration_duration.............: avg=2.73s    min=16.67ms med=1.35s max=33.48s   p(90)=2.62s p(95)=13.95s 
     iterations.....................: 50000  689.206352/s
     vus............................: 33     min=33       max=2000
     vus_max........................: 2000   min=2000     max=2000


running (1m12.5s), 0000/2000 VUs, 50000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 2000 VUs  1m12.5s/5m0s  50000/50000 shared iters
```

## 결과 분석

mvc+jpa, webflux-r2dbc 조합에선 전혀 발생하지 않던 timeout이 corouter, webflux에서는 발생하는 문제가 있음.

실패율, 실행시간도 좀 더 높다.

예상대로 blocking이 발생해서 그런 건지는 명확하지 않음.

# Test 2

## 설명

Test 1에서 netty + jpa의 조합인 2 case가 실패율과 실행시간이 높게 나온게 blocking 떄문인지 확인하기 위해서

mvc가 타임아웃이 발생하는 정도까지 쿼리 대기 시간을 늘려서 테스트해보면 더 정확하게 알 수 있을 듯.

## 실행 결과
