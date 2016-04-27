
//CANVAS JAVASCTIPT
var thickness = 0;
var y = 2;
var opacity = 1;
var opacity2 = opacity/10;
var lineThickness;

$( document ).ready(function() {
console.log( "ready!" );



$(".colorBtn").click(function AddWhiteBorder(){
	//$(this).css({
	//	'border':'3px solid #4e4e4e',
	//	'border-radius':'100%'});
	$(".colorBtn").not(this).css({
		'border':'none'
		});
});

	
$("#color1").click(function AddBorder(){
			$(this).css({
				'border':'4px solid #B40431'});
			tipColor = "red";
			console.log(tipColor);
			//ctx.strokeStyle = tipColor;
});


$("#color2").click(function AddBorder(){
			$(this).css({
				'border':'4px solid #FF8000'});
			tipColor = "orange";
			console.log(tipColor);
			//ctx.strokeStyle = tipColor;
});


$("#color3").click(function AddBorder(){
			$(this).css({
				'border':'4px solid #F7FE2E'});
			tipColor = "yellow";
			console.log(tipColor);
			//ctx.strokeStyle = tipColor;
});

$("#color4").click(function AddBorder(){
			$(this).css({
				'border':'4px solid #81F781'});
			tipColor = "green";
			console.log(tipColor);
			//ctx.strokeStyle = tipColor;
});

$("#color5").click(function AddBorder(){
			$(this).css({
				'border':'4px solid #A901DB'});
			tipColor = "purple";
			console.log(tipColor);
			//ctx.strokeStyle = tipColor;
});


$("#color6").click(function AddBorder(){
			$(this).css({
				'border':'4px solid #2E64FE'});
	
});

$("#color7").click(function AddBorder(){
			$(this).css({
				'border':'4px solid #FFFFFF'});
	
});
	
	
$("#color8").click(function AddBorder(){
			$(this).css({
				'border':'4px solid #A4A4A4'});
	
});


$("#color9").click(function AddBorder(){
			$(this).css({
				'border':'3px solid #000000'});
	
});
	
	

	
	

	
$("#color3").click(function Yellow(){
		tipColor = "#FFFF33";
		console.log(tipColor);
		//ctx.strokeStyle = tipColor;
	});
	
$("#color4").click(function Green(){
		tipColor = "#33CC33";
		console.log(tipColor);
		//ctx.strokeStyle = tipColor;
	});
	
$("#color5").click(function Purple(){
		tipColor = "#9933CC";
		console.log(tipColor);
		//ctx.strokeStyle = tipColor;
	});
	
	
$("#color6").click(function Blue(){
		tipColor = "#3366FF";
		console.log(tipColor);
		//ctx.strokeStyle = tipColor;
	});
	
$("#color7").click(function White(){
		tipColor = "#FFFFFF ";
		console.log(tipColor);
		//ctx.strokeStyle = tipColor;
	});
	
$("#color8").click(function Grey(){
		tipColor = "#A8A8A8";
		console.log(tipColor);
		//ctx.strokeStyle = tipColor;
	});
	
$("#color9").click(function Black(){
		tipColor = "#000000";
		console.log(tipColor);
		//ctx.strokeStyle = tipColor;
	});

console.log("saveForms all loaded");


$( "#toggle_button" ).click(function() {
  $('#toggle_button').css('margin-top', '-0.5%');
  $( "#toggle" ).slideToggle( "fast", function() {
    // Animation complete.
    if($('#toggle').css('display') == 'none'){
		$('#toggle_button').css('margin-top', '10px;');
        }
	else{
		$('#toggle_button').css('margin-top','10%');
		}
	});	
});
 



$(function AdjustOpacitySlider() {
	var slider = $("#opacity_slider").slider({    
			min: 0,
			max: 100,
			step: 5,
			value: 80,
			range: "min",  
		    
			create: function(event, ui) {
					var tooltip = $('<div class="tooltip"/>');
					//$(event.target).find('.ui-slider-handle').append(tooltip);
					//$(ui.handle).find('.tooltip').text(ui.value);
					$('.badge').text(ui.value);
					},
					
			slide: function(event, ui) {
					$("#opacity_slider").val(ui.value);
					$('.badge').text(ui.value);
					//$(ui.handle).find('.tooltip').text(ui.value);
					opacity = $("#opacity_slider").slider('value');
					console.log(opacity);
				   },
			change: function(event, ui) {
					$('#hidden').attr('value', ui.value);
					opacity = $("#opacity_slider").slider('value');
					console.log(opacity);
					$('.badge').text(ui.value);
					}
		
		});
	
		console.log(opacity);
});
	
	
$(function AdjustLineThickness() {
		/*$("#line_thickness_slider").slider(
			{step:1,
			min:1,
			max:5,
			value:1,
			slide: function (event, ui){
			$("#line_thickness_slider").val(ui.value);
			$("#hiddenline_thickness_slider").val(ui.value);
					},
			change: function(event, ui){
			if(ui.value == 1){
			$('#brush1').show();
			$('#brush2').hide();
			$('#brush3').hide();
			$('#brush4').hide();
			$('#brush5').hide();
			y = 0.5;
			
					}
								
			else if(ui.value == 2) {
			$('#brush1').hide();
			$('#brush2').show();
			$('#brush3').hide();
			$('#brush4').hide();
			$('#brush5').hide();
			//ctx.lineWidth = 7;
			y = 2
					}
			else if(ui.value == 3){
			$('#brush1').hide();
			$('#brush2').hide();
			$('#brush3').show();
			$('#brush4').hide();
			$('#brush5').hide();	
			y = 4;
					}
			else if(ui.value == 4){
			$('#brush1').hide();
			$('#brush2').hide();
			$('#brush3').hide();
			$('#brush4').show();
			$('#brush5').hide();	
			y = 6;
					}
			else if(ui.value == 5){
			$('#brush1').hide();
			$('#brush2').hide();
			$('#brush3').hide();
			$('#brush4').hide();
			$('#brush5').show();
			y = 8;
					}					
	  		}//change function

   });*/
   
   $('#ex15').slider().on('slideStop', function(ev){
						//ctx.lineWidth = ev.value*20;
						//lineThickness = ev.value*3;
						//ctx.lineWidth = ev.value*20;
						//ctx2.lineWidth = ev.value*20;
						y = ev.value/3;
						//ctx.lineWidth = y/2;
						//y = ev.value*3;
						//console.log( 'Current Thickness Value:' + ' ' + ctx.lineWidth);				
	});	









						
});

			

$("#trash").click(function(){
	clearCanvas();
});



$("#download").click(function(){
	save();
});



$("#toggle").click(function(){
        $("#user-panel").toggle();
    });

function FullScreenCanvas() {
        		var canvas = document.getElementById('can'),
                context = canvas.getContext('2d');

        // resize the canvas to fill browser window dynamically
        		window.addEventListener('resize', resizeCanvas, false);
        
        	function resizeCanvas() {
                canvas.width = window.innerWidth-10;
                canvas.height = window.innerHeight-10;

                /**
                 * Your drawings need to be inside this function otherwise they will be reset when 
                 * you resize the browser window and the canvas goes will be cleared.
                 */
        	}
			$("#canvas").offset({ top: 0, left: 0 });
			
}




}); //document ready
	 
