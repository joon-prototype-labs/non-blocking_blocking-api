import http from 'k6/http';
import {sleep} from 'k6';

export const options = {
    vus: 100, // 동시 사용자 수
    duration: '30s', // 테스트 지속 시간
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
    sleep(0.5);
    // http.get('http://localhost:' + port + '/ask-without-db-call', params);
    // sleep(0.5);
}
