'use strict';
// tag::vars[]

import React, { Component } from "react";

const client = require('./client');


// end::vars[]

// tag::app[]
class Flows extends Component {

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
class FlowList extends Component{
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
class Flow extends Component{
    render() {
        return (
            <tr>
                <td>{this.props.flow.name}</td>
            </tr>
    )
    }
}
// end::flow[]



export default Flows;