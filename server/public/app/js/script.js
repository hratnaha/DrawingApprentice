
//CANVAS JAVASCTIPT
var thickness = 0;
var y = 2;
var opacity = 1;
var opacity2 = opacity/10;
var lineThickness;

$( document ).ready(function() {
console.log( "ready!" );


$(".saveForm").click(function AddWhiteBorder(){
	//$(this).css({
	//	'border':'3px solid #4e4e4e',
	//	'border-radius':'100%'});
	$(".saveForm").not(this).css({
		'border':'none'
		});
});

	
$("#color1").click(function AddBorder(){
			$(this).css({
				'border':'3px solid #B40431',
				'border-radius':'100%'});
			tipColor = "red";
			console.log(tipColor);
			//ctx.strokeStyle = tipColor;
	});
	
	
	
$("#color2").click(function AddBorder(){
		$(this).css({
				'border':'3px solid #FA8258',
				'border-radius':'100%'});
			tipColor = "#ff6666";
			console.log(tipColor);
			//ctx.strokeStyle = tipColor;
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

