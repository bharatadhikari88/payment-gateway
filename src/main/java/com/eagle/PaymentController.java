package com.eagle;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.PaymentMethod;
import com.braintreegateway.PaymentMethodNonce;
import com.braintreegateway.PaymentMethodRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;

@RestController
public class PaymentController {

    @Autowired
    private BraintreeGateway gateway;

    /** Create customer on merchant account **/
    @PostMapping("/customer")
    public Customer createCustomer() {
	Result<Customer> result = gateway.customer().create(new CustomerRequest().firstName("test"));
	Customer customer = null;
	if (result.isSuccess()) {
	    customer = result.getTarget();
	}
	return customer;
    }

    /** add card for customer **/
    @PostMapping("/paymentMethod/{id}")
    public CreditCard createPaymentMethod(@PathVariable("id") String customerId) {
	CreditCardRequest creditCardRequest = new CreditCardRequest();
	creditCardRequest.number("5555555555554444").cvv("100").expirationDate("11/2019").customerId(customerId)
		.options().verifyCard(true);
	Result<CreditCard> result = gateway.creditCard().create(creditCardRequest);
	CreditCard creditCard = null;
	if (result.isSuccess()) {
	    creditCard = result.getTarget();
	}
	return creditCard;
    }

    /** use token field from card and create nonce **/
    @PostMapping("/paymentMethodNonce/{paymentMethodToken}")
    public PaymentMethodNonce createPaymentMethodNonce(@PathVariable("paymentMethodToken") String paymentMethodToken) {
	Result<PaymentMethodNonce> result = gateway.paymentMethodNonce().create(paymentMethodToken);
	PaymentMethodNonce paymentMethodNonce = null;
	if (result.isSuccess()) {
	    paymentMethodNonce = result.getTarget();
	}
	return paymentMethodNonce;
    }

    /** use nonce for payment. Lifespan of nonce is 3 hour or one use **/
    @PostMapping("/payment/{paymentMethodNonce}")
    public Transaction makePayment(@PathVariable("paymentMethodNonce") String paymentMethodNonce) {
	Result<Transaction> result = gateway.transaction().sale(new TransactionRequest().amount(new BigDecimal(10))
		.paymentMethodNonce(paymentMethodNonce).options().done());

	Transaction transaction = null;
	if (result.isSuccess()) {
	    transaction = result.getTarget();
	}
	return transaction;
    }

    @GetMapping("/customer/{id}")
    public Customer getCustomer(@PathVariable("id") String customerId) {
	return gateway.customer().find(customerId);
    }

    @PostMapping("/paymentMethod/{id}/{paymentMethodNonce}")
    public PaymentMethod createPaymentMethod(@PathVariable("id") String customerId,
	    @PathVariable("paymentMethodNonce") String paymentMethodNonce) {
	Result<? extends PaymentMethod> result = gateway.paymentMethod()
		.create(new PaymentMethodRequest().customerId(customerId).paymentMethodNonce(paymentMethodNonce));
	PaymentMethod paymentMethod = null;
	if (result.isSuccess()) {
	    paymentMethod = result.getTarget();
	}
	return paymentMethod;
    }

}
