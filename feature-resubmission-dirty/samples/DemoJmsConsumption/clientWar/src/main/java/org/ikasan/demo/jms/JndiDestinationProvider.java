package org.ikasan.demo.jms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jndi.JndiTemplate;

public class JndiDestinationProvider {

	private JndiTemplate jndiTemplate;
	private List<String> parentPaths;

	@Autowired
	public JndiDestinationProvider(JndiTemplate jndiTemplate,
			List<String> parentPaths) {
		super();
		this.jndiTemplate = jndiTemplate;
		this.parentPaths = parentPaths;
	}

	public Destination getDestination(String destinationName) {
		Destination result = null;
		try {

			for (String parentPath : parentPaths) {
				result = findDestination(parentPath, destinationName);
				if (result != null) {
					break;
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		
		return result;
	}

	public List<Destination> getDestinations() {

		List<Destination> result = new ArrayList<Destination>();

		for (String parentPath : parentPaths) {
			Properties environment = jndiTemplate.getEnvironment();
			Context ctx;
			try {
				ctx = new InitialContext(environment);
				NamingEnumeration<NameClassPair> list = ctx.list(parentPath);

				while (list.hasMore()) {
					NameClassPair next = list.next();
					String name = next.getName();
					Destination lookup = findDestination(parentPath, name);
					result.add(lookup);

				}
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	public List<String> getDestinationNames() {
		List<String> destinationNames = new ArrayList<String>();
		try {
			for (Destination destination : getDestinations()) {
				if (destination instanceof Topic) {

					destinationNames.add(((Topic) destination).getTopicName());

				} else {
					destinationNames.add(((Queue) destination).getQueueName());
				}
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Collections.sort(destinationNames);
		return destinationNames;
	}

	private Destination findDestination(String parentPath, String name)
			throws NamingException {
		Object lookup = jndiTemplate.lookup(parentPath + "/" + name);

		if (!(lookup instanceof Destination)) {
			throw new RuntimeException(
					"Only expecting to find Destinations under [" + parentPath
							+ "]");
		}
		return (Destination) lookup;
	}
}
