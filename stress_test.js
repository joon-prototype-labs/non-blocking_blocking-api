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

//let port = 18000 // webmvc + controller + jpa
//let port = 18001 // webflux + coRouter + jpa
//let port = 18002 // webflux + reactive + jpa
let port = 18003 // webflux + reactive + r2dbc

export default function () {
    const params = {
        //timeout: '8s',
    };

    http.get('http://localhost:' + port + '/ask', params);
    // http.get('http://localhost:' + port + '/ask-without-db-call', params);
    // sleep(0.5);
}
