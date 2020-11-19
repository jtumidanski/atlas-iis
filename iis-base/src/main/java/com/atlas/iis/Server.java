package com.atlas.iis;

import java.net.URI;

import com.atlas.shared.rest.RestServerFactory;
import com.atlas.shared.rest.RestService;
import com.atlas.shared.rest.UriBuilder;

public class Server {
   public static void main(String[] args) {
      URI uri = UriBuilder.host(RestService.ITEM_INFORMATION).uri();
      RestServerFactory.create(uri, "com.atlas.iis.rest");
   }
}
