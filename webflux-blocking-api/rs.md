# webflux-blocking-api(webflux + reactive + jpa)

## Test 1

```
     data_received..................: 21 kB  557 B/s
     data_sent......................: 41 kB  1.1 kB/s
     http_req_blocked...............: avg=128.3ms  min=0s    med=226µs max=3.51s  p(90)=4.09ms  p(95)=1.9s    
     http_req_connecting............: avg=128.15ms min=0s    med=185µs max=3.51s  p(90)=3.16ms  p(95)=1.9s    
     http_req_duration..............: avg=6.2s     min=0s    med=6.7s  max=8s     p(90)=7.99s   p(95)=8s      
       { expected_response:true }...: avg=4.81s    min=1.39s med=4.64s max=7.72s  p(90)=6.7s    p(95)=6.72s   
     http_req_failed................: 51.21% ✓ 252       ✗ 240  
     http_req_receiving.............: avg=210.15µs min=0s    med=0s    max=6.48ms p(90)=170.2µs p(95)=811.84µs
     http_req_sending...............: avg=46.8µs   min=0s    med=15µs  max=2.75ms p(90)=65.3µs  p(95)=142.04µs
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s    max=0s     p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=6.2s     min=0s    med=6.7s  max=8s     p(90)=7.99s   p(95)=8s      
     http_reqs......................: 492    12.963214/s
     iteration_duration.............: avg=6.86s    min=1.9s  med=8.22s max=8.53s  p(90)=8.5s    p(95)=8.52s   
     iterations.....................: 492    12.963214/s
     vus............................: 3      min=3       max=100
     vus_max........................: 100    min=100     max=100

```
