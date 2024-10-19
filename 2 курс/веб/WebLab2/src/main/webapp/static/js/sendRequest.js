const plane = document.getElementById('coordinate-plane')
const form = document.getElementById('coords-form')

function selectOnlyThis(checkBoxId){
    for (let i = 1; i <= 5; i++){
        document.getElementById("r" + i).checked = false
    }
    document.getElementById(checkBoxId).checked = true
}

function checkX(x){
    return !((-3 <= x) && (x <= 5));
}

function checkY(y){
    return !((-5 <= y) && (y <= 3));
}

function checkR(r){
    return !((1 <= r) && (r <= 5));
}

async function sendData(x, y, r){
    let formData = new URLSearchParams()
    formData.set('x', x)
    formData.set('y', y)
    formData.set('r', r)

    await fetch('http://localhost:44044/WebLab2/controller',
        {
            method: 'POST',
            body: formData
        })
        .then(function (response) {return response.text()}).then((html) => {
            document.body.innerHTML = html
        });
    console.log("data fetched")
}

function parseFormData(){
    let x = document.getElementById("x-selector")
    let r = document.querySelector(".r-checkbox:checked")

    if (r == null){
        alert("Введите R");
        return
    }

    const xValue = parseInt(x.options[x.selectedIndex].value);
    const yValue = parseFloat(document.getElementById("YInput").value);
    const rValue = parseFloat(r.value)

    console.log(xValue, yValue, rValue)

    let errorsStr = "";

    if (isNaN(xValue)){
        errorsStr += "Введите X\n";
    } else if (checkX(xValue)){
        errorsStr += ("X введён неверно\n");
    }

    if (isNaN(yValue)){
        errorsStr += ("Введите Y\n");
    } else if (checkY(yValue)){
        errorsStr += ("Y введён неверно\n");
    }

    if (isNaN(rValue)){
        errorsStr += ("Введите R\n");
    } else if (checkR(rValue)){
        errorsStr += ("R введён неверно\n");
    }

    if (errorsStr){
        alert(errorsStr);
        return
    }

    sendData(x.options[x.selectedIndex].value, document.getElementById("YInput").value, r.value)
    drawDot(plane, xValue, yValue, rValue)
}

function saveDot(x, y, r) {
    localStorage.setItem("x", x.toString())
    localStorage.setItem("y", y.toString())
    localStorage.setItem("r", r.toString())
}

function drawDot(canvas, x, y, r){
    const ctx = canvas.getContext('2d')

    ctx.beginPath();
    ctx.translate(canvas.width/2, canvas.height/2);
    let plotX = x*(120/r);
    let plotY = -y*(120/r);

    ctx.arc(plotX, plotY, 5, 0, 2*Math.PI);
    ctx.fillStyle = 'purple';
    ctx.fill();

    ctx.resetTransform();
    ctx.closePath()

    saveDot(x, y, r);
}

form.addEventListener("submit", (event) =>{
    event.preventDefault()
    parseFormData()
})

plane.addEventListener("mousedown", async (event) => {
    event.preventDefault()

    let r = document.querySelector(".r-checkbox:checked")
    if (r == null) {
        alert("Введите R");
        return
    }

    let canvasPos = canvas.getBoundingClientRect()
    const clickX = event.clientX - canvasPos.left
    const clickY = event.clientY - canvasPos.top

    console.log(clickX, clickY)

    let rVal = r.value
    let x = ((clickX.toFixed(4) - 150) / (120/rVal)).toFixed(4)
    let y = ((150 - clickY.toFixed(4)) / (120/rVal)).toFixed(4)

    console.log(x, y, rVal)

    sendData(x.toString(), y.toString(), rVal.toString())
    drawDot(plane, x, y, rVal)
})
