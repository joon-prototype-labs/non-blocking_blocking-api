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
     http_req_failed................: 7.23%  ✓ 3616       ✗ 46384 f
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

```
export const options = {
    scenarios: {
        stress_test: {
            executor: 'shared-iterations',
            vus: 3000,           // 3000 virtual users
            iterations: 30000,   // 3만 번 반복
            maxDuration: '5m'    // 최대 5분 제한
        }
    }
};
```

조건을 좀 바꾸고, 실행하는 db 쿼리도 0.02(20ms)로 바꾸고 2번 호출하도록 함. (blocking 여부를 확실하게 하기 위해서).

- 테스트 시간이 늘어나는걸 고려하여 반복 조건은 좀 낮춤. 
- 동시 트래픽을 늘리기 위해서 유저 수를 늘림. 

## 실행 결과

mvc

이전과 크게 다르지 않음. 요청 좀 실패하다가 계속 성공

vuser수가 늘어서 초반 실패율이 높아짐. (천천히 올리는 옵션을 쓰면 될거같긴 한데, 그럼 나머지 테스트도 다 바꿔야 해서 귀찮음.)

``` 
WARN[0000] Request Failed                                error="Get \"http://localhost:18000/ask\": read tcp 127.0.0.1:53935->127.0.0.1:18000: read: connection reset by peer"

     data_received..................: 3.8 MB 28 kB/s
     data_sent......................: 2.5 MB 18 kB/s
     http_req_blocked...............: avg=21.91ms  min=0s      med=1µs    max=448.11ms p(90)=78.34ms p(95)=169.94ms
     http_req_connecting............: avg=21.87ms  min=0s      med=0s     max=448.1ms  p(90)=77.45ms p(95)=169.86ms
     http_req_duration..............: avg=12.75s   min=710µs   med=15s    max=20.74s   p(90)=16s     p(95)=16.29s  
       { expected_response:true }...: avg=14.32s   min=45.5ms  med=15.04s max=20.74s   p(90)=16.02s  p(95)=16.39s  
     http_req_failed................: 11.09% ✓ 3327       ✗ 26673 
     http_req_receiving.............: avg=161.83µs min=0s      med=41µs   max=92.1ms   p(90)=152µs   p(95)=420µs   
     http_req_sending...............: avg=55.3µs   min=2µs     med=11µs   max=100.67ms p(90)=38µs    p(95)=122.04µs
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s     max=0s       p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=12.75s   min=678µs   med=15s    max=20.74s   p(90)=16s     p(95)=16.28s  
     http_reqs......................: 30000  219.505871/s
     iteration_duration.............: avg=12.91s   min=45.55ms med=15s    max=20.74s   p(90)=16s     p(95)=16.29s  
     iterations.....................: 30000  219.505871/s
     vus............................: 169    min=169      max=3000
     vus_max........................: 3000   min=3000     max=3000


running (2m16.7s), 0000/3000 VUs, 30000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 3000 VUs  2m16.7s/5m0s  30000/30000 shared iters
```

---

r2dbc

mvc와 동일하게 초반 말고 에러 안남.

상대적으로 mvc 대비 2배 빠름.

non-blocking이라 그런거일수도 있고, 아마 결과값(MONO 안에 있는)을 사용하지 않기 떄문일수도 있음.

어쨌든? db call 호출로 인한 병목은 없다고 보임.

```
WARN[0000] Request Failed                                error="Get \"http://localhost:18003/ask\": read tcp 127.0.0.1:55967->127.0.0.1:18003: read: connection reset by peer"
WARN[0000] Request Failed                                error="Get \"http://localhost:18003/ask\": read tcp 127.0.0.1:55960->127.0.0.1:18003: read: connection reset by peer"

     data_received..................: 2.4 MB 34 kB/s
     data_sent......................: 2.5 MB 36 kB/s
     http_req_blocked...............: avg=12.04ms  min=0s      med=1µs   max=297.19ms p(90)=69.34ms p(95)=116.4ms 
     http_req_connecting............: avg=11.92ms  min=0s      med=0s    max=284.95ms p(90)=69ms    p(95)=115.08ms
     http_req_duration..............: avg=6.39s    min=0s      med=7.7s  max=8.03s    p(90)=7.84s   p(95)=7.9s    
       { expected_response:true }...: avg=7.15s    min=361.5ms med=7.71s max=8.03s    p(90)=7.85s   p(95)=7.91s   
     http_req_failed................: 10.85% ✓ 3257      ✗ 26743 
     http_req_receiving.............: avg=102.75µs min=0s      med=31µs  max=53.89ms  p(90)=95µs    p(95)=254µs   
     http_req_sending...............: avg=76.01µs  min=0s      med=9µs   max=14.72ms  p(90)=40µs    p(95)=221.04µs
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s    max=0s       p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=6.39s    min=0s      med=7.7s  max=8.02s    p(90)=7.84s   p(95)=7.9s    
     http_reqs......................: 30000  430.67754/s
     iteration_duration.............: avg=6.57s    min=68.42ms med=7.7s  max=8.03s    p(90)=7.84s   p(95)=7.9s    
     iterations.....................: 30000  430.67754/s
     vus............................: 337    min=337     max=3000
     vus_max........................: 3000   min=3000    max=3000


running (1m09.7s), 0000/3000 VUs, 30000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 3000 VUs  1m09.7s/5m0s  30000/30000 shared iters 
```

