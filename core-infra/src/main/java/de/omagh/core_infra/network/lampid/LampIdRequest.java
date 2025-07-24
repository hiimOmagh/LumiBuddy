package de.omagh.core_infra.network.lampid;

/**
 * Minimal request body for lamp identification.
 */
public class LampIdRequest {
    private final String image;

    public LampIdRequest(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
