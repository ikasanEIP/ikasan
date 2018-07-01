'use strict';
// tag::vars[]

import React, {Component} from "react";

const client = require('./client');
// end::vrs[]

// tag::app[]
class Flows extends Component {

    constructor(props) {
        super(props);
        this.state = {flows: [], moduleName: ""};
    }


    componentDidMount() {

        const {match: {params}} = this.props;
        //console.dir('moduleName', {this.props.match.params.moduleName});
        var moduleName = this.props.match.params.moduleName;
        var path = "./rest/discovery/flows/" + moduleName;

        this.setState({moduleName: moduleName});

        client({method: 'GET', path: path}).done(response => {
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
class FlowList extends Component {
    render() {
        var flows = this.props.flows.map(flow =>
            <Flow key={flow.name} flow={flow}/>
        );
        return (

            <div className="middle">
                <h3>Flows</h3>
                <table id="initiatorsList" className="listTable">
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
class Flow extends Component {
    render() {
        return (
            <tr>
                <td>
                    <Link  to={`/flows/${this.props.flows.module}/${this.props.flow.name}/components/`} >{this.props.flow.name}</Link>
                </td>
                <td>type</td>
                <td className={`initiatorState-${this.props.flow.state}`}>
                {this.props.flow.state}
                </td>
                <td>button</td>
            </tr>
        )
    }
}

// end::flow[]


export default Flows;