function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}

function rgbToHex(r, g, b) {
    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}

function rgbDoubleToHex(r, g, b) {
    r = r * 255;
    g = g * 255;
    b = b * 255;
    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}

function hexToRgb(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
}

function clearcanvas() {
		alert("clear canvas!!!");
        myCanvasContext1.clearRect(0, 0, canvas.width, canvas.height);
		myCanvasContext2.clearRect(0, 0, canvas.width, canvas.height);
		myCanvasContext3.clearRect(0, 0, canvas.width, canvas.height);
}

$("#grouping").click(function(){
	if($("#grouping").hasClass("isGrouping")){
		$("#grouping").removeClass("isGrouping");
		groupingMode(false);
	}
	else{
		$("#grouping").addClass("isGrouping");
		groupingMode(true);
	}
});

var bottomToggleBoolean = false;

var toggleBottomUi = function() {
	var bottomUiDiv = $('#bottomUiDiv');
	var colorsUi = $('<div onclick="" class="colorUi"><ul><li><button onclick="" class="btn btn-lg btn-default clickable">Color 1</button></li><li><button onclick="" class="btn btn-lg btn-default">Color 2</button></li><li><button class="btn btn-lg btn-default">Color 3</button></li><li><button class="btn btn-lg btn-default">Color 4</button></li><li><button class="btn btn-lg btn-default">Color 5</button></li></ul></div>');
	console.log(colorsUi);
	
	if (bottomToggleBoolean) {
		bottomToggleBoolean = false;
		bottomUiDiv.empty();
		colorsUi.click(function(){});

	} else {
		bottomToggleBoolean = true;
		bottomUiDiv.append(colorsUi);
		colorsUi.click(function(e) {
		e.stopPropagation();
		});
	}
}


