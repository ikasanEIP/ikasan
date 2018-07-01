'use strict';
// tag::vars[]

import React, {Component} from "react";

const client = require('./client');
// end::vrs[]

// tag::FlowComponents[]
class FlowComponents extends Component {

    constructor(props) {
        super(props);
        this.state = {components: []};

    }

    componentDidMount() {


    }

    render() {
        var components = this.props.components.map(component =>
            <FlowComponent key={component.name} component={component}/>
        );
        return (

            <div className="middle">
                <h3>Flow Elements</h3>

                <table id="flowElementsList" className="listTable">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Component Type</th>
                    </tr>
                    </thead>

                    <tbody>
                    {components}
                    </tbody>
                </table>
            </div>
        )
    }
}
// end::FlowComponents[]


// tag::FlowComponent[]
class FlowComponent extends Component {
    render() {
        return (
            <tr>
                <td>
                    <Link
                        to={`/flows/${this.props.flows.module}/${this.props.flow.name}/component/`}>{this.props.flow.name}</Link>
                </td>
                <td>
                    Type
                </td>

            </tr>
        );
    }
}

// end::flow[]


export default FlowComponents;