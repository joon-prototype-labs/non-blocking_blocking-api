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
