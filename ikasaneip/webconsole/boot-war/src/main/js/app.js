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


            <div class="middle">

                <h2>Module</h2>

                <span id="moduleDescription"></span>

                <h3>Details</h3>
                <table id="schedulerDetails" class="keyValueTable">
                    <tr>
                        <th>
                            Module Name
                        </th>
                        <td>
                            moduleNae
                        </td>
                    </tr>
                </table>

                 <h3>Flows</h3>
                 <table id="initiatorsList" class="listTable">
                    <thead>
                        <tr>
                            <th>Flow Name</th>
                            <th>Type</th>
                            <th>Status</th>
                            <th>&nbsp;</th>
                        </tr>
                    </thead>

                    <tbody>
                        {flows}
                    </tbody>
                </table>
            </div>
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
// ReactDOM.render(
// <App />,
//     document.getElementById('react')
// )
// end::render[]

