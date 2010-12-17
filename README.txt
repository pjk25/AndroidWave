
 README.txt
 CalFitWaveProject
 
 Created by Philip Kuryloski on 2010-12-10.

 ================
 = Introduction =
 ================

CalFitWAVE is the underlying service for the refactored CalFit application.

The idea is to make CalFit a lightweight app on top of a rich background
service that other similar applications can be rapidly created from.

 ================
 = Organization =
 ================

by Philip Kuryloski

Question - assume that all data exchange goes through WAVE?
What about sync model - under normal circumstances, all phone data is sync'd
to "cloud", where it is routed.  That routing is controlled by recipes, which
can be managed from either the phone or the "cloud" portal. Phone origin
forwarding is exceptionary -> driven by a lack of cloud connection or need for
expediation.

Or does the user authorize WAVE "apps" that use wave services - One benefit,
we don't have to worry about dev's breaking the assumption that outgoing data
is channeled back through WAVE.  However, since this is a research piece of
software, are we overstepping our bounds by trying to curtail this.  On an
absolute level, it's very difficult to enforce such a restriction, but at best
we can only provide devs with tools to make is simple to not break privacy
rules anyway.

The best approach is probably to make WAVE bi-directional, allowing apps to
essentially register with recipes (possibly embedded recipes in the app), and
have data streams unlocked to that app when a recipe is registered and
authorized. In other words, no data without recipe. Advantage of
bi-directional service is we automatically provide the "cloud" contingency
chain (of failing over to say, SMS when IP is unavailable, or direct IP when
the cloud is unavailable).

So in this case is an app a recipe? Should an app be allowed to manage
multiple recipes? Do we make an app export it's algorithm into the recipe?
(probably not as it would complicate WAVE even more, although it should
provide the recipe granularity tables in later/advanced versions of wave).

So what does WAVE provide then?
- internally abstracted sensors for use with recipes
- management of recipes (including linking/registration with WAVE apps)
- synchronization with Personal Health Record ("cloud")
- automatic routing based on message priority (including direct messaging from
  mobile through appropriate channel when sync is unavailable)
- ability to sync app-generated data (which conform to a registered recipe)

How is it constructed?
Is there a WAVE service and a WAVE UI as separate applications? Probably makes
sense to roll it into one, with special message interface between the two not
available to other apps. It would be nice to be able to shut down the WAVE UI
app without killing the service, but does a non-front UI use much resources in
Android? Probably simplest to use the 1-1 service for WAVE and WAVE UI

AIDL or Messenger class?
it appears AIDL allows for a richer definition, so it probably makes sense to
use that

So, we have the WAVE UI activity, which makes use of the WAVE service

WAVE UI - List of authorized recipes - and linked apps and destinations, which user can edit.
          List of recipe output destinations (entities or individuals identified by public key)
          Maybe manage sync and failover channels
              (i.e. Use SMS when IP unavailable?)
          Maybe list available sensors, indicating active sensors
          
          3 tab interface - recipes, destinations, sensors, settings

WAVE - Logic for sensors
       Logic for sync
       Logic for recipes
       Interface for WAVE UI - Get recipe list
                               Auth/deauth recipe
                               get sync status
                               *set sync settings
                               *get sensor list (all *w/active indications)
       Interface for WAVE Apps - Publish Recipe
                                 Register/request recipe(s) (should trigger switch to WAVE UI)
                                 Get sensor data (push and/or pull?)
                                 Push recipe data
                                 Push notification? (at varying priority/latency?)
                                 Get notification?

       Can the same service export two different interfaces?  It seems not.
       So do we create two services that access the same data? We don't really
       need a service for the UI, as it shouldn't be responsible for backgrounding anything.
       Now it seems we can have multiple interfaces, from a service, through
       multiple intents.  Good because the waveui can be separate, but bad
       because we really ought to authenticate the UI app then. We should use a
       LocalBinder

