
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



    $("#download").click(function (){
        console.log("In the download function. This is: ");
        console.log(this); 
        downloadCanvas(this);
	//save();
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


function getStats() {
    var allData = getData();
    var userLines = allData.userLines;
    var compLines = allData.computerLines;
    console.log("userLines = " + userLines);
}
	 
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

//D3 viz stuff
function InitChart(data) {
    //Drawing Activity over Time
    $('#ActivityChart').append('<svg id="visualisation" width="1100" height="350"></svg>');

    console.log("in the initChart, starting to build the data: ");
    console.log(data); 
    var startTime = data[0].allPoints[0].timestamp;
    var lineData = []; 
    console.log("Start time: " + startTime);
    console.log("Data.length = " + data.length); 
    

    for (var i = 0; i < data.length; i++) {
        //cycle through lines
        //code for adding zero before line
        //console.log("In the first for loop, i=" + i + "total length: " + data.length);
        
        if (i != 0) {
                //if not the first line, then add zero points before point before line
            var initialTime = data[i].allPoints[0].timestamp;
            var normalTime = initialTime - startTime;
            var newPoint = {
                'x': normalTime - 1,
                'y': 0
            };
            //console.log("Adding point before: " + newPoint);
            lineData.push(newPoint); 
        }
        for (var j = 0; j < data[i].allPoints.length ; j++) {
            //console.log("In the second loop, j=" + j + "length of j loop: " + data[i].allPoints.length); 
            //cycle through points        
            //normalize timestamps to first time stamp
            var curPoint = data[i].allPoints[j];
            var initialTime = curPoint.timestamp;
            var normalTime = initialTime - startTime;

            var newPoint = {
                'x': normalTime,
                'y': 1
            };
            lineData.push(newPoint); 


        }
        //console.log("LineData: ");
        //console.log(lineData); 

        //code for adding zero point after line
        var lastPoint = data[i].allPoints[data[i].allPoints.length - 1];
        //console.log("Trying to add in the zero point after, LastPoint: " + lastPoint); 
        var initialTime = lastPoint.timestamp;
        var normalTime = initialTime - startTime;
        var newPoint = {
            'x': normalTime + 1,
            'y': 0
        };
        lineData.push(newPoint);         
         
    }

    
    
    /*
    var lineData = [{
            'x': 1,
            'y': 5
        }, {
            'x': 20,
            'y': 20
        }, {
            'x': 40,
            'y': 10
        }, {
            'x': 60,
            'y': 40
        }, {
            'x': 80,
            'y': 5
        }, {
            'x': 100,
            'y': 60
        }, {
            'x': 150,
            'y': 200
        }
    ];
    
    */
    
    var vis = d3.select("#visualisation"),
        WIDTH = 1000,
        HEIGHT = 300,
        MARGINS = {
            top: 20,
            right: 20,
            bottom: 20,
            left: 50
        },
        xRange = d3.scale.linear().range([MARGINS.left, WIDTH - MARGINS.right]).domain([d3.min(lineData, function (d) {
                return d.x;
            }),
            d3.max(lineData, function (d) {
                return d.x;
            })
        ]),

        yRange = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([d3.min(lineData, function (d) {
                return d.y;
            }),
            d3.max(lineData, function (d) {
                return d.y;
            })
        ]),

        xAxis = d3.svg.axis()
      .scale(xRange)
      .tickSize(5)
      .tickSubdivide(false),

        yAxis = d3.svg.axis()
      .scale(yRange)
      .tickSize(5)
      .orient("left")
      .tickSubdivide(true);
    
    
    vis.append("svg:g")
    .attr("class", "x axis")
    .attr("transform", "translate(0," + (HEIGHT - MARGINS.bottom) + ")")
    .call(xAxis);
    
    vis.append("svg:g")
    .attr("class", "y axis")
    .attr("transform", "translate(" + (MARGINS.left) + ",0)")
    .call(yAxis);
    
    // now add titles to the axes
    vis.append("text")
            .attr("text-anchor", "middle")// this makes it easy to centre the text as the transform is applied to the anchor
            .attr("transform", "translate(" + (20 / 2) + "," + (HEIGHT / 2) + ")rotate(-90)")// text is drawn off the screen top left, move down and out and rotate
            .text("Active or Not");
    vis.append("text")
            .attr("text-anchor", "middle")// this makes it easy to centre the text as the transform is applied to the anchor
            .attr("transform", "translate(" + (WIDTH / 2) + "," + (15 + HEIGHT - (1 / 3)) + ")")// centre below axis
            .text("Time (ms)");
    
    var lineFunc = d3.svg.line()
  .x(function (d) {
        return xRange(d.x);
    })
  .y(function (d) {
        return yRange(d.y);
    })
  .interpolate('linear');
    
    vis.append("svg:path")
  .attr("d", lineFunc(lineData))
  .attr("stroke", "blue")
  .attr("stroke-width", 2)
  .attr("fill", "none");

}

function InitChart2(data) {
    //line length over line order
    $('#LengthChart').append('<svg id="visualisation2" width="1100" height="350"></svg>');
    var lineData = [];

    for (var i = 0; i < data.length; i++) {
        //cycle through lines to find their length 
        var length = data[i].totalDistance;
        var newPt = {
            'x': i,
            'y': length
        }
        lineData.push(newPt);
        console.log(lineData); 
    }
   
    
    
    /*
    var lineData = [{
            'x': 1,
            'y': 5
        }, {
            'x': 20,
            'y': 20
        }, {
            'x': 40,
            'y': 10
        }, {
            'x': 60,
            'y': 40
        }, {
            'x': 80,
            'y': 5
        }, {
            'x': 100,
            'y': 60
        }, {
            'x': 150,
            'y': 200
        }
    ];
    
    */
    
    var vis = d3.select("#visualisation2"),
        WIDTH = 1000,
        HEIGHT = 300,
        MARGINS = {
            top: 20,
            right: 20,
            bottom: 20,
            left: 50
        },
        xRange = d3.scale.linear().range([MARGINS.left, WIDTH - MARGINS.right]).domain([d3.min(lineData, function (d) {
                return d.x;
            }),
            d3.max(lineData, function (d) {
                return d.x;
            })
        ]),

        yRange = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([d3.min(lineData, function (d) {
                return d.y;
            }),
            d3.max(lineData, function (d) {
                return d.y;
            })
        ]),

        xAxis = d3.svg.axis()
      .scale(xRange)
      .tickSize(5)
      .tickSubdivide(true),

        yAxis = d3.svg.axis()
      .scale(yRange)
      .tickSize(5)
      .orient("left")
      .tickSubdivide(true);
    
    
    vis.append("svg:g")
    .attr("class", "x axis")
    .attr("transform", "translate(0," + (HEIGHT - MARGINS.bottom) + ")")
    .call(xAxis);
    
    vis.append("svg:g")
    .attr("class", "y axis")
    .attr("transform", "translate(" + (MARGINS.left) + ",0)")
    .call(yAxis);
    
    vis.append("text")
            .attr("text-anchor", "middle")// this makes it easy to centre the text as the transform is applied to the anchor
            .attr("transform", "translate(" + (20 / 2) + "," + (HEIGHT / 2) + ")rotate(-90)")// text is drawn off the screen top left, move down and out and rotate
            .text("Line Length");
    vis.append("text")
            .attr("text-anchor", "middle")// this makes it easy to centre the text as the transform is applied to the anchor
            .attr("transform", "translate(" + (WIDTH / 2) + "," + (15 + HEIGHT - (1/ 3)) + ")")// centre below axis
            .text("Line Number");
    
    var lineFunc = d3.svg.line()
  .x(function (d) {
        return xRange(d.x);
    })
  .y(function (d) {
        return yRange(d.y);
    })
  .interpolate('linear');
    
    vis.append("svg:path")
  .attr("d", lineFunc(lineData))
  .attr("stroke", "blue")
  .attr("stroke-width", 2)
  .attr("fill", "none");

}



function InitChart3(){

    //change svg call
    //change data source
    //add style component
    
    //$('#ActivityChart2').append('<svg id="visualization3" width="1100" height="500"></svg>');
    
    /*
    var lineData = [];
    
    for (var i = 0; i < data.length; i++) {
        //cycle through lines to find their length 
        var length = data[i].totalDistance;
        var newPt = {
            'x': i,
            'y': length
        }
        lineData.push(newPt);
        console.log(lineData);
    }
    */
    var lineData = [{
            'x': 1,
            'y': 5
        }, {
            'x': 20,
            'y': 20
        }, {
            'x': 40,
            'y': 10
        }, {
            'x': 60,
            'y': 40
        }, {
            'x': 80,
            'y': 5
        }, {
            'x': 100,
            'y': 60
        }, {
            'x': 150,
            'y': 200
        }
    ];
    
    
    var margin = { top: 10, right: 10, bottom: 100, left: 40 },
        margin2 = { top: 430, right: 10, bottom: 20, left: 40 },
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom,
        height2 = 500 - margin2.top - margin2.bottom;
    
    var parseDate = d3.time.format("%b %Y").parse;
    
    var x = d3.scale.linear().range([0, width]),
        x2 = d3.scale.linear().range([0, width]),
        y = d3.scale.linear().range([height, 0]),
        y2 = d3.scale.linear().range([height2, 0]);
    
    var xAxis = d3.svg.axis().scale(x).orient("bottom"),
        xAxis2 = d3.svg.axis().scale(x2).orient("bottom"),
        yAxis = d3.svg.axis().scale(y).orient("left");
    
    var brush = d3.svg.brush()
    .x(x2)
    .on("brush", brushed);
    
    var area = d3.svg.area()
    .interpolate("monotone")
    .x(function (d) { return x(d.x); })
    .y0(height)
    .y1(function (d) { return y(d.y); });
    
    var area2 = d3.svg.area()
    .interpolate("monotone")
    .x(function (d) { return x2(d.x); })
    .y0(height2)
    .y1(function (d) { return y2(d.y); });
    
    var svg = d3.select("#visualization3")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom);
    
    svg.append("defs").append("clipPath")
    .attr("id", "clip")
  .append("rect")
    .attr("width", width)
    .attr("height", height);
    
    var focus = svg.append("g")
    .attr("class", "focus")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
    
    var context = svg.append("g")
    .attr("class", "context")
    .attr("transform", "translate(" + margin2.left + "," + margin2.top + ")");
    
    loadData(lineData);
    function loadData(data) {
        x.domain(d3.extent(data.map(function (d) { return d.x; })));
        y.domain([0, d3.max(data.map(function (d) { return d.y; }))]);
        x2.domain(x.domain());
        y2.domain(y.domain());
        
        focus.append("path")
      .datum(data)
      .attr("class", "area")
      .attr("d", area);
        
        focus.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis);
        
        focus.append("g")
      .attr("class", "y axis")
      .call(yAxis);
        
        context.append("path")
      .datum(data)
      .attr("class", "area")
      .attr("d", area2);
        
        context.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height2 + ")")
      .call(xAxis2);
        
        context.append("g")
      .attr("class", "x brush")
      .call(brush)
    .selectAll("rect")
      .attr("y", -6)
      .attr("height", height2 + 7);
    }
    
    function brushed() {
        x.domain(brush.empty() ? x2.domain() : brush.extent());
        focus.select(".area").attr("d", area);
        focus.select(".x.axis").call(xAxis);
    }
    
    function type(d) {
        d.date = parseDate(d.date);
        d.price = +d.price;
        return d;
    }

}



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

