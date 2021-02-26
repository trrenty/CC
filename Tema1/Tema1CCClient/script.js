function run() {
    console.log("run");
    const Http = new XMLHttpRequest();
    const url = "https://localhost:5001/api/test";


    Http.open("GET", url);
    Http.setRequestHeader("ApiKey", "234gdfdaJKHkjhfakH3241sG");

    Http.send();

    Http.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var obj = JSON.parse(this.response);
            // console.log(obj.result);
            // console.log(document.getElementById('resultImg').src);

            document.getElementById('resultImg').src = 'data:image/gif;base64,' + obj.result;
            document.getElementById('run').innerHTML = "";

            // console.log(document.getElementById('resultImg').src);
            return;

        } else {
            document.getElementById('run').innerHTML = ' ' + this.status + '\n' + this.response;

        }
    }
}

function monitor() {
    const Http = new XMLHttpRequest();
    const url = "https://localhost:5001/api/test/stats";


    Http.open("GET", url);
    Http.setRequestHeader("ApiKey", "234gdfdaJKHkjhfakH3241sG");

    Http.send();

    Http.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var obj = JSON.parse(this.response);

            document.getElementById('monitor').innerHTML = JSON.stringify(obj, null, 2);

            // console.log(document.getElementById('resultImg').src);
            return;

        } else {
            document.getElementById('monitor').innerHTML = ' ' + this.status + '\n' + this.response;

        }
    }
}

function parallel() {
    console.log("parallel");

    var res = ""

    var i = 0;
    const sendRequest = () => {
        console.log("req " + (i++) % 10 + " sent at " + performance.now())
        document.getElementById('parallel').innerHTML += "req " + (i) % 10 + " send at: " + performance.now() + '\n';

        const fetchPromise = fetch("https://localhost:5001/api/test/stats", {
            headers: {
                ApiKey: "234gdfdaJKHkjhfakH3241sG"
            }
        });
        fetchPromise.then(response => setTimeout(() => {
            console.log(response);
            console.log("response " + (i++) % 10 + " received at: " + performance.now())
            document.getElementById('parallel').innerHTML += "response " + (i) % 10 + " received at: " + performance.now() + '\n';
        }, 1000))
    }

    // 5 batches * 2 requests = 10 requests.
    const batches = Array(2).fill(Array(10).fill(sendRequest))

    ;
    (async function() {
        for (const batch of batches) {
            try {
                console.log('-- sending batch --')
                await Promise.all(batch.map(f => f()))
            } catch (err) {
                console.error(err)
            }
        }
    })()

}

function metrics() {
    console.log("metrics");
    // https://localhost:5001/api/test/metrics

    const Http = new XMLHttpRequest();
    const url = "https://localhost:5001/api/test/metrics";



    Http.open("GET", url);
    Http.setRequestHeader("ApiKey", "234gdfdaJKHkjhfakH3241sG");

    Http.send();

    Http.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {

            document.getElementById('metrics').innerHTML = this.response;

            // console.log(document.getElementById('resultImg').src);
            return;

        } else {
            document.getElementById('metrics').innerHTML = ' ' + this.status + '\n' + this.response;

        }
    }

}