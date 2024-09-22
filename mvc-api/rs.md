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

## Test 2

```
     data_received..................: 6.4 kB 166 B/s
     data_sent......................: 36 kB  928 B/s
     http_req_blocked...............: avg=1.37ms   min=1µs   med=346µs max=5.72ms p(90)=5.05ms p(95)=5.21ms  
     http_req_connecting............: avg=831.13µs min=0s    med=272µs max=3.4ms  p(90)=2.77ms p(95)=2.81ms  
     http_req_duration..............: avg=7.61s    min=2.11s med=7.99s max=8.01s  p(90)=8s     p(95)=8s      
       { expected_response:true }...: avg=4.35s    min=2.11s med=3.96s max=6.7s   p(90)=6.7s   p(95)=6.7s    
     http_req_failed................: 89.41% ✓ 380       ✗ 45   
     http_req_receiving.............: avg=137.86µs min=0s    med=0s    max=8.24ms p(90)=12.8µs p(95)=342.19µs
     http_req_sending...............: avg=51.81µs  min=4µs   med=16µs  max=1.73ms p(90)=64µs   p(95)=138.39µs
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s    max=0s     p(90)=0s     p(95)=0s      
     http_req_waiting...............: avg=7.61s    min=2.1s  med=7.99s max=8.01s  p(90)=8s     p(95)=8s      
     http_reqs......................: 425    11.043497/s
     iteration_duration.............: avg=8.13s    min=2.62s med=8.5s  max=8.68s  p(90)=8.54s  p(95)=8.62s   
     iterations.....................: 425    11.043497/s
     vus............................: 15     min=15      max=100
     vus_max........................: 100    min=100     max=100

```

## Test 3

```
     data_received..................: 8.5 kB 223 B/s
     data_sent......................: 36 kB  945 B/s
     http_req_blocked...............: avg=1.05ms   min=0s    med=488.5µs max=5.32ms p(90)=3.29ms  p(95)=3.49ms  
     http_req_connecting............: avg=952.48µs min=0s    med=429.5µs max=3.76ms p(90)=3.12ms  p(95)=3.3ms   
     http_req_duration..............: avg=7.47s    min=1.44s med=8s      max=8s     p(90)=8s      p(95)=8s      
       { expected_response:true }...: avg=4.23s    min=1.44s med=4.23s   max=7.02s  p(90)=7.02s   p(95)=7.02s   
     http_req_failed................: 86.04% ✓ 370       ✗ 60   
     http_req_receiving.............: avg=84.79µs  min=0s    med=0s      max=1.9ms  p(90)=119.2µs p(95)=531.59µs
     http_req_sending...............: avg=30.14µs  min=4µs   med=19µs    max=843µs  p(90)=56.2µs  p(95)=77µs    
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s      max=0s     p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=7.47s    min=1.44s med=8s      max=8s     p(90)=8s      p(95)=8s      
     http_reqs......................: 430    11.253698/s
     iteration_duration.............: avg=7.99s    min=1.95s med=8.5s    max=9.39s  p(90)=8.51s   p(95)=8.51s   
     iterations.....................: 430    11.253698/s
     vus............................: 10     min=10      max=100
     vus_max........................: 100    min=100     max=100
     
```

### Test 4

```
     data_received..................: 92 kB 2.6 kB/s
     data_sent......................: 48 kB 1.3 kB/s
     http_req_blocked...............: avg=253.61µs min=0s       med=4µs    max=2.99ms  p(90)=790.8µs  p(95)=2.2ms   
     http_req_connecting............: avg=116.74µs min=0s       med=0s     max=1.54ms  p(90)=621.19µs p(95)=708.84µs
     http_req_duration..............: avg=5.28s    min=910.53ms med=5.61s  max=6.53s   p(90)=5.71s    p(95)=5.74s   
       { expected_response:true }...: avg=5.28s    min=910.53ms med=5.61s  max=6.53s   p(90)=5.71s    p(95)=5.74s   
     http_req_failed................: 0.00% ✓ 0         ✗ 570  
     http_req_receiving.............: avg=968.02µs min=6µs      med=82.5µs max=53.54ms p(90)=678.2µs  p(95)=1.15ms  
     http_req_sending...............: avg=77.03µs  min=2µs      med=13µs   max=10.01ms p(90)=139.6µs  p(95)=332.39µs
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s     max=0s      p(90)=0s       p(95)=0s      
     http_req_waiting...............: avg=5.28s    min=909.33ms med=5.61s  max=6.51s   p(90)=5.71s    p(95)=5.73s   
     http_reqs......................: 570   15.948603/s
     iteration_duration.............: avg=5.78s    min=1.41s    med=6.11s  max=7.03s   p(90)=6.21s    p(95)=6.24s   
     iterations.....................: 570   15.948603/s
     vus............................: 20    min=20      max=100
     vus_max........................: 100   min=100     max=100

```

### Test 5

```

     data_received..................: 47 kB  1.2 kB/s
     data_sent......................: 62 kB  1.6 kB/s
     http_req_blocked...............: avg=762.5µs  min=0s    med=5µs   max=6.66ms p(90)=3.95ms p(95)=5.34ms  
     http_req_connecting............: avg=579.95µs min=0s    med=0s    max=4.23ms p(90)=3.45ms p(95)=3.79ms  
     http_req_duration..............: avg=4.59s    min=422µs med=4.55s max=8s     p(90)=7.99s  p(95)=7.99s   
       { expected_response:true }...: avg=3.61s    min=422µs med=3.55s max=7.96s  p(90)=6.61s  p(95)=7.07s   
     http_req_failed................: 22.32% ✓ 150       ✗ 522  
     http_req_receiving.............: avg=70.56µs  min=0s    med=20µs  max=5.49ms p(90)=97.9µs p(95)=208.19µs
     http_req_sending...............: avg=28.97µs  min=2µs   med=11µs  max=3.78ms p(90)=35µs   p(95)=61.89µs 
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s    max=0s     p(90)=0s     p(95)=0s      
     http_req_waiting...............: avg=4.59s    min=391µs med=4.55s max=8s     p(90)=7.99s  p(95)=7.99s   
     http_reqs......................: 672    17.800454/s
     iteration_duration.............: avg=10.19s   min=2.53s med=9.11s max=17.04s p(90)=17.01s p(95)=17.01s  
     iterations.....................: 336    8.900227/s
     vus............................: 3      min=3       max=100
     vus_max........................: 100    min=100     max=100

```
