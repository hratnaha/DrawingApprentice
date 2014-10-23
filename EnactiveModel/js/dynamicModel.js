var awareness = null;
var mental_environment = null;
var svg = null;
$(function() {
	svg = $('#enactive_model').svg({onLoad: modelInitial});
});

function awarenessLoaded(svg, error) {
    $("#Awareness").attr("transform", "translate(46 -58)");

    var intentionRect = document.getElementById("Intention").getBoundingClientRect();
	var PercptRect = document.getElementById("PerceptualLogic").getBoundingClientRect();
	svg.line(15,10, 50, -37, {stroke: 'black', strokeWidth: 0.5, id: 'line1'});
	svg.line(116,10, 78, -37, {stroke: 'black', strokeWidth: 0.5, id: 'line2'});
	svg.line(18,15, 50, -7, {stroke: 'black', strokeWidth: 0.5, id: 'line3'});
	svg.line(110,15, 78, -7, {stroke: 'black', strokeWidth: 0.5, id: 'line4'});
	$("#line1").attr("marker-start", "url(#triangle2)");
	$("#line2").attr("marker-start", "url(#triangle2)");
	$("#line3").attr("marker-end", "url(#triangle1)");
	$("#line4").attr("marker-end", "url(#triangle1)");
}
function mental_environmentLoaded(svg, error) { 
    $("#Envi_Mental").attr("transform", "translate(0, 10)");
}
function labelsLoaded(svg, error) { 
    $("#Labels").attr("transform", "translate(-2, -40)");
}
function axisLoaded(svg, error) { 
    $("#axislabel").attr("transform", "translate(-2, 40)");
}
function modelInitial(svg) {
    svg.load("/svg/AwarenessBox2.svg", {addTo: true, 
         changeSize: false, onLoad: awarenessLoaded});
    svg.load("/svg/Enviro_Mental.svg", {addTo: true, 
        changeSize: false, onLoad: mental_environmentLoaded});
    svg.load("/svg/TextLabels.svg", {addTo: true, 
        changeSize: false, onLoad: labelsLoaded});
    svg.load("/svg/AxisLabels.svg", {addTo: true, 
        changeSize: false, onLoad: axisLoaded});

    $("#enactive_model").children("svg").attr("id", "model_canvas")
    // starting to control the position of awareness after awareness is loaded
    $("#model_canvas").mousemove(modelMove);

}

function modelMove(event){
	// only tranlate the x axis
	var clientX = event.clientX < 170 ? 170 : event.clientX;
	clientX = clientX > 438 ? 438 : clientX;
	var x = (clientX - this.offsetLeft - 50) / 5.216;// - 204.5;
	$("#Awareness").attr("transform", "translate(" + x + ", -58)");
	// 170 300 438
	if(clientX <= 300){
		var regionF = (clientX - 170) / 131;
		regionF = regionF < 0 ? 0 : regionF; 
		var globalF	 = 1 - regionF;
		$("#imgGlobal").css("opacity", globalF);
		$("#imgRegional").css("opacity", regionF);
		$("#Labels").css("opacity", regionF);
	}else{
		var regionF = (438 - clientX) / 138;
		regionF = regionF < 0 ? 0 : regionF;
		var localF = 1- regionF;	

		$("#imgLocal").css("opacity", localF);
		$("#imgRegional").css("opacity", regionF);
		$("#Labels").css("opacity", regionF);
	}

	var intentionRect = document.getElementById("Intention").getBoundingClientRect();
	var PercptRect = document.getElementById("PerceptualLogic").getBoundingClientRect();
	$("#line1").attr("x2", x + 3);
	$("#line2").attr("x2", x + 32);
	$("#line3").attr("x2", x + 3);
	$("#line4").attr("x2", x + 32);
}


var colours = ['purple', 'red', 'orange', 'yellow', 'lime', 'green', 'blue', 'navy', 'black'];

function drawShape() {
	var shape = this.id;
	var svg = $('#enactive_model').svg('get');
	if (shape == 'rect') {
		svg.rect(random(300), random(200), random(100) + 100, random(100) + 100,
			{fill: colours[random(9)], stroke: colours[random(9)],
			'stroke-width': random(5) + 1});
	}
	else if (shape == 'line') {
		svg.line(random(400), random(300), random(400), random(300),
			{stroke: colours[random(9)], 'stroke-width': random(5) + 1});
	}
	else if (shape == 'circle') {
		svg.circle(random(300) + 50, random(200) + 50, random(80) + 20,
			{fill: colours[random(9)], stroke: colours[random(9)],
			'stroke-width': random(5) + 1});
	}
	else if (shape == 'ellipse') {
		svg.ellipse(random(300) + 50, random(200) + 50, random(80) + 20, random(80) + 20,
			{fill: colours[random(9)], stroke: colours[random(9)],
			'stroke-width': random(5) + 1});
	}
}

function random(range) {
	return Math.floor(Math.random() * range);
}