---

webflux

이전과 동일하나 타임아웃으로 실패하는 확률이 더 늘어남.

```
WARN[0106] Request Failed                                error="Get \"http://localhost:18002/ask\": dial: i/o timeout"
WARN[0106] Request Failed                                error="Get \"http://localhost:18002/ask\": dial: i/o timeout"

     data_received..................: 1.6 MB 15 kB/s
     data_sent......................: 1.8 MB 17 kB/s
     http_req_blocked...............: avg=446.17ms min=0s      med=1µs   max=19.51s   p(90)=86.64ms p(95)=93.56ms 
     http_req_connecting............: avg=446.12ms min=0s      med=0s    max=19.51s   p(90)=86.52ms p(95)=93.39ms 
     http_req_duration..............: avg=2.1s     min=0s      med=1.7s  max=36.98s   p(90)=3.96s   p(95)=4.1s    
       { expected_response:true }...: avg=3.47s    min=47.37ms med=3s    max=36.98s   p(90)=4.09s   p(95)=4.73s   
     http_req_failed................: 39.51% ✓ 11854      ✗ 18146 
     http_req_receiving.............: avg=65.41µs  min=0s      med=21µs  max=46.65ms  p(90)=93µs    p(95)=153µs   
     http_req_sending...............: avg=140.55µs min=0s      med=8µs   max=102.73ms p(90)=74µs    p(95)=717.04µs
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s    max=0s       p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=2.1s     min=0s      med=1.7s  max=36.98s   p(90)=3.96s   p(95)=4.1s    
     http_reqs......................: 30000  281.139306/s
     iteration_duration.............: avg=10.23s   min=2.56ms  med=3.58s max=56.48s   p(90)=25.94s  p(95)=26.39s  
     iterations.....................: 30000  281.139306/s
     vus............................: 960    min=960      max=3000
     vus_max........................: 3000   min=3000     max=3000


running (1m46.7s), 0000/3000 VUs, 30000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 3000 VUs  1m46.7s/5m0s  30000/30000 shared iters
```

---

corouter

이전 webflux, 

```
WARN[0106] Request Failed                                error="Get \"http://localhost:18001/ask\": dial: i/o timeout"
WARN[0106] Request Failed                                error="Get \"http://localhost:18001/ask\": dial: i/o timeout"

     data_received..................: 1.7 MB 16 kB/s
     data_sent......................: 1.8 MB 17 kB/s
     http_req_blocked...............: avg=748.63ms min=0s      med=1µs   max=19.52s  p(90)=74.42ms p(95)=1.1s    
     http_req_connecting............: avg=748.55ms min=0s      med=0s    max=19.52s  p(90)=72.88ms p(95)=1.1s    
     http_req_duration..............: avg=1.98s    min=0s      med=1.6s  max=34.9s   p(90)=3.66s   p(95)=3.92s   
       { expected_response:true }...: avg=3.16s    min=53.67ms med=2.72s max=34.9s   p(90)=3.86s   p(95)=4.21s   
     http_req_failed................: 37.51% ✓ 11256      ✗ 18744 
     http_req_receiving.............: avg=44.39µs  min=0s      med=17µs  max=36.27ms p(90)=57µs    p(95)=84.04µs 
     http_req_sending...............: avg=117.23µs min=0s      med=6µs   max=64.21ms p(90)=38µs    p(95)=152.04µs
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s    max=0s      p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=1.98s    min=0s      med=1.6s  max=34.9s   p(90)=3.66s   p(95)=3.92s   
     http_reqs......................: 30000  281.603632/s
     iteration_duration.............: avg=9.82s    min=28.2ms  med=3.22s max=51.64s  p(90)=26.06s  p(95)=26.45s  
     iterations.....................: 30000  281.603632/s
     vus............................: 375    min=375      max=3000
     vus_max........................: 3000   min=3000     max=3000


running (1m46.5s), 0000/3000 VUs, 30000 complete and 0 interrupted iterations
stress_test ✓ [======================================] 3000 VUs  1m46.5s/5m0s  30000/30000 shared iters
```
