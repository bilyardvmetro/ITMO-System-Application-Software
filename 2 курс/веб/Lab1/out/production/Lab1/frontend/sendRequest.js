let x;
let yForm = document.getElementById("YInput");
let rForm = document.getElementById("RInput");

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

    if (isNaN(yValue) || isNaN(rValue) || checkForms(yValue, rValue)){
        return;
    }

    const url = `http://localhost:8083/fcgi-bin/FcgiServer.jar?x=${xValue}&y=${yValue}&r=${rValue}`;
    console.log(url)

    fetch(url, {method: 'GET'}).then(response => response.json()).then(data => {
        console.log("otvet prishel\n")
        addTableRow(data, xValue, yValue, rValue);

    }).catch((error) => {
        console.log(error);
    });
}

function checkForms(y, r){
    //TODO сделать правильные алерты
    if (!((-5 <= y) && (y <= 3))){
        yForm.setCustomValidity("Проверьте число Y на корректность");
        return true;
    }

    if (!((1 <= r) && (r <= 4))){
        rForm.setCustomValidity("Проверьте число R на корректность");
        return true;
    }

    return false;
}

function addTableRow(data, x, y, r){
    console.log("tablica delaetsya")
    let tbody = document.querySelector("tbody");
    let newRow = tbody.insertRow();

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
}

//TODO  добавить отрисовку точки