btnObjRecSame = $("#btnObjRecSame img");
btnObjRecRelated = $("#btnObjRecRelated img");



btnTracing.click(function(){
	  ChooseCreativity(1);
	  var ending = btnTracing.attr('src').slice(-3); //, src.slice( -3 );
    switch( ending ) {
       case 'jpg': 
           btnTracing.attr('src',"images/Tracing.gif"); //= this.attr('src').replace( /jpg$/, 'gif' );
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRecSame.attr('src','images/object-recognition.jpg')
           break;
       case 'gif': 
           btnTracing.attr('src',"images/Tracing.jpg");
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRecSame.attr('src','images/object-recognition.jpg')
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
		   btnObjRecSame.attr('src','images/object-recognition.jpg')
           break;
       case 'gif': 
           btnTracing.attr('src',"images/Tracing.jpg");
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRecSame.attr('src','images/object-recognition.jpg')
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
		   btnObjRecSame.attr('src','images/object-recognition.jpg')
           break;
       case 'gif': 
           btnTracing.attr('src',"images/Tracing.jpg");
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRecSame.attr('src','images/object-recognition.jpg')
           break;
    }
});


//Case Button Object Recognition
btnObjRecSame.click(function(){
	ChooseCreativity(3);
	  var ending = btnObjRecSame.attr('src').slice(-3); //, src.slice( -3 );
    switch( ending ) {
       case 'jpg': 
           btnTracing.attr('src',"images/Tracing.jpg"); 
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRecSame.attr('src','images/object-recognition.gif')
           break;
       case 'gif': 
           btnTracing.attr('src',"images/Tracing.jpg");
		   btnMimicking.attr('src',"images/Mimicking.jpg");
		   btnTransforming.attr('src',"images/Transforming.jpg");
		   btnObjRecSame.attr('src','images/object-recognition.jpg')
           break;
    }
});

