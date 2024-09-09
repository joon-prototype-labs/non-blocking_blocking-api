# corouter-api(webflux + coRouter + jpa)

## Test 1

```
     data_received..................: 23 kB  641 B/s
     data_sent......................: 40 kB  1.1 kB/s
     http_req_blocked...............: avg=35.29ms min=0s       med=215.5µs max=1.1s   p(90)=5.09ms p(95)=5.96ms 
     http_req_connecting............: avg=34.97ms min=0s       med=187.5µs max=1.1s   p(90)=3.08ms p(95)=3.18ms 
     http_req_duration..............: avg=5.79s   min=0s       med=5.88s   max=8s     p(90)=7.99s  p(95)=8s     
       { expected_response:true }...: avg=4.53s   min=1.06s    med=4.59s   max=7.64s  p(90)=6.18s  p(95)=6.61s  
     http_req_failed................: 48.59% ✓ 242       ✗ 256  
     http_req_receiving.............: avg=34.44µs min=0s       med=8µs     max=875µs  p(90)=93µs   p(95)=130µs  
     http_req_sending...............: avg=33.37µs min=0s       med=17.5µs  max=1.23ms p(90)=45µs   p(95)=86.89µs
     http_req_tls_handshaking.......: avg=0s      min=0s       med=0s      max=0s     p(90)=0s     p(95)=0s     
     http_req_waiting...............: avg=5.79s   min=0s       med=5.88s   max=8s     p(90)=7.99s  p(95)=8s     
     http_reqs......................: 498    14.176596/s
     iteration_duration.............: avg=6.7s    min=501.73ms med=7.89s   max=8.53s  p(90)=8.5s   p(95)=8.51s  
     iterations.....................: 498    14.176596/s
     vus............................: 4      min=4       max=100
     vus_max........................: 100    min=100     max=100

```

## Test 2

```
     data_received..................: 11 kB  277 B/s
     data_sent......................: 37 kB  956 B/s
     http_req_blocked...............: avg=126.63ms min=0s      med=297µs   max=6.71s  p(90)=5.83ms p(95)=6.45ms  
     http_req_connecting............: avg=126.07ms min=0s      med=240.5µs max=6.71s  p(90)=3.06ms p(95)=3.29ms  
     http_req_duration..............: avg=7.15s    min=0s      med=7.99s   max=8.02s  p(90)=8s     p(95)=8s      
       { expected_response:true }...: avg=5.61s    min=3.46s   med=5.57s   max=7.58s  p(90)=7.04s  p(95)=7.5s    
     http_req_failed................: 72.60% ✓ 318       ✗ 120  
     http_req_receiving.............: avg=123.07µs min=0s      med=0s      max=5.48ms p(90)=78.2µs p(95)=277.34µs
     http_req_sending...............: avg=55.12µs  min=0s      med=19µs    max=3.59ms p(90)=72.6µs p(95)=200.84µs
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s      max=0s     p(90)=0s     p(95)=0s      
     http_req_waiting...............: avg=7.15s    min=0s      med=7.99s   max=8.02s  p(90)=8s     p(95)=8s      
     http_reqs......................: 438    11.491196/s
     iteration_duration.............: avg=7.81s    min=501.6ms med=8.5s    max=9.43s  p(90)=8.53s  p(95)=8.54s   
     iterations.....................: 438    11.491196/s
     vus............................: 2      min=2       max=100
     vus_max........................: 100    min=100     max=100

```

## Test 3

```
data_received..................: 158 kB 5.0 kB/s
     data_sent......................: 151 kB 4.8 kB/s
     http_req_blocked...............: avg=268.33µs min=0s   med=4µs   max=10.54ms p(90)=12µs   p(95)=3.06ms  
     http_req_connecting............: avg=196.87µs min=0s   med=0s    max=8.7ms   p(90)=0s     p(95)=2.66ms  
     http_req_duration..............: avg=1.21s    min=1.1s med=1.16s max=2.47s   p(90)=1.21s  p(95)=1.52s   
       { expected_response:true }...: avg=1.21s    min=1.1s med=1.16s max=2.47s   p(90)=1.21s  p(95)=1.52s   
     http_req_failed................: 0.00%  ✓ 0         ✗ 1800 
     http_req_receiving.............: avg=56.62µs  min=5µs  med=33µs  max=1.93ms  p(90)=89.1µs p(95)=137.04µs
     http_req_sending...............: avg=28.92µs  min=2µs  med=12µs  max=6.04ms  p(90)=31µs   p(95)=42µs    
     http_req_tls_handshaking.......: avg=0s       min=0s   med=0s    max=0s      p(90)=0s     p(95)=0s      
     http_req_waiting...............: avg=1.21s    min=1.1s med=1.16s max=2.47s   p(90)=1.21s  p(95)=1.52s   
     http_reqs......................: 1800   57.196368/s
     iteration_duration.............: avg=1.71s    min=1.6s med=1.66s max=2.98s   p(90)=1.72s  p(95)=2.02s   
     iterations.....................: 1800   57.196368/s
     vus............................: 40     min=40      max=100
     vus_max........................: 100    min=100     max=100
 
```

### Test 4

```
     data_received..................: 47 kB  1.4 kB/s
     data_sent......................: 50 kB  1.4 kB/s
     http_req_blocked...............: avg=1.26ms   min=0s       med=7µs    max=18.9ms  p(90)=6.27ms  p(95)=6.72ms  
     http_req_connecting............: avg=664.72µs min=0s       med=0s     max=18.33ms p(90)=2.36ms  p(95)=2.74ms  
     http_req_duration..............: avg=5.03s    min=720.17ms med=4.41s  max=8.02s   p(90)=8s      p(95)=8s      
       { expected_response:true }...: avg=3.98s    min=720.17ms med=4.11s  max=7.97s   p(90)=5.29s   p(95)=6.25s   
     http_req_failed................: 26.26% ✓ 156       ✗ 438  
     http_req_receiving.............: avg=136.52µs min=0s       med=19.5µs max=9.34ms  p(90)=96.7µs  p(95)=387.64µs
     http_req_sending...............: avg=165.39µs min=2µs      med=15µs   max=38.72ms p(90)=141.9µs p(95)=504.84µs
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s     max=0s      p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=5.03s    min=720.1ms  med=4.41s  max=8.02s   p(90)=8s      p(95)=8s      
     http_reqs......................: 594    17.144722/s
     iteration_duration.............: avg=5.54s    min=1.22s    med=4.91s  max=9.01s   p(90)=8.51s   p(95)=8.52s   
     iterations.....................: 594    17.144722/s
     vus............................: 52     min=52      max=100
     vus_max........................: 100    min=100     max=100

```
