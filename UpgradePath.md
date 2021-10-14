[![Build Status](https://travis-ci.org/ikasanEIP/ikasan.svg?branch=3.1.x)](https://travis-ci.org/ikasanEIP/ikasan)

![Problem Domain](ikasaneip/developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Upgrade Path

## Summary
This document details the changes required to migrate from Ikasaneip-3.2.0 to Ikasaneip-3.3.0.
This is not a comprehensive list of all changes in the release, but a guide to how to upgrade from one version to the next.

## Required Changes

### Builder Fluent API
#### Change Summary
Previous versions of the fluent API allowed for evaluation constructs such as ```when(..)``` and ```otherwise(..)``` to be specified in any order.
To simplify and aid reading of the fluent API the ```otherwise(...)``` must now be the last construct, and no longer requires a further ```.build()``` specification to create the route.

#### Upgrade Steps
This change will potentially cause compilation errors on previous versions of Ikasan. 
This can be resolved by simply re-ordering the ```otherwise(..)``` to be the last option 
in the list of ```when(..)``` statements.

Additionally, the ```.build()``` is not longer required and can be removed.
