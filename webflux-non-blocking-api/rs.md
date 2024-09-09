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
