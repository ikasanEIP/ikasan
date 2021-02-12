window.Vaadin.Flow.designerConnector = {
    initLazy : function(designer) {

        // Check whether the connector was already initialized for the Iron list
        if (designer.$connector) {
            return;
        }
        console.log('init designer');

        designer.$connector = {};
        this.clippboardFigure=null;

        designer.$connector.designer = new View(this, "canvas-wrapper");

        let _this = designer.$connector.designer;

        Mousetrap.bind(['ctrl+a', 'command+a'], $.proxy(function (event) {
            debugger;
            _this.getFigures().each((i, figure)=>{
                figure.select(false);
            });

            return false;
        },this));

        let x=100;
        let y=100;

        let canvasRightClickX=0;
        let canvasRightClickY=0;
        let rightClickX=0;
        let rightClickY=0;

        $(document).ready(function () {
            $("#canvas-wrapper").mouseover(function (e) {
                x=e.offsetX;
                y=e.offsetY;
            });
            $("#canvas-wrapper").on("contextmenu", function(e){
                canvasRightClickX=e.offsetX;
                canvasRightClickY=e.offsetY;
                rightClickX=e.pageX;
                rightClickY=e.pageY;
                return false;
            });
        });


        // $(document).on("contextmenu", function(e){
        //     rightClickX=e.pageX;
        //     rightClickY=e.pageY
        //     return false;
        // });

        class FigureLite {
            constructor(name, x, y, width, height) {
                this.identifier = name;
                this.x = x;
                this.y = y;
                this.width = width;
                this.height= height;
            }

        }

        class Container {
            constructor(figures, x, y, windowx, windowy) {
                this.figures = figures;
                this.x = x;
                this.y = y;
                this.windowx = windowx;
                this.windowy = windowy;
            }

        }

        designer.$connector.getSelected = function () {
            console.log(_this.getFigures());
            let figures = new Array();

            _this.getFigures().each((i, figure)=>{
                figures.push(new FigureLite(figure.getId(), figure.x, figure.y, figure.getWidth(), figure.getHeight()));
            });

            let container = new Container(figures, canvasRightClickX, canvasRightClickY, rightClickX, rightClickY);

            console.log(JSON.stringify(container));
            return JSON.stringify(container);
        }

        designer.$connector.addIcon = function (identifier, image, h, w) {
            debugger;
            let messageChannel = new draw2d.shape.basic.Image({id: identifier, path: image, width:w, height:h, x:x, y:y, keepAspectRatio: true});
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

        designer.$connector.rotate = function (degrees) {
            _this.getFigures().each((i, figure)=>{
                if(figure.isSelected()) {
                    // _this.getCommandStack().execute(new draw2d.command.CommandRotate(_this.getSelection(), degrees % 360));
                    // figure.setRotationAngle(degrees % 360);
                    // figure.rotationAngle = (figure.getRotationAngle() + 90) % 360;
                    // figure.repaint();

                    // let command = figure.createCommand(new draw2d.command.CommandType(draw2d.command.CommandType.ROTATE));
                    console.log('figure current rotation angle ' + figure.getRotationAngle());
                    console.log('degrees ' + degrees);
                    console.log(figure);
                    figure.setRotationAngle((figure.getRotationAngle() + degrees) % 360);
                    figure.repaint();
                    // let command = new draw2d.command.CommandRotate(figure, (figure.getRotationAngle() + degrees) % 360);
                    // if (command !== null) {
                    //     _this.getCommandStack().execute(command);
                    //     figure.repaint();
                    // }
                }
            });
        }

        designer.$connector.addBoundary = function (h, w) {
            let boundary =  new draw2d.shape.basic.Rectangle({
                x: x,
                y: y,
                bgColor: "#ffffff",
                alpha  : 0.7,
                width: w,
                height: h,
                radius: 10,
                dash: "--",
                rotationAngle: 15,
            });

            debugger;
            boundary.uninstallEditPolicy(new draw2d.policy.figure.RectangleSelectionFeedbackPolicy());
            debugger;
            boundary.installEditPolicy(new RotateRectangleSelectionFeedbackPolicy());
            debugger;
            designer.$connector.designer.add(boundary);
        }

        designer.$connector.designer.on("dblclick", function(emitter, event){
            let figure = event.figure;
            let figureLite = new FigureLite(figure.getId(), figure.x, figure.y, figure.getWidth(), figure.getHeight());
            let element = document.getElementById("canvas-wrapper");
            element.$server.doubleClickEvent(JSON.stringify(figureLite));
        });

        // designer.$connector.designer.on("contextmenu", function(emitter, event){
        //     let figure = event.figure;
        //     let figureLite = new FigureLite(figure.getId(), figure.getX(), figure.getY(), figure.getWidth(), figure.getHeight());
        //     let element = document.getElementById("canvas-wrapper");
        //     debugger;
        //     element.$server.rightClickEvent(JSON.stringify(figureLite), event.pageX, event.pageY);
        // });

        designer.$connector.addTriangle = function (h, w) {
            let triangle = new TriangleFigure({x: x, y:y, width:w, height:h});

            designer.$connector.designer.add(triangle);
        }


        designer.$connector.addOval = function (h, w) {
            let oval =  new draw2d.shape.basic.Oval({width:w,height:h, x:x, y:y});

            designer.$connector.designer.add(oval);
        }

        designer.$connector.addCircle = function () {
            let circle =new draw2d.shape.basic.Circle({diameter:80, x:x, y:y, bgColor:"rgba(255,0,100,0.5)"});

            designer.$connector.designer.add(circle);
        }

        designer.$connector.addLabel = function () {
            let label = new draw2d.shape.basic.Label({
                text:"Double click to edit!",
                color:"rgba(255,255,255,0)",
                fontColor:"#0d0d0d",
                bgColor:"rgba(255,255,255,0)",
                outlineColor:"rgba(255,255,255,0)",
                fontFamily: "Trebuchet MS",
                fontSize: "18pt",
                x:x, y:y
            });


            label.installEditor(new draw2d.ui.LabelInplaceEditor());

            designer.$connector.designer.add(label);
        }


        designer.$connector.setBackgroundColor = function (color) {
            debugger;
            _this.getFigures().each((i, figure)=>{
                debugger;
                if(figure.isSelected()) {
                    debugger;
                    figure.setBackgroundColor(color);
                }
            });
        }

        designer.$connector.setLineType = function (pattern) {
            debugger;
            _this.getFigures().each((i, figure)=>{
                debugger;
                if(figure.isSelected()) {
                    debugger;
                    figure.setDashArray(pattern);
                }
            });
        }

        designer.$connector.setRadius = function (radius) {
            debugger;
            _this.getFigures().each((i, figure)=>{
                debugger;
                if(figure.isSelected()) {
                    debugger;
                    figure.setRadius(radius);
                }
            });
        }

        designer.$connector.exportJson = function () {
            debugger
            let writer = new draw2d.io.json.Writer();
            let result = null;
            writer.marshal(designer.$connector.designer, function(json){
                result = JSON.stringify(json,null,2);
            });

            return result;
        }
    }
}
