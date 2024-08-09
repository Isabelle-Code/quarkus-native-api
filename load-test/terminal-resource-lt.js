import http from 'k6/http';
import {check, sleep} from 'k6';

import {uuidv4} from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
    stages: [
        {duration: '30s', target: 200},
        {duration: '1m30s', target: 100},
        {duration: '20s', target: 10},
    ],
};

// The function that defines VU logic.
//
// See https://grafana.com/docs/k6/latest/examples/get-started-with-k6/ to learn more
// about authoring k6 scripts.
//
export default function () {
    const serial = uuidv4()
    const payload = JSON.stringify({
        serial: serial,
        model: 'n915',
    });

    const resp = http.post('http://localhost:8080/terminals/', payload, {
        headers: {
            'Content-Type': 'application/json',
        },
    });

    check(resp, {'status was 200': (r) => r.status === 200});
    sleep(1);


    const terminalList = http.get('http://localhost:8080/terminals/');
    check(terminalList, {
        'status was 200 on listing': (r) => r.status === 200
    });

    sleep(1);

    check(terminalList.body, {
        'list contains terminal': (r) => {
            const body = JSON.parse(r);
            return body.data.some((t) => t.serial === serial);
        }
    });

    sleep(1);

    const respBody = JSON.parse(resp.body);
    const terminalId = respBody.id;

    const deletedTerminal = http.del(`http://localhost:8080/terminals/terminal/${terminalId}`);
    check(deletedTerminal, {
        'status was 204 on deleting': (r) => r.status === 204
    });


}
