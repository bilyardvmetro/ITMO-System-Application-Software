let x;
document.querySelectorAll(".x-button").forEach(button => {
    button.addEventListener("click", function () {
        x = this.getAttribute("data-value");
    })
});

async function submitForm(event){
    event.preventDefault();

    const xValue = parseInt(x);
    const yValue = parseFloat(document.getElementById("YInput").value);
    const rValue = parseFloat(document.getElementById("RInput").value);

    if (isNaN(xValue)){
        alert("Введите X");
        return
    }

    if (isNaN(yValue)){
        alert("Введите Y");
        return
    }

    if (isNaN(rValue)){
        alert("Введите R");
        return
    }

    if (checkY(yValue) || checkR(rValue)){
        return;
    }

    // drawDot(xValue, yValue, rValue);

    const url = `http://localhost:8080/fcgi-bin/FcgiServer.jar?x=${xValue}&y=${yValue}&r=${rValue}`;
    console.log(url)

    fetch(url, {method: 'GET'}).then(response => response.json()).then(data => {
        console.log("otvet prishel\n")
        addTableRow(data, xValue, yValue, rValue);

    }).catch((error) => {
        console.log(error);
    });
}

function checkY(y){
    if (!((-5 <= y) && (y <= 3))){
        alert("Y введён неверно")
        return true;
    }
    return false;
}

function checkR(r){
    if (!((1 <= r) && (r <= 4))){
        alert("R введён неверно")
        return true;
    }
    return false;
}

function addTableRow(data, x, y, r){
    console.log("tablica delaetsya")
    let tbody = document.querySelector("tbody");
    let newRow = document.createElement("tr");

    let xCell = newRow.insertCell();
    let xValue = document.createTextNode(x)
    xCell.appendChild(xValue);

    let yCell = newRow.insertCell();
    let yValue = document.createTextNode(y)
    yCell.appendChild(yValue);

    let rCell = newRow.insertCell();
    let rValue = document.createTextNode(r)
    rCell.appendChild(rValue);

    let currentTimeCell = newRow.insertCell();
    let currentTimeValue = document.createTextNode(new Date().toLocaleTimeString())
    currentTimeCell.appendChild(currentTimeValue)

    let executionTimeCell = newRow.insertCell();
    let executionTimeValue = document.createTextNode(data.execution_time);
    executionTimeCell.appendChild(executionTimeValue);

    let resultCell = newRow.insertCell();
    let resultValue = document.createTextNode(data.result);
    resultCell.appendChild(resultValue);

    tbody.prepend(newRow);
}

function drawDot(x, y, r){

    // TODO починить отрисовку точки
    const canvas = document.getElementById('coordinate-plane');
    const ctx = canvas.getContext('2d');

    let plotX = 2*x/r * 30;
    let plotY = 2*-y/r * 30;

    ctx.beginPath();
    ctx.translate(canvas.width/2, canvas.height/2);
    ctx.arc(plotX, plotY, 6, 0, 2*Math.PI);
    ctx.fillStyle = 'purple';
    ctx.fill();
    ctx.resetTransform();
    ctx.closePath();
}
