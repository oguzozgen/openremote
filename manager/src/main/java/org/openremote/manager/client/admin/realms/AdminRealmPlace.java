/*
 * Copyright 2016, OpenRemote Inc.
 *
 * See the CONTRIBUTORS.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.manager.client.admin.realms;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import org.openremote.manager.client.admin.AdminPlace;

public class AdminRealmPlace extends AdminPlace {

    final String realmName;

    public AdminRealmPlace() {
        realmName = null;
    }

    public AdminRealmPlace(String realmName) {
        this.realmName = realmName;
    }

    public String getRealmName() {
        return realmName;
    }

    @Prefix("realm")
    public static class Tokenizer implements PlaceTokenizer<AdminRealmPlace> {

        @Override
        public AdminRealmPlace getPlace(String token) {
            return new AdminRealmPlace(token != null && token.length() > 0 ? token : null);
        }

        @Override
        public String getToken(AdminRealmPlace place) {
            return place.getRealmName() != null ? place.getRealmName() : "";
        }
    }

}
