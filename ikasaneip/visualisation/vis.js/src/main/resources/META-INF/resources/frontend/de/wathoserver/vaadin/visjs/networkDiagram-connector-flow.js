window.Vaadin.Flow.networkDiagramConnector = {
	initLazy : function(graph, initialNodes, initialEdges, options) {

        // Check whether the connector was already initialized for the Iron list
        if (graph.$connector) {
            return;
        }
        console.log('init networkDiagramConnector');

        graph.$connector = {};

        var drawn = false;

        console.log(initialNodes);
        var nodesParent = JSON.parse(initialNodes);

        graph.nodes = new vis.DataSet(nodesParent);
        graph.edges = new vis.DataSet(JSON.parse(initialEdges));

        graph.$connector.updateNodeStates = function (nodesStates) {
            nodesParent = JSON.parse(nodesStates);
        };

        var self = this;
        var customNodeifAdded = false;
        var customNodeID;
        var customNodeLabel;
        var customEdgeifAdded = false;
        var customEdgeID;
        var customEdgeLabel;

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

        // Enable event dispatching to vaadin only for registered eventTypes to
        // avoid to much overhead.
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

        // graph.$connector.diagram.on("beforeDrawing", function (ctx) {
        //     var inode;
        //     var nodePositions = graph.$connector.diagram.getPositions();
        //     var arrayLength = graph.nodes.length;
        //     for (inode = 0; inode < arrayLength; inode++) {
        //         var node = nodesParent[inode];
        //         var nodePosition = nodePositions[node.id];
        //
        //         if (node.foundStatus === "FOUND" || node.foundStatus === "NOT_FOUND") {
        //             var img = new Image();
        //             img.src = node.foundImage;
        //             ctx.drawImage(img, nodePosition.x + 50, nodePosition.y - 25, 15, 15);
        //         }
        //     }
        // });

        graph.$connector.drawNodeFoundStatus = function () {
            graph.$connector.diagram.on("beforeDrawing", function (ctx) {
                var inode;
                var nodePositions = graph.$connector.diagram.getPositions();
                var arrayLength = graph.nodes.length;
                for (inode = 0; inode < arrayLength; inode++) {
                    var node = nodesParent[inode];
                    var nodePosition = nodePositions[node.id];

                    if (node.wiretapFoundStatus === "FOUND") {
                        var img = new Image();
                        img.src = node.wiretapFoundImage;
                        ctx.drawImage(img, nodePosition.x + 50, nodePosition.y - 25, 15, 15);
                    }
                    else if (node.wiretapFoundStatus === "NOT_FOUND") {
                        ctx.clearRect(nodePosition.x + 50, nodePosition.y - 25, 15, 15);
                    }
                }
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

                var stroke = true;
                var radius = 20;
                var fill = false;

                if (typeof radius === 'number') {
                    radius = {tl: radius, tr: radius, br: radius, bl: radius};
                } else {
                    var defaultRadius = {tl: 0, tr: 0, br: 0, bl: 0};
                    for (var side in defaultRadius) {
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

                // ctx.drawImage(HTMLImageElement("/images/ikasan-titling-transparent.png"), 0, 0);
            });

            // graph.draw();
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
                // ctx.back

                var stroke = true;
                var radius = 20;
                var fill = true;

                if (typeof radius === 'number') {
                    radius = {tl: radius, tr: radius, br: radius, bl: radius};
                } else {
                    var defaultRadius = {tl: 0, tr: 0, br: 0, bl: 0};
                    for (var side in defaultRadius) {
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

            // graph.draw();
        }

        // var animateStatus = true;
        // var updateSrarusVar = setInterval(function() { updateFrameTimer(); }, 1000);
        //
        // function updateFrameTimer() {
        //     if (animateStatus) {
        //
        //         currentRadius += 0.05;
        //     }
        // }

        graph.$connector.drawStatusBorder = function (x, y, width, height, colour) {
            graph.$connector.diagram.on("beforeDrawing", function (ctx) {

                ctx.lineWidth=5.0
                ctx.beginPath();
                ctx.setLineDash([0, 0]);
                ctx.strokeStyle = colour;
                ctx.fillStyle = 'rgba(224,224,224,0.5)';
                // ctx.back

                var stroke = true;
                var radius = 20;
                var fill = false;

                if (typeof radius === 'number') {
                    radius = {tl: radius, tr: radius, br: radius, bl: radius};
                } else {
                    var defaultRadius = {tl: 0, tr: 0, br: 0, bl: 0};
                    for (var side in defaultRadius) {
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

            // graph.draw();
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
