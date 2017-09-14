/*
 * $Id$  
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.dashboard.ui.topology.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.graph.Arc.Direction;
import com.vaadin.graph.GraphRepository;

/**
 * Simple memory-only implementation of GraphRepository interface
 * 
 */
public class SimpleGraphRepositoryImpl implements
		GraphRepository<NodeImpl, ArcImpl>, Serializable
{

	private static final long serialVersionUID = 1L;

	private String homeNodeId;

	private Map<String, NodeImpl> nodeMap = new HashMap<String, NodeImpl>();

	private Map<String, ArcImpl> edgeMap = new HashMap<String, ArcImpl>();

	// edge-id -> head-node-id
	private Map<String, String> headMap = new HashMap<String, String>();

	// edge-id -> tail-node-id
	private Map<String, String> tailMap = new HashMap<String, String>();

	// node-id -> incoming-edge-id
	private Map<String, Set<String>> incomingMap = new HashMap<String, Set<String>>();

	// node-id -> outgoing-edge-id
	private Map<String, Set<String>> outgoingMap = new HashMap<String, Set<String>>();

	// private Map<String, GNode> nodeMap;

	public NodeImpl getTail(ArcImpl arc)
	{
		return nodeMap.get(tailMap.get(arc.getId()));
	}

	public NodeImpl getHead(ArcImpl arc)
	{
		return nodeMap.get(headMap.get(arc.getId()));
	}

	public Iterable<String> getArcLabels()
	{
		List<String> ret = new ArrayList<String>(edgeMap.size());
		for (ArcImpl e : edgeMap.values())
		{
			ret.add(e.getLabel());
		}
		return ret;
	}

	public Collection<ArcImpl> getArcs(NodeImpl node, String label,
			Direction dir)
	{
		Set<String> idset;
		if (Direction.INCOMING == dir)
		{
			idset = incomingMap.get(node.getId());
		} else
		{
			idset = outgoingMap.get(node.getId());
		}
		List<ArcImpl> result = new ArrayList<ArcImpl>();
		if (idset != null)
		{
			for (String eid : idset)
			{
				ArcImpl arc = edgeMap.get(eid);
				if (arc.getLabel().equals(label))
				{
					result.add(arc);
				}
			}
		}
		return result;
	}

	public NodeImpl getHomeNode()
	{
		return nodeMap.get(homeNodeId);
	}

	public NodeImpl getOpposite(NodeImpl node, ArcImpl arc)
	{
		String hnid = headMap.get(arc.getId());
		String tnid = tailMap.get(arc.getId());

		if (hnid != null && tnid != null)
		{
			if (hnid.equals(node.getId()))
			{
				// given node is head so return tail as an opposite
				return nodeMap.get(tnid);
			} else if (tnid.equals(node.getId()))
			{
				// given node is tail so return head as an opposite
				return nodeMap.get(hnid);
			} else
			{
				// what is this edge ?
				return null;
			}
		} else
		{
			// not a node of the graph
			return null;
		}
	}

	public NodeImpl getNodeById(String id)
	{
		return nodeMap.get(id);
	}

	public String getHomeNodeId()
	{
		return homeNodeId;
	}

	public void setHomeNodeId(String homeNodeId)
	{
		this.homeNodeId = homeNodeId;
	}

	public NodeImpl addNode(String id, String label)
	{
		NodeImpl n = new NodeImpl(id, label);
		nodeMap.put(id, n);
		return n;
	}

	public ArcImpl joinNodes(String nid1, String nid2, String eid, String label)
	{
		ArcImpl e = new ArcImpl(eid, label);
		edgeMap.put(eid, e);
		headMap.put(eid, nid1);
		tailMap.put(eid, nid2);

		addToOutgoing(nid1, eid);
		addToIncomming(nid2, eid);
		return e;
	}

	public void clear()
	{
		homeNodeId = null;
		nodeMap.clear();
		edgeMap.clear();
		headMap.clear();
		tailMap.clear();
		incomingMap.clear();
		outgoingMap.clear();
	}

	protected void addToOutgoing(String nid, String eid)
	{
		Set<String> s = outgoingMap.get(nid);
		if (s == null)
		{
			s = new HashSet<String>();
			outgoingMap.put(nid, s);
		}
		s.add(eid);
	}

	protected void addToIncomming(String nid, String eid)
	{
		Set<String> s = incomingMap.get(nid);
		if (s == null)
		{
			s = new HashSet<String>();
			incomingMap.put(nid, s);
		}
		s.add(eid);
	}
}