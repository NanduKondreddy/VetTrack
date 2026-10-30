package com.vettrack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OwnerCreateRequest {

    @NotBlank(message = "Owner name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    @Email(message = "Valid email is required")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    public OwnerCreateRequest() {}

    public OwnerCreateRequest(String name, String phone, String email, String city) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.city = city;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