btnObjRecSame.click(function () {
    ChooseCreativity(4);
    /*
    var ending = btnMimicking.attr('src').slice(-3); //, src.slice( -3 );
    switch (ending) {
        case 'jpg':
            btnTracing.attr('src', "images/Tracing.jpg");
            btnMimicking.attr('src', "images/Mimicking.jpg");
            btnTransforming.attr('src', "images/Transforming.gif");
            break;
        case 'gif':
            btnTracing.attr('src', "images/Tracing.jpg");
            btnMimicking.attr('src', "images/Mimicking.jpg");
            btnTransforming.attr('src', "images/Transforming.jpg");
            break;
    }
     * */
});

btnObjRecRelated.click(function () {
    ChooseCreativity(5);
    /*
    var ending = btnMimicking.attr('src').slice(-3); //, src.slice( -3 );
    switch (ending) {
        case 'jpg':
            btnTracing.attr('src', "images/Tracing.jpg");
            btnMimicking.attr('src', "images/Mimicking.jpg");
            btnTransforming.attr('src', "images/Transforming.gif");
            break;
        case 'gif':
            btnTracing.attr('src', "images/Tracing.jpg");
            btnMimicking.attr('src', "images/Mimicking.jpg");
            btnTransforming.attr('src', "images/Transforming.jpg");
            break;
    }
     */
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

function displaySpeech(reco_Object, drawn_Object){
    //get the current mode
    //use the object from 
    var string = "<p> I think you're drawing a " + reco_Object + ". I'll draw a " + drawn_Object + " to go with it. </p>";
    
    $("#bubbleText").html(string);
    $("#speechBubble").fadeIn();

    setTimeout(hideBubble, 5000)

    
    //$("#speechBubble").show(); 

}

$("#up").click("displayFeedback(1);")
$("#down").click("displayFeedback(0);")


function displayFeedack(vote) {
    //get the current mode
    //use the object from 
    if (vote == 1) {
        var string = "<p> Glad you liked it! I'll do that more often."
    }
    else {
        var string = "<p> Alright, I'll do that less often."
    }
    
    $("#bubbleText").html(string);
    $("#speechBubble").fadeIn();
    
    setTimeout(hideBubble, 5000)

    //$("#speechBubble").show(); 

}



function hideBubble(){
    console.log("Trying to hide the bubble"); 
    $("#speechBubble").fadeOut();
}