# AndroidWave #

AndroidWave is a UC Berkeley project.

## Introduction ##

AndroidWave is privacy aware sensor API for Android devices, targeted at
fitness and medically oriented applications. Privacy control is based around a
unit called a Recipe. Recipes are a unit of privacy negotiation, allowing the
user to grant applications access to information derived from the phones
sensor based upon an explanation of the purpose of the outgoing data.  Recipes
are signed to provide proof of their authenticity.  Client applications are
then provided with an API for the WaveService that allows them to quickly and
easily leverage the public set of recipes for data such as an estimation of
energy expenditure.

## Organization ##

The primary component of AndroidWave is the **WaveService**. WaveService
handles sensor abstraction, recipes, and data synchronization. WaveService
provides two interfaces, a local private interface for the Wave UI, and a
public interface for Wave Client Applications. The WaveUI allows the user to
manage the authorization (and de-authorization) of recipes, and is primarily a
privacy oriented interface.

### WaveService ###

WaveService has two primary sub-components: The recipes engine & The sync
engine. The recipes engine provides the recipe "runtime" in which downloaded
and verified recipes are executed. The sync engine in turn handles
synchronization of data. Both are mentioned as they will have a _plugin_
architecture.

## Approach ##

We intend to use a test-driven approach to building this project. The skeleton project already contains test code.

## Additional Documentation ##

See the [wiki](https://github.com/pjk25/AndroidWave/wiki) for additional documentation.
