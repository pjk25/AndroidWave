This page describes the functional requirements of the WAVE system implemented on Android, AndroidWave.

# Purpose

The purpose of AndroidWave is to abstract and provide additional fusion and
processing of sensor data on Android devices, available to other applications
through an API in a manner that provides the device's user with a rich set of
privacy controls.

# Logic

## Sensors

Wave must abstract built-in and attached sensors (i.e. Bluetooth Shimmer) in a
generic manner.

Sensors need to uniquely identified, including specifics such as shimmer v1
and v2 accelerometer. In the future we might want to provide a calibration of
such sensors within wave. Part of this component is the hybrid localization,
defined as a virtual sensor.

## Recipes

Recipes are the building block for privacy in the Wave system. A recipe acts
as a unit of negotiation for privacy. A recipe collects five pieces of
information into an atomic unit: input data, output data, granularity table,
the algorithm which generates output data from input data, and a clear,
subject readable description of the recipe. Clients receive data only after
they gave been granted a recipe authorization through the Wave UI.

Wave should manage recipes, including their download and verification of
authenticity from a recipe authority. Client applications will request a
authorization for a recipe, which the user can then approve or deny at a
certain granularity. Granularity refers to choice of frequency and precision
of input data (and output data as specified by the recipe's granularity
table).  When a recipe has been authorized, the algorithm provided with that
recipe is authenticated, verified, and dynamically loaded by Wave so that
recipe output can be delivered to the authorized client application.

When a user is presented with a request for recipe authorization, they should
receive an additional warning if the requesting application makes use of
internet access.

### Algorithm Engine

_to be written_

## Synchronization

Wave should provide a generic mechanism, possibly plug-in based, that allows
wave generated data to be synchronized to a repository or service, such as the
patient's Personal Health Record (PHR).

An HL7-out synchronizer would be a good start.

## Notifications

Wave should provide a mechanism for wave clients to send emergency
notifications back through wave and through an appropriate (based on desired
maximum latency) channel to an authorized endpoint. The typical channel would
be the subjects PHR (which could then be configured for additional
forwarding). An alternate emergency channel might be SMS (*note: technical
limitations of Android might not allow automated SMS sending). Any such
notification events would be logged in wave and synchronized to the subject's
PHR. This means we have to have a way to represent endpoints for data in the
system, such as a spouse or physician.

Clients apps can request the ability to send notifications to particular
endpoints.

# Client Application Interface

AndroidWave must provide a service and corresponding interface available to
client applications. Through this interface, the client application should be
able to request authorization for a given recipe (based on a recipe unique ID
and version), and in turn receive notification that the request has been
confirmed or denied. The interface should allow the client application to
register for the reception of recipe output data through the interface. The
client application should also be able to release or forfeit an authorized
recipe when it no longer has use for it.

# User Interface _(Management App)_

AndroidWave must provide a user interface for the management of Wave features.
The user interface should provide the ability to view a list of all currently
authorized recipes on the device. It should also provide a detail view for
each, including timestamps for authorization, as well as the ability to revoke
or modify the granularity of a recipe. The interface should also provide a
synchronization view, which details the status of data synchronization to
remote repositories.

Details:
Main tabbed view, with 3 three views
1st Tab - Recipe Authorizations as list view
2nd Tab - Logged Notifications
3rd Tab - Synchronization Status

Recipe Auth list: Click a single recipe to produce a detailed view of recipe
detailed recipe view should have recipe's sensors, it's output, a description
Authorize/Revoke authorization button.  When authorized timestamp.
