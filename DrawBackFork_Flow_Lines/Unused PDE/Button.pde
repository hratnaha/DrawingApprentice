class Button {
  int x; 
  int y; 
  int width; 
  int height; 
  String mode; 
  boolean mouseOver = false; 
  boolean active = false; 
  
  public Button(int x, int y, int width, int height)
  {
    this.x = x; 
    this.y = y; 
    this.width = width; 
    this.height = height; 
    render(); 
  }
  
  void mousePressed()
  {
    //listens for if buttons are pressed
     if (mouseX > x && mouseX < x + width)
      if (mouseY > y && mouseY < y + height)
      {
        if (active = true)
          active = false; 
        else 
        {
          active = true; 
          
        }
      }
  }
  
  void mouseMoved()
  {
    //check if the mouse if over the boundary of the button
    //if so change the color of the button 
    //set the condition of the mouseOver value to true
    //render
    if (mouseX > x && mouseX < x + width)
      if (mouseY > y && mouseY < y + height)
      {
        active = true; 
        render(); 
      }
  }
  
  void render()
  {
    if (mouseOver || active)
      fill(100, 0, 0); 
    else 
      fill(0,100,0); 
    rect(x, y, width, height); 
  }
}