//Toggle
var theToggle = document.getElementById('toggle');

// based on Todd Motto functions
// http://toddmotto.com/labs/reusable-js/

// hasClass
function hasClass(elem, className) {
	return new RegExp(' ' + className + ' ').test(' ' + elem.className + ' ');
}
// addClass
function addClass(elem, className) {
    if (!hasClass(elem, className)) {
    	elem.className += ' ' + className;
    }
}
// removeClass
function removeClass(elem, className) {
	var newClass = ' ' + elem.className.replace( /[\t\r\n]/g, ' ') + ' ';
	if (hasClass(elem, className)) {
        while (newClass.indexOf(' ' + className + ' ') >= 0 ) {
            newClass = newClass.replace(' ' + className + ' ', ' ');
        }
        elem.className = newClass.replace(/^\s+|\s+$/g, '');
    }
}
// toggleClass
function toggleClass(elem, className) {
	var newClass = ' ' + elem.className.replace( /[\t\r\n]/g, " " ) + ' ';
    if (hasClass(elem, className)) {
        while (newClass.indexOf(" " + className + " ") >= 0 ) {
            newClass = newClass.replace( " " + className + " " , " " );
        }
        elem.className = newClass.replace(/^\s+|\s+$/g, '');
    } else {
        elem.className += ' ' + className;
    }
}

theToggle.onclick = function() {
   toggleClass(this, 'on');
   return false;
}


//Slider 
$(function() {
 
    var slider = $('#slider'),
        tooltip = $('.tooltip');
 
    tooltip.hide();
 
    slider.slider({
        range: "min",
        min: 1,
        value: 20,
 
        start: function(event,ui) {
          tooltip.fadeIn('fast');
        },
 
        slide: function(event, ui) {
 
            var value = slider.slider('value'),
                volume = $('.volume');
			
			console.log("ui.value is "+ ui.value);
 
            tooltip.css('left', value*4.5).text(ui.value); //have to scale value * n
 
            if(value <= 33) { 
                volume.css('background-position', '0 -15px');
            } 
            else if (value <= 66 && value > 33) {
                volume.css('background-position', '0 -110px');
            } 
            else if (value > 66 && value <= 100) {
                volume.css('background-position', '0 -220px');
            };
           // else {
           //     volume.css('background-position', '0 -75px');
           // }
 
        },
 
        stop: function(event,ui) {
          tooltip.fadeOut('fast');
        },
    });
 
});




