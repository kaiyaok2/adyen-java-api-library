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

public class ApplicationInfo {
    @SerializedName("adyenLibrary")
    private CommonField adyenLibrary;

    @SerializedName("adyenPaymentSource")
    private CommonField adyenPaymentSource;

    @SerializedName("merchantApplication")
    private CommonField merchantApplication;

    @SerializedName("merchantDevice")
    private MerchantDevice merchantDevice;

    @SerializedName("externalPlatform")
    private ExternalPlatform externalPlatform;

    @SerializedName("paymentDetailsSource")
    private CommonField paymentDetailsSource;

    @SerializedName("shopperInteractionDevice")
    private ShopperInteractionDevice shopperInteractionDevice;

    public CommonField getAdyenLibrary() {
        return adyenLibrary;
    }

    public void setAdyenLibrary(CommonField adyenLibrary) {
        this.adyenLibrary = adyenLibrary;
    }

    public CommonField getAdyenPaymentSource() {
        return adyenPaymentSource;
    }

    public void setAdyenPaymentSource(CommonField adyenPaymentSource) {
        this.adyenPaymentSource = adyenPaymentSource;
    }

    public CommonField getMerchantApplication() {
        return merchantApplication;
    }

    public void setMerchantApplication(CommonField merchantApplication) {
        this.merchantApplication = merchantApplication;
    }

    public MerchantDevice getMerchantDevice() {
        return merchantDevice;
    }

    public void setMerchantDevice(MerchantDevice merchantDevice) {
        this.merchantDevice = merchantDevice;
    }

    public ExternalPlatform getExternalPlatform() {
        return externalPlatform;
    }

    public void setExternalPlatform(ExternalPlatform externalPlatform) {
        this.externalPlatform = externalPlatform;
    }

    public CommonField getPaymentDetailsSource() {
        return paymentDetailsSource;
    }

    public void setPaymentDetailsSource(CommonField paymentDetailsSource) {
        this.paymentDetailsSource = paymentDetailsSource;
    }

    public ShopperInteractionDevice getShopperInteractionDevice() {
        return shopperInteractionDevice;
    }

    public void setShopperInteractionDevice(ShopperInteractionDevice shopperInteractionDevice) {
        this.shopperInteractionDevice = shopperInteractionDevice;
    }

    @Override
    public String toString() {
        return "ApplicationInfo{"
                + "adyenLibrary="
                + adyenLibrary
                + ", adyenPaymentSource="
                + adyenPaymentSource
                + ", merchantApplication="
                + merchantApplication
                + ", merchantDevice="
                + merchantDevice
                + ", externalPlatform="
                + externalPlatform
                + ", paymentDetailsSource="
                + paymentDetailsSource
                + ", shopperInteractionDevice="
                + shopperInteractionDevice
                + '}';
    }
}
