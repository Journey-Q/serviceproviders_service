package com.example.serviceproviders_service.services;

import com.example.serviceproviders_service.dto.ProductRequest;
import com.example.serviceproviders_service.dto.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;
    //stripe api
    //productname , amount , quantity , currency
    //return sessionId , sessionurl

    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        Stripe.apiKey =secretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
                        .setUnitAmount(productRequest.getAmount())
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(productRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:8080/success")
                        .setCancelUrl("http://localhost:8080/cancel")
                        .addLineItem(lineItem)
                        .build();

        Session session = null;

        try {
            session = Session.create(params);
        } catch (StripeException ex) {
            System.out.println(ex.getMessage());
        }

        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment successful")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
