window.Vaadin.Flow.networkDiagramConnector = {
	initLazy : function(graph, initialNodes, initialEdges, options) {

		// Check whether the connector was already initialized for the Iron list
		if (graph.$connector) {
			return;
		}
		console.log('init networkDiagramConnector');

		graph.$connector = {};

		console.log(initialNodes);
		var nodesParent = JSON.parse(initialNodes);

		graph.nodes = new vis.DataSet(nodesParent);
		graph.edges = new vis.DataSet(JSON.parse(initialEdges));

        graph.$connector.updateNodeStates = function(nodesStates) {
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
		graph.options.manipulation.addNode = function(nodeData, callback) {
			if (customNodeifAdded == true) {
				nodeData.label = customNodeLabel;
				nodeData.id = customNodeID;
			}
			self.onManipulationNodeAdded(nodeData);
			callback(nodeData);
		};
		graph.options.manipulation.addEdge = function(edgeData, callback) {
			if (customEdgeifAdded == true) {
				edgeData.label = customEdgeLabel;
				edgeData.id = customEdgeID;
			}
			self.onManipulationEdgeAdded(edgeData);
			callback(edgeData);
		};
		graph.options.manipulation.deleteNode = function(nodeData, callback) {
			self.onManipulationNodeDeleted(nodeData);
			callback(nodeData);
		};
		graph.options.manipulation.deleteEdge = function(edgeData, callback) {
			self.onManipulationEdgeDeleted(edgeData);
			callback(edgeData);
		};
		graph.options.manipulation.editEdge = function(edgeData, callback) {
			self.onManipulationEdgeEdited(edgeData);
			callback(edgeData);
		};
		console.log("networkdiagram options: " + JSON.stringify(graph.options));
		graph.$connector.diagram = new vis.Network(graph, {
			nodes : graph.nodes,
			edges : graph.edges
		}, graph.options);

		// Enable event dispatching to vaadin only for registered eventTypes to
		// avoid to much overhead.
		graph.$connector.enableEventDispatching = function(vaadinEventType) {
			const eventType = vaadinEventType.substring(7);
			graph.$connector.diagram
					.on(
							eventType,
							function(params) {
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
													function(key, value) {
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
											detail : params
										}));
							});
		}

        var currentRadius = 0;
		var animateRadius = true;
        var colorCircle = 'rgba(0, 255, 0, 0.8)';
        var colorBorder = 'rgba(0, 255, 0, 0.2)';
        var updateFrameVar = setInterval(function() { updateFrameTimer(); }, 60);

        function updateFrameTimer() {
            if (animateRadius) {
                graph.$connector.diagram.redraw();
                currentRadius += 0.05;
            }
        }

        graph.$connector.diagram.on("beforeDrawing", function(ctx) {
            if (animateRadius) {
                var inode;
                var nodePositions = graph.$connector.diagram.getPositions();
                var arrayLength = graph.nodes.length;
                for (inode = 0; inode < arrayLength; inode++) {
                    var radius = Math.abs(50 * Math.sin(currentRadius + inode / 50.0));
                    var node = nodesParent[inode];
                    ctx.strokeStyle = node.edgeColour;
                    ctx.fillStyle = node.fillColour;
                    var nodePosition = nodePositions[node.id];
                    ctx.circle(nodePosition.x, nodePosition.y, radius);
                    ctx.fill();
                    ctx.stroke();
                }
            };
        });

        graph.$connector.diagram.on("afterDrawing", function(ctx) {
            if (animateRadius) {
                var inode;
                var nodePositions = graph.$connector.diagram.getPositions();
                var arrayLength = graph.nodes.length;
                for (inode = 0; inode < arrayLength; inode++) {
                    var node = nodesParent[inode];
                    var nodePosition = nodePositions[node.id];

                    if(node.foundStatus === "FOUND" || node.foundStatus === "NOT_FOUND")
                    {
                        var img = new Image();
                        img.src = node.foundImage;
                        ctx.drawImage(img, nodePosition.x + 35, nodePosition.y - 15, 15, 15);
                    }
                }
            };
        });

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
