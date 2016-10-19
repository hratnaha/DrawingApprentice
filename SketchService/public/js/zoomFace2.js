var canvas = document.getElementsByTagName('canvas')[0];
canvas.style.background = 'pink';
var canvas2 = document.getElementById('canvas2');
var canvas3 = document.getElementById('canvas3');
var ctx = canvas.getContext('2d');
var ctx2 = canvas2.getContext('2d');  
var ctx3 = canvas3.getContext('2d');

// Display the reverse translated object on canvas3 to buffer (canvas2)
copyToBuffer = function() {
    ctx2.save();
    ctx2.setTransform(1,0,0,1,0,0);
    //ctx2.clearRect(0,0,canvas.width,canvas.height);
    ctx2.restore();
    ctx2.drawImage(canvas3, 0, 0);
}

window.onload = function() {
    var lastX=canvas.width/2, lastY=canvas.height/2;
    var dragStart,dragged;

    // // Change what point you zoom in on
    // canvas.addEventListener('mousemove',function(evt){
    //     lastX = evt.offsetX || (evt.pageX - canvas.offsetLeft);
    //     lastY = evt.offsetY || (evt.pageY - canvas.offsetTop);
    //     dragged = true;
    //     if (dragStart){
    //         var pt = ctx.transformedPoint(lastX,lastY);
    //         ctx.translate(pt.x-dragStart.x,pt.y-dragStart.y);
    //         redraw();
    //     }
    //     return evt.preventDefault() && false;        
    // });
    
    // Return mouse position to original canvas context after zoom
    canvas.addEventListener('mouseup',function(evt){
        ctx.setTransform(1,0,0,1,0,0);
        return evt.preventDefault() && false;                
    });

    // Return mouse position to original canvas context after zoom
    canvas.addEventListener('mousedown',function(evt){
        ctx.setTransform(1,0,0,1,0,0);
        return evt.preventDefault() && false;                
    });

    // Animation
    trackTransforms(ctx);
    function redraw(){
        ctx.save();  // Save context transformed in zoom()
        ctx.setTransform(1,0,0,1,0,0);  // Revert back to original canvas to clear it
        ctx.clearRect(0,0,canvas.width,canvas.height);
        ctx.restore();  // Go back to the context created in zoom()
        ctx.drawImage(canvas2, 0, 0);  // Display from the buffer canvas onto the new context

        ctx3.save();
        ctx3.setTransform(1,0,0,1,0,0);
        ctx3.clearRect(0,0,canvas.width,canvas.height);
        ctx3.restore(); 
        ctx3.drawImage(canvas2, 0, 0);
        
    }

    // Scale/transform the new context when scroll is called
    var scaleFactor = 1.1;
    var zoom = function(clicks){
        //this is storing where to zoom from 
        //insert code here to change where it is zooming from 
        var pt = ctx.transformedPoint(lastX,lastY);

        console.log("X: " + pt.x + ", Y:" + pt.y);
        ctx.translate(pt.x,pt.y);
        var factor = Math.pow(scaleFactor,clicks);
        ctx.scale(factor,factor);
        ctx.translate(-pt.x,-pt.y);

        // reverse on canvas3
        //ctx3.translate(pt.x,pt.y);
        ctx3.scale(1/factor,1/factor);
        //ctx3.translate(-pt.x,-pt.y);
        redraw();
    }

    var handleScroll = function(evt){
        zoom(evt.wheelDelta/1000);
        return evt.preventDefault() && false;
    };
    canvas.addEventListener('DOMMouseScroll',handleScroll,false);
    canvas.addEventListener('mousewheel',handleScroll,false);
};

// Adds ctx.getTransform() - returns an SVGMatrix
// Adds ctx.transformedPoint(x,y) - returns an SVGPoint
function trackTransforms(ctx){
    var svg = document.createElementNS("http://www.w3.org/2000/svg",'svg');
    var xform = svg.createSVGMatrix();

    var scale = ctx.scale;
    ctx.scale = function(sx,sy){
        xform = xform.scaleNonUniform(sx,sy);
        return scale.call(ctx,sx,sy);
    };

    var translate = ctx.translate;
    ctx.translate = function(dx,dy){
        xform = xform.translate(dx,dy);
        return translate.call(ctx,dx,dy);
    };
    var pt  = svg.createSVGPoint();
    ctx.transformedPoint = function(x,y){
        pt.x=x; pt.y=y;
        return pt.matrixTransform(xform.inverse());
    }
}
