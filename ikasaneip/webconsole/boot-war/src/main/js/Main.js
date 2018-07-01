import React, { Component } from "react";
import {
    Route,
    NavLink,
    HashRouter
} from "react-router-dom";
import Flows from "./Flows";
import Modules from "./Modules";

class Main extends Component {
    render() {
        return (
            <HashRouter>
                <div>
                    <div id="subheader">
                        <ul id="mainNavigation">
                            <li><a href="<c:url value='/home.htm'/>">Home</a></li>
                            <li><NavLink to="/modules">Modules</NavLink></li>
                            <li><NavLink to="/flows">Flows</NavLink></li>
                        </ul>

                    </div>

                    <div id="middle" className="middle">
                        <Route path="/modules" component={Modules}/>
                        <Route path="/flows" component={Flows}/>
                    </div>
                </div>
            </HashRouter>
    );
    }
}

export default Main;