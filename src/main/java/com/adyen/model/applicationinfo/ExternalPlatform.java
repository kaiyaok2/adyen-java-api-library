/*
 *                       ######
 *                       ######
 * ############    ####( ######  #####. ######  ############   ############
 * #############  #####( ######  #####. ######  #############  #############
 *        ######  #####( ######  #####. ######  #####  ######  #####  ######
 * ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 * ###### ######  #####( ######  #####. ######  #####          #####  ######
 * #############  #############  #############  #############  #####  ######
 *  ############   ############  #############   ############  #####  ######
 *                                      ######
 *                               #############
 *                               ############
 *
 * Adyen Java API Library
 *
 * Copyright (c) 2017 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 */
package com.adyen.model.applicationinfo;

import com.google.gson.annotations.SerializedName;

public class ExternalPlatform {
    @SerializedName("integrator")
    private String integrator;

    public String getIntegrator() {
        return integrator;
    }

    public void setIntegrator(String integrator) {
        this.integrator = integrator;
    }

    @Override
    public String toString() {
        return "ExternalPlatform{" + "integrator='" + integrator + '\'' + '}';
    }
}
