var lastX = display.width/2, lastY = display.height/2;
var scaleFactor = 1.1;
var zoomPercentage = 0.0;
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

    var pt  = svg.createSVGPoint();
    ctx.transformedPoint = function(x,y){
        pt.x=x; pt.y=y;
        console.log(xform.inverse());
        return pt.matrixTransform(xform.inverse());
    }
}

trackTransforms(dplCtx);
trackTransforms(entireCtx);
trackTransforms(bffrCtx);

function redraw(){
    // Clear the entire canvas
    var p1 = dplCtx.transformedPoint(0,0);
    var p2 = dplCtx.transformedPoint(display.width,display.height);
    dplCtx.fillStyle = "#CCCCCC";
    dplCtx.fillRect(p1.x,p1.y,p2.x-p1.x,p2.y-p1.y);
    dplCtx.clearRect(0, 0, display.width, display.height);

    // find the portion of the entire canvas to draw on the display canvas
    dplCtx.drawImage(entire, 0, 0, display.width, display.height);
}

var zoom = function(clicks){
    clicks = Math.round(clicks * 10) / 10;
    var factor = Math.pow(scaleFactor,clicks);
    if (zoomPercentage + clicks < 10
        && zoomPercentage + clicks > -10) {
        zoomPercentage += clicks;
        var pt = entireCtx.transformedPoint(lastX,lastY);
        entireCtx.translate(pt.x,pt.y);
        dplCtx.translate(pt.x, pt.y);
        
        entireCtx.scale(factor,factor);
        dplCtx.scale(factor,factor);
        entireCtx.translate(-pt.x,-pt.y);
        dplCtx.translate(-pt.x,-pt.y);
        
        redraw();
   }
}

var handleScroll = function(evt){
    var delta = evt.wheelDelta ? evt.wheelDelta/100 : 0;
	if (delta) zoom(delta);
	return evt.preventDefault() && false;
};

function moveCanvas (evt){
    console.log(evt);
    lastX = evt.offsetX; //|| (evt.pageX - canvas.offsetLeft);
    lastY = evt.offsetY;// || (evt.pageY - canvas.offsetTop);
}

buffer.addEventListener('mousewheel',handleScroll,false);
