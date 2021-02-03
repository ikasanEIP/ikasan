View = draw2d.Canvas.extend({

    leftMouseX:0,
    leftMouseY:0,
    rightMouseX:0,
    rightMouseY:0,

    init:function(app, id){
        let _this = this;

        this._super(id, 16000, 16000);
        this.clippboardFigure=null;
        this.grid =  new draw2d.policy.canvas.ShowGridEditPolicy(20);

        this.setScrollArea("#"+id);

        this.installEditPolicy( this.grid);
        this.installEditPolicy( new draw2d.policy.canvas.FadeoutDecorationPolicy());
        this.installEditPolicy( new draw2d.policy.canvas.SnapToGeometryEditPolicy());
        this.installEditPolicy( new draw2d.policy.canvas.SnapToCenterEditPolicy());
        this.installEditPolicy( new draw2d.policy.canvas.SnapToInBetweenEditPolicy());

        this.installEditPolicy(  new draw2d.policy.connection.DragConnectionCreatePolicy({
            createConnection: function() {
                // return my special kind of connection
                let con =  new draw2d.Connection({
                    targetDecorator: new draw2d.decoration.connection.ArrowDecorator(),
                    router: new draw2d.layout.connection.ManhattanConnectionRouter()
                });
                return con;
            }
        }));

        let policy = new draw2d.policy.canvas.CanvasPolicy();
        policy.onClick = function(canvas, mouseX, mouseY) {
            console.log("Mouse click:" + mouseX + "," + mouseY);
            _this.setLeftMouseX(mouseX);
            _this.setLeftMouseY(mouseY);
        }

        policy.onRightMouseDown = function(canvas, mouseX, mouseY) {
            console.log("Right Mouse click:" + mouseX + "," + mouseY);
            _this.setRightMouseX(mouseX);
            _this.setRightMouseY(mouseY);
        }
        this.installEditPolicy(policy);

        Mousetrap.bind(['left'],function (event) {
            var diff = _this.getZoom()<0.5?0.5:1;
            _this.getSelection().each(function(i,f){f.translate(-diff,0);});
            return false;
        });
        Mousetrap.bind(['up'],function (event) {
            var diff = _this.getZoom()<0.5?0.5:1;
            _this.getSelection().each(function(i,f){f.translate(0,-diff);});
            return false;
        });
        Mousetrap.bind(['right'],function (event) {
            var diff = _this.getZoom()<0.5?0.5:1;
            _this.getSelection().each(function(i,f){f.translate(diff,0);});
            return false;
        });
        Mousetrap.bind(['down'],function (event) {
            var diff = _this.getZoom()<0.5?0.5:1;
            _this.getSelection().each(function(i,f){f.translate(0,diff);});
            return false;
        });

        Mousetrap.bind(['ctrl+c', 'command+c'], $.proxy(function (event) {
            var primarySelection = this.getSelection().getPrimary();
            if(primarySelection!==null){
                this.clippboardFigure = primarySelection.clone();
                this.clippboardFigure.translate(5,5);
            }
            return false;
        },this));

        Mousetrap.bind(['ctrl+v', 'command+v'], $.proxy(function (event) {
            if(this.clippboardFigure!==null){
                var cloneToAdd = this.clippboardFigure.clone();
                var command = new draw2d.command.CommandAdd(this, cloneToAdd, cloneToAdd.getPosition());
                this.getCommandStack().execute(command);
                this.setCurrentSelection(cloneToAdd);
            }
            return false;
        },this));


        var zoom=new draw2d.policy.canvas.WheelZoomPolicy();
        this.installEditPolicy(zoom);

        var setZoom = $.proxy(function(newZoom, animate){
            var bb = this.getBoundingBox().getCenter();
            var c = $("#canvas");
            this.setZoom(newZoom);
            c.scrollTop((bb.y/newZoom- c.height()/2));
            c.scrollLeft((bb.x/newZoom- c.width()/2));
        },this);

        // Inject the ZoomIn Button and the callbacks
        //
        $("#canvas_zoom_in").on("click",function(){
            setZoom(_this.getZoom()*1.2,true);
        });

        // Inject the OneToOne Button
        //
        $("#canvas_zoom_normal").on("click",function(){
            setZoom(1.0, false);
        });

        // Inject the ZoomOut Button and the callback
        //
        $("#canvas_zoom_out").on("click",function(){
            setZoom(_this.getZoom()*0.8,true);
        });

        $('#canvas_config_grid').on('change', function (e) {
            if($(this).prop('checked')){
                _this.installEditPolicy( _this.grid);
            }
            else{
                _this.uninstallEditPolicy( _this.grid);
            }
        });

        $("#canvas_config_items").on("click",$.proxy(function(e){
            e.stopPropagation();
        },this));

        this.reset();

    },

    setCursor:function(cursor)
    {
        if(cursor!==null){
            this.html.css("cursor","url(assets/images/cursors/"+cursor+") 0 0, default");
        }
        else{
            this.html.css("cursor","default");
        }
    },


    /**
     * @method
     * Reset the view/canvas and starts with a clean and new document with default decorations
     *
     *
     */
    reset: function()
    {
        this.clear();
    },

    setZoom:function(newZoom)
    {
        $("#canvas_zoom_normal").text((parseInt((1.0/newZoom)*100))+"%");
        this._super(newZoom);
    },

    /**
     * Reset the view without any decorations. This is good before loading a document
     *
     */
    clear: function()
    {
        this._super();
    },

    getExtFigure: function(id){
        var figure = null;
        this.getExtFigures().each(function(i,e){
            if(e.id===id){
                figure=e;
                return false;
            }
        });
        return figure;
    },

    getExtFigures: function(){
        var figures = this.getFigures().clone();

        // the export rectangles are not part of the document itself. In this case we
        // filter them out
        //
        figures.grep(function(figure){
            return (typeof figure.isExtFigure  !=="undefined");
        });

        var lines = this.getLines().clone();
        lines.grep(function(line){
            return (typeof line.isExtFigure  !=="undefined");
        });

        figures.addAll(lines);

        return figures;
    },


    getBoundingBox: function(){
        var xCoords = [];
        var yCoords = [];
        this.getExtFigures().each(function(i,f){
            if(f instanceof shape_designer.figure.ExtPort){
                return;
            }
            var b = f.getBoundingBox();
            xCoords.push(b.x, b.x+b.w);
            yCoords.push(b.y, b.y+b.h);
        });
        var minX   = Math.min.apply(Math, xCoords);
        var minY   = Math.min.apply(Math, yCoords);
        var width  = Math.max(10,Math.max.apply(Math, xCoords)-minX);
        var height = Math.max(10,Math.max.apply(Math, yCoords)-minY);

        return new draw2d.geo.Rectangle(minX,minY,width,height);
    },

    add: function(figure, x,y){
        this._super(figure, x,y);
    },

    hideDecoration: function(){
        this.uninstallEditPolicy( this.grid);
        this.getFigures().each( function(index, figure){
            figure.unselect();
        });
    },

    showDecoration: function(){
        this.installEditPolicy( this.grid);
    },

    /**
     * @method
     * Return the width of the canvas
     *
     * @return {Number}
     **/
    getWidth: function()
    {
        return this.html.find("svg").width();
    },


    /**
     * @method
     * Return the height of the canvas.
     *
     * @return {Number}
     **/
    getHeight: function()
    {
        return this.html.find("svg").height();
    },

    centerDocument:function()
    {
        this.setZoom(1.0);
        // get the bounding box of the document and translate the complete document
        // into the center of the canvas. Scroll to the top left corner after them
        //
        var bb = this.getBoundingBox();

        var dx = (this.getWidth()/4)-(bb.x+bb.w/2);
        var dy = (this.getHeight()/4)-(bb.y+bb.h/2);

        this.getFigures().each(function(i,f){
            f.translate(dx,dy);
        });
        this.getLines().each(function(i,f){
            f.translate(dx,dy);
        });

        var center = bb.getCenter();
        var c = $("#canvas");
        c.scrollTop((center.y- c.height()/2));
        c.scrollLeft((center.x- c.width()/2));
    },

    getLeftMouseX: function()
    {
        return this.leftMouseX;
    },

    getLeftMouseY: function()
    {
        return this.leftMouseY;
    },

    getRightMouseX: function()
    {
        return this.rightMouseX;
    },

    getRightMouseY: function()
    {
        return this.rightMouseY;
    },

    setLeftMouseX: function(x)
    {
        this.leftMouseX = x;
    },

    setLeftMouseY: function(y)
    {
        this.leftMouseY = y;
    },

    setRightMouseX: function(x)
    {
        this.rightMouseX = x;
    },

    setRightMouseY: function(y)
    {
        this.rightMouseY = y;
    }

});