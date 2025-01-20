import http from 'k6/http';
import {sleep} from 'k6';

export const options = {
    scenarios: {
        stress_test: {
            executor: 'shared-iterations',
            vus: 2000,           // 2000 virtual users
            iterations: 50000,   // 5만 번 반복
            maxDuration: '5m'    // 최대 5분 제한
        }
    }
};

//let port = 18000 // webmvc + controller + jpa
let port = 18001 // webflux + coRouter + jpa
//let port = 18002 // webflux + reactive + jpa
//let port = 18003 // webflux + reactive + r2dbc

export default function () {
    const params = {
        //timeout: '8s',
    };

    http.get('http://localhost:' + port + '/ask', params);
    // http.get('http://localhost:' + port + '/ask-without-db-call', params);
    // sleep(0.5);
}
