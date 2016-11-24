var maxUndoSteps = 10; // const for undo setup

var undoManager = {
    maxStep : maxUndoSteps,
    curUndoStep : maxUndoSteps,
    undo : function(){
        this.curUndoStep = this.undoStack.length < this.curUndoStep ? 
            this.undoStack.length : this.curUndoStep;
        this.curUndoStep = this.curUndoStep - 1;
        
        if(this.curUndoStep < 0)
            return null;
        try{
            var step = this.undoStack[this.curUndoStep];
            if(step && step != ""){
                var stepImage = localStorage.getItem(step);                
                // question, can we load the image with this stored URL?
                return stepImage;
            }
        }catch(err){
            console.log("undo failed: " + err);   
        }
    },
    redo : function(){
        if(this.curUndoStep >= undoStack.length)
            return null;
        try{
            var step = this.undoStack[this.curUndoStep];
            this.curUndoStep = this.curUndoStep + 1;

            if(step && step != ""){
                var stepImage = localStorage.getItem(step);                
                // question, can we load the image with this stored URL?
                return stepImage;
            }
        }catch(err){
            console.log("redo failed: " + err);
        }
    },
    addStack : function(canvas){
        var imgAsDataURL = canvas.toDataURL("image/png");
        // once add something to the canvas, reset this to the max step
        this.curUndoStep = maxUndoSteps;
        try{
            var now = (new Date()).getTime();
            
            if(localStorage.length > 5 || this.undoStack.length > 10){
                // remove the first item;
                var firstStep = this.undoStack.shift();
                localStorage.removeItem(firstStep);
            }
            
            this.undoStack.push(now); // push to the end of the stack for record
            localStorage.setItem(now, imgAsDataURL); // store the image to the local storage
        }catch(err){
            
            if(localStorage.length > 5 || this.undoStack.length > 10){
                // remove the first item;
                var firstStep = this.undoStack.shift();
                localStorage.removeItem(firstStep);
            }

            console.log("save to local storage failed: " + err );
        }
    },
    undoStack : [],
};
