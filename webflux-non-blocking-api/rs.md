# webflux-non-blocking-api(webflux + reactive + r2dbc)

## Test 1

```
     data_received..................: 8.6 kB 224 B/s
     data_sent......................: 36 kB  940 B/s
     http_req_blocked...............: avg=1.21ms   min=2µs   med=427µs max=13.45ms p(90)=4.09ms  p(95)=4.44ms  
     http_req_connecting............: avg=885.35µs min=0s    med=356µs max=4.07ms  p(90)=2.84ms  p(95)=2.91ms  
     http_req_duration..............: avg=7.43s    min=1.47s med=8s    max=8.01s   p(90)=8s      p(95)=8s      
       { expected_response:true }...: avg=5.5s     min=1.47s med=5.8s  max=7.9s    p(90)=7.72s   p(95)=7.8s    
     http_req_failed................: 77.20% ✓ 332      ✗ 98   
     http_req_receiving.............: avg=51.99µs  min=0s    med=0s    max=2.86ms  p(90)=114.3µs p(95)=168.19µs
     http_req_sending...............: avg=83.3µs   min=5µs   med=32µs  max=4.67ms  p(90)=124.1µs p(95)=211.49µs
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s    max=0s      p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=7.43s    min=1.47s med=7.99s max=8s      p(90)=8s      p(95)=8s      
     http_reqs......................: 430    11.18913/s
     iteration_duration.............: avg=7.93s    min=1.98s med=8.5s  max=8.66s   p(90)=8.51s   p(95)=8.51s   
     iterations.....................: 430    11.18913/s
     vus............................: 6      min=6      max=100
     vus_max........................: 100    min=100    max=100

```

위 결과가 이상해서 로그 확인해보니까 netty에서 Memory Leak 에러 떠서 thread-pool 조정 후 다시 테스트.

근데 이러면 적절한 조건이 아닌 것 같아 다시 테스트 수행할 예정

```properties
spring.r2dbc.pool.max-size= 20
spring.r2dbc.pool.initial-size= 10
```

```
     data_received..................: 58 kB 1.7 kB/s
     data_sent......................: 55 kB 1.6 kB/s
     http_req_blocked...............: avg=681.07µs min=0s    med=4µs    max=6.32ms p(90)=4.12ms p(95)=4.83ms  
     http_req_connecting............: avg=507.99µs min=0s    med=0s     max=4.32ms p(90)=3.17ms p(95)=3.7ms   
     http_req_duration..............: avg=4.42s    min=1.59s med=4.56s  max=5.89s  p(90)=4.59s  p(95)=4.62s   
       { expected_response:true }...: avg=4.42s    min=1.59s med=4.56s  max=5.89s  p(90)=4.59s  p(95)=4.62s   
     http_req_failed................: 0.00% ✓ 0         ✗ 660  
     http_req_receiving.............: avg=62.38µs  min=6µs   med=36µs   max=1.13ms p(90)=108µs  p(95)=165.39µs
     http_req_sending...............: avg=52.79µs  min=2µs   med=13.5µs max=5.5ms  p(90)=52.1µs p(95)=115.29µs
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s     max=0s     p(90)=0s     p(95)=0s      
     http_req_waiting...............: avg=4.42s    min=1.59s med=4.56s  max=5.89s  p(90)=4.59s  p(95)=4.62s   
     http_reqs......................: 660   19.039338/s
     iteration_duration.............: avg=4.93s    min=2.1s  med=5.06s  max=6.4s   p(90)=5.09s  p(95)=5.13s   
     iterations.....................: 660   19.039338/s
     vus............................: 20    min=20      max=100
     vus_max........................: 100   min=100     max=100

```
