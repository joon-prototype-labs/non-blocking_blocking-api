
```js
import http from 'k6/http';
import {sleep} from 'k6';

export const options = {
    scenarios: {
        stress_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                {duration: '1m', target: 10000} // N분동안 startVUs or 이전 vuser 수에서 target 수까지 증가
            ],
        }
    }
};

let port = 18000 // webmvc + controller + jpa
//let port = 18001 // webflux + coRouter + jpa
//let port = 18002 // webflux + reactive + jpa
//let port = 18003 // webflux + reactive + r2dbc

export default function () {
    http.get('http://localhost:' + port + '/ask', params);
}
```

기본 타임아웃 시간인 30초 적용 됨.

---

mvc + jpa

후반부 (1분 지나고 나서 vuser 수 줄어들기 시작 할 때)에서 실패하기 시작함.

```
 WARN[0086] Request Failed                                error="Get \"http://localhost:18000/ask\": dial: i/o timeout"
WARN[0086] Request Failed                                error="Get \"http://localhost:18000/ask\": dial: i/o timeout"

     data_received..................: 2.5 MB 28 kB/s
     data_sent......................: 1.7 MB 19 kB/s
     http_req_blocked...............: avg=221.94µs min=0s      med=2µs    max=40.85ms p(90)=420µs  p(95)=604µs   
     http_req_connecting............: avg=179.4µs  min=0s      med=0s     max=40.03ms p(90)=348µs  p(95)=460µs   
     http_req_duration..............: avg=18.98s   min=0s      med=18.81s max=42.88s  p(90)=37.06s p(95)=39.31s  
       { expected_response:true }...: avg=20.82s   min=48.78ms med=20.7s  max=42.88s  p(90)=37.5s  p(95)=39.51s  
     http_req_failed................: 8.84%  ✓ 1707       ✗ 17582  
     http_req_receiving.............: avg=264.5µs  min=0s      med=55µs   max=50.49ms p(90)=436µs  p(95)=987.59µs
     http_req_sending...............: avg=43.26µs  min=0s      med=18µs   max=33.73ms p(90)=53µs   p(95)=89µs    
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s     max=0s      p(90)=0s     p(95)=0s      
     http_req_waiting...............: avg=18.98s   min=0s      med=18.81s max=42.88s  p(90)=37.06s p(95)=39.31s  
     http_reqs......................: 19289  213.694682/s
     iteration_duration.............: avg=21.27s   min=48.85ms med=22.74s max=42.88s  p(90)=37.06s p(95)=39.31s  
     iterations.....................: 19289  213.694682/s
     vus............................: 2527   min=83       max=9916 
     vus_max........................: 10000  min=10000    max=10000


running (1m30.3s), 00000/10000 VUs, 19289 complete and 2386 interrupted iterations
stress_test ✓ [======================================] 02419/10000 VUs  1m0s
```

webflux(corouter) + jpa

4000~5000 대부터 타임아웃 발생

```
WARN[0084] Request Failed                                error="Get \"http://localhost:18001/ask\": dial: i/o timeout"

     data_received..................: 1.3 MB 16 kB/s
     data_sent......................: 1.3 MB 15 kB/s
     http_req_blocked...............: avg=1.95s    min=0s      med=1µs      max=19.6s    p(90)=13.11s p(95)=19.51s  
     http_req_connecting............: avg=1.95s    min=0s      med=0s       max=19.6s    p(90)=13.11s p(95)=19.51s  
     http_req_duration..............: avg=1.36s    min=0s      med=739.73ms max=29.23s   p(90)=2.99s  p(95)=3.52s   
       { expected_response:true }...: avg=2.49s    min=47.4ms  med=2.1s     max=29.23s   p(90)=3.46s  p(95)=3.79s   
     http_req_failed................: 45.19% ✓ 12412      ✗ 15053  
     http_req_receiving.............: avg=213.95µs min=0s      med=16µs     max=123.37ms p(90)=291µs  p(95)=766.79µs
     http_req_sending...............: avg=139.63µs min=0s      med=4µs      max=67.07ms  p(90)=80µs   p(95)=309µs   
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s       max=0s       p(90)=0s     p(95)=0s      
     http_req_waiting...............: avg=1.36s    min=0s      med=739.44ms max=29.23s   p(90)=2.99s  p(95)=3.51s   
     http_reqs......................: 27465  329.256175/s
     iteration_duration.............: avg=14.38s   min=47.43ms med=19.54s   max=48.74s   p(90)=25.94s p(95)=26.05s  
     iterations.....................: 27465  329.256175/s
     vus............................: 1      min=1        max=9868 
     vus_max........................: 10000  min=10000    max=10000


running (1m23.4s), 00000/10000 VUs, 27465 complete and 0 interrupted iterations
stress_test ✓ [======================================] 00000/10000 VUs  1m0s

```

4000~5000 대부터 타임아웃 발생

webflux(mono) + jpa

