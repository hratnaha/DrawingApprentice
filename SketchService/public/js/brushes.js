var buffer = document.getElementById('buffer');
var bffrCtx = buffer.getContext('2d');
var display = document.getElementById('display');
var dplCtx = display.getContext('2d');
var entire = document.getElementById('entire');
var entireCtx = entire.getContext('2d');
var isDrawing;

var brushes = {};

function brushUp(e){
    isDrawing = false;
    // draw the current brush from the buffer to the displaying canvas
    dplCtx.save();
    dplCtx.setTransform(1,0,0,1,0,0);
    dplCtx.drawImage(buffer, 0, 0);
    dplCtx.restore();

    entireCtx.save();
    var mtx = entireCtx.getTransform();
    mtx = mtx.inverse();
    entireCtx.setTransform(mtx.a, mtx.b, mtx.c, mtx.d, mtx.e, mtx.f);
    entireCtx.drawImage(buffer, 0, 0, buffer.width, buffer.height);
    entireCtx.restore();
    
    // clean the buffer context
    bffrCtx.save();
    bffrCtx.setTransform(1,0,0,1,0,0);
    bffrCtx.clearRect(0,0,buffer.width,buffer.height);
    bffrCtx.restore();
}

brushes.SimplePencil = {};
brushes.SimplePencil.onmousedown = function(e){    
    isDrawing = true;
    bffrCtx.beginPath();
    bffrCtx.moveTo(e.clientX, e.clientY);
};
brushes.SimplePencil.onmousemove = function(e){
    if (isDrawing) {
        bffrCtx.lineTo(e.clientX, e.clientY);
        bffrCtx.stroke();
    }
    moveCanvas(e);
};
brushes.SimplePencil.onmouseup = brushUp;
brushes.SmoothShadow = {};
brushes.SmoothShadow.onmousedown = function(e){
    isDrawing = true;

    bffrCtx.beginPath();
    bffrCtx.lineWidth = 10;
    bffrCtx.lineJoin = bffrCtx.lineCap = 'round';
    bffrCtx.shadowBlur = 10;
    bffrCtx.shadowColor = 'rgb(0, 0, 0)';
    bffrCtx.moveTo(e.clientX, e.clientY);
};
brushes.SmoothShadow.onmousemove = function(e){
    if (isDrawing) {
        bffrCtx.lineTo(e.clientX, e.clientY);
        bffrCtx.stroke();
    }
    moveCanvas(e);
};
brushes.SmoothShadow.onmouseup = brushUp;

buffer.onmousedown = brushes.SimplePencil.onmousedown;
buffer.onmousemove = brushes.SimplePencil.onmousemove;
buffer.onmouseup = brushes.SimplePencil.onmouseup;