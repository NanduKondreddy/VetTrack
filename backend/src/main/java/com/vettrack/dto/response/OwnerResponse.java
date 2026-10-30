package com.vettrack.dto.response;

import java.time.OffsetDateTime;

public class OwnerResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String city;
    private int animalCount;
    private OffsetDateTime createdAt;

    public OwnerResponse() {}

    public OwnerResponse(Long id, String name, String phone, String email, String city, int animalCount, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.animalCount = animalCount;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public int getAnimalCount() { return animalCount; }
    public void setAnimalCount(int animalCount) { this.animalCount = animalCount; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