```
WARN[0086] Request Failed                                error="Get \"http://localhost:18002/ask\": dial: i/o timeout"
WARN[0086] Request Failed                                error="Get \"http://localhost:18002/ask\": dial: i/o timeout"

     data_received..................: 1.2 MB 14 kB/s
     data_sent......................: 1.2 MB 14 kB/s
     http_req_blocked...............: avg=1.86s    min=0s      med=1µs      max=19.68s   p(90)=13.1s  p(95)=19.51s  
     http_req_connecting............: avg=1.86s    min=0s      med=0s       max=19.57s   p(90)=13.1s  p(95)=19.51s  
     http_req_duration..............: avg=1.36s    min=0s      med=720.86ms max=29.27s   p(90)=3.04s  p(95)=3.58s   
       { expected_response:true }...: avg=2.59s    min=57.45ms med=2.08s    max=29.27s   p(90)=3.57s  p(95)=4.15s   
     http_req_failed................: 47.36% ✓ 12616      ✗ 14020  
     http_req_receiving.............: avg=172.06µs min=0s      med=14µs     max=118.25ms p(90)=187µs  p(95)=584.24µs
     http_req_sending...............: avg=165.58µs min=0s      med=4µs      max=182.67ms p(90)=61µs   p(95)=154µs   
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s       max=0s       p(90)=0s     p(95)=0s      
     http_req_waiting...............: avg=1.36s    min=0s      med=720.67ms max=29.27s   p(90)=3.04s  p(95)=3.58s   
     http_reqs......................: 26636  310.172419/s
     iteration_duration.............: avg=15.22s   min=2.43ms  med=20.32s   max=48.71s   p(90)=25.93s p(95)=26.03s  
     iterations.....................: 26636  310.172419/s
     vus............................: 26     min=26       max=9930 
     vus_max........................: 10000  min=10000    max=10000


running (1m25.9s), 00000/10000 VUs, 26636 complete and 0 interrupted iterations
stress_test ✓ [======================================] 00000/10000 VUs  1m0s
```

webflux(mono) + r2db2

타임아웃 에러 하나도 없는 깔끔한 성공.

요청 처리량도 2배 차이.

```
     data_received..................: 2.9 MB 33 kB/s
     data_sent......................: 2.8 MB 32 kB/s
     http_req_blocked...............: avg=187.59µs min=0s       med=2µs    max=32.81ms  p(90)=380µs  p(95)=602.89µs
     http_req_connecting............: avg=147.39µs min=0s       med=0s     max=29.13ms  p(90)=317µs  p(95)=465µs   
     http_req_duration..............: avg=13.29s   min=239.19ms med=13.22s max=26.96s   p(90)=24.29s p(95)=25.57s  
       { expected_response:true }...: avg=13.29s   min=239.19ms med=13.22s max=26.96s   p(90)=24.29s p(95)=25.57s  
     http_req_failed................: 0.00%  ✓ 0          ✗ 32783  
     http_req_receiving.............: avg=228.08µs min=5µs      med=43µs   max=114.86ms p(90)=337µs  p(95)=793.89µs
     http_req_sending...............: avg=50.31µs  min=1µs      med=11µs   max=26.35ms  p(90)=43µs   p(95)=96µs    
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s     max=0s       p(90)=0s     p(95)=0s      
     http_req_waiting...............: avg=13.29s   min=239.06ms med=13.22s max=26.96s   p(90)=24.29s p(95)=25.57s  
     http_reqs......................: 32783  376.740233/s
     iteration_duration.............: avg=13.29s   min=239.56ms med=13.22s max=26.96s   p(90)=24.29s p(95)=25.57s  
     iterations.....................: 32783  376.740233/s
     vus............................: 264    min=43       max=9890 
     vus_max........................: 10000  min=10000    max=10000


running (1m27.0s), 00000/10000 VUs, 32783 complete and 0 interrupted iterations
stress_test ✓ [======================================] 00000/10000 VUs  1m0s
```

## 결론

MVC + JPA: 초당 214개 요청
WebFlux + JPA: 초당 310-329개 요청
WebFlux (Mono) + R2DBC: 초당 376 요청 처리

WebFlux로 전환하면서 초당 처리할 수 있는 요청 수가 약 50% 증가함. 그러나 이는 요청 성공율로 미루어 보았을 때, 효율적이라고 볼 수 없다.    
(실패를 많이 해서 성공한 것일수도 있고...)

이벤트 루프라서 몇 안되는 스레드(10개)를 동기 블로킹인 jpa가 호출하면서 blocking하기 때문이다. 따라서 요청을 처리할 수 있는 스레드가 부족해지고, 이는 서버의 스레드 풀이 포화상태가 되어 새로운 연결을 받아들이지 못하는 상황이 된다.

TCP 연결 단계 (http_req_blocked & http_req_connecting)를 보면 WebFlux + JPA 조합에 대기 시간이 매우 높은 걸 알 수 있다.

그러나 값을 명확하게 확인하기에는 timeout이 너무 많이 발생해서 통계를 비교하기에 적절하지 않다.

vuser 수를 줄이고, 테스트를 통해서 거의 대부분의 요청이 성공하는 상황에서 어떻게 되는지 비교해봐야 할 듯.
