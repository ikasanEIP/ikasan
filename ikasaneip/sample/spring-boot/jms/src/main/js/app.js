'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
// end::vars[]

// tag::app[]
class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {flows: []};
    }

    componentDidMount() {
        client({method: 'GET', path: './rest/discovery/flows/sample-boot-jms'}).done(response => {
            this.setState({flows: response.entity});
    });
    }

    render() {
        return (
            <FlowList flows={this.state.flows}/>
    )
    }
}
// end::app[]

// tag::flow-list[]
class FlowList extends React.Component{
    render() {
        var flows = this.props.flows.map(flow =>
            <Flow key={flow.name} flow={flow}/>
    );
        return (
            <table>
            <tbody>
            <tr>
            <th>First Name</th>
        </tr>
        {flows}
    </tbody>
        </table>
    )
    }
}
// end::flow-list[]

// tag::flow[]
class Flow extends React.Component{
    render() {
        return (
            <tr>
            <td>{this.props.flow.name}</td>
        </tr>
    )
    }
}
// end::flow[]

// tag::render[]
ReactDOM.render(
<App />,
    document.getElementById('react')
)
// end::render[]

