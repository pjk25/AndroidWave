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

### Notable Packages ###

* **edu.berkeley.androidwave.waveclient** package which must be imported by
  AndroidWave client applications. It contains the definition of the public
  WaveService API for calling into the WaveService, the reverse direction
  interface which clients must implement to receive data, and the Parcelable
  objects which are exchanged over these interfaces. This package is contained
  in the [waveclient](https://github.com/pjk25/waveclient) submodule.

* **edu.berkeley.androidwave.waveexception** subclasses of Exception used
  within AndroidWave

* **edu.berkeley.androidwave.waverecipe** package containing the WaveRecipe
  and related classes. The WaveRecipe class is an in-memory representation of
  a recipe.
  
  * **.waverecipealgorithm** package which must be imported by waverecipe
    implementations.  Includes the interface which a recipe's data processing
    algorithm must implement.  This package is contained in the
    [waverecipealgorithm](https://github.com/pjk25/waverecipealgorithm)
    submodule.
    
* **edu.berkeley.androidwave.waveservice** package containing the sensing
  subsystem of AndroidWave.
  
  * **.sensors** package containing WaveSensor, the abstract parent class
    representing a sensor on the device.  The package also contain subclasses
    which wrap Android's existing hardware sensors.
    
* **edu.berkeley.androidwave.waveui** the user interface classes.

## Related Projects ##

* Submodules:
  * https://github.com/pjk25/waveclient
  * https://github.com/pjk25/waverecipealgorithm

* Sample Clients
  * https://github.com/pjk25/WaveClientSample
  * https://github.com/pjk25/AndroidWaveTesterClient
  * https://github.com/pjk25/WaveLogger
  
* Recipes
  * https://github.com/pjk25/AndroidWaveRecipes
  * https://github.com/pjk25/PassThroughRecipes

## About ##

The primary codebase (as of July 2011) was created by Philip Kuryloski while
working as a postdoctoral scholar at the University of California, Berkeley,
in 2011.