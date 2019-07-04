package org.ikasan.vaadin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import org.ikasan.vaadin.visjs.network.Edge;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.Options;

@SuppressWarnings("serial")
@Route("")
public class DemoView extends VerticalLayout {

  public DemoView() {
    setMargin(true);
    setPadding(true);
    setSpacing(true);
    // setWidth("400px");

    final NetworkDiagram nd =
        new NetworkDiagram(Options.builder().withHeight("400px").withWidth("400px").build());

    final List<Node> nodes = new LinkedList<>();
    nodes.add(new Node("1", "Label 1"));
    nodes.add(new Node("2", "Label 2"));
    nodes.add(new Node("3", "Label 3"));
    nodes.add(new Node("4", "Label 4"));
    final ListDataProvider<Node> dataProvider = new ListDataProvider<>(nodes);
    nd.setNodesDataProvider(dataProvider);
    nd.setEdges(new Edge("1", "2"), new Edge("2", "3"), new Edge("2", "4"), new Edge("3", "1"));
    final Registration registration = nd.addSelectNodeListener(
        ls -> Notification.show("NodeId selected " + ls.getParams().getArray("nodes").toJson()));
    add(nd);
    add(new HorizontalLayout(new Button("Add Node", e -> {
      final String id = UUID.randomUUID().toString();
      nodes.add(new Node(id, id));
      dataProvider.refreshAll();
    }), new Button("remove all Nodes", e -> {
      nodes.clear();
      dataProvider.refreshAll();
      registration.remove();
    }), new Button("fit", e -> {
      nd.diagramFit();
    }), new Button("selectNode", e -> {
      nd.diagramSelectNodes(Arrays.asList(new String[] {"2", "3"}));
    }), new Button("unselectAll", e -> {
      nd.diagramUnselectAll();;
    })));
  }

}
