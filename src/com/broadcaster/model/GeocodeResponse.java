package com.broadcaster.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeocodeResponse {
    public String status;
    public List<GRAddress> results = new ArrayList<GRAddress>();

    public String getLocality() {
        for (GRAddress address : results) {
            for (String type : address.types) {
                if (type.equals("locality")) {
                    return address.formatted_address;
                }
            }
        }
        return null;
    }

    public class GRAddress {
        public Collection<String> types = new ArrayList<String>();
        public String formatted_address;
        public Collection<GRAddressComponent> address_components = new ArrayList<GRAddressComponent>();
        public GRGeometry geometry;

        public String getLocality() {
            for (GRAddressComponent address : address_components) {
                for (String type : address.types) {
                    if (type.equals("locality")) {
                        return address.short_name;
                    }
                }
            }
            return null;
        }
        
        public double getLat() {
            return geometry.location.lat;
        }
        
        public double getLng() {
            return geometry.location.lng;
        }
    }

    public class GRAddressComponent {
        public String long_name;
        public String short_name;
        public Collection<String> types = new ArrayList<String>();
    }

    public class GRGeometry {
        public GRLocation location;
        public String locationType;
        public GRArea viewport;
        public GRArea bounds;
    }

    public class GRArea {
        public GRLocation southWest;
        public GRLocation northEast;
    }

    public class GRLocation {
        public double lat;
        public double lng;
    }
}
