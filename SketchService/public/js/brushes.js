var el = document.getElementById('c');
var ctx = el.getContext('2d');
var isDrawing;

var brushes = {};

brushes.SimplePencil = {};
brushes.SimplePencil.onmousedown = function(e){
    isDrawing = true;
    ctx.moveTo(e.clientX, e.clientY);
};
brushes.SimplePencil.onmousemove = function(e){
    if (isDrawing) {
        ctx.lineTo(e.clientX, e.clientY);
        ctx.stroke();
    }
};
brushes.SimplePencil.onmouseup = function(e){
    isDrawing = false;
};
brushes.SmoothShadow = {};
brushes.SmoothShadow.onmousedown = function(e){
    isDrawing = true;
    ctx.lineWidth = 10;
    ctx.lineJoin = ctx.lineCap = 'round';
    ctx.shadowBlur = 10;
    ctx.shadowColor = 'rgb(0, 0, 0)';
    ctx.moveTo(e.clientX, e.clientY);
};
brushes.SmoothShadow.onmousemove = function(e){
    if (isDrawing) {
        ctx.lineTo(e.clientX, e.clientY);
        ctx.stroke();
    }
};
brushes.SmoothShadow.onmouseup = function(e){
    isDrawing = false;
};

el.onmousedown = brushes.SmoothShadow.onmousedown;
el.onmousemove = brushes.SimplePencil.onmousemove;
el.onmouseup = brushes.SimplePencil.onmouseup;