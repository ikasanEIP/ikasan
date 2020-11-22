window.Vaadin.Flow.networkDiagramConnector = {
	initLazy : function(graph, initialNodes, initialEdges, options) {

        // Check whether the connector was already initialized for the Iron list
        if (graph.$connector) {
            return;
        }
        console.log('init networkDiagramConnector');

        graph.$connector = {};

        console.log(initialNodes);
        let nodesParent = JSON.parse(initialNodes);

        graph.nodes = new vis.DataSet(nodesParent);
        graph.edges = new vis.DataSet(JSON.parse(initialEdges));

        graph.$connector.updateNodeStates = function (nodesStates) {
            nodesParent = JSON.parse(nodesStates);
        };

        let self = this;
        let customNodeifAdded = false;
        let customNodeID;
        let customNodeLabel;
        let customEdgeifAdded = false;
        let customEdgeID;
        let customEdgeLabel;

        let wiretapBeforeImage = new Image();
        wiretapBeforeImage.src = "frontend/images/wiretap.png";

        let wiretapAfterImage = new Image();
        wiretapAfterImage.src = "frontend/images/wiretap.png";

        let logWiretapBeforeImage = new Image();
        logWiretapBeforeImage.src = "frontend/images/log-wiretap.png";

        let logWiretapAfterImage= new Image();
        logWiretapAfterImage.src = "frontend/images/log-wiretap.png";

        let wiretapImage = new Image();
        wiretapImage.src = "frontend/images/wiretap-service.png";

        let replayImage = new Image();
        replayImage.src = "frontend/images/replay-service.png";

        let hospitalImage = new Image();
        hospitalImage.src = "frontend/images/hospital-service.png";

        let errorImage = new Image();
        errorImage.src = "frontend/images/error-service.png";

        let flowManualImage = new Image();
        flowManualImage.src = "frontend/images/flow-manual.png";

        let flowAutoImage = new Image();
        flowAutoImage.src = "frontend/images/flow-automatic.png";

        let flowDisabledImage = new Image();
        flowDisabledImage.src = "frontend/images/flow-disabled.png";

        graph.options = JSON.parse(options);
        graph.options.manipulation.addNode = function (nodeData, callback) {
            if (customNodeifAdded == true) {
                nodeData.label = customNodeLabel;
                nodeData.id = customNodeID;
            }
            self.onManipulationNodeAdded(nodeData);
            callback(nodeData);
        };
        graph.options.manipulation.addEdge = function (edgeData, callback) {
            if (customEdgeifAdded == true) {
                edgeData.label = customEdgeLabel;
                edgeData.id = customEdgeID;
            }
            self.onManipulationEdgeAdded(edgeData);
            callback(edgeData);
        };
        graph.options.manipulation.deleteNode = function (nodeData, callback) {
            self.onManipulationNodeDeleted(nodeData);
            callback(nodeData);
        };
        graph.options.manipulation.deleteEdge = function (edgeData, callback) {
            self.onManipulationEdgeDeleted(edgeData);
            callback(edgeData);
        };
        graph.options.manipulation.editEdge = function (edgeData, callback) {
            self.onManipulationEdgeEdited(edgeData);
            callback(edgeData);
        };
        console.log("networkdiagram options: " + JSON.stringify(graph.options));
        graph.$connector.diagram = new vis.Network(graph, {
            nodes: graph.nodes,
            edges: graph.edges
        }, graph.options);

        let scaleOption = { scale : 0.6,
            animation: false};
        graph.$connector.diagram.moveTo(scaleOption);

        // Enable event dispatching to vaadin only for registered eventTypes to
        // avoid too much overhead.
        graph.$connector.enableEventDispatching = function (vaadinEventType) {
            const eventType = vaadinEventType.substring(7);
            graph.$connector.diagram
                .on(
                    eventType,
                    function (params) {
                        if (params != null) {
                            // removing dom nodes from params cause they
                            // can't send back to server.
                            if (params.hasOwnProperty('event')) {
                                // source of click event
                                delete params.event.firstTarget;
                                delete params.event.target;
                            }
                            JSON
                                .stringify(
                                    params,
                                    function (key, value) {
                                        if (value instanceof Node) {
                                            console
                                                .log("Message JsonObject contained a dom node reference  "
                                                    + key
                                                    + "  which "
                                                    + "should not be sent to the server and can cause a cyclic dependecy.");
                                            delete params[key];
                                        }
                                        return value;
                                    });
                        }
                        graph.dispatchEvent(new CustomEvent(
                            vaadinEventType, {
                                detail: params
                            }));
                    });
        }

        graph.$connector.drawNodeFoundStatus = function () {
            graph.$connector.diagram.on("afterDrawing", function (ctx) {
                let inode;
                let nodePositions = graph.$connector.diagram.getPositions();
                let arrayLength = graph.nodes.length;

                for (inode = 0; inode < arrayLength; inode++) {
                    let node = nodesParent[inode];
                    let nodePosition = nodePositions[node.id];

                    if (node.wiretapFoundStatus === "FOUND") {
                        ctx.drawImage(wiretapImage, nodePosition.x + node.wiretapFoundImageX
                            , nodePosition.y + node.wiretapFoundImageY
                            , node.wiretapFoundImageW
                            , node.wiretapFoundImageH);
                    }
                    if (node.errorFoundStatus === "FOUND") {
                        ctx.drawImage(errorImage, nodePosition.x + node.errorFoundImageX
                            , nodePosition.y + node.errorFoundImageY
                            , node.errorFoundImageW
                            , node.errorFoundImageH);
                    }
                    if (node.exclusionFoundStatus === "FOUND") {
                        ctx.drawImage(hospitalImage, nodePosition.x + node.exclusionFoundImageX
                            , nodePosition.y + node.exclusionFoundImageY
                            , node.exclusionFoundImageW
                            , node.exclusionFoundImageH);
                    }
                    if (node.replayFoundStatus === "FOUND") {
                        ctx.drawImage(replayImage, nodePosition.x + node.replayFoundImageX
                            , nodePosition.y + node.replayFoundImageY
                            , node.replayFoundImageW
                            , node.replayFoundImageH);
                    }
                    if (node.wiretapBeforeStatus === "FOUND") {
                        ctx.drawImage(wiretapBeforeImage, nodePosition.x + node.wiretapBeforeImageX
                            , nodePosition.y + node.wiretapBeforeImageY
                            , node.wiretapBeforeImageW
                            , node.wiretapBeforeImageH);
                    }
                    if (node.wiretapAfterStatus === "FOUND") {
                        ctx.drawImage(wiretapAfterImage, nodePosition.x + node.wiretapAfterImageX
                            , nodePosition.y + node.wiretapAfterImageY
                            , node.wiretapAfterImageW
                            , node.wiretapAfterImageH);
                    }
                    if (node.logWiretapBeforeStatus === "FOUND") {
                        ctx.drawImage(logWiretapBeforeImage, nodePosition.x + node.logWiretapBeforeImageX
                            , nodePosition.y + node.logWiretapBeforeImageY
                            , node.logWiretapBeforeImageW
                            , node.logWiretapBeforeImageH);
                    }
                    if (node.logWiretapAfterStatus === "FOUND") {
                        ctx.drawImage(logWiretapAfterImage, nodePosition.x + node.logWiretapAfterImageX
                            , nodePosition.y + node.logWiretapAfterImageY
                            , node.logWiretapAfterImageW
                            , node.logWiretapAfterImageH);
                    }
                }
            });
        }

        graph.$connector.addWiretapBefore = function (x, y, h, w) {
            graph.$connector.diagram.on("afterDrawing", function (ctx) {
                ctx.drawImage(wiretapBeforeImage, x, y, w, h);
            });
        }

        graph.$connector.addWiretapAfter = function (x, y, h, w) {
            graph.$connector.diagram.on("afterDrawing", function (ctx) {
                ctx.drawImage(wiretapAfterImage, x, y, w, h);
            });
        }

        graph.$connector.addLogWiretapAfter = function (x, y, h, w) {
            graph.$connector.diagram.on("afterDrawing", function (ctx) {
                ctx.drawImage(logWiretapAfterImage, x, y, w, h);
            });
        }

        graph.$connector.addLogWiretapBefore = function (x, y, h, w) {
            graph.$connector.diagram.on("afterDrawing", function (ctx) {
                ctx.drawImage(logWiretapBeforeImage, x, y, w, h);
            });
        }

        graph.$connector.drawFlowControl = function (x, y, h, w, startupType) {
            graph.$connector.diagram.on("afterDrawing", function (ctx) {
                if ("manual" === startupType) {
                    ctx.drawImage(flowManualImage, x, y, w, h);
                }
                else if ("automatic" === startupType) {
                    ctx.drawImage(flowAutoImage, x, y, w, h);
                }
                else if ("disabled" === startupType) {
                    ctx.drawImage(flowDisabledImage, x, y, w, h);
                }
            });
        }

        graph.$connector.removeImage = function (x, y, h, w) {
            graph.$connector.diagram.on("afterDrawing", function (ctx) {
                ctx.clearRect(x, y, h, w);
                ctx.fillStyle = 'rgba(224,224,224,0.8)';
                ctx.fillRect(x, y, h, w);
            });
        }

        graph.$connector.drawStatus = function (x, y, radius, colour) {
            graph.$connector.diagram.on("afterDrawing", function (ctx) {
                ctx.beginPath();
                ctx.arc(x, y, radius, 0, 2 * Math.PI, false);
                ctx.fillStyle = colour;
                ctx.fill();
                ctx.lineWidth = 2;
                ctx.strokeStyle = '#003300';
                ctx.stroke();
            });
        }

        graph.$connector.drawModuleSquare = function (x, y, width, height, text) {
            graph.$connector.diagram.on("beforeDrawing", function (ctx) {
                ctx.font = '18px sans-serif';
                ctx.textAlign = 'center';

                ctx.fillStyle = '#000';
                ctx.fillText(text, x + (width / 2) , y + 25);

                ctx.beginPath();
                ctx.setLineDash([]);
                ctx.strokeStyle = 'black';

                let stroke = true;
                let radius = 20;
                let fill = false;

                if (typeof radius === 'number') {
                    radius = {tl: radius, tr: radius, br: radius, bl: radius};
                } else {
                    let defaultRadius = {tl: 0, tr: 0, br: 0, bl: 0};
                    for (let side in defaultRadius) {
                        radius[side] = radius[side] || defaultRadius[side];
                    }
                }
                ctx.beginPath();
                ctx.moveTo(x + radius.tl, y);
                ctx.lineTo(x + width - radius.tr, y);
                ctx.quadraticCurveTo(x + width, y, x + width, y + radius.tr);
                ctx.lineTo(x + width, y + height - radius.br);
                ctx.quadraticCurveTo(x + width, y + height, x + width - radius.br, y + height);
                ctx.lineTo(x + radius.bl, y + height);
                ctx.quadraticCurveTo(x, y + height, x, y + height - radius.bl);
                ctx.lineTo(x, y + radius.tl);
                ctx.quadraticCurveTo(x, y, x + radius.tl, y);
                ctx.closePath();
                if (fill) {
                    ctx.fill();
                }
                if (stroke) {
                    ctx.stroke();
                }
            });
        }

        graph.$connector.drawBoundary = function (x, y, width, height, text, colour) {
            graph.$connector.diagram.on("beforeDrawing", function (ctx) {
                ctx.font = '18px sans-serif';
                ctx.textAlign = 'center';

                ctx.fillStyle = '#000';

                ctx.beginPath();
                ctx.setLineDash([5, 5]);
                ctx.strokeStyle = 'black';

                let stroke = true;
                let radius = 20;
                let fill = true;

                if (typeof radius === 'number') {
                    radius = {tl: radius, tr: radius, br: radius, bl: radius};
                } else {
                    let defaultRadius = {tl: 0, tr: 0, br: 0, bl: 0};
                    for (let side in defaultRadius) {
                        radius[side] = radius[side] || defaultRadius[side];
                    }
                }
                ctx.beginPath();
                ctx.moveTo(x + radius.tl, y);
                ctx.lineTo(x + width - radius.tr, y);
                ctx.quadraticCurveTo(x + width, y, x + width, y + radius.tr);
                ctx.lineTo(x + width, y + height - radius.br);
                ctx.quadraticCurveTo(x + width, y + height, x + width - radius.br, y + height);
                ctx.lineTo(x + radius.bl, y + height);
                ctx.quadraticCurveTo(x, y + height, x, y + height - radius.bl);
                ctx.lineTo(x, y + radius.tl);
                ctx.quadraticCurveTo(x, y, x + radius.tl, y);
                ctx.closePath();
                if (fill) {
                    ctx.fillStyle = 'white';
                    ctx.fill();
                    ctx.fillStyle = colour;
                    ctx.fill();
                }
                if (stroke) {
                    ctx.stroke();
                }

                ctx.fillStyle = 'black';
                ctx.fillText(text, x + (width / 2) , y + 25);
                ctx.setLineDash([0, 0]);
            });
        }

        graph.$connector.drawFlowBorder = function (x, y, width, height, text) {
            graph.$connector.diagram.on("beforeDrawing", function (ctx) {

                ctx.font = '18px sans-serif';
                ctx.textAlign = 'center';

                ctx.lineWidth=1.0
                ctx.beginPath();
                ctx.setLineDash([10, 10]);
                ctx.strokeStyle = '#000';
                ctx.fillStyle = 'rgba(224,224,224,0.5)';

                let stroke = true;
                let radius = 20;
                let fill = true;

                if (typeof radius === 'number') {
                    radius = {tl: radius, tr: radius, br: radius, bl: radius};
                } else {
                    let defaultRadius = {tl: 0, tr: 0, br: 0, bl: 0};
                    for (let side in defaultRadius) {
                        radius[side] = radius[side] || defaultRadius[side];
                    }
                }
                ctx.beginPath();
                ctx.moveTo(x + radius.tl, y);
                ctx.lineTo(x + width - radius.tr, y);
                ctx.quadraticCurveTo(x + width, y, x + width, y + radius.tr);
                ctx.lineTo(x + width, y + height - radius.br);
                ctx.quadraticCurveTo(x + width, y + height, x + width - radius.br, y + height);
                ctx.lineTo(x + radius.bl, y + height);
                ctx.quadraticCurveTo(x, y + height, x, y + height - radius.bl);
                ctx.lineTo(x, y + radius.tl);
                ctx.quadraticCurveTo(x, y, x + radius.tl, y);
                ctx.closePath();
                if (fill) {
                    ctx.fill();
                }
                if (stroke) {
                    ctx.stroke();
                }

                ctx.fillStyle = '#000';
                ctx.fillText(text, x + (width / 2) , y + 25);

                ctx.lineWidth=2.0
                ctx.setLineDash([0, 0]);
            });
        }

        graph.$connector.drawStatusBorder = function (x, y, width, height, colour) {
            graph.$connector.diagram.on("beforeDrawing", function (ctx) {

                ctx.lineWidth=5.0
                ctx.beginPath();
                ctx.setLineDash([0, 0]);
                ctx.strokeStyle = colour;
                ctx.fillStyle = 'rgba(224,224,224,0.5)';
                // ctx.back

                let stroke = true;
                let radius = 20;
                let fill = false;

                if (typeof radius === 'number') {
                    radius = {tl: radius, tr: radius, br: radius, bl: radius};
                } else {
                    let defaultRadius = {tl: 0, tr: 0, br: 0, bl: 0};
                    for (let side in defaultRadius) {
                        radius[side] = radius[side] || defaultRadius[side];
                    }
                }
                ctx.beginPath();
                ctx.moveTo(x + radius.tl, y);
                ctx.lineTo(x + width - radius.tr, y);
                ctx.quadraticCurveTo(x + width, y, x + width, y + radius.tr);
                ctx.lineTo(x + width, y + height - radius.br);
                ctx.quadraticCurveTo(x + width, y + height, x + width - radius.br, y + height);
                ctx.lineTo(x + radius.bl, y + height);
                ctx.quadraticCurveTo(x, y + height, x, y + height - radius.bl);
                ctx.lineTo(x, y + radius.tl);
                ctx.quadraticCurveTo(x, y, x + radius.tl, y);
                ctx.closePath();
                if (fill) {
                    ctx.fill();
                }
                if (stroke) {
                    ctx.stroke();
                }

                ctx.lineWidth=2.0
                ctx.setLineDash([0, 0]);
            });
        }

        graph.$connector.scale = function (scale) {
            let scaleOption = {
                scale: scale,
                animation: false
            };
            graph.$connector.diagram.moveTo(scaleOption);
        }

		// not used yet
		graph.$connector.disableEventDispatching = function(vaadinEventType) {
			const eventType = vaadinEventType.substring(7);
			console.log("disable registered eventType " + eventType);
			graph.diagram.off(eventType, function(params) {
				graph.dispatchEvent(new Event(vaadinEventType));
			});
		}

		graph.$connector.addEdges = function(edges) {
			let edgesObject = JSON.parse(edges);
			graph.edges.add(edgesObject);
		}

		graph.$connector.updateEdges = function(edges) {
			alert('updateEdges: ' + edges);
		}

		graph.$connector.setNodes = function(index, nodes) {
			console.log("setNodes " + JSON.stringify(nodes));
			for (let i = 0; i < graph.nodes.length; i++) {
				// const itemsIndex = index + i;
				// console.log(typeof nodes[i])
				// console.log(typeof nodes[i].nodes)
				const node = JSON.parse(nodes[i].nodes);
				// console.log(JSON.stringify(node));
				graph.nodes.add(node);
			}

            nodesParent = graph.nodes;
		}

		graph.$connector.addNodes = function(nodes) {
			// console.log("addNodes: " + typeof nodes + "=" +
			// JSON.stringify(nodes));
			let nodesObject = JSON.parse(nodes);
			// console.log("addNodesParsed: " + typeof nodesObject + "=" +
			// JSON.stringify(nodesObject));
			graph.nodes.add(nodesObject);
		}

		graph.$connector.updateNodes = function(nodes) {
			alert('updateNodes: ' + nodes);
		}

		graph.$connector.clearNodes = function() {
			graph.nodes.clear();
		};

		graph.$connector.clearEdges = function() {
			graph.edges.clear();
		};

		graph.$connector.updateNodesSize = function(newSize) {
			const delta = newSize - graph.nodes.length;
			if (delta > 0) {
				graph.nodes.length = newSize;

				// graph.notifySplices("nodes", [{index: newSize - delta,
				// removed: [], addedCount : delta, object: graph.nodes, type:
				// "splice"}]);
			} else if (delta < 0) {
				const removed = graph.nodes.slice(newSize, graph.nodes.length);
				graph.nodes.splice(newSize);
				// graph.notifySplices("nodes", [{index: newSize, removed:
				// removed, addedCount : 0, object: graph.nodes, type:
				// "splice"}]);
			}
		};

		graph.$connector.updateEdgesSize = function(newSize) {
			const delta = newSize - graph.edges.length;
			if (delta > 0) {
				graph.edges.length = newSize;

				// graph.notifySplices("edges", [{index: newSize - delta,
				// removed: [], addedCount : delta, object: graph.edges, type:
				// "splice"}]);
			} else if (delta < 0) {
				const removed = graph.edges.slice(newSize, graph.edges.length);
				graph.edges.splice(newSize);
				// graph.notifySplices("edges", [{index: newSize, removed:
				// removed, addedCount : 0, object: graph.edges, type:
				// "splice"}]);
			}
		};
	}
}
