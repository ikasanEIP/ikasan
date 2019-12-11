# Vis.js Network diagram for Vaadin Framework

[![Published on Vaadin Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/visjs-vaadin-component) 
[![Vaadin Directory version for visjs-vaadin-component](http://img.shields.io/vaadin-directory/version/visjs-vaadin-component.svg)](https://vaadin.com/directory/component/visjs-vaadin-component)
[![Vaadin Directory release for visjs-vaadin-component](http://img.shields.io/vaadin-directory/release-date/visjs-vaadin-component.svg)](https://vaadin.com/directory/component/visjs-vaadin-component)
[![Vaadin Directory star rating for visjs-vaadin-component](http://img.shields.io/vaadin-directory/star/visjs-vaadin-component.svg)](https://vaadin.com/directory/component/visjs-vaadin-component)
[![Vaadin Directory star rating for visjs-vaadin-component](http://img.shields.io/vaadin-directory/rating-count/visjs-vaadin-component.svg)](https://vaadin.com/directory/component/visjs-vaadin-component)

## Introduction
[Vis.js](http://visjs.org) is a dynamic, browser based visualization library. The library uses JSON for configuration and communication. 

![example](doc/simpleexample.png)

## About vis.js in this component
 - Implements the **latest vis.js network module (4.21.0)** version for **Vaadin >= 10**.
 - Integrates a hot fix for a regression until the next release. See https://github.com/almende/vis/commit/f6445cdf07df337ef829d27542823868d2a33873

## About the addon
 - It only implements the network module.
 - The vis.js library is developed by Almende B.V, as part of CHAP. Vis.js runs fine on Chrome, Firefox, Opera, Safari, IE9+, and most mobile browsers (with full touch support).
 - Visit [http://visjs.org/docs/network/index.html](http://visjs.org/docs/network/index.html) for the documentation on visjs.
 - Visit http://visjs.org/network_examples.html for network examples in JS. Not everything is possible with this addon.

## What is implemented?
Everything in here [vis.js network](http://visjs.org/docs/network/), except

* Clustering
* Graph methods that return objects (in progress)
* Loading data from DOT or Gephi

## Development instructions

Starting the test/demo server:
```
mvn jetty:run
```

This deploys a simple demo at http://localhost:8080. For more advanced demo see TODO (https://visjs-vaadin-component.herokuapp.com/)
