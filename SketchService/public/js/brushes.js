var buffer = document.getElementById('buffer');
var bffrCtx = buffer.getContext('2d');
var display = document.getElementById('display');
var dplCtx = display.getContext('2d');
var entire = document.getElementById('entire');
var entireCtx = entire.getContext('2d');
var brushes = {};
var isDrawing;
var isPan;
var currentTime = new Date();
var lastClick = currentTime.getTime();

function checkDoubleClick() {
    currentTime = new Date();
    if (currentTime.getTime() - lastClick > 500) { // Not a double click
        lastClick = currentTime.getTime();
    } else { // Time in between clicks is short enough to be a double click
        isPan = true;
    }
}

function handlePan(pt) { // Pan the entire canvas
    entireCtx.translate(pt.x, pt.y);
    dplCtx.translate(pt.x, pt.y);
    redraw();
}

function checkInsideCanvas(pt) { // Point inside drawable canvas
    if (pt.x > 0 && pt.x < display.width && pt.y > 0 && pt.y < display.height) {
        return true;
    } else {
        return false;
    } 
}

function brushUp(e){
    isDrawing = false;
    isPan = false;
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
    checkDoubleClick();
    var pt = dplCtx.transformedPoint(e.clientX, e.clientY); // Current point on drawable canvas    
    if (checkInsideCanvas(pt)) {
        isDrawing = true;
        bffrCtx.beginPath();
        bffrCtx.moveTo(e.clientX, e.clientY);
   }
};
brushes.SimplePencil.onmousemove = function(e){
    var pt = entireCtx.transformedPoint(e.clientX,e.clientY);
    if (isPan) { // Pan drawable canvas
        handlePan(pt);
    } else if (isDrawing && checkInsideCanvas(pt)) {
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
