# mvc-api(webmvc + controller + jpa)

## Test 1

```
     data_received..................: 9.9 kB 264 B/s
     data_sent......................: 36 kB  959 B/s
     http_req_blocked...............: avg=1.32ms   min=0s    med=429µs max=8.37ms p(90)=4.32ms p(95)=4.87ms  
     http_req_connecting............: avg=943.97µs min=0s    med=330µs max=7.96ms p(90)=2.87ms p(95)=3.04ms  
     http_req_duration..............: avg=7.37s    min=1.08s med=8s    max=8.02s  p(90)=8s     p(95)=8s      
       { expected_response:true }...: avg=4.13s    min=1.08s med=4.13s max=7.17s  p(90)=7.17s  p(95)=7.17s   
     http_req_failed................: 83.72% ✓ 360       ✗ 70   
     http_req_receiving.............: avg=30.03µs  min=0s    med=0s    max=995µs  p(90)=89.2µs p(95)=222.74µs
     http_req_sending...............: avg=38.05µs  min=3µs   med=19µs  max=1.16ms p(90)=62.1µs p(95)=88.54µs 
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s    max=0s     p(90)=0s     p(95)=0s      
     http_req_waiting...............: avg=7.37s    min=1.08s med=8s    max=8.02s  p(90)=8s     p(95)=8s      
     http_reqs......................: 430    11.414578/s
     iteration_duration.............: avg=7.87s    min=1.58s med=8.5s  max=8.53s  p(90)=8.51s  p(95)=8.51s   
     iterations.....................: 430    11.414578/s
     vus............................: 10     min=10      max=100
     vus_max........................: 100    min=100     max=100

```
