package com.universidad.parchate.ui.screens.map

import com.google.android.gms.maps.model.MapStyleOptions

internal val ParchateMapStyleOptions = MapStyleOptions(
    """
    [
      {
        "elementType": "geometry",
        "stylers": [{"color": "#1e1c32"}]
      },
      {
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#d8cfe5"}]
      },
      {
        "elementType": "labels.text.stroke",
        "stylers": [{"color": "#1e1c32"}]
      },
      {
        "featureType": "administrative",
        "elementType": "geometry.stroke",
        "stylers": [{"color": "#4a456e"}]
      },
      {
        "featureType": "poi",
        "elementType": "geometry",
        "stylers": [{"color": "#272440"}]
      },
      {
        "featureType": "poi.park",
        "elementType": "geometry",
        "stylers": [{"color": "#213842"}]
      },
      {
        "featureType": "road",
        "elementType": "geometry",
        "stylers": [{"color": "#35325a"}]
      },
      {
        "featureType": "road",
        "elementType": "geometry.stroke",
        "stylers": [{"color": "#24213d"}]
      },
      {
        "featureType": "road",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#ff9fb4"}]
      },
      {
        "featureType": "transit",
        "elementType": "geometry",
        "stylers": [{"color": "#3a365b"}]
      },
      {
        "featureType": "water",
        "elementType": "geometry",
        "stylers": [{"color": "#ff5277"}]
      },
      {
        "featureType": "water",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#fff2f5"}]
      }
    ]
    """.trimIndent()
)

internal val ParchateEventsMapStyleOptions = MapStyleOptions(
    """
    [
      {
        "elementType": "geometry",
        "stylers": [{"color": "#171428"}]
      },
      {
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#dcd6eb"}]
      },
      {
        "elementType": "labels.text.stroke",
        "stylers": [{"color": "#171428"}]
      },
      {
        "featureType": "administrative",
        "elementType": "geometry.stroke",
        "stylers": [{"color": "#4d496c"}]
      },
      {
        "featureType": "administrative.land_parcel",
        "stylers": [{"visibility": "off"}]
      },
      {
        "featureType": "landscape",
        "elementType": "geometry",
        "stylers": [{"color": "#151224"}]
      },
      {
        "featureType": "poi",
        "stylers": [{"visibility": "off"}]
      },
      {
        "featureType": "road",
        "elementType": "geometry",
        "stylers": [{"color": "#312d4f"}]
      },
      {
        "featureType": "road.arterial",
        "elementType": "geometry",
        "stylers": [{"color": "#433d63"}]
      },
      {
        "featureType": "road.highway",
        "elementType": "geometry",
        "stylers": [{"color": "#554f79"}]
      },
      {
        "featureType": "road",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#b6b0d0"}]
      },
      {
        "featureType": "road.highway",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#ffd2db"}]
      },
      {
        "featureType": "transit",
        "stylers": [{"visibility": "off"}]
      },
      {
        "featureType": "water",
        "elementType": "geometry",
        "stylers": [{"color": "#1f2747"}]
      },
      {
        "featureType": "water",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#f4eef9"}]
      }
    ]
    """.trimIndent()
)
