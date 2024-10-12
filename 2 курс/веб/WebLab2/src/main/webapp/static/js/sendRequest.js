// const ctx = document.getElementById('coordinate-plane').getContext('2d');
//
// ctx.addEventListener("mousedown", (event) =>{
//     drawDot(event)
// })

let form = document.getElementById('coords-form')

form.addEventListener("submit", (event) =>{
    event.preventDefault()
    sendData()
})

async function sendData(){
    let x = document.getElementById("x-selector")
    let r = document.querySelector(".r-checkbox:checked")

    if (r == null){
        alert("Введиет R");
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

    let formData = new URLSearchParams()
    formData.set('x', x.options[x.selectedIndex].value)
    formData.set('y', document.getElementById("YInput").value)
    formData.set('r', r.value)

    await fetch('http://127.0.0.1:8080/WebLab2/controller',
        {
            method: 'POST',
            body: formData
        })
        .then(function (response) {return response.text()}).then((html) => {
            document.body.innerHTML = html
        });
    console.log("data fetched")
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


function drawDot(ctx, event){
    let canvasPos = ctx.getBoundingClientRect()
    const x = event.clientX - canvasPos.left
    const y = event.clientY - canvasPos.top

    console.log(x, y)

    // let plotX = 2*x/r * 30;
    // let plotY = 2*-y/r * 30;
    //
    // ctx.beginPath();
    // ctx.translate(canvas.width/2, canvas.height/2);
    // ctx.arc(plotX, plotY, 6, 0, 2*Math.PI);
    // ctx.fillStyle = 'purple';
    //
    // ctx.fill();
    // ctx.resetTransform();
    // ctx.closePath();
}
