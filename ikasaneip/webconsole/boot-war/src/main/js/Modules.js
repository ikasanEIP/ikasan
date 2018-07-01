'use strict';
// tag::vars[]

import React, { Component } from "react";
import { Link } from 'react-router-dom'
const client = require('./client');


// end::vars[]

// tag::app[]
class Modules extends Component {

    constructor(props) {
        super(props);
        this.state = {modules: []};
    }

    componentDidMount() {
        client({method: 'GET', path: './rest/discovery/'}).done(response => {
            this.setState({modules: response.entity});
    });
    }

    render() {
        return (
            <ModuleList modules={this.state.modules}/>
    )
    }
}
// end::app[]


// tag::module-list[]
class ModuleList extends Component {
    render() {
        var modules = this.props.modules.map(module =>
            <Module key={module.name} module={module}/>
        );
        return (

            <table id="modulesList" className="listTable">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                </tr>
                </thead>

                <tbody>
                {modules}
                </tbody>
            </table>


    )
    }
}
// end::moule-list[]

// tag::module[]
class Module extends Component {
    render() {
        return (

            <tr>
                <td>
                    <Link  to={`/flows/${this.props.module.name}`} >{this.props.module.name}</Link>
                </td>
                <td>
                 {this.props.module.description}
                </td>
            </tr>
        );

    }
}
// end::module[]



export default Modules;