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
