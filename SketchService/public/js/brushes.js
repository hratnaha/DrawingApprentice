var buffer = document.getElementById('buffer');
var bffrCtx = buffer.getContext('2d');
var display = document.getElementById('display');
var dplCtx = display.getContext('2d');
var entire = document.getElementById('entire');
var entireCtx = entire.getContext('2d');
var brushes = {};
var isDrawing, points = [ ], lastPoint;;
var isPan;
var currentTime = new Date();
var lastClick = currentTime.getTime();

function midPointBtw(p1, p2) {
    return {
        x: p1.x + (p2.x - p1.x)/2,
        y: p1.y + (p2.y - p1.y)/2
    };
}


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
    console.log(e);
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

brushes.EdgeSmoothing = {};
brushes.EdgeSmoothing.onmousedown = function(e) {
  isDrawing = true;
  bffrCtx.moveTo(e.clientX, e.clientY);
};
brushes.EdgeSmoothing.onmousemove = function(e) {
  if (isDrawing) {
    var radgrad = bffrCtx.createRadialGradient(
      e.clientX,e.clientY,10,e.clientX,e.clientY,20);
    
    radgrad.addColorStop(0, '#000');
    radgrad.addColorStop(0.5, 'rgba(0,0,0,0.5)');
    radgrad.addColorStop(1, 'rgba(0,0,0,0)');
    bffrCtx.fillStyle = radgrad;
    
    bffrCtx.fillRect(e.clientX-20, e.clientY-20, 40, 40);
  }
};

brushes.Bezier = {};
brushes.Bezier.onmousedown = function(e) {
  isDrawing = true;
  points.push({ x: e.clientX, y: e.clientY });
  bffrCtx.lineWidth = 10;
  bffrCtx.lineJoin = bffrCtx.lineCap = 'round';
};

brushes.Bezier.onmousemove = function(e) {
  if (!isDrawing) return;
  
  points.push({ x: e.clientX, y: e.clientY });

  bffrCtx.clearRect(0, 0, bffrCtx.canvas.width, bffrCtx.canvas.height);
  
  var p1 = points[0];
  var p2 = points[1];
  
  bffrCtx.beginPath();
  bffrCtx.moveTo(p1.x, p1.y);
  console.log(points);

  for (var i = 1, len = points.length; i < len; i++) {
    var midPoint = midPointBtw(p1, p2);
    bffrCtx.quadraticCurveTo(p1.x, p1.y, midPoint.x, midPoint.y);
    p1 = points[i];
    p2 = points[i+1];
  }
  bffrCtx.lineTo(p1.x, p1.y);
  bffrCtx.stroke();
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
  bffrCtx.lineJoin = bffrCtx.lineCap = 'round';
};

brushes.FurPen.onmousemove = function(e) {
  if (!isDrawing) return;
  
  var currentPoint = { x: e.clientX, y: e.clientY };
  var dist = distanceBetween(lastPoint, currentPoint);
  var angle = angleBetween(lastPoint, currentPoint);
  
  for (var i = 0; i < dist; i++) {
    x = lastPoint.x + (Math.sin(angle) * i) - 25;
    y = lastPoint.y + (Math.cos(angle) * i) - 25;
    bffrCtx.drawImage(img, x, y);
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
  bffrCtx.lineWidth = 3;
  bffrCtx.lineJoin = bffrCtx.lineCap = 'round';
};

brushes.SlicedStrokes.onmousemove = function(e) {
  if (!isDrawing) return;

  bffrCtx.beginPath();
  
  bffrCtx.globalAlpha = 1;
  bffrCtx.moveTo(lastPoint.x, lastPoint.y);
  bffrCtx.lineTo(e.clientX, e.clientY);
  bffrCtx.stroke();
  
  bffrCtx.moveTo(lastPoint.x - 4, lastPoint.y - 4);
  bffrCtx.lineTo(e.clientX - 4, e.clientY - 4);
  bffrCtx.stroke();
  
  bffrCtx.moveTo(lastPoint.x - 2, lastPoint.y - 2);
  bffrCtx.lineTo(e.clientX - 2, e.clientY - 2);
  bffrCtx.stroke();
  
  bffrCtx.moveTo(lastPoint.x + 2, lastPoint.y + 2);
  bffrCtx.lineTo(e.clientX + 2, e.clientY + 2);
  bffrCtx.stroke();
  
  bffrCtx.moveTo(lastPoint.x + 4, lastPoint.y + 4);
  bffrCtx.lineTo(e.clientX + 4, e.clientY + 4);
  bffrCtx.stroke();
    
  lastPoint = { x: e.clientX, y: e.clientY };
};

brushes.SlicedStrokes.onmouseup = function() {
  isDrawing = false;
};

brushes.MultiLine = {};
brushes.MultiLine.onmousedown = function(e) {
  isDrawing = true;
  points.push({ x: e.clientX, y: e.clientY });
  bffrCtx.lineWidth = 1;
  bffrCtx.lineJoin = bffrCtx.lineCap = 'round';
};

brushes.MultiLine.onmousemove = function(e) {
  if (!isDrawing) return;
  
  points.push({ x: e.clientX, y: e.clientY });
  bffrCtx.clearRect(0, 0, bffrCtx.canvas.width, bffrCtx.canvas.height);
  
  bffrCtx.strokeStyle = 'rgba(0,0,0,1)';
  stroke(offsetPoints(-4));
  bffrCtx.strokeStyle = 'rgba(0,0,0,0.8)';
  stroke(offsetPoints(-2));
  bffrCtx.strokeStyle = 'rgba(0,0,0,0.6)';
  stroke(points);
  bffrCtx.strokeStyle = 'rgba(0,0,0,0.4)';
  stroke(offsetPoints(2));
  bffrCtx.strokeStyle = 'rgba(0,0,0,0.2)';
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
  
  bffrCtx.beginPath();
  bffrCtx.moveTo(p1.x, p1.y);

  for (var i = 1, len = points.length; i < len; i++) {
    var midPoint = midPointBtw(p1, p2);
    bffrCtx.quadraticCurveTo(p1.x, p1.y, midPoint.x, midPoint.y);
    p1 = points[i];
    p2 = points[i+1];
  }
  bffrCtx.lineTo(p1.x, p1.y);
  bffrCtx.stroke();
}

brushes.MultiLine.onmouseup = function() {
  isDrawing = false;
  points.length = 0;
};


buffer.onmousedown = brushes.SlicedStrokes.onmousedown;
buffer.onmousemove = brushes.SlicedStrokes.onmousemove;
buffer.onmouseup = brushes.SlicedStrokes.onmouseup;