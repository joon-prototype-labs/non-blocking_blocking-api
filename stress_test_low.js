import http from 'k6/http';
import {sleep} from 'k6';

export const options = {
    vus: 300, // 동시 사용자 수
    duration: '30s', // 테스트 지속 시간
};

let port = 18000 // webflux + coRouter
//let port = 18001 // webmvc + controller

export default function () {
    const params = {
        timeout: '8s',
    };

    const res = http.get('http://localhost:' + port + '/ask', params);
    sleep(0.1);
}
