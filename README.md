# Bakglatt, en app for skientusiaster

Bakglatt ble utviklet i forbindelse med emnet IN2000 Software Engineering med prosjektarbeid våren 2021.

Funksjonalitet baserer seg på data fra API til Meterologisk Institutt for å vise værmelding og snøstatus for et utvalg skisteder i Norge. Denne informasjonen blir så prosessert til å gi en anbefaling for hvilken skismøring du bør bruke akkurat nå!

## Se appen
Bakglatt er kartbasert og bruker verktøyet laget av [Mapbox](https://www.mapbox.com/). Derfor trenger appen informasjon herifra for å fungere som den skal.
Fra Mapbox kan du hente to nøkler, en PrivateKey og en SecretKey. Dette må legges inn for bruk i applikasjonen.

I [gradle.properties](.../blob/main/gradle.properties) må du legge inn SecretKey i denne linjen
```MAPBOX_DOWNLOADS_TOKEN = <INSERT-MAPBOX_DOWNLOADS_TOKEN-HERE>```

I [strings.xml](.../blob/main/app/src/main/res/values/strings.xml) må du legge inn PrivateKey i denne linjen
```<string name="mapbox_access_token">INSERT-MAPBOX-ACCESS-TOKEN</string>```

For å kunne endre fargene basert på theme må de lages i [Mapbox Studio](https://www.mapbox.com/mapbox-studio). Dette må så legges inn i filen [MapFragment.kt](.../blob/main/app/src/main/java/com/example/skismoring/ui/map/MapFragment.kt), i disse linjene.
```var currentStyleUri = "<INSERT-MAPBOX-STYLE-URI>"```

Deretter må du synkronisere gradle for at den skal laste ned nødvendige filer før kjøring. Etter dette er det klart for kjøring!
