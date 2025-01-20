import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        stress_test: {
            executor: 'shared-iterations',
            vus: 2000,           // 1000 virtual users
            iterations: 200000,   // 10만 번 반복
            maxDuration: '5m'    // 최대 5분 제한
        }
    },
    thresholds: {
        http_req_duration: ['p(95)<1000'],  // 95%의 요청이 1초 이내 처리
        http_reqs: ['rate>500'],            // 초당 최소 500 요청 처리
    }
};

//let port = 18000 // webmvc + controller
//let port = 18001 // webflux + coRouter
//let port = 18002 // webflux + reactive

// k6 run -e PORT=18000 ./stress_test.js

let port = __ENV.PORT || 8080;

export default function () {
    const response = http.get(`http://localhost:${port}/api/process`);

    check(response, {
        'status is 200': (r) => r.status === 200,
        'has valid data': (r) => {
            try {
                const body = JSON.parse(r.body);
                return body.data &&
                    Array.isArray(body.data) &&
                    typeof body.count === 'number' &&
                    typeof body.firstItem === 'string';
            } catch {
                return false;
            }
        },
    });
}
