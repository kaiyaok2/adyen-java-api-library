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
 * Copyright (c) 2021 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 */
package com.adyen;

import com.adyen.enums.Gender;
import com.adyen.enums.VatCategory;
import com.adyen.httpclient.AdyenHttpClient;
import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.AbstractPaymentRequest;
import com.adyen.model.Address;
import com.adyen.model.Amount;
import com.adyen.model.AuthenticationResultRequest;
import com.adyen.model.Name;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentRequest3d;
import com.adyen.model.PaymentRequest3ds2;
import com.adyen.model.RequestOptions;
import com.adyen.model.ThreeDS2RequestData;
import com.adyen.model.additionalData.InvoiceLine;
import com.adyen.model.checkout.DefaultPaymentMethodDetails;
import com.adyen.model.checkout.LineItem;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PersonalDetails;
import com.adyen.model.modification.AbstractModificationRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.DonationRequest;
import com.adyen.model.modification.RefundRequest;
import com.adyen.model.modification.VoidPendingRefundRequest;
import com.adyen.model.nexo.AmountsReq;
import com.adyen.model.nexo.MessageCategoryType;
import com.adyen.model.nexo.MessageClassType;
import com.adyen.model.nexo.MessageHeader;
import com.adyen.model.nexo.MessageType;
import com.adyen.model.nexo.PaymentTransaction;
import com.adyen.model.nexo.SaleData;
import com.adyen.model.nexo.SaleToPOIRequest;
import com.adyen.model.nexo.TransactionIdentification;
import com.adyen.model.terminal.TerminalAPIRequest;
import com.adyen.util.DateUtil;
import com.adyen.util.Util;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseTest {
    protected static final Gson PRETTY_PRINT_GSON = new GsonBuilder().setPrettyPrinting().create();
    protected static final Gson GSON = new Gson();
    protected static final ObjectMapper OBJECT_MAPPER =  new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    public static final String DUMMY_PROTOCOL_IMAGE_URL = "dummy_protocol/image_url/";
    public static final String DUMMY_PROTOCOL_PRODUCT_URL = "dummy_protocol/product_url/";

    /**
     * Helper Function to determine if two JSON-Like strings are equal under order permutation.
     * @param firstInput, secondInput: two objects to be compared.
     * @return Boolean signifies equality or not.
     */
    public static boolean jsonStringEqual(String firstInput, String secondInput) throws JSONException {
        Object firstObject = jsonStringToMapOrSet(firstInput);
        Object secondObject = jsonStringToMapOrSet(secondInput);
        return firstObject.equals(secondObject);
    }

    /**
     * Helper Function (Recursive) to convert JsonString to a nested map(w.r.t JSONObject)/set(w.r.t. JSONArray) structure.
     * @param input: JSON string to be converted.
     * @return Converted Map
     */
    private static Object jsonStringToMapOrSet(String input) throws JSONException {
        if (input.charAt(0) != '{' && input.charAt(0) != '[') {
            return input;
        } else if (input.charAt(0) == '[') {
            JSONArray array = new JSONArray(input);
            Set < Object > jsonSet = new HashSet < > ();
            for (int i = 0; i < array.length(); i++) {
                jsonSet.add(jsonStringToMapOrSet(array.get(i).toString()));
            }
            return jsonSet;
        } else {
            JSONObject object = new JSONObject(input);
            Iterator < String > keys = object.keys();
            Map < String, Object > jsonMap = new HashMap < > ();
            while (keys.hasNext()) {
                String key = keys.next();
                jsonMap.put(key, jsonStringToMapOrSet((object.get(key)).toString()));
            }
            return jsonMap;
        }
    }

    /**
     * Returns a Client object that has a mocked response
     */
    protected Client createMockClientFromResponse(String response) {
        AdyenHttpClient adyenHttpClient = mock(AdyenHttpClient.class);
        try {
            when(adyenHttpClient.request(anyString(), anyString(), any(Config.class), anyBoolean(), any(RequestOptions.class))).thenReturn(response);
            when(adyenHttpClient.request(anyString(), anyString(), any(Config.class), anyBoolean(), isNull())).thenReturn(response);
            when(adyenHttpClient.request(anyString(), any(), any(Config.class), anyBoolean(), isNull(), any())).thenReturn(response);
            when(adyenHttpClient.request(anyString(), any(), any(Config.class), anyBoolean(), isNull(), any(), any())).thenReturn(response);

        } catch (IOException | HTTPClientException e) {
            e.printStackTrace();
        }
        Client client = new Client();
        client.setHttpClient(adyenHttpClient);

        Config config = new Config();
        config.setHmacKey("DFB1EB5485895CFA84146406857104ABB4CBCABDC8AAF103A624C8F6A3EAAB00");
        config.setCheckoutEndpoint(Client.CHECKOUT_ENDPOINT_TEST);
        client.setConfig(config);

        return client;
    }

    /**
     * Returns a Client object that has a mocked response from fileName
     */
    protected Client createMockClientFromFile(String fileName) {
        String response = getFileContents(fileName);

        return createMockClientFromResponse(response);
    }

    /**
     * Helper for file reading
     */
    public String getFileContents(String fileName) {
        String result = "";

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            byte[] buffer = new byte[1024];
            int length;
            InputStream fileStream = classLoader.getResourceAsStream(fileName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while ((length = fileStream.read(buffer)) != - 1) {
                outputStream.write(buffer, 0, length);
            }
            result = outputStream.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Populates the basic parameters (browser data, merchant account, shopper IP)
     */
    protected <T extends AbstractPaymentRequest> T createBasePaymentRequest(T abstractPaymentRequest) {
        abstractPaymentRequest.merchantAccount("AMerchant")
                              .setBrowserInfoData("User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36", "*/*")
                              .setShopperIP("1.2.3.4");

        return abstractPaymentRequest;
    }

    /**
     * Returns a sample PaymentRequest opbject with full card data
     */
    protected PaymentRequest createFullCardPaymentRequest() {
        return createBasePaymentRequest(new PaymentRequest()).reference("123456")
                .setAmountData("1000", "EUR")
                .setCardData("5136333333333335", "John Doe", "08", "2018", "737");
    }

    protected PaymentsRequest createAfterPayPaymentRequest() {

        PaymentsRequest paymentsRequest = new PaymentsRequest();
        paymentsRequest.setMerchantAccount("YOUR_MERCHANT_ACCOUNT");
        paymentsRequest.setCountryCode("NL");

        Amount amount = new Amount();
        amount.setCurrency("EUR");
        amount.setValue(1000L);

        paymentsRequest.setAmount(amount);
        paymentsRequest.setShopperReference("YOUR_UNIQUE_SHOPPER_ID");
        paymentsRequest.setReference("YOUR_ORDER_NUMBER");
        paymentsRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);

        DefaultPaymentMethodDetails defaultPaymentMethodDetails = new DefaultPaymentMethodDetails();
        defaultPaymentMethodDetails.setType("afterpay_default");

        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFirstName("EndToEnd");
        personalDetails.setLastName("lastName");
        personalDetails.setGender(Gender.MALE);
        personalDetails.setDateOfBirth("2000-02-02");
        personalDetails.setTelephoneNumber("+31612345678");
        personalDetails.setShopperEmail("SHOPPER@EMAIL_ADDRESS.COM");

        defaultPaymentMethodDetails.setPersonalDetails(personalDetails);
        defaultPaymentMethodDetails.setSeparateDeliveryAddress(false);

        paymentsRequest.setPaymentMethod(defaultPaymentMethodDetails);

        Address billingAddress = new Address();
        billingAddress.setStreet("Simon Carmiggeltstraat");
        billingAddress.setHouseNumberOrName("136");
        billingAddress.setCity("Amsterdam");
        billingAddress.setPostalCode("1011DJ");
        billingAddress.setCountry("NL");

        paymentsRequest.setBillingAddress(billingAddress);
        paymentsRequest.setShopperIP("192.0.2.1");

        List<LineItem> lineItems = new ArrayList<>();

        lineItems.add(
                new LineItem()
                    .quantity(1L)
                    .amountExcludingTax(331L)
                    .taxPercentage(2100L)
                    .description("Shoes")
                    .id("Item #1")
                    .taxAmount(69L)
                    .amountIncludingTax(400L)
                    .taxCategory(LineItem.TaxCategoryEnum.HIGH)
                    .imageUrl(DUMMY_PROTOCOL_IMAGE_URL)
                    .productUrl(DUMMY_PROTOCOL_PRODUCT_URL)
        );

        lineItems.add(
                new LineItem()
                .quantity(2L)
                .amountExcludingTax(248L)
                .taxPercentage(2100L)
                .description("Socks")
                .id("Item #2")
                .taxAmount(52L)
                .amountIncludingTax(300L)
                .taxCategory(LineItem.TaxCategoryEnum.HIGH)
                .imageUrl(DUMMY_PROTOCOL_IMAGE_URL)
                .productUrl(DUMMY_PROTOCOL_PRODUCT_URL)
        );

        paymentsRequest.setLineItems(lineItems);

        return paymentsRequest;

    }

    /**
     * Returns a sample PaymentRequest opbject with full OpenInvoice request
     */
    protected PaymentRequest createOpenInvoicePaymentRequest() {

        Date dateOfBirth = DateUtil.parseYmdDate("1970-07-10");

        PaymentRequest paymentRequest = createBasePaymentRequest(new PaymentRequest()).reference("123456").setAmountData("200", "EUR");

        // Set Shopper Data
        paymentRequest.setShopperEmail("youremail@email.com");
        paymentRequest.setDateOfBirth(dateOfBirth);
        paymentRequest.setTelephoneNumber("0612345678");
        paymentRequest.setShopperReference("4");

        // Set Shopper Info
        Name shopperName = new Name();
        shopperName.setFirstName("Testperson-nl");
        shopperName.setLastName("Approved");
        shopperName.gender(Name.GenderEnum.MALE);
        paymentRequest.setShopperName(shopperName);

        // Set Billing and Delivery address
        Address address = new Address();
        address.setCity("Gravenhage");
        address.setCountry("NL");
        address.setHouseNumberOrName("1");
        address.setPostalCode("2521VA");
        address.setStateOrProvince("Zuid-Holland");
        address.setStreet("Neherkade");
        paymentRequest.setDeliveryAddress(address);
        paymentRequest.setBillingAddress(address);

        // Use OpenInvoice Provider (klarna, ratepay)
        paymentRequest.selectedBrand("klarna");

        Long itemAmount = new Long("9000");
        Long itemVatAmount = new Long("1000");
        Long itemVatPercentage = new Long("1000");

        List<InvoiceLine> invoiceLines = new ArrayList<>();

        // invoiceLine1
        InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setCurrencyCode("EUR");
        invoiceLine.setDescription("Test product");
        invoiceLine.setItemAmount(itemAmount);
        invoiceLine.setItemVATAmount(itemVatAmount);
        invoiceLine.setItemVatPercentage(itemVatPercentage);
        invoiceLine.setVatCategory(VatCategory.NONE);
        invoiceLine.setNumberOfItems(1);
        invoiceLine.setItemId("1234");

        // invoiceLine2
        InvoiceLine invoiceLine2 = new InvoiceLine();
        invoiceLine2.setCurrencyCode("EUR");
        invoiceLine2.setDescription("Test product 2");
        invoiceLine2.setItemAmount(itemAmount);
        invoiceLine2.setItemVATAmount(itemVatAmount);
        invoiceLine2.setItemVatPercentage(itemVatPercentage);
        invoiceLine2.setVatCategory(VatCategory.NONE);
        invoiceLine2.setNumberOfItems(1);
        invoiceLine2.setItemId("4567");

        invoiceLines.add(invoiceLine);
        invoiceLines.add(invoiceLine2);

        paymentRequest.setInvoiceLines(invoiceLines);

        return paymentRequest;
    }

    /**
     * Returns a sample PaymentRequest object with CSE data
     */
    protected PaymentRequest createCSEPaymentRequest() {

        return createBasePaymentRequest(new PaymentRequest()).reference("123456")
                .setAmountData("1000", "EUR")
                .setCSEToken("adyenjs_0_1_4p1$...");
    }

    /**
     * Returns a PaymentRequest3d object for 3D secure authorisation
     */
    protected PaymentRequest3d create3DPaymentRequest() {

        return createBasePaymentRequest(new PaymentRequest3d()).set3DRequestData("mdString", "paResString");
    }

    /**
     * Returns a PaymentRequest3d object for 3D secure authorisation
     */
    protected PaymentRequest3ds2 create3DS2PaymentRequest() {

        PaymentRequest3ds2 paymentRequest3ds2 = createBasePaymentRequest(new PaymentRequest3ds2());
        paymentRequest3ds2.setThreeDS2Token("— - BINARY DATA - -");
        paymentRequest3ds2.setThreeDS2RequestData(new ThreeDS2RequestData());
        paymentRequest3ds2.getThreeDS2RequestData().setThreeDSCompInd("Y");
        return paymentRequest3ds2;
    }

    /**
     * Returns a Client that has a mocked error response from fileName
     */
    protected Client createMockClientForErrors(int status, String fileName) {
        String response = getFileContents(fileName);

        AdyenHttpClient adyenHttpClient = mock(AdyenHttpClient.class);
        HTTPClientException httpClientException = new HTTPClientException(status, "An error occured", new HashMap<>(), response);
        try {
            when(adyenHttpClient.request(anyString(), anyString(), any(Config.class), anyBoolean(), isNull(), any())).thenThrow(httpClientException);
        } catch (IOException | HTTPClientException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        Client client = new Client();
        client.setHttpClient(adyenHttpClient);
        Config config = new Config();
        config.setCheckoutEndpoint(Client.CHECKOUT_ENDPOINT_TEST);
        client.setConfig(config);

        return client;
    }

    protected <T extends AbstractModificationRequest> T createBaseModificationRequest(T modificationRequest) {
        modificationRequest.merchantAccount("AMerchant").originalReference("originalReference").reference("merchantReference");

        return modificationRequest;
    }

    protected CaptureRequest createCaptureRequest() {
        CaptureRequest captureRequest = createBaseModificationRequest(new CaptureRequest());

        captureRequest.fillAmount("15.00", "EUR");

        return captureRequest;
    }

    protected RefundRequest createRefundRequest() {
        Amount amount = Util.createAmount("15.00", "EUR");

        return createBaseModificationRequest(new RefundRequest()).modificationAmount(amount);
    }

    protected VoidPendingRefundRequest createVoidPendingRefundRequest() {
        return createBaseModificationRequest(new VoidPendingRefundRequest()).tenderReference("tenderReference");
    }

    protected DonationRequest createDonationRequest() {
        Amount amount = Util.createAmount("15.00", "EUR");

        DonationRequest donationRequest = new DonationRequest();
        donationRequest.setMerchantAccount("AMerchant");
        donationRequest.setDonationAccount("donationAccount");
        donationRequest.setModificationAmount(amount);
        donationRequest.setOriginalReference("originalReference");

        return donationRequest;
    }

    protected TerminalAPIRequest createTerminalAPIPaymentRequest() throws DatatypeConfigurationException {
        SaleToPOIRequest saleToPOIRequest = new SaleToPOIRequest();

        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setProtocolVersion("3.0");
        messageHeader.setMessageClass(MessageClassType.SERVICE);
        messageHeader.setMessageCategory(MessageCategoryType.PAYMENT);
        messageHeader.setMessageType(MessageType.REQUEST);
        messageHeader.setSaleID("001");
        messageHeader.setServiceID("001");
        messageHeader.setPOIID("P400Plus-123456789");

        saleToPOIRequest.setMessageHeader(messageHeader);

        com.adyen.model.nexo.PaymentRequest paymentRequest = new com.adyen.model.nexo.PaymentRequest();

        SaleData saleData = new SaleData();
        TransactionIdentification transactionIdentification = new TransactionIdentification();
        transactionIdentification.setTransactionID("001");
        XMLGregorianCalendar timestamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
        transactionIdentification.setTimeStamp(timestamp);
        saleData.setSaleTransactionID(transactionIdentification);

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        AmountsReq amountsReq = new AmountsReq();
        amountsReq.setCurrency("EUR");
        amountsReq.setRequestedAmount(BigDecimal.ONE);
        paymentTransaction.setAmountsReq(amountsReq);

        paymentRequest.setSaleData(saleData);
        paymentRequest.setPaymentTransaction(paymentTransaction);

        saleToPOIRequest.setPaymentRequest(paymentRequest);

        TerminalAPIRequest terminalAPIRequest = new TerminalAPIRequest();
        terminalAPIRequest.setSaleToPOIRequest(saleToPOIRequest);

        return terminalAPIRequest;
    }

    protected AuthenticationResultRequest createAuthenticationResultRequest() {
        AuthenticationResultRequest authenticationResultRequest = new AuthenticationResultRequest();
        authenticationResultRequest.setMerchantAccount("AMerchant");
        authenticationResultRequest.setPspReference("APspReference");
        return authenticationResultRequest;
    }
}
