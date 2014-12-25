$("#opacity_slider").slider(
  {
    value: 50,
	animate:true,
    min: 0,
    max: 100,
    create: function( event, ui )
	 {
      setSliderTicks(event.target);
     },
  });

