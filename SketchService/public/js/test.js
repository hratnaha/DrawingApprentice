// canvas element in DOM
var canvas1 = document.getElementById('c');
var context1 = canvas1.getContext('2d');

// buffer canvas
var canvas2 = document.getElementById('canvas2');
canvas2.width = 500;
canvas2.height = 300;
var context2 = canvas2.getContext('2d');

// create something on the canvas
context2.beginPath();
context2.moveTo(10,10);
context2.lineTo(150,300);
context2.fillText("Big smile!",10,200);
context2.stroke();

context1.beginPath();
// context1.moveTo(10,10);
// context1.lineTo(100,100);
context1.fillText("Sad face",10,10);
context1.stroke();

//render the buffered canvas onto the original canvas element
swapCanvases();

document.getElementById("test").onclick=function(){
  swapCanvases();
};

function swapCanvases(){
  if(canvas1.style.visibility=='visible'){
    canvas1.style.visibility='hidden';
    canvas2.style.visibility='visible';
    alert('entered canvas 2');
    context2.drawImage(canvas1, 0, 0);
  }else{
    canvas1.style.visibility='visible';
    canvas2.style.visibility='hidden';
    alert('entered canvas 1');
  }
}

// var renderToCanvas = function (width, height, renderFunction) {
//     var buffer = document.createElement('canvas');
//     buffer.width = width;
//     buffer.height = height;
//     renderFunction(buffer.getContext('2d'));
//     return buffer;
// };
