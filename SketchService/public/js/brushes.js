var el = document.getElementsByTagName('canvas')[0];
var ctx = el.getContext('2d');
// <<<<<<< HEAD
// var canvas3 = document.getElementById('canvas3');
// var ctx3 = canvas3.getContext('2d');var isDrawing;
// =======
var isDrawing, points = [ ], lastPoint;
// >>>>>>> 48c6d8c0cb8d5856027c55bd8510e2289bbe1bb8

var brushes = {};

function midPointBtw(p1, p2) {
    return {
        x: p1.x + (p2.x - p1.x)/2,
        y: p1.y + (p2.y - p1.y)/2
    };
}

brushes.SimplePencil = {};
brushes.SimplePencil.onmousedown = function(e){
    isDrawing = true;
    ctx.beginPath();
    ctx3.beginPath();
    ctx.moveTo(e.clientX, e.clientY);
    ctx3.moveTo(e.clientX, e.clientY);
};
brushes.SimplePencil.onmousemove = function(e){
    if (isDrawing) {
        ctx.lineTo(e.clientX, e.clientY);
        ctx.stroke();
        ctx3.lineTo(e.clientX, e.clientY);
        ctx3.stroke();
    }
};
brushes.SimplePencil.onmouseup = function(e){
    isDrawing = false;
    copyToBuffer();
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

// <<<<<<< HEAD
// el.onmousedown = brushes.SimplePencil.onmousedown;
// =======
brushes.EdgeSmoothing = {};
brushes.EdgeSmoothing.onmousedown = function(e) {
  isDrawing = true;
  ctx.moveTo(e.clientX, e.clientY);
};
brushes.EdgeSmoothing.onmousemove = function(e) {
  if (isDrawing) {
    var radgrad = ctx.createRadialGradient(
      e.clientX,e.clientY,10,e.clientX,e.clientY,20);
    
    radgrad.addColorStop(0, '#000');
    radgrad.addColorStop(0.5, 'rgba(0,0,0,0.5)');
    radgrad.addColorStop(1, 'rgba(0,0,0,0)');
    ctx.fillStyle = radgrad;
    
    ctx.fillRect(e.clientX-20, e.clientY-20, 40, 40);
  }
};
brushes.EdgeSmoothing.onmouseup = function() {
  isDrawing = false;
};

brushes.Bezier = {};
brushes.Bezier.onmousedown = function(e) {
  isDrawing = true;
  points.push({ x: e.clientX, y: e.clientY });
  ctx.lineWidth = 10;
  ctx.lineJoin = ctx.lineCap = 'round';
};

brushes.Bezier.onmousemove = function(e) {
  if (!isDrawing) return;
  
  points.push({ x: e.clientX, y: e.clientY });

  ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
  
  var p1 = points[0];
  var p2 = points[1];
  
  ctx.beginPath();
  ctx.moveTo(p1.x, p1.y);
  console.log(points);

  for (var i = 1, len = points.length; i < len; i++) {
    var midPoint = midPointBtw(p1, p2);
    ctx.quadraticCurveTo(p1.x, p1.y, midPoint.x, midPoint.y);
    p1 = points[i];
    p2 = points[i+1];
  }
  ctx.lineTo(p1.x, p1.y);
  ctx.stroke();
};

brushes.Bezier.onmouseup = function() {
  isDrawing = false;
  points.length = 0;
};

brushes.FurPen = {};
var img = new Image();
img.src = 'http://www.tricedesigns.com/wp-content/uploads/2012/01/brush2.png';

function distanceBetween(point1, point2) {
  return Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
}
function angleBetween(point1, point2) {
  return Math.atan2( point2.x - point1.x, point2.y - point1.y );
}

brushes.FurPen.onmousedown = function(e) {
  isDrawing = true;
  lastPoint = { x: e.clientX, y: e.clientY };
  ctx.lineJoin = ctx.lineCap = 'round';
};

brushes.FurPen.onmousemove = function(e) {
  if (!isDrawing) return;
  
  var currentPoint = { x: e.clientX, y: e.clientY };
  var dist = distanceBetween(lastPoint, currentPoint);
  var angle = angleBetween(lastPoint, currentPoint);
  
  for (var i = 0; i < dist; i++) {
    x = lastPoint.x + (Math.sin(angle) * i) - 25;
    y = lastPoint.y + (Math.cos(angle) * i) - 25;
    ctx.drawImage(img, x, y);
  }
  
  lastPoint = currentPoint;
};

brushes.FurPen.onmouseup = function() {
  isDrawing = false;
};

brushes.SlicedStrokes = {};
brushes.SlicedStrokes.onmousedown = function(e) {
  isDrawing = true;
  lastPoint = { x: e.clientX, y: e.clientY };
  ctx.lineWidth = 3;
  ctx.lineJoin = ctx.lineCap = 'round';
};

brushes.SlicedStrokes.onmousemove = function(e) {
  if (!isDrawing) return;

  ctx.beginPath();
  
  ctx.globalAlpha = 1;
  ctx.moveTo(lastPoint.x, lastPoint.y);
  ctx.lineTo(e.clientX, e.clientY);
  ctx.stroke();
  
  ctx.moveTo(lastPoint.x - 4, lastPoint.y - 4);
  ctx.lineTo(e.clientX - 4, e.clientY - 4);
  ctx.stroke();
  
  ctx.moveTo(lastPoint.x - 2, lastPoint.y - 2);
  ctx.lineTo(e.clientX - 2, e.clientY - 2);
  ctx.stroke();
  
  ctx.moveTo(lastPoint.x + 2, lastPoint.y + 2);
  ctx.lineTo(e.clientX + 2, e.clientY + 2);
  ctx.stroke();
  
  ctx.moveTo(lastPoint.x + 4, lastPoint.y + 4);
  ctx.lineTo(e.clientX + 4, e.clientY + 4);
  ctx.stroke();
    
  lastPoint = { x: e.clientX, y: e.clientY };
};

SlicedStrokes.onmouseup = function() {
  isDrawing = false;
};

brushes.MultiLine = {};
brushes.MultiLine.onmousedown = function(e) {
  isDrawing = true;
  points.push({ x: e.clientX, y: e.clientY });
  ctx.lineWidth = 1;
  ctx.lineJoin = ctx.lineCap = 'round';
};

brushes.MultiLine.onmousemove = function(e) {
  if (!isDrawing) return;
  
  points.push({ x: e.clientX, y: e.clientY });
  ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
  
  ctx.strokeStyle = 'rgba(0,0,0,1)';
  stroke(offsetPoints(-4));
  ctx.strokeStyle = 'rgba(0,0,0,0.8)';
  stroke(offsetPoints(-2));
  ctx.strokeStyle = 'rgba(0,0,0,0.6)';
  stroke(points);
  ctx.strokeStyle = 'rgba(0,0,0,0.4)';
  stroke(offsetPoints(2));
  ctx.strokeStyle = 'rgba(0,0,0,0.2)';
  stroke(offsetPoints(4));
};

function offsetPoints(val) {
  var offsetPoints = [ ];
  for (var i = 0; i < points.length; i++) {
    offsetPoints.push({ 
      x: points[i].x + val,
      y: points[i].y + val
    });
  }
  return offsetPoints;
}

function stroke(points) {
  var p1 = points[0];
  var p2 = points[1];
  
  ctx.beginPath();
  ctx.moveTo(p1.x, p1.y);

  for (var i = 1, len = points.length; i < len; i++) {
    var midPoint = midPointBtw(p1, p2);
    ctx.quadraticCurveTo(p1.x, p1.y, midPoint.x, midPoint.y);
    p1 = points[i];
    p2 = points[i+1];
  }
  ctx.lineTo(p1.x, p1.y);
  ctx.stroke();
}

brushes.MultiLine.onmouseup = function() {
  isDrawing = false;
  points.length = 0;
};


el.onmousedown = brushes.SmoothShadow.onmousedown;
//el.onmousedown = brushes.EdgeSmooting.onmousedown;
//el.onmousedown = brushes.Bezier.onmousedown;
//el.onmousedown = brushes.FurPen.onmousedown;
//el.onmousedown = brushes.SlicedStrokes.onmousedown;
//el.onmosuedown = brushes.MultiLine.onmousedown;
// >>>>>>> 48c6d8c0cb8d5856027c55bd8510e2289bbe1bb8
el.onmousemove = brushes.SimplePencil.onmousemove;
el.onmouseup = brushes.SimplePencil.onmouseup;

