const canvas = document.getElementById('coordinate-plane');
const ctx = canvas.getContext('2d');

canvas.width = 300;
canvas.height = 300;

function drawAxis() {
    ctx.beginPath();
    ctx.moveTo(0, canvas.height / 2);
    ctx.lineTo(canvas.width, canvas.height / 2);
    ctx.strokeStyle = 'black';
    ctx.lineWidth = 2;
    ctx.stroke();


    ctx.beginPath();
    ctx.moveTo(canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2, canvas.height);
    ctx.strokeStyle = 'black';
    ctx.lineWidth = 2;
    ctx.stroke();
}

function drawGrid() {
    ctx.beginPath();
    for (let x = 0; x < 330; x += 30) {
        ctx.moveTo(x, 0);
        ctx.lineTo(x, canvas.height);
        ctx.strokeStyle = '#427aa1';
        ctx.lineWidth = 1;
        ctx.stroke();
    }

    for (let y = 0; y < 330; y += 30) {
        ctx.moveTo(0, y);
        ctx.lineTo(canvas.width, y);
        ctx.strokeStyle = '#427aa1';
        ctx.lineWidth = 1;
        ctx.stroke();
    }
}

function drawCircle(x, y, r) {
    ctx.beginPath();
    ctx.moveTo(x, y);
    ctx.fillStyle = '#8ba9f0';
    ctx.arc(x, y, r, 0, Math.PI*0.5, false);
    ctx.closePath();
    ctx.fill();
}

function drawRect(x,y) {
    ctx.beginPath();
    ctx.moveTo(x, y);
    ctx.fillStyle = '#8ba9f0';
    ctx.rect(x, y, x-270, y-90);
    ctx.closePath();
    ctx.fill();

}

function drawTriangle(x,y) {
    ctx.beginPath();
    ctx.moveTo(x, y);
    ctx.fillStyle = '#8ba9f0';
    ctx.lineTo(x-60, y);
    ctx.lineTo(x, y-60);
    ctx.closePath();
    ctx.fill();
}

function drawCoords() {
    ctx.fillStyle = 'black';
    ctx.font = '1.5em Montserrat';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'bottom';

    ctx.fillText('R/2', 210, 150);
    ctx.beginPath();
    ctx.moveTo(210, 145);
    ctx.lineTo(210,155);
    ctx.strokeStyle = 'black';
    ctx.closePath();
    ctx.stroke();

    ctx.fillText('R', 270, 150);
    ctx.beginPath();
    ctx.moveTo(270, 145);
    ctx.lineTo(270,155);
    ctx.strokeStyle = 'black';
    ctx.closePath();
    ctx.stroke();

    ctx.textAlign = 'left';

    ctx.fillText('-R/2', 150, 210);
    ctx.beginPath();
    ctx.moveTo(145, 210);
    ctx.lineTo(155, 210);
    ctx.strokeStyle = 'black';
    ctx.closePath();
    ctx.stroke();

    ctx.fillText('-R', 150, 270);
    ctx.beginPath();
    ctx.moveTo(145, 270);
    ctx.lineTo(155, 270);
    ctx.strokeStyle = 'black';
    ctx.closePath();
    ctx.stroke();

    ctx.textAlign = 'center';

    ctx.beginPath();
    ctx.fillText('-R/2', 90, 150);
    ctx.moveTo(90, 145);
    ctx.lineTo(90,155);
    ctx.strokeStyle = 'black';
    ctx.closePath();
    ctx.stroke();

    ctx.beginPath();
    ctx.fillText('-R', 30, 150);
    ctx.moveTo(30, 145);
    ctx.lineTo(30,155);
    ctx.strokeStyle = 'black';
    ctx.closePath();
    ctx.stroke();

    ctx.textAlign = 'left';

    ctx.beginPath();
    ctx.fillText('R/2', 150, 90);
    ctx.moveTo(145, 90);
    ctx.lineTo(155, 90);
    ctx.strokeStyle = 'black';
    ctx.closePath();
    ctx.stroke();

    ctx.beginPath();
    ctx.fillText('R', 150, 30);
    ctx.moveTo(145, 30);
    ctx.lineTo(155, 30);
    ctx.strokeStyle = 'black';
    ctx.closePath();
    ctx.stroke();

}

drawCircle(150,150,60);
drawRect(150,150);
drawTriangle(150,150);
drawGrid();
drawCoords();
drawAxis();
