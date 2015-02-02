
//CANVAS JAVASCTIPT
var thickness = 0;
var x = "#000000";

$( document ).ready(function() {
console.log( "ready!" );


$(".saveForm").click(function AddWhiteBorder(){
	$(this).css({
		'border':'3px solid white',
		'border-radius':'15px'});
	$(".saveForm").not(this).css({
		'border':'none'
		});

	});

	
$("#saveForm1").click(function Red(){
		x = "red";
		console.log(x);
		ctx.strokeStyle = x;
	});
	
$("#saveForm2").click(function Peach(){
		x = "#FF6666";
		console.log(x);
		ctx.strokeStyle = x;
	});
	
$("#saveForm3").click(function Yellow(){
		x = "#FFFF33";
		console.log(x);
		ctx.strokeStyle = x;
	});
	
$("#saveForm4").click(function Green(){
		x = "#33CC33";
		console.log(x);
		ctx.strokeStyle = x;
	});
	
$("#saveForm5").click(function Purple(){
		x = "#9933CC";
		console.log(x);
		ctx.strokeStyle = x;
	});
	
	
$("#saveForm6").click(function Blue(){
		x = "#3366FF";
		console.log(x);
		ctx.strokeStyle = x;
	});
	
$("#saveForm7").click(function White(){
		x = "#FFFFFF ";
		console.log(x);
		ctx.strokeStyle = x;
	});
	
$("#saveForm8").click(function Grey(){
		x = "#A8A8A8";
		console.log(x);
		ctx.strokeStyle = x;
	});
	
$("#saveForm9").click(function Black(){
		x = "#000000";
		console.log(x);
		ctx.strokeStyle = x;
	});


$(function AdjustOpacitySlider() {
	var slider = $("#opacity_slider").slider({    
			min: 0,
			max: 1,
			step: 0.1,
			value: 0.5,
			range: "min",  
		    
			create: function(event, ui) {
					var tooltip = $('<div class="tooltip"/>');
					$(event.target).find('.ui-slider-handle').append(tooltip);
					$(ui.handle).find('.tooltip').text(ui.value);
					},
					
			slide: function(event, ui) {
					$("#opacity_slider").val(ui.value);
					$(ui.handle).find('.tooltip').text(ui.value);
				   },
			change: function(event, ui) {
					$('#hidden').attr('value', ui.value);
					}
		});
});
	
$(function AdjustLineThickness() {
		$("#line_thickness_slider").slider(
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
					}
								
			else if(ui.value == 2) {
			$('#brush1').hide();
			$('#brush2').show();
			$('#brush3').hide();
			$('#brush4').hide();
			$('#brush5').hide();
					}
			else if(ui.value == 3){
			$('#brush1').hide();
			$('#brush2').hide();
			$('#brush3').show();
			$('#brush4').hide();
			$('#brush5').hide();	
					}
			else if(ui.value == 4){
			$('#brush1').hide();
			$('#brush2').hide();
			$('#brush3').hide();
			$('#brush4').show();
			$('#brush5').hide();	
					}
			else if(ui.value == 5){
			$('#brush1').hide();
			$('#brush2').hide();
			$('#brush3').hide();
			$('#brush4').hide();
			$('#brush5').show();
					}					
	  		}//change function

   });
						
});

			

$("#trash").click(function(){
	erase();
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