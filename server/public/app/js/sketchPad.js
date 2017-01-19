//var canvas = "{}";
var selectedPalette = 'palette1';

function sketchUtil() {
    // get the canvas element and its context
    var container = document.getElementById('container');
    bothCanvas.setAttribute('width', container.offsetWidth);
    bothCanvas.setAttribute('height', container.offsetHeight);

    var both = document.getElementById('both');
    both.setAttribute('width', container.offsetWidth);
    both.setAttribute('height', container.offsetHeight);
    var bothCtx = both.getContext('2d');

    var canvas = document.getElementById('sketchpad');
    canvas.setAttribute('width', container.offsetWidth);
    canvas.setAttribute('height', container.offsetHeight);
    var context = canvas.getContext('2d');
    context.lineWidth = 1;
	
    var entire = document.getElementById('entire');
    entire.setAttribute('width', container.offsetWidth);
    entire.setAttribute('height', container.offsetHeight);
    entire.setAttribute('style', 'visibility:hidden');    
    var entireCtx = entire.getContext('2d');

    $("#opacity").click(function() {
        context.globalAlpha = this.value/100.;
        console.log(this.value/100.);
    });

    function trackTransforms(ctx){
        var svg = document.createElementNS("http://www.w3.org/2000/svg",'svg');
        var xform = svg.createSVGMatrix();
        ctx.getTransform = function(){ return xform; };
        
        var savedTransforms = [];
        var save = ctx.save;
        ctx.save = function(){
            savedTransforms.push(xform.translate(0,0));
            return save.call(ctx);
        };
        var restore = ctx.restore;
        ctx.restore = function(){
            xform = savedTransforms.pop();
            return restore.call(ctx);
        };

        var scale = ctx.scale;
        ctx.scale = function(sx,sy){
            xform = xform.scaleNonUniform(sx,sy);
            return scale.call(ctx,sx,sy);
        };
        var rotate = ctx.rotate;
        ctx.rotate = function(radians){
            xform = xform.rotate(radians*180/Math.PI);
            return rotate.call(ctx,radians);
        };
        var translate = ctx.translate;
        ctx.translate = function(dx,dy){
            xform = xform.translate(dx,dy);
            return translate.call(ctx,dx,dy);
        };
        var transform = ctx.transform;
        ctx.transform = function(a,b,c,d,e,f){
            var m2 = svg.createSVGMatrix();
            m2.a=a; m2.b=b; m2.c=c; m2.d=d; m2.e=e; m2.f=f;
            xform = xform.multiply(m2);
            return transform.call(ctx,a,b,c,d,e,f);
        };
        var setTransform = ctx.setTransform;
        ctx.setTransform = function(a,b,c,d,e,f){
            xform.a = a;
            xform.b = b;
            xform.c = c;
            xform.d = d;
            xform.e = e;
            xform.f = f;
            return setTransform.call(ctx,a,b,c,d,e,f);
        };
        var pt = svg.createSVGPoint();
        ctx.transformedPoint = function(x,y){
            pt.x=x; pt.y=y;
            return pt.matrixTransform(xform.inverse());
        }
    }
    trackTransforms(context);
    trackTransforms(entireCtx);
    trackTransforms(bothCtx);

    function redraw(){
        // Clear the entire canvas
        var p1 = bothCtx.transformedPoint(0,0);
        var p2 = bothCtx.transformedPoint(both.width,both.height);
        bothCtx.fillStyle = "#CCCCCC";
        bothCtx.fillRect(p1.x,p1.y,p2.x-p1.x,p2.y-p1.y);
        bothCtx.clearRect(0, 0, both.width, both.height);
        // find the portion of the entire canvas to draw on the display canvas
        bothCtx.drawImage(entire, 0, 0, both.width, both.height);
    }

    var scaleFactor = 1.1;
    var zoomPercentage = 1.0;
    function handlePinch (evt) { // zooming
        var clicks = evt.wheelDelta ? evt.wheelDelta/100 : 0;
        clicks = Math.round(clicks * 10) / 10;
        var factor = Math.pow(scaleFactor,clicks);
        if (zoomPercentage * factor < 5 && zoomPercentage * factor > 0.3) {
            zoomPercentage *= factor;
            y = y * factor; // change brush width
            var pt = entireCtx.transformedPoint(lastX,lastY);
            entireCtx.translate(pt.x,pt.y);
            bothCtx.translate(pt.x, pt.y);
            entireCtx.scale(factor,factor);
            bothCtx.scale(factor,factor);
            entireCtx.translate(-pt.x,-pt.y);
            bothCtx.translate(-pt.x,-pt.y);        
            redraw();
        }
        return evt.preventDefault() && false;
    }

    function handleScroll (evt){ // panning
        evt.preventDefault();
        entireCtx.translate(evt.deltaX, evt.deltaY);
        bothCtx.translate(evt.deltaX, evt.deltaY);
        redraw();
    };

    function handleMouseWheel (evt) {
        console.log(evt);
        if (Math.abs(evt.wheelDelta) == 120) {
            handlePinch(evt);
            console.log("zoom");
        } else {
            handleScroll(evt);
            console.log("pan");
        }
    }
    canvas.addEventListener('wheel',handleMouseWheel,false);

    var lastX = both.width/2, lastY = both.height/2;
    function moveCanvas (evt){
        lastX = evt.x; //|| (evt.pageX - canvas.offsetLeft);
        lastY = evt.y;// || (evt.pageY - canvas.offsetTop);
    }

    function checkInsideCanvas(pt) { // Check is point is inside drawable canvas
        return pt.x > 0 && pt.x < both.width && pt.y > 0 && pt.y < both.height;
    }

	if(roomId != ""){
        var img = new Image();
        img.onload = function(){
            bothInputContext.drawImage(img,0,0);
        };
        // img.src = "/DrawingApprentice/session_pic/" + roomId + ".png"; // adam server
        img.src = "/session_pic/" + roomId + ".png";  // local
    }
    
    var curstroke;
    var strCounter = 0, pkptCounter = 0;
    var colorline;
    $('.colorPalette').click(function() {
        var curPalette = document.getElementById(selectedPalette);
        curPalette.style.border = '5px solid grey';
        selectedPalette = this.id;
        document.getElementById(selectedPalette).style.border = '5px solid white';        
    });
    function createNewStroke() {
        strCounter++;
        pkptCounter = 0;
        var color =  hexToRgb(context.strokeStyle);
        var colorR = color.r / 255;
        var colorG = color.g / 255;
        var colorB = color.b / 255;
        
        var now = (new Date()).getTime();
        var userid = userData == "" ? "unknown" : userData.id;
        var newstroke = {
            // create a temporary stroke for sending
            type : "stroke",
            data : {
                timestamp : now,
                id : "stroke_" + strCounter,
                color : {
                    r: colorR,
                    g: colorG,
                    b: colorB,
                    a: opacity2
                },
                lineWidth : y,
                allPoints : []
            },
            userid : userid
        };
        return newstroke;
    }
    $('#brushesPanel').on('click','*',function(e) {
        if (brushType != this.id) {
            document.getElementById(brushType).style.marginTop = "30px";
        }
        brushType = this.id;
        document.getElementById(this.id).style.marginTop = "10px";
        e.stopPropagation();
    });

    function pushNewPacketPoint(coors) {
        var now = (new Date()).getTime();
        pkptCounter++;
        // construct a new packet point
        var pkpt = {
            id : "pt_" + pkptCounter,
            x : coors.x,
            y : coors.y,
            timestamp : now,
            pressure : pressurevalue,
            color: document.getElementById(selectedPalette).style.backgroundColor          //passing color in hex form
        };
        curstroke.data.allPoints.push(pkpt);
    }

    var points = [ ];
    var lastPoint;
    var brushType = 'Bezier';
    var img = new Image();
    img.src = 'http://www.tricedesigns.com/wp-content/uploads/2012/01/brush2.png';

    function distanceBetween(point1, point2) {
    return Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }
    function angleBetween(point1, point2) {
    return Math.atan2( point2.x - point1.x, point2.y - point1.y );
    }
    function hexToRgb(hex) {
        var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
        return result ? {
            r: parseInt(result[1], 16),
            g: parseInt(result[2], 16),
            b: parseInt(result[3], 16)
        } : null;
    }    
    // create a drawer which tracks touch movements
    var drawer = {
        isDrawing: false,
        touchstart: function (coors) {
            var pt = bothCtx.transformedPoint(coors.x, coors.y); // Current point on drawable canvas                
            colorline = document.getElementById('background').value;
            curstroke = createNewStroke();
			if ($('#group').hasClass("active")){
                context.setLineDash([5]);
                context.strokeStyle = document.getElementById(selectedPalette).style.backgroundColor;
            } else {
                colorline = document.getElementById('background').value;
                context.strokeStyle = document.getElementById(selectedPalette).style.backgroundColor;
                context.setLineDash([0]);
				context.lineWidth = y;
            }
            if (checkInsideCanvas(pt)) { // start drawing if inside canvas
                this.isDrawing = true;
                context.beginPath();
                context.moveTo(coors.x, coors.y);
                if (brushType == 'Sketch' || brushType == 'Rake'
                    || brushType == 'Ink' || brushType == 'Marker') {
                    lastPoint = { x: coors.x, y: coors.y };
                } else if (brushType == 'RealisticSketch') {
                    points = [ ];
                    points.push({ x: coors.x, y: coors.y });                    
                }
            }
        },
        touchmove: function (coors) {
            var pt = entireCtx.transformedPoint(coors.x, coors.y);
            if (this.isDrawing && checkInsideCanvas(pt)) {
                context.lineWidth = y;
                switch (brushType) {
                    case 'RealisticSketch':
                        context.lineWidth = 1;
                        points.push({ x: coors.x, y: coors.y });
                        context.beginPath();
                        context.moveTo(points[points.length - 2].x, points[points.length - 2].y);
                        context.lineTo(points[points.length - 1].x, points[points.length - 1].y);
                        context.stroke();                        
                        for (var i = 0, len = points.length; i < len; i++) {
                            dx = points[i].x - points[points.length-1].x;
                            dy = points[i].y - points[points.length-1].y;
                            d = dx * dx + dy * dy;
                            if (d < 1000) {
                            context.beginPath();
                            context.globalAlpha = 0.3;
                            context.moveTo( points[points.length-1].x + (dx * 0.2), points[points.length-1].y + (dy * 0.2));
                            context.lineTo( points[i].x - (dx * 0.2), points[i].y - (dy * 0.2));
                            context.stroke();
                            }
                        }
                        break;

                    case 'Sketch':
                        // context.lineWidth = 1;
                        context.lineJoin = context.lineCap = 'round';
                        context.beginPath();                        
                        context.moveTo(lastPoint.x - getRandomInt(0, 2), lastPoint.y - getRandomInt(0, 2));
                        context.lineTo(coors.x - getRandomInt(0, 2), coors.y - getRandomInt(0, 2));
                        context.stroke();                        
                        context.moveTo(lastPoint.x, lastPoint.y);
                        context.lineTo(coors.x, coors.y);
                        context.stroke();
                        context.moveTo(lastPoint.x + getRandomInt(0, 2), lastPoint.y + getRandomInt(0, 2));
                        context.lineTo(coors.x + getRandomInt(0, 2), coors.y + getRandomInt(0, 2));
                        context.stroke();                            
                        lastPoint = { x: coors.x, y: coors.y };
                        break;
                    
                    case 'Rake':
                        context.lineWidth = 1;
                        context.lineJoin = context.lineCap = 'round';
                        context.beginPath();
                        var lines = y/2;
                        for (i = 0; i < lines; i++) {
                            var offset = (-y/2) + (3*i);
                            context.moveTo(lastPoint.x + offset, lastPoint.y + offset);
                            context.lineTo(coors.x + offset, coors.y + offset);
                            context.stroke();
                        }
                        lastPoint = { x: coors.x, y: coors.y };
                        break;
                    
                    case 'Ink':
                        context.lineWidth = 2;
                        context.lineJoin = context.lineCap = 'round';
                        context.beginPath();
                        var lines = y/2;
                        for (i = 0; i < lines; i++) {
                            var offset = (-y/2) + (2*i);
                            context.moveTo(lastPoint.x + offset, lastPoint.y + offset);
                            context.lineTo(coors.x + offset, coors.y + offset);
                            context.stroke();
                        }
                        lastPoint = { x: coors.x, y: coors.y };
                        break;

                    case 'Marker':
                        context.lineWidth = 3;
                        context.lineJoin = context.lineCap = 'round';
                        context.beginPath();
                        var lines = y/2;
                        var alphaOffset = 1.0/lines;
                        for (i = 0; i < lines; i++) {
                            context.globalAlpha = 1.0-(i*alphaOffset);
                            var offset = (-y/2) + (2*i);
                            context.moveTo(lastPoint.x + offset, lastPoint.y + offset);
                            context.lineTo(coors.x + offset, coors.y + offset);
                            context.stroke();
                        }
                        lastPoint = { x: coors.x, y: coors.y };
                        break;
                        
                    case 'Oil':
                        context.lineJoin = context.lineCap = 'round';
                        context.shadowBlur = 10;
                        var r = hexToRgb(context.strokeStyle).r;
                        var g = hexToRgb(context.strokeStyle).g;
                        var b = hexToRgb(context.strokeStyle).b;
                        context.shadowColor = 'rgba('+r+','+g+','+b+',.25)';                    
                        context.lineTo(coors.x, coors.y);
                        context.stroke();                        
                        break;

                    case 'Bezier':
                        // points.push({ x: coors.x, y: coors.y });
                        context.lineJoin = context.lineCap = 'round';
                        points.push({ x: coors.x, y: coors.y });
                        context.clearRect(0, 0, canvas.width, canvas.height);
                        var p1 = points[0];
                        var p2 = points[1];                        
                        context.beginPath();
                        context.moveTo(p1.x, p1.y);
                        for (var i = 1, len = points.length; i < len; i++) {
                            var midPoint = midPointBtw(p1, p2);
                            context.quadraticCurveTo(p1.x, p1.y, midPoint.x, midPoint.y);
                            p1 = points[i];
                            p2 = points[i+1];
                        }
                        context.lineTo(p1.x, p1.y);
                        context.stroke();   
                        break;
                }
                var json_coor = JSON.stringify(coors); //converting to json
                pushNewPacketPoint(coors);
            }
            moveCanvas(coors);
        },
        touchend: function (coors) {
            if (this.isDrawing) {
                this.isDrawing = false;
                this.touchmove(coors);
                var stringStroke = JSON.stringify(curstroke);
                onTouchUp(stringStroke);
                var height = canvas.height;
                // draw the current brush from the buffer to the displaying canvas
                bothCtx.save();
                bothCtx.setTransform(1,0,0,1,0,0);
                bothCtx.drawImage(canvas, 0, 0);
                bothCtx.restore();
                // draw the last stroke onto the entire canvas
                entireCtx.save();
                var mtx = entireCtx.getTransform();
                mtx = mtx.inverse();
                entireCtx.setTransform(mtx.a, mtx.b, mtx.c, mtx.d, mtx.e, mtx.f);
                entireCtx.drawImage(canvas, 0, 0, canvas.width, canvas.height);
                entireCtx.restore();
                // clean the buffer context
                context.save();
                context.setTransform(1,0,0,1,0,0);
                context.clearRect(0,0,canvas.width,canvas.height);
                context.restore();
				//if (document.getElementById('grouping').clicked == true) {
                if (!$('#group').hasClass("active")) {
                    bothInputContext.drawImage(canvas, 0, 0);
                } else {
                    var label = prompt("Please enter the type of object you drew:", "Object");
                    setGroupLabel(label); 
                    changeGrouping();
                    context.clearRect(0, 0, canvas.width, canvas.height);
                }
                switch (brushType) {
                    case 'Marker':
                        context.globalAlpha = 1;
                    case 'Oil':
                        context.shadowBlur = 0;
                        break;
                    case 'Bezier':
                        points.length = 0;
                        break;
                }
            }
        }
    };
    function getRandomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }    
    function midPointBtw(p1, p2) {
        return {
            x: p1.x + (p2.x - p1.x)/2,
            y: p1.y + (p2.y - p1.y)/2
        };
    }

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
        context.beginPath();
        context.moveTo(p1.x, p1.y);
        for (var i = 1, len = points.length; i < len; i++) {
            var midPoint = midPointBtw(p1, p2);
            context.quadraticCurveTo(p1.x, p1.y, midPoint.x, midPoint.y);
            p1 = points[i];
            p2 = points[i+1];
        }
        context.lineTo(p1.x, p1.y);
        context.stroke();
    }
    
    // create a function to pass touch events and coordinates to drawer
    function draw(event) {
        var type = null;
        var timer = null;
        var now = new Date();
        var now_utc = new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate(), now.getUTCHours(), now.getUTCMinutes(), now.getUTCSeconds());
        
        // map mouse events to touch events
        switch (event.type) {
            case "mousedown":
                event.touches = [];
                event.touches[0] = {
                    pageX: event.pageX,
                    pageY: event.pageY
                };
                type = "touchstart";
                break;
            case "mousemove":
                event.touches = [];
                event.touches[0] = {
                    pageX: event.pageX,
                    pageY: event.pageY
                };
                type = "touchmove";
                
                break;
            case "mouseup":
                event.touches = [];
                event.touches[0] = {
                    pageX: event.pageX,
                    pageY: event.pageY
                };
                type = "touchend";
                clearTimeout(timer);
                break;            
        }
        
        // touchend clear the touches[0], so we need to use changedTouches[0]
        var coors;
        if (event.type === "touchend") {
            coors = {
                x: event.changedTouches[0].pageX - this.parentElement.offsetLeft,
                y: event.changedTouches[0].pageY - this.parentElement.offsetTop
            };
        }
        else {
            // get the touch coordinates
            coors = {
                x: event.touches[0].pageX - this.parentElement.offsetLeft,
                y: event.touches[0].pageY - this.parentElement.offsetTop
            };
        }
        type = type || event.type;
        // pass the coordinates to the appropriate handler
        drawer[type](coors);
    }
    
    // detect touch capabilities
    var touchAvailable = ('createTouch' in document) || ('ontouchstart' in window);
    
    // attach the touchstart, touchmove, touchend event listeners.
    if (touchAvailable) {
        canvas.addEventListener('touchstart', draw, false);
        canvas.addEventListener('touchmove', draw, false);
        canvas.addEventListener('touchend', draw, false);
    }
    // attach the mousedown, mousemove, mouseup event listeners.
    else {
        canvas.addEventListener('mousedown', draw, false);
        canvas.addEventListener('mousemove', draw, false);
        canvas.addEventListener('mouseup', draw, false);
    }
    
    // prevent elastic scrolling
    document.body.addEventListener('touchmove', function (event) {
        event.preventDefault();
    }, false); // end body.onTouchMove

}
//Pressure implementation
var pressurevalue = 0;
var log = function (msg) {
    $("<div>").text(msg).appendTo($("#log"));
    //console.log(msg);
};
var logtouch = function (evtype, t) {
    pressurevalue = t.webkitForce;
}

var mouseIsDown = false;
function pressure1() {
    $(document).on("mousemove", function () { log("mousemove"); });
    $(document).on("touchstart touchmove touchend touchcancel", function (ev) {3
        $.each(ev.originalEvent.touches, function (i, t) { logtouch(ev.type + "-touches", t); });
        $.each(ev.originalEvent.changedTouches, function (i, t) { logtouch(ev.type + "-changed", t); });
        $.each(ev.originalEvent.targetTouches, function (i, t) { logtouch(ev.type + "-target", t); });
    });
}
pressure1();