//Slider -thickness
$(function() {
 
    var sliderThickness = $('#slider-thickness'),
        tooltip = $('.tooltip-thickness');
 
    tooltip.hide();
 
    sliderThickness.slider({
        range: "min",
        min: 1,
		max: 20,
        value: 5,
 
        start: function(event,ui) {
          tooltip.fadeIn('fast');
        },
 
        stop: function(event, ui) {
 
            lineThickness = sliderThickness.slider('value'),
                thickness = $('.thickness');
			console.log("Thickness is: " +  lineThickness);
			y = lineThickness;  
 
            tooltip.css('left', lineThickness).text(ui.value); //have to scale value * n
 
           /* if(value <= 33) { 
                thickness.css('background-position', '0 -15px');
            } 
            else if (value <= 66 && value > 33) {
                thickness.css('background-position', '0 -110px');
            } 
            else if (value > 66 && value <= 100) {
                thickness.css('background-position', '0 -220px');
            };*/
           // else {
           //     volume.css('background-position', '0 -75px');
           // }
 
        },
 
        change: function(event,ui) {
          tooltip.fadeOut('fast');
        },
    });
 
});




btnTracing = $("#btnTracing img");
btnMimicking = $("#btnMimicking img");
btnTransforming = $("#btnTransforming img");
btnObjRec = $("#btnObjRec img");


btnTracing.click(function(){
	  ChooseCreativity(1);
	  var ending = btnTracing.attr('src').slice(-3); //, src.slice( -3 );
    switch( ending ) {
       case 'jpg': 
           btnTracing.attr('src',"images/Tracing.gif"); //= this.attr('src').replace( /jpg$/, 'gif' );
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRec.attr('src','images/object-recognition.jpg')
           break;
       case 'gif': 
           btnTracing.attr('src',"images/Tracing.jpg");
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRec.attr('src','images/object-recognition.jpg')
           break;
    }
});


btnMimicking.click(function(){
	ChooseCreativity(2);
	  var ending = btnMimicking.attr('src').slice(-3); //, src.slice( -3 );
    switch( ending ) {
       case 'jpg': 
           btnTracing.attr('src',"images/Tracing.jpg"); 
		   btnMimicking.attr('src',"images/Mimicking.gif");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRec.attr('src','images/object-recognition.jpg')
           break;
       case 'gif': 
           btnTracing.attr('src',"images/Tracing.jpg");
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRec.attr('src','images/object-recognition.jpg')
           break;
    }
});

btnTransforming.click(function(){
	ChooseCreativity(3);
	  var ending = btnTransforming.attr('src').slice(-3); //, src.slice( -3 );
    switch( ending ) {
       case 'jpg': 
           btnTracing.attr('src',"images/Tracing.jpg"); 
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.gif");
		   btnObjRec.attr('src','images/object-recognition.jpg')
           break;
       case 'gif': 
           btnTracing.attr('src',"images/Tracing.jpg");
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRec.attr('src','images/object-recognition.jpg')
           break;
    }
});


//Case Button Object Recognition
btnObjRec.click(function(){
	ChooseCreativity(3);
	  var ending = btnObjRec.attr('src').slice(-3); //, src.slice( -3 );
    switch( ending ) {
       case 'jpg': 
           btnTracing.attr('src',"images/Tracing.jpg"); 
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRec.attr('src','images/object-recognition.gif')
           break;
       case 'gif': 
           btnTracing.attr('src',"images/Tracing.jpg");
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRec.attr('src','images/object-recognition.jpg')
           break;
    }
});








/*$('img.img-toggle').click(function() {
    var ending = this.src.slice( -3 );
    switch( ending ) {
       case 'jpg': 
           this.src = this.src.replace( /jpg$/, 'gif' );
           break;
       case 'gif': 
           this.src = this.src.replace( /gif$/, 'jpg' );
           break;
    }
});*/




 $("#ex8").slider({
			tooltip: 'always'
	});
	
// Without JQuery
var slider = new Slider("#ex8", {
	tooltip: 'always'
});
				
var slider = new Slider("#ex15", {
	tooltip: 'always'
});
	

function AppendSuccessMsg(){
	$(".alert").addClass("alert-success");
		
}

function AppendSuccessMsg(){
	$(".alert").addClass("alert-success");
		
}


function upVoteBounce(){
	$('#logo img').attr("src","images/buddy_confirmation.gif");
	}
function downVotePouty(){
	$('#logo img').attr("src","images/buddy_pouty.gif");
	
	}