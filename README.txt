
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
           - includes logic for establishing forwdarding channels
       Logic for recipes
       Interface for WAVE UI - get recipe list
                               auth/deauth recipe
                               auth/deauth forwarding channel
                               get sync status
                               *set sync settings
                               *get sensor list (all *w/active indications)
                               emergency channel list
       Interface for WAVE Apps - Publish Recipe
                                 Register/request recipe(s) (should trigger switch to WAVE UI)
                                 get recipe data
                                 Push notification? (at varying priority/latency?)
                                 Get notification?

       Can the same service export two different interfaces?  It seems not.
       So do we create two services that access the same data? We don't really
       need a service for the UI, as it shouldn't be responsible for backgrounding anything.
       Now it seems we can have multiple interfaces, from a service, through
       multiple intents.  Good because the waveui can be separate, but bad
       because we really ought to authenticate the UI app then. We should use a
       LocalBinder

       How do we publish a recipe?  We want to provide a way to authenticate
       recipes. If the recipe lives in an app, it's actual binary must be
       authenticated, which means it should be a compiled class which is
       checksummed and loaded.  If recipes are server based only, then we
       could just say that a trusted host distributes trusted recipes.

       Probably should deliver recipe as a compiled class, although we would
       want to protect wave's memory from that class.

       Basically this gives us in-wave and out-wave recipes. In wave recipes
       compute inside of wave, out recipes outside of it. They should be
       distributed as archives, for which there is a signature. The archive
       would include 1 or 2 parts. The first part is the public part,
       indicating data/recipe rates/precision, etc., the second is the
       optional compiled class for local computation. There must be a defined
       way to calculate a signature for that archive that can be verified via
       public/private key. Need a way to identify a recipe uniquely.

       Why do we need the compiled part? Just let the app do it?. A compiled
       part would let us make sure than an app only gets recipe output, that
       would be good. But we have to decide on how tightly to control that
       compiled component. So what about recipes that need offsite processing?
       Let's not go directly from the phone then. So how do we use the phone
       as a secure interface to manage recipes activated for remote web
       devices? The real management of recipes would come at the PHR. The
       phone would just be a place where those could be manipulated.  We still
       need local recipes on the phone, though.  In fact, we need local
       recipes in general, for the devices that execute a recipe, but then a
       compact way of representing these on the phone or web for management.
       
       Still need a way to handle phone collected, but off phone processed
       recipes.  That would be a multi-device recipe.  Should we have a master
       and slave recipes? A hierarchy of recipes? Or just one recipe that
       involves multiple devices. In that case, why not manage all recipes
       from the PHR? Need a way to identify device/sensor pairs then, a way to
       advertise devices.

       How to handle failover message forwarding from the phone then? It would
       only work with local recipes anyway, so maybe we don't need to control
       that.  It could just be a phone convenience provided by WAVE.

       Should a phone advertise sensors delivered over internet?  Or sensors
       and an internet link?  The user's PHR should be able to examine a graph
       of recipes, so maybe we don't have multi-device recipes.  That saves us
       from assuming that the links between multiple devices in a multi-device
       recipe are adequately secure.

       Do we have a special multi-device exception for the subject's devices
       and the PHR? Do we act as if the subject's devices and the PHR are one
       logical unit? On one hand it makes clear the convention that all
       outgoing data (from the user's devices) is logged at the PHR, at the
       cost of breaking the underlying single device per recipe convention. We
       should probably not break the underlying convention, and just design
       the PHR interface such that the link is clear. However, if a recipe
       that uses offsite computation through the PHR, but sensors from the
       phone, how do we manage this? Do we use 2 recipes? Do we call data
       forwarding from the phone not a recipe, do we view the PHR and phone as
       one unit with one recipe? Maybe we need to elevate the PHR as a logical
       unit, as the "hub" for recipes. In this case, all recipes would exist
       at the PHR level, just with portions implemented at different devices.
       Maybe we need to answer this question: Does it matter if the user is
       aware of distributed computing of data within their own info space as a
       privacy benefit?

       What is the basic that we can start from, that demonstrates the tech?
       Recipes that exist on the phone, and are synched to the PHR. PHR shows
       active recipes, and is used for data log and forward. It is assumed
       that all data flows through the PHR unless an exception occurs. We need
       a way to reason about these exceptions. They should flow through wave,
       and so they will be logged for later sync with the PHR as exception
       events. The initial type would be PHR unreachable exceptions (urgent
       message exceptions would be later introduced), which are triggered
       based on a need to meet a certain recipe output delivery latency. So,
       we add that recipes now need to include a maximum delivery latency. The
       phone should give an overview of it's emergency channels in the wave
       ui. A recipe which needs partial calculation at the PHR will be split
       in two pieces. Recipe output is uniquely identified by the unique
       recipe ID and by the recipe's output streams, and so this allows two
       recipes to be distributed as a pair.

       So what are the basic blocks here? The recipe, which is single device
       and specifies inputs, outputs, granularity table, output max latency.
       Wave on the phone needs to list recipes, and alternate/emergency
       channels.
       
       "Channels" - transmits sensor data from one device to another.  Useful
       for example, when a recipe on the PHR needs data from the phone.  It is
       characterized by sensors, rates, endpoints, and some measure of its
       security. Two levels of security may be misleading as users may think
       of them as "secure" and "unsecure", but the idea would be that an
       encrypted, authenticated link between smartphone and PHR would be of
       high security, while SMS would be less secure.

       Recipe Objects: need RecipeUID, factory ability for in and out
       "packets" of data. Need public XML component, optional private compiled
       class.  

       WaveRecipeOutputSample should have a priority

       WaveService should be bound by clients
       
       A remaining question - what about destinations within android client apps?
       