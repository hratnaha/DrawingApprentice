//var canvas = "{}";

function sketchUtil() {
	
    // get the canvas element and its context
    var container = document.getElementById('container');
    bothCanvas.setAttribute('width', container.offsetWidth);
    bothCanvas.setAttribute('height', container.offsetHeight);

    var canvas = document.getElementById('sketchpad');
    canvas.setAttribute('width', container.offsetWidth);
    canvas.setAttribute('height', container.offsetHeight);
    var context = canvas.getContext('2d');
    context.lineWidth = 1;
	
	if(roomId != ""){
        var img = new Image();
        img.onload = function(){
            bothInputContext.drawImage(img,0,0);
        };
        img.src = "/session_pic/" + roomId + ".png";
    }
    
    var curstroke;
    var strCounter = 0, pkptCounter = 0;
    var colorline;
    
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
            color: tipColor          //passing color in hex form
        };
        
        curstroke.data.allPoints.push(pkpt);
    }
    
	
    // create a drawer which tracks touch movements
    var drawer = {
        isDrawing: false,
        touchstart: function (coors) {
            colorline = document.getElementById('background').value;
            curstroke = createNewStroke();
			if ($('#group').hasClass("active")){
            //if ($("#cboxGrouping").attr('checked') == "checked") {
                //alert("grouping checked");
                console.log("Grouping checked"); 
                context.setLineDash([5]);
                context.strokeStyle = tipColor;
				//console.log(tipColor);
				context.globalAlpha = opacity2;
				
	
            } else {
                colorline = document.getElementById('background').value;
                context.strokeStyle = tipColor;
                context.setLineDash([0]);
				context.lineWidth = y;
				context.globalAlpha = opacity2;
			
            }
            curstroke = createNewStroke();
            context.beginPath();
            context.moveTo(coors.x, coors.y);
            this.isDrawing = true;
        },
        touchmove: function (coors) {
            if (this.isDrawing) {
                context.lineTo(coors.x, coors.y);
                context.stroke();
				context.lineWidth = y;
				context.globalAlpha = opacity2;
                var json_coor = JSON.stringify(coors); //converting to json
                pushNewPacketPoint(coors);
            }
        },
        touchend: function (coors) {
            if (this.isDrawing) {
                this.touchmove(coors);
                var stringStroke = JSON.stringify(curstroke);
                onTouchUp(stringStroke);
                var height = canvas.height;

                this.isDrawing = false;
				//if (document.getElementById('grouping').clicked == true) {
                if (!$('#group').hasClass("active")) {
                    bothInputContext.drawImage(canvas, 0, 0);
                }
                else {
                    var label = prompt("Please enter the type of object you drew:", "Object");
                    changeGrouping();
                    context.clearRect(0, 0, canvas.width, canvas.height);
                }
            }
        }
    };
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
    console.log(msg);
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

