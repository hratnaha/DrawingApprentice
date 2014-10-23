var canvas = "{}";
   
function sketchUtil() {
    // get the canvas element and its context
    var container = document.getElementById('container');
    canvas = document.getElementById('sketchpad');
    canvas.setAttribute('width', container.offsetWidth * 0.95);
    canvas.setAttribute('height', container.offsetHeight * 0.90);
    var context = canvas.getContext('2d');
    var curstroke;
    var strCounter = 0, pkptCounter = 0;
    var colorline;

    function createNewStroke(){                
        strCounter++;
        pkptCounter = 0;

        var now = (new Date()).getTime();
        var newstroke = {                       // create a temporary stroke for sending
            type : "stroke",
            data : {
                timestamp : now,
                id : "stroke_" + strCounter,
                color : {
                        r: 1,
                        g: 0,
                        b: 0,
                        a: 1
                    },
                packetPoints : []
            }
        };
        return newstroke;
    }
    function pushNewPacketPoint(coors){
        var now = (new Date()).getTime();
        pkptCounter++;
        // construct a new packet point
        var pkpt = {
                        id : "pt_" + pkptCounter,
                        x : coors.x,
                        y : coors.y,
                        timestamp : now,
                        pressure : pressurevalue,
                        color: colorline          //passing color in hex form
                    };

        curstroke.data.packetPoints.push(pkpt);
    }

    // create a drawer which tracks touch movements
    var drawer = {
        isDrawing: false,
        touchstart: function (coors) {
            curstroke = createNewStroke();

            context.beginPath();
            context.moveTo(coors.x, coors.y);
            this.isDrawing = true;
        },
        touchmove: function (coors) {
            if (this.isDrawing) {
                context.lineTo(coors.x, coors.y);
                context.stroke();
                colorline = document.getElementById('background').value;
                context.strokeStyle=colorline;
                var json_coor =  JSON.stringify(coors); //converting to json
                pushNewPacketPoint(coors);

            }

        },
        touchend: function (coors) {
            if (this.isDrawing) {
                this.touchmove(coors);

                var stringStroke = JSON.stringify(curstroke);
                doSend(stringStroke);
                this.isDrawing = false;
            }
        }
};
    // create a function to pass touch events and coordinates to drawer
    function draw(event) {
        var type = null;
        var timer = null;

        var now = new Date();
        var now_utc = new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate(),  now.getUTCHours(), now.getUTCMinutes(), now.getUTCSeconds());

        // map mouse events to touch events
        switch(event.type){
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
                    clearTimeout( timer );
            break;
            
        }
        
        // touchend clear the touches[0], so we need to use changedTouches[0]
        var coors;
        if(event.type === "touchend") {
            coors = {
                x: event.changedTouches[0].pageX,
                y: event.changedTouches[0].pageY
            };
        }
        else {
            // get the touch coordinates
            coors = {
                x: event.touches[0].pageX,
                y: event.touches[0].pageY
            };
        }
        type = type || event.type;
        // pass the coordinates to the appropriate handler
        drawer[type](coors);
    }
    
    // detect touch capabilities
    var touchAvailable = ('createTouch' in document) || ('ontouchstart' in window);
    
    // attach the touchstart, touchmove, touchend event listeners.
    if(touchAvailable){
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
    $(document).on("touchstart touchmove touchend touchcancel", function (ev) {
        $.each(ev.originalEvent.touches, function (i, t) { logtouch(ev.type + "-touches", t); });
        $.each(ev.originalEvent.changedTouches, function (i, t) { logtouch(ev.type + "-changed", t); });
        $.each(ev.originalEvent.targetTouches, function (i, t) { logtouch(ev.type + "-target", t); });
    });
}
pressure1();