package com.microservice.seller.controller;

import com.microservice.seller.model.Seller;
import com.microservice.seller.repository.SellerRepository;
import com.microservice.seller.response.Response;
import com.microservice.seller.service.SpecificationSearch;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Enumeration;

@RestController
@RequestMapping("api/seller")
@RequiredArgsConstructor
@Slf4j
public class SellerController {

    private final SellerRepository sellerRepository;
    private final SpecificationSearch search;
    private final WebClient webClient;


    @PostMapping("register")
    public ResponseEntity<Response> saveAdminDetails(@RequestBody Seller seller) throws InterruptedException {
        System.out.println(seller.getName());
        Thread.sleep(10000);
        System.out.println(seller.getName());

        Response response = new Response();
        log.info("trace id {}", MDC.get("traceId"));
        Assert.notNull(seller, "Admin model is null");
        sellerRepository.save(seller);
        response.setData(seller);
        return ResponseEntity.ok(response);
    }

    @GetMapping("fetch")
    public ResponseEntity<Response> getAllAdminDetails(Pageable pageable, HttpServletRequest request) {
        Response response = new Response();
        System.out.println(request.getRemoteUser());
//        System.out.println(request.changeSessionId());
        Enumeration<String> host = request.getHeaders("Host");
        System.out.println(request.getRemotePort());
        System.out.println(request.getRemoteHost());
        System.out.println(request.getRemoteUser());
        System.out.println(request.getRemoteAddr());
        Enumeration<String> headerNames = request.getHeaderNames();
        System.out.println(headerNames);
        response.setData(sellerRepository.findAll(pageable));
        StringBuilder hostValues = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String hostValue = headerNames.nextElement();
            hostValues.append(hostValue).append(", ");
        }
        System.out.println(hostValues);
        return ResponseEntity.ok(response);
    }

    @GetMapping("fetchById/{id}")
    public ResponseEntity<Response> findById(@PathVariable("id") Integer id) {
        Response response = new Response();
        response.setData(sellerRepository.findById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("buyer")
    public ResponseEntity<Response> findTheBuyerDetails() {
//        HttpHeaders headers= new HttpHeaders();
//        headers.set("Authorization","bearer token");
        // Handle the response asynchronously
        Response response = webClient.get()
                .uri("http://localhost:8082/api/buyer/fetch")
                .header(HttpHeaders.AUTHORIZATION, "godson")//to set the header values
                .retrieve()
                .bodyToMono(Response.class)
                .block();

        return ResponseEntity.ok(response);
    }

    @PostMapping("spec")
    public ResponseEntity<Response> searchCriteria(@RequestBody Seller seller, Pageable pageable) {
        Response response = new Response();
        Page<Seller> page = search.criteriaSearch(seller, pageable);
        response.setData(page);
        return ResponseEntity.ok(response);
    }

    //hashing using java
    @GetMapping("calling")
    public void callingTheOtherServices(@RequestBody String name) throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] bytes = name.getBytes();
        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        byte[] digest = instance.digest(bytes);
        String v = new String(digest, StandardCharsets.UTF_8);
        System.out.println(v);
        System.out.println("input : " + name);
        System.out.println("digest : " + Arrays.toString(digest));
    }

    @GetMapping("syn")
    public void symmetricKey(@RequestBody String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        KeyGenerator instance = KeyGenerator.getInstance("AES");
        instance.init(256);
        Key keys = instance.generateKey();
        System.out.println(keys.getFormat());
        //extra
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] random = new byte[16];
        secureRandom.nextBytes(random);
        IvParameterSpec spec = new IvParameterSpec(random);
        //we have created the key now we need to encrypted it
        Cipher ch = Cipher.getInstance("AES/ECB/PKCS5Padding");
        ch.init(Cipher.ENCRYPT_MODE, keys, spec);
        byte[] encrypted_text = ch.doFinal(key.getBytes());
        String encrypted = new String(encrypted_text, StandardCharsets.UTF_8);
        System.out.println(encrypted);
        //now we are encrypted the message
        ch.init(Cipher.DECRYPT_MODE, keys, spec);
        byte[] bytes = ch.doFinal(encrypted_text);
        String str = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(str);
        //now we are decrypted the message
    }

    @GetMapping("asy")
    public void AsymmetricCryptography(@RequestBody String key) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //generate the private and the public key
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
        byte[] encrypted_Text = cipher.doFinal(key.getBytes());
        //encrypted the text
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());
        byte[] decrypted_Text = cipher.doFinal(encrypted_Text);
        String txt = new String(decrypted_Text, StandardCharsets.UTF_8);
        System.out.println(txt);
    }

    @GetMapping("dig")
    public void DigitalSignature(@RequestBody String key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//encrypted the message and signature
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(key.getBytes());
        byte[] sign = signature.sign();
        String val = new String(sign, StandardCharsets.UTF_8);
        System.out.println(val);
//        decrypting the message and the signature
        Signature signature1 = Signature.getInstance("SHA256withRSA");
        signature1.initVerify(keyPair.getPublic());
        signature1.update(key.getBytes());
        boolean matches = signature1.verify(sign);
        System.out.println(matches);
    }

}
