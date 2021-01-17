window.Vaadin.Flow.designerConnector = {
    initLazy : function(designer) {

        // Check whether the connector was already initialized for the Iron list
        if (designer.$connector) {
            return;
        }
        console.log('init designer');

        designer.$connector = {};
        this.clippboardFigure=null;

        // designer.$connector.designer = new draw2d.Canvas("canvas-wrapper", 16000, 16000);

        designer.$connector.designer = new View(this, "canvas-wrapper");

        // designer.$connector.designer.installEditPolicy(new draw2d.policy.canvas.ShowGridEditPolicy(20));
        //
        // designer.$connector.designer.installEditPolicy(new draw2d.policy.canvas.SnapToGeometryEditPolicy());
        // designer.$connector.designer.installEditPolicy(
        //     new draw2d.policy.canvas.SnapToInBetweenEditPolicy()
        // );
        // designer.$connector.designer.installEditPolicy(new draw2d.policy.canvas.SnapToCenterEditPolicy());
        // designer.$connector.designer.installEditPolicy(new draw2d.policy.canvas.CoronaDecorationPolicy());
        //
        // designer.$connector.designer.installEditPolicy(  new draw2d.policy.connection.DragConnectionCreatePolicy({
        //     createConnection: function() {
        //         // return my special kind of connection
        //         let con =  new draw2d.Connection({
        //             targetDecorator: new draw2d.decoration.connection.ArrowDecorator(),
        //             router: new draw2d.layout.connection.ManhattanConnectionRouter()
        //         });
        //         return con;
        //     }
        // }));
        //
        // designer.$connector.designer.installEditPolicy(new draw2d.policy.canvas.WheelZoomPolicy());
        //
        // let keyboardPolicy = new draw2d.policy.canvas.ExtendedKeyboardPolicy();
        //
        // designer.$connector.designer.installEditPolicy(keyboardPolicy);
        //
        // let _this = designer.$connector.designer;
        //
        // Mousetrap.bind(['left'],function (event) {
        //     let diff = _this.getZoom()<0.5?0.5:1;
        //     _this.getSelection().each(function(i,f){f.translate(-diff,0);});
        //     return false;
        // });
        // Mousetrap.bind(['up'],function (event) {
        //     let diff = _this.getZoom()<0.5?0.5:1;
        //     _this.getSelection().each(function(i,f){f.translate(0,-diff);});
        //     return false;
        // });
        // Mousetrap.bind(['right'],function (event) {
        //     let diff = _this.getZoom()<0.5?0.5:1;
        //     _this.getSelection().each(function(i,f){f.translate(diff,0);});
        //     return false;
        // });
        // Mousetrap.bind(['down'],function (event) {
        //     let diff = _this.getZoom()<0.5?0.5:1;
        //     _this.getSelection().each(function(i,f){f.translate(0,diff);});
        //     return false;
        // });
        //
        // Mousetrap.bind(['ctrl+c', 'command+c'], $.proxy(function (event) {
        //     debugger;
        //     let primarySelection = _this.getPrimarySelection();
        //     if(primarySelection!==null){
        //         this.clippboardFigure = primarySelection.clone();
        //         this.clippboardFigure.translate(5,5);
        //     }
        //     return false;
        // },this));
        //
        // Mousetrap.bind(['ctrl+v', 'command+v'], $.proxy(function (event) {
        //     if(this.clippboardFigure!==null){
        //         let cloneToAdd = this.clippboardFigure.clone();
        //         cloneToAdd.keepAspectRatio = true;
        //         let command = new draw2d.command.CommandAdd(_this, cloneToAdd, cloneToAdd.getPosition());
        //         _this.getCommandStack().execute(command);
        //         _this.setCurrentSelection(cloneToAdd);
        //     }
        //     return false;
        // },this));

        let _this = designer.$connector.designer;

        Mousetrap.bind(['ctrl+a', 'command+a'], $.proxy(function (event) {
            debugger;
            _this.getFigures().each((i, figure)=>{
                figure.select(false);
            });

            return false;
        },this));


        // // Load a standard draw2d JSON object into the canvas
        // //
        // let jsonDocument = [
        // ];
        // // unmarshal the JSON document into the canvas
        // // (load)
        // let reader = new draw2d.io.json.Reader();
        // reader.unmarshal(designer.$connector.designer, jsonDocument);

        let x=100;
        let y=100;


        $(document).ready(function () {
            $("#canvas-wrapper").mouseover(function (e) {
                x=e.offsetX;
                y=e.offsetY;
            });
        });

        designer.$connector.addIcon = function (image, h, w) {
            debugger;
            let messageChannel = new draw2d.shape.basic.Image({path: image, width:w, height:h, x:x, y:y, keepAspectRatio: true});
            messageChannel.createPort("input");
            messageChannel.createPort("output");

            designer.$connector.designer.add(messageChannel);
        }

        designer.$connector.bringToFront = function () {
            _this.getFigures().each((i, figure)=>{
                if(figure.isSelected()) {
                    figure.toFront();
                }
            });
        }

        designer.$connector.sendToBack = function () {
            _this.getFigures().each((i, figure)=>{
                if(figure.isSelected()) {
                    figure.toBack();
                }
            });
        }

        designer.$connector.group = function () {
            _this.getCommandStack().execute(new draw2d.command.CommandGroup(_this, _this.getSelection()))
        }

        designer.$connector.ungroup = function () {
            debugger;
            _this.getCommandStack().execute(new draw2d.command.CommandUngroup(_this, _this.getSelection()))
        }

        designer.$connector.addBoundary = function (h, w) {
            debugger;
            let boundary =  new draw2d.shape.basic.Rectangle({
                x: x,
                y: y,
                bgColor: "#ffffff",
                alpha  : 0.7,
                width: w,
                height: h,
                radius: 10,
                dash: "--"
            });

            designer.$connector.designer.add(boundary);
        }

    }
}
