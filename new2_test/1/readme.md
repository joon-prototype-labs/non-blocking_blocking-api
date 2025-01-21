## 테스트 설명

조건
- JINX 7c9d87815966e2c0eb83b9cae95777d8c0dc2866 시점
- 로컬 환경
- firestore token 검증 호출 X
- 인증 없이 하드코딩 된 사용자
- 다음 스크립트 사용

```js
import http from 'k6/http';
import {sleep} from 'k6';

export const options = {
    scenarios: {
        stress_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 1000 },
                { duration: '30s', target: 1000 },
                { duration: '30s', target: 2000 },
                { duration: '30s', target: 2000 },
                { duration: '30s', target: 3000 },
                { duration: '3m', target: 3000 }
            ],
            gracefulRampDown: '30s'
        }
    }
};

const payload = JSON.stringify({
    latitude: 0,
    latitudeDelta: 180,
    longitude: 0,
    longitudeDelta: 360
});

const params = {
    headers: {
        'Host': 'localhost:8080',
        'Content-Type': 'application/json',
        'User-Agent': 'insomnia/9.3.2',
        'Accept': '*/*'
    }
};

export default function () {
    const response = http.request('GET', 'http://localhost:8080/api/v2/location/all', payload, params);
} 
```

## 결과

```
WARN[0254] Request Failed                                error="Get \"http://localhost:8080/api/v2/location/all\": dial: i/o timeout"

     data_received..................: 439 MB 1.3 MB/s
     data_sent......................: 28 MB  81 kB/s
     http_req_blocked...............: avg=69.56ms  min=0s       med=2µs   max=19.54s  p(90)=4µs    p(95)=8µs   
     http_req_connecting............: avg=69.55ms  min=0s       med=0s    max=19.53s  p(90)=0s     p(95)=0s    
     http_req_duration..............: avg=5.59s    min=0s       med=4.05s max=39.7s   p(90)=11.28s p(95)=13.21s
       { expected_response:true }...: avg=5.7s     min=21.18ms  med=4.1s  max=39.7s   p(90)=11.32s p(95)=13.25s
     http_req_failed................: 1.91%  ✓ 2450       ✗ 125491
     http_req_receiving.............: avg=339.62µs min=0s       med=76µs  max=51.6ms  p(90)=771µs  p(95)=1.6ms 
     http_req_sending...............: avg=65.34µs  min=0s       med=16µs  max=47.87ms p(90)=58µs   p(95)=179µs 
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s    max=0s      p(90)=0s     p(95)=0s    
     http_req_waiting...............: avg=5.59s    min=0s       med=4.05s max=39.7s   p(90)=11.28s p(95)=13.21s
     http_reqs......................: 127941 373.636128/s
     iteration_duration.............: avg=6.15s    min=579.83µs med=4.17s max=59.08s  p(90)=12.13s p(95)=13.96s
     iterations.....................: 127941 373.636128/s
     vus............................: 193    min=27       max=3000
     vus_max........................: 3000   min=3000     max=3000
```

- [snapshot](JinxApiApplication_2025_01_21_105339.jfr)

## 결과 분석

성능 별로임.

로컬에 한가지 요청만 보내니까 DB 단 캐싱(어플리케이션 말고, 캐시버퍼 말하는 것)되서 이 정도지, 실제로는 더 많이 실패했을 것. + 사용자 수가 늘어나면 더 많이 실패하고

parallel 스레드는 코틀린 작업 처리하는 스레드(실제 실행보다는 코루틴 관리 쪽인듯?), reactor-http-nio는 netty 이벤트 루프 스레드고 제일 활동량이 높다.

추가로 찾아본 Spring에서 사용하는 DBCP 라이브러리인 HikariCP에선 별도의 실행을 위한 스레드 풀을 관리하지 않는다.   
커넥션 풀을 관리하는 스레드를 포함해서 몇 개 되지 않음. (구체적인 개수는 안 찾아봄. Connetion 개수를 변경하는 스레드는 있는거 같고...)

따라서 호출하는 어플리케이션의 스레드 풀에 의존하는데, 이는 이벤트루프 환경에서 작업을 방해하게 되고, 작업을 처리하지 못하게 되므로 timeout이 발생하는 원인이 된다.

즉, 성능이 별로다. 지금 서비스야 트래픽이 적으니 상관 없지만, 나중에는 병목 지점이 될 수 있다고 보여진다